package com.microink.clandroid.android.notification;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * @author Cass
 * @version v1.0
 * @Date 5/19/21 10:51 AM
 *
 * 声音通知工具类
 */
public class VoiceNotificationUtil {

    /**
     * 播放系统通知提示音
     * @param context
     */
    public static void playSystemNotificationVoice(Context context) {
        if (null == context) {
            return;
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(context.getApplicationContext(), uri);
        rt.play();
    }
}
