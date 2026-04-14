package me.spencernold.janus.generic;

import java.lang.reflect.Array;
import java.util.List;
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

    public static <T> T[] concat(Class<T> clazz, T[] arr1, T[] arr2) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(clazz, arr1.length + arr2.length);
        System.arraycopy(arr1, 0, array, 0, arr1.length);
        System.arraycopy(arr2, 0, array, arr1.length, arr2.length);
        return array;
    }

    public static <T> T[] transform(T[] array, Function<T, T> function) {
        final int length = array.length;
        for (int i = 0; i < length; i++)
            array[i] = function.apply(array[i]);
        return array;
    }

    public static byte[] toByteArray(List<Byte> list) {
        final int length = list.size();
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++)
            array[i] = list.get(i);
        return array;
    }
}
