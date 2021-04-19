package com.microink.clandroid.java.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Cass
 * @version v1.0
 * @Date 4/13/21 5:40 PM
 *
 * 异常工具类
 */
public class ExceptionUtil {

    /**
     * 异常转为String
     * @param e
     * @return
     */
    public static String exceptionToString(Throwable e) {
        if (e == null){
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
