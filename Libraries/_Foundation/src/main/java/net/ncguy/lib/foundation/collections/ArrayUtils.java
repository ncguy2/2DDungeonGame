package net.ncguy.lib.foundation.collections;

import java.lang.reflect.Array;

public class ArrayUtils {

    public static <T> T[] Append(Class<T> type, T[] arr, T... elems) {
        T[] newArr = (T[]) Array.newInstance(type, arr.length + elems.length);

        System.arraycopy(arr, 0, newArr, 0, arr.length);
        System.arraycopy(elems, 0, newArr, arr.length, elems.length);

        return newArr;
    }

}
