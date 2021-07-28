package com.microink.clandroid.android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/7/28 2:47 下午
 *
 * SharePreference工具类
 */
public class SharedPreferenceUtil {

    // 默认的CLAndroid的preference名字
    public static final String CL_ANDROID = "CLAndroid";

    public static void saveString(Context context, String preferenceName,
            String key, String value) {
        SharedPreferences.Editor editor = getEditor(context, preferenceName);
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String preferenceName, String key,
            String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, 0);
        return settings.getString(key, defaultValue);
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getDefaultEditor(context);
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key,
            String defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static void saveInt(Context context, String preferenceName,
            String key, int value) {
        SharedPreferences.Editor editor = getEditor(context, preferenceName);
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String preferenceName, String key,
            int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, 0);
        return settings.getInt(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getDefaultEditor(context);
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public static void saveLong(Context context, String preferenceName,
            String key, long value) {
        SharedPreferences.Editor editor = getEditor(context, preferenceName);
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String preferenceName, String key,
            long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, 0);
        return settings.getLong(key, defaultValue);
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences.Editor editor = getDefaultEditor(context);
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    public static void saveFloat(Context context, String preferenceName,
            String key, float value) {
        SharedPreferences.Editor editor = getEditor(context, preferenceName);
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String preferenceName, String key,
            float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, 0);
        return settings.getFloat(key, defaultValue);
    }

    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences.Editor editor = getDefaultEditor(context);
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public static void saveBoolean(Context context, String preferenceName,
            String key, boolean value) {
        SharedPreferences.Editor editor = getEditor(context, preferenceName);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String preferenceName, String key,
            boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, 0);
        return settings.getBoolean(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getDefaultEditor(context);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    /**
     * 获取Editor
     * @param context
     * @param preferenceName
     * @return
     */
    private static SharedPreferences.Editor getEditor(Context context, String preferenceName) {
        SharedPreferences sp = context.getSharedPreferences(preferenceName, 0);
        SharedPreferences.Editor editor = sp.edit();
       return editor;
    }

    /**
     * 获取默认的Editor
     * @param context
     * @return
     */
    private static SharedPreferences.Editor getDefaultEditor(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        return editor;
    }
}
