package com.microink.clandroid.java.array;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/10/18 10:57 上午
 *
 * 数组工具类
 */
public class ArrayUtil {

    public static <T> String arrayToString(T[] array, String startStr, String emptyStr) {
        if (null == array || 0 == array.length) {
            return emptyStr;
        }
        StringBuilder stringBuilder = new StringBuilder(startStr);
        stringBuilder.append("[");
        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(array[i].toString());
            if (i == array.length - 1) {
                stringBuilder.append("]");
            } else {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
