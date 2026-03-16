package me.spencernold.janus.generic;

import java.util.function.Function;

public class Arrays2 {

    public static <T> String[] transformToString(T[] values) {
        return transformToString(values, String::valueOf);
    }

    public static <T> String[] transformToString(T[] values, Function<T, String> function) {
        String[] array = new String[values.length];
        for (int i = 0; i < values.length; i++)
            array[i] = function.apply(values[i]);
        return array;
    }
}
