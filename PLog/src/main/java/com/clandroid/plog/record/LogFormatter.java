package com.clandroid.plog.record;


import com.clandroid.plog.core.PLog;

/**
 * Created by wyh on 2019/3/10.
 */

public interface LogFormatter {

    String format(@PLog.DebugLevel.Level int debugLevel, String tag, String msg);

}
