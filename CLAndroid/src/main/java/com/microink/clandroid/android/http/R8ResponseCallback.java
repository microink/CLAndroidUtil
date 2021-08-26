package com.microink.clandroid.android.http;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Call;

/**
 * @author Cass
 * @version v1.0
 * @Date 2021/8/26 2:44 下午
 *
 * R8优化会将 ResponseCallBack中的this.getClass()->ResponseCallBack.class
 * 创建一个继承自ResponseCallBack的类让R8不会执行上面的替换
 */
class R8ResponseCallback extends ResponseCallBack<String>{
    @Override
    public void onFailed(int code, Exception e, @Nullable @org.jetbrains.annotations.Nullable String responseStr, long useTime, @Nullable @org.jetbrains.annotations.Nullable Call call) {

    }

    @Override
    public void onResponse(int code, @NonNull @NotNull String response, String responseStr, long useTime, @Nullable @org.jetbrains.annotations.Nullable Call call) {

    }
}
