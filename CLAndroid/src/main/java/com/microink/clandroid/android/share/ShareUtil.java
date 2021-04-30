package com.microink.clandroid.android.share;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Cass
 * @version v1.0
 * @Date 3/12/21 3:50 PM
 *
 * 分享工具类
 */
public class ShareUtil {

    /**
     * 分享图片
     * @param activity
     * @param bitmap
     * @param title
     * @param text
     * @param saveBitmapNameNoSuffix 保存Bitmap的文件名 不要有后缀
     * @throws IOException
     */
    public static void shareImage(Activity activity, Bitmap bitmap, String title, String text,
            String saveBitmapNameNoSuffix)
            throws IOException {
        Intent share_intent = new Intent();
        // 设置分享行为
        share_intent.setAction(Intent.ACTION_SEND);
        // 设置分享内容的类型
        share_intent.setType("image/*");
        Uri uri = saveBitmap(activity, bitmap, Bitmap.CompressFormat.JPEG, "image/*",
                saveBitmapNameNoSuffix + ".jpeg", null);
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
        //share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        // 添加分享内容
        if (!TextUtils.isEmpty(text)) {
            share_intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        // 创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, title);
        activity.startActivity(share_intent);
    }

    /**
     * 分享文字
     */
    public static void shareText(Activity activity, String title, String content) {
        Intent share_intent = new Intent();
        // 设置分享行为
        share_intent.setAction(Intent.ACTION_SEND);
        // 设置分享内容的类型
        share_intent.setType("text/plain");
        // 添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_SUBJECT, title);
        // 添加分享内容
        share_intent.putExtra(Intent.EXTRA_TEXT, content);
        // 创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, title);
        activity.startActivity(share_intent);
    }

    @NonNull
    private static Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
            @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType,
            @NonNull final String displayName, @Nullable final String subFolder) throws IOException {
        String relativeLocation = Environment.DIRECTORY_PICTURES;

        if (!TextUtils.isEmpty(subFolder)) {
            relativeLocation += File.separator + subFolder;
        }

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
        }

        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null) {
                throw new IOException("Failed to get output stream.");
            }

            if (bitmap.compress(format, 95, stream) == false) {
                throw new IOException("Failed to save bitmap.");
            }

            return uri;
        } catch (IOException e) {
            if (uri != null)
            {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        } finally {
            if (stream != null)
            {
                stream.close();
            }
        }
    }
}
