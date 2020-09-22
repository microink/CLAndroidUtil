package com.microink.clandroid.java.reflect;

import java.lang.reflect.Field;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 2:52 PM
 *
 * Reflection tool class
 * 反射工具类
 */
public class ReflectUtil {

    /**
     * Get field
     * 获取属性值
     *
     * @param fieldName Field name
     * @param clazz Class
     * @param object Object
     * @return Field object
     */
    public static Object getFieldValueByFieldName(String fieldName, Class clazz, Object object) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            // get private field
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != field) {
                field.setAccessible(false);
            }
        }
    }

    /**
     * Get field
     * 获取属性值
     *
     * @param fieldName Field name
     * @param object Object
     * @return Field object
     */
    public static Object getFieldValueByFieldName(String fieldName, Object object) {
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
            // get private field
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != field) {
                field.setAccessible(false);
            }
        }
    }

    /**
     * Set field
     * 设置属性值
     *
     * @param fieldName Field name
     * @param object object
     * @return Whether the setup was successful
     */
    public static boolean setFieldValueByFieldName(String fieldName, Object object, Object value) {
        try {
            Class c = object.getClass();
            Field f = c.getDeclaredField(fieldName);
            // Cancel the language access check
            // 取消语言访问检查
            f.setAccessible(true);
            // set
            f.set(object, value);
            f.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
