package com.microink.clandroid.android.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Call;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/17 6:42 PM
 *
 * 网络请求泛型模板超类
 */
public abstract class AbsCallback <T>{

    public abstract void onFailed(int code, Exception e,
            @Nullable String responseStr, long useTime, @Nullable Call call);
    public abstract void onResponse(int code, @NonNull T response,
            String responseStr, long useTime, @Nullable Call call);
}
