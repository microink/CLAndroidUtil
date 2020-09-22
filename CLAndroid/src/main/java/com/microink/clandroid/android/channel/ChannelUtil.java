package com.microink.clandroid.android.channel;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/22 10:33 AM
 *
 * Channel Util
 */
public class ChannelUtil {
    /**
     * Get channel ID
     * 获取渠道ID
     *
     * Like this
     * <application>
     *     ...
     *     <meta-data android:name="CHANNEL_ID" android:value="GooglePlay" />
     * </application>
     *
     * @param context Context
     * @param metaData meta-data android name
     * @return In AndroidManifest.xml channel name
     */
    public static String getChannelID(Context context, String metaData) {
        if (null == context || TextUtils.isEmpty(metaData)) {
            return null;
        }
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String adtChannelID = appInfo.metaData.getString(metaData);
        return adtChannelID;
    }
}
