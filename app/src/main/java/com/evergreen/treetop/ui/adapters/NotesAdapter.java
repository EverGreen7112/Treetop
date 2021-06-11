package com.evergreen.treetop.ui.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnContextClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.evergreen.treetop.R;
import com.evergreen.treetop.ui.adapters.NotesAdapter.BaseHolder;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends Adapter<BaseHolder> {

    private final List<String> m_data = new ArrayList<>();
    private final List<Boolean> m_types = new ArrayList<>();
    private final Context m_context;

    private static final int TEXT_TYPE = 0;
    private static final int IMAGE_TYPE = 1;

    private static final int TEXT_NOTE_HEIGHT = LayoutParams.WRAP_CONTENT;
    private static final int TEXT_NOTE_WIDTH = LayoutParams.MATCH_PARENT;
    private static final int IMAGE_NOTE_HEIGHT = LayoutParams.WRAP_CONTENT;
    private static final int IMAGE_NOTE_WIDTH = LayoutParams.MATCH_PARENT;

    public final int DENSITY;
    private static final int CONTEXT_DELETE_ITEM_ID = 12;

    public NotesAdapter(Context context) {
        m_context = context;
        DENSITY = (int)context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getItemCount() {
        return m_data.size();
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TEXT_TYPE:
                return new TextHolder();
            case IMAGE_TYPE:
                return new ImageHolder();
            default:
                return null; // Should never ever happen
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        holder.setContent(m_data.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return m_types.get(position) ? IMAGE_TYPE : TEXT_TYPE;
    }

    public void add(String data, boolean isImage) {
        m_data.add(data);
        m_types.add(isImage);
        notifyItemInserted(getItemCount());
    }

    public void clear() {
        m_data.clear();
        m_types.clear();
        notifyDataSetChanged();
    }

    public static abstract class BaseHolder extends ViewHolder {

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void setContent(String data);

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, CONTEXT_DELETE_ITEM_ID, Menu.NONE, "Delete Note");


//            delete.setOnMenuItemClickListener( item -> {
//                    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//
//                    Editor editor = TM_NotesActivity.getSaveFile(m_context).edit();
//                    editor.remove(TM_NotesActivity.NOTE_TYPE_PREFIX_KEY + info.position);
//                    editor.remove(TM_NotesActivity.NOTE_PREFIX_KEY + info.position);
//                    editor.putInt(TM_NotesActivity.NOTES_NUMBER_KEY + info.position, );
//                    editor.apply();
//                    return true;
//            });
        }

    }

    private class TextHolder extends BaseHolder {

        public TextHolder() {
            super(new TextView(m_context));
            TextView view = (TextView)itemView;
            view.setLayoutParams(new LayoutParams(TEXT_NOTE_WIDTH, TEXT_NOTE_HEIGHT));
            view.setPaddingRelative(10, 10, 10, 10);
            view.setTextSize(16);
            itemView.setBackground(ContextCompat.getDrawable(m_context, R.drawable.bottom_border));
        }

        @Override
        public void setContent(String text) {
            ((TextView)itemView).setText(text);
        }
    }

    private class ImageHolder extends BaseHolder {

        public ImageHolder() {
            super(new ImageView(m_context));
            ((ImageView)itemView).setAdjustViewBounds(true);
            itemView.setLayoutParams(new LayoutParams(IMAGE_NOTE_WIDTH, IMAGE_NOTE_HEIGHT));
        }

        @Override
        public void setContent(String path) {
            ((ImageView)itemView).setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }





}
