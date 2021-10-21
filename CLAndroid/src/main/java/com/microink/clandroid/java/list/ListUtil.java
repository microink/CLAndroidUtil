package com.microink.clandroid.java.list;

import java.util.List;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/10/18 10:37 上午
 *
 * 列表工具类
 */
public class ListUtil {

    public static <T> String listToString(List<T> list, String startStr, String emptyStr) {
        if (null == list || 0 == list.size()) {
            return emptyStr;
        }
        StringBuilder stringBuilder = new StringBuilder(startStr);
        stringBuilder.append("[");
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).toString());
            if (i == list.size() - 1) {
                stringBuilder.append("]");
            } else {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
