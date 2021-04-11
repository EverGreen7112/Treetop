package com.evergreen.treetop.architecture;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utilities {
    public static <T> String stringify(List<T> list) {
        return  stringify(list, obj -> obj);
    }

    public static <T> String stringify(List<T> list, Function<T, Object> propertyMapper) {
        return "[" + list.stream().map(propertyMapper).map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    public static <T> String stringify(T[] arr) {
        return stringify(Arrays.asList(arr));
    }
}
