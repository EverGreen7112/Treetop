package com.evergreen.treetop.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.evergreen.treetop.R;
import com.evergreen.treetop.activities.tasks.units.TM_UnitPickerActivity;
import com.evergreen.treetop.architecture.Exceptions.NoSuchDocumentException;
import com.evergreen.treetop.architecture.LoggingUtils;
import com.evergreen.treetop.architecture.tasks.data.Unit;
import com.evergreen.treetop.architecture.tasks.data.User;
import com.evergreen.treetop.architecture.tasks.handlers.UnitDB;
import com.evergreen.treetop.architecture.tasks.utils.DBUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class UnitPickerAdapter extends Adapter<ViewHolder> {

    private List<Unit> m_previousData;
    private final List<Unit> m_data;
    private final TM_UnitPickerActivity m_context;
    private final User m_user;
    private final Unit m_rootUnit;


    public UnitPickerAdapter(TM_UnitPickerActivity context) throws ExecutionException, InterruptedException {
        this(context, null, null);
    }

    public UnitPickerAdapter(TM_UnitPickerActivity context, User user) throws ExecutionException, InterruptedException {
        this(context, user, null);
    }

    public UnitPickerAdapter(TM_UnitPickerActivity context, User user, Unit rootUnit) throws ExecutionException, InterruptedException {
        m_data = UnitDB.getInstance().getRootUnits();
        m_context = context;
        m_user = user;
        m_rootUnit = rootUnit;
        Log.v("UI_EVENT", "Created a new GoalList with values " + LoggingUtils.stringify(m_data));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v("UI_EVENT", "Created new UnitHolder");
        return new UnitHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        new Thread(() -> {
            UnitHolder holderUnit = (UnitHolder)holder;
            Unit unit = m_data.get(position);

            m_context.runOnUiThread( () -> {
                holderUnit.setContent(unit);

                holderUnit.getNameView().setOnClickListener(v -> {
                    m_context.setResult(
                            Activity.RESULT_OK,
                            new Intent()
                            .putExtra(TM_UnitPickerActivity.RESULT_PICKED_EXTRA_KEY, DBUnit.of(m_data.get(position)))
                    );

                    m_context.finish();
                });


                if (unit.getChildrenIds().size() > 0)  {
                    holderUnit.getMoreView().setOnClickListener(v ->
                            new Thread( () -> {
                                try {
                                    goDown(unit);
                                } catch (ExecutionException e) {
                                    Toast.makeText(m_context, "Could not retrieve children", Toast.LENGTH_SHORT).show();
                                    Log.w("DB_ERROR", "Tried to retrieve children of " + unit.toString()
                                            + ", but failed:\n" + ExceptionUtils.getStackTrace(e));
                                } catch (InterruptedException e) {
                                    Log.w("DB_ERROR", "Canceled retrieving " + unit.toString() + ":\n"
                                            + ExceptionUtils.getStackTrace(e));
                                }
                            }).start()
                    );
                }

                Log.v("UI_EVENT", "Bound new GoalHolder to " + m_data.get(position));
            });
        }).start();

    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    public class UnitHolder extends ViewHolder {

        private final TextView m_textName;
        private final TextView m_textMore;

        public UnitHolder(ViewGroup parent) {

            super(LayoutInflater.from(m_context)
                    .inflate(R.layout.listrow_recycler_unit_picker, parent, false)
            );

            m_textName = itemView.findViewById(R.id.tm_unit_picker_text_title);
            m_textMore = itemView.findViewById(R.id.tm_unit_picker_text_more_icon);
        }

        public void setContent(Unit unit) {
            m_textName.setText(unit.getName());
            if (unit.getChildrenIds().size() > 0) {
                m_textMore.setText("ᐅ");
            }
        }

        public View getView() {
            return itemView;
        }

        public TextView getNameView() {
            return m_textName;
        }

        public TextView getMoreView() {
            return m_textMore;
        }

    }

    public List<String> getUnitIds() {
        return m_data.stream().map(Unit::getId).collect(Collectors.toList());
    }

    public List<Unit> getData() {
        return m_data;
    }

    public void clear() {
        int init = getItemCount();
        m_data.clear();
        notifyItemRangeRemoved(0, init);
    }


    public void  filterForUser(User user) {
        if (user == null) return;
        List<Unit> newData = m_data.stream().filter(unit -> user.getUnitIds().contains(unit.getId())).collect(Collectors.toList());
        m_data.clear();
        m_data.addAll(newData);
    }

    public void goUp() throws ExecutionException, InterruptedException, NoSuchDocumentException {

        Unit pivot = m_data.get(0);

        if (isRootUnit(pivot)) {
            Toast.makeText(m_context, "Viewing Root Units", Toast.LENGTH_SHORT).show();
            return;
        }

        Unit parent = pivot.getParent();

        if (parent.isRootUnit()) {
            m_data.clear();
            m_data.addAll(UnitDB.getInstance().getRootUnits());
            filterForUser(m_user);
            m_context.runOnUiThread(this::notifyDataSetChanged);
            return;
        }

        goDown(parent.getParent());

    }

    public void goDown(Unit parent) throws ExecutionException, InterruptedException {
        List<Unit> children = parent.getChildren();
        m_data.clear();
        m_data.addAll(children);
        filterForUser(m_user);
        m_context.runOnUiThread(() -> {
            notifyDataSetChanged();
            m_context.getSupportActionBar().setTitle(parent.getName());
        });
    }

    // MUST be ran in TM_UnitPickerActivity#onBackPressed.
    public void onBackPressed() {
        new Thread( () -> {
            Looper.prepare();
            try {
                goUp();
            } catch (ExecutionException e) {
                Toast.makeText(m_context, "Failed to get parent units", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve parents of " + m_data.get(0).toString()
                        + " but failed:\n" + ExceptionUtils.getStackTrace(e));
            } catch (InterruptedException e) {
                Log.w("DB_ERROR", "Cancelled retrieval of parent units.\n" + ExceptionUtils.getStackTrace(e));
            } catch (NoSuchDocumentException e) {
                Toast.makeText(m_context, "Could not identify parent unit", Toast.LENGTH_SHORT).show();
                Log.w("DB_ERROR", "Tried to retrieve parents of " + m_data.get(0).toString()
                        + " but there is no such unit:\n" + ExceptionUtils.getStackTrace(e));
            }
        }).start();
    }

    private boolean isRootUnit(Unit unit) {
        if (m_rootUnit == null) {
            return unit.isRootUnit();
        }

        return unit.getParentId().equals(m_rootUnit.getId());
    }



}


