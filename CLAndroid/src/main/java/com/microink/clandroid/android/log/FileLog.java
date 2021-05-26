package com.microink.clandroid.android.log;

import com.microink.clandroid.CLAndroidUtil;
import com.microink.clandroid.java.exception.ExceptionUtil;
import com.clandroid.plog.core.PLog;

//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.core.Filter;
//import org.apache.logging.log4j.core.appender.ConsoleAppender;
//import org.apache.logging.log4j.core.config.Configurator;
//import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
//import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
//import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
//import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
//import org.apache.logging.log4j.spi.ExtendedLogger;
//import org.apache.logging.log4j.spi.LoggerContext;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Cass
 * @version v1.0
 * @Date 5/26/21 2:25 PM
 *
 * 本地记录log工具类
 */
public class FileLog {
    public static boolean LOG_DEBUG_FILE = false;

    private static final String TAG = "FileLog";

    private static volatile FileLog sInstance;

    //private ExtendedLogger logger; // Logger记录

    private String savePath; // 保存路径
    private Executor ioExecutor; // 记录本地线程池

    private FileLog() {
        if (null == CLAndroidUtil.sContext) {
            return;
        }
        ioExecutor = Executors.newSingleThreadExecutor();
        String path = CLAndroidUtil.sContext.getExternalFilesDir("log")
                .getAbsolutePath();
        savePath = path;

        PLog.Config config = new PLog.Config.Builder(CLAndroidUtil.sContext)
                .logDir(savePath) //日志存放目录，默认优先存储于SD卡
                .logcatDebugLevel(PLog.DebugLevel.VERBOSE) //允许输出到Logcat的级别
                .recordDebugLevel(PLog.DebugLevel.VERBOSE) //允许记录到日志文件的级别
                .fileSizeLimitDay(15) //单天日志文件存储上限
                .overdueDay(3) //日志文件过期天数
                .cipherKey("CLAndroid") //日志密钥
                .build();
        PrintLineLog.i("CassTest", config.toString());
        PLog.init(config);
    }

    public static FileLog getInstance() {
        if (null == sInstance) {
            synchronized (FileLog.class) {
                if (null == sInstance) {
                    sInstance = new FileLog();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     */
    //public void init() {
    //    ConfigurationBuilder<BuiltConfiguration> builder =
    //            ConfigurationBuilderFactory.newConfigurationBuilder();
    //    builder.setStatusLevel(Level.DEBUG);
    //    builder.setConfigurationName("RollingBuilder");
    //    builder.add(builder.newFilter("ThresholdFilter",
    //            Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
    //            .addAttribute("level", Level.TRACE));
    //    // create a console appender
    //    AppenderComponentBuilder appenderBuilder =
    //            builder.newAppender("Stdout", "CONSOLE")
    //                    .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
    //    appenderBuilder.add(builder.newLayout("PatternLayout")
    //            .addAttribute("pattern", "%d  [%t] %-5level: %msg%n%throwable"));
    //    builder.add(appenderBuilder);
    //
    //
    //    // create a rolling file appender
    //    LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
    //            .addAttribute("pattern", "%d  [%t] %-5level: %msg%n");
    //    ComponentBuilder triggeringPolicy = builder.newComponent(null, "Policies")
    //            //.addComponent(builder.newComponent("CronTriggeringPolicy")
    //            // .addAttribute("schedule", "0 0 0 * * ?"))//每天凌晨清理文件
    //            .addComponent(builder.newComponent(null, "SizeBasedTriggeringPolicy")
    //                    .addAttribute("size", "1M"));
    //    ComponentBuilder defaultRolloverStrategy =
    //            builder.newComponent(null, "DefaultRolloverStrategy")
    //                    .addAttribute("max", 20);//设置文件夹下有几个日志文件
    //    DateFormat formatter = new SimpleDateFormat("yyyy-MMdd-HH-mm-ss");
    //    long timestamp = System.currentTimeMillis();
    //    String time = formatter.format(new Date());
    //    String fileName = "log-" + time + "-" + timestamp + ".log";
    //    appenderBuilder = builder.newAppender("rolling", "RollingFile")
    //            .addAttribute("fileName", savePath + File.separator + fileName)
    //            .addAttribute("filePattern", savePath + File.separator + "rolling-%d{yyyy-MM-dd}-%i.txt")
    //            //.add(builder.newFilter("MarkerFilter", Filter.Result.ACCEPT,
    //            //        Filter.Result.DENY)
    //            //        .addAttribute("marker", "NAME"))//过滤"NAME"的log
    //            .add(layoutBuilder)
    //            .addComponent(triggeringPolicy)
    //            .addComponent(defaultRolloverStrategy);
    //
    //    builder.add(appenderBuilder);
    //
    //    // create the new logger
    //    builder.add(builder.newLogger("FileLog", Level.DEBUG)
    //            .add(builder.newAppenderRef("rolling"))
    //            .addAttribute("additivity", false));
    //    builder.add(builder.newRootLogger(Level.DEBUG)
    //            .add(builder.newAppenderRef("rolling")));
    //    LoggerContext ctx = Configurator.initialize(builder.build());
    //    logger = ctx.getLogger("FileLog");
    //}

    public void v(final String msg) {
        if (!LOG_DEBUG_FILE) {
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PLog.record(PLog.DebugLevel.VERBOSE, TAG, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void d(final String msg) {
        if (!LOG_DEBUG_FILE) {
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PLog.record(PLog.DebugLevel.DEBUG, TAG, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void i(final String msg) {
        if (!LOG_DEBUG_FILE) {
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PLog.record(PLog.DebugLevel.INFO, TAG, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void w(final String msg) {
        if (!LOG_DEBUG_FILE) {
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PLog.record(PLog.DebugLevel.WARNING, TAG, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void e(final String msg) {
        if (!LOG_DEBUG_FILE) {
            return;
        }
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PLog.record(PLog.DebugLevel.ERROR, TAG, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void e(Throwable throwable) {
        if (!LOG_DEBUG_FILE) {
            return;
        }
        final String msg = ExceptionUtil.exceptionToString(throwable);
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PLog.record(PLog.DebugLevel.ERROR, TAG, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
