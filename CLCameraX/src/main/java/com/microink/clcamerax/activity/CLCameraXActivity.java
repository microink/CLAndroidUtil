package com.microink.clcamerax.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;
import com.microink.clandroid.android.activity.CLBaseActivity;
import com.microink.clandroid.android.dialog.DialogUtil;
import com.microink.clandroid.android.img.BitmapUtil;
import com.microink.clandroid.android.log.PrintLineLog;
import com.microink.clandroid.java.camera.YuvToRgbConverter;
import com.microink.clcamerax.camera.CustomLifecycle;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.MutableConfig;
import androidx.camera.extensions.BokehImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author Cass
 * @version v1.0
 * @Date 5/6/21 10:07 AM
 *
 * CameraX基类Activity
 */
public abstract class CLCameraXActivity extends CLBaseActivity {

    protected static final String CL_CAMERAX_TAG = "CLCameraXActivity";

    // 权限code
    protected static final int PERMISSION_CAMERA_CODE = 1004;

    protected PreviewView mPreviewView; // 预览视图
    protected ImageView mIVPause; // 暂停视图

    protected Camera mCamera; // 相机
    protected ImageCapture imageCapture; // 相机捕获类
    protected Executor takePictureExecutor; // 拍照线程池
    protected CustomLifecycle customLifecycle; // 相机自定义生命周期控制
    protected ImageProxy imageProxy; // 暂存的ImageProxy，关闭用以获取下一帧
    protected Bitmap handleBitmap; // 处理中的Bitmap，保留引用用以后续recycle

    protected boolean isIgnore; // 是否忽略当前帧，因为缓存的是上一帧的，不然识别的是之前的数据
    protected boolean isPause; // 是否暂停中

    @Override
    protected void initData() {
        takePictureExecutor = Executors.newSingleThreadExecutor();
        customLifecycle = new CustomLifecycle();

        initDataCameraX();
    }

    /**
     * CameraXActivity的专用initData方法
     */
    protected abstract void initDataCameraX();

    @Override
    protected void initView() {
        mPreviewView = findViewById(getPreviewViewId());
        int pauseViewId = getPauseViewId();
        if (0 != pauseViewId) {
            mIVPause = findViewById(pauseViewId);
        }

        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initViewCameraX();
    }

    /**
     * 获取预览视图ID
     * @return
     */
    protected abstract int getPreviewViewId();

    /**
     * 获取暂停视图ID
     * @return
     */
    protected abstract int getPauseViewId();

    /**
     * CameraXActivity的专用initView方法
     */
    protected abstract void initViewCameraX();

    @Override
    protected void fullUI() {

        // 检查权限
        checkPermission();

        fullUICameraX();
    }

    /**
     * CameraXActivity的专用fullUI方法
     */
    protected abstract void fullUICameraX();

    /**
     * 检查相机权限
     */
    private void checkPermission() {
        String[] permissions = new String[]{Manifest.permission.CAMERA};
        boolean cameraPermissionOpen =
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                        permissions[0]);
        if (!cameraPermissionOpen) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA_CODE);
        } else {
            mPreviewView.post(new Runnable() {
                @Override
                public void run() {
                    initCameraPreviewSize(resizePreviewViewWidthHeight());
                }
            });
        }
    }

    /**
     * 定义预览视图的大小，int[width, height]
     * 如果不定义获取定义错误，则使用默认
     * @return
     */
    protected abstract int[] resizePreviewViewWidthHeight();

    /**
     * 初始化相机预览视图大小
     * 此时已经可以获取视图的LayoutParams
     */
    protected void initCameraPreviewSize(int[] widthHeight) {
        int[] sizes = widthHeight;
        if (null == sizes || 2 != sizes.length) {
            // 宽高错误
        } else {
            // 宽高参数正确，设置宽高
            ViewGroup.LayoutParams layoutParams = mPreviewView.getLayoutParams();
            layoutParams.width = sizes[0];
            layoutParams.height = sizes[1];
            mPreviewView.setLayoutParams(layoutParams);
        }
        initCamera();
    }

    private void initCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(mBaseActivity);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(mBaseActivity));
    }

    /**
     * 绑定预览视图
     * @param cameraProvider
     */
    @SuppressLint("UnsafeExperimentalUsageError")
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // 选择后置摄像头
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // 拍照参数
        ImageCapture.Builder builder = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                // 默认拍照最高大小
                //.setTargetResolution(captureSize)
                //.setTargetRotation(mPreviewView.getDisplay().getRotation())
                // 最高质量，但是拍摄延迟高
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY);
        // 最快速度，但是质量可能低
        //.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY);

        // 拍照供应商拓展
        addCameraXEx(builder, cameraSelector);

        imageCapture = builder.build();

        Size chooseSize = chooseSize();
        if (null == chooseSize) {
            // 相机参数异常
            sizeError();
            return;
        }

        // 预览参数
        Preview preview = new Preview.Builder()
                //.setTargetRotation(mPreviewView.getDisplay().getRotation())
                .build();


        // 图像分析参数
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(chooseSize)
                        //.setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        //.setTargetRotation(mPreviewView.getDisplay().getRotation())
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        // 相机线程池
        Executor cameraExecutor = Executors.newSingleThreadExecutor();
        final YuvToRgbConverter yuvToRgbConverter =
                new YuvToRgbConverter(mBaseActivity);

        // 图像分析相关逻辑
        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {

            @Override
            public void analyze(@NonNull ImageProxy image) {
                if (isIgnore) {
                    isIgnore = false;
                    try {
                        image.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (0 >= image.getWidth() || 0 >= image.getHeight()) {
                    try {
                        image.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Rect rect = image.getCropRect();
                if (isPause) {
                    image.close();
                    return;
                }
                isIgnore = true;
                // 转换bitmap
                long time = System.currentTimeMillis();
                Image realImage = image.getImage();
                if (null == realImage) {
                    image.close();
                    return;
                }
                //realImage.setCropRect(rect);
                if (0 >= rect.width() || 0 >= rect.height()) {
                    try {
                        image.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(),
                        Bitmap.Config.ARGB_8888);
                yuvToRgbConverter.yuvToRgb(image.getImage(), bitmap, rect);

                bitmap = BitmapUtil.rotateBitmap(bitmap, 90, false);

                long useTime = System.currentTimeMillis() - time;
                convertYUVRotateBitmapTime(useTime);

                if (isPause) {
                    if (null != bitmap) {
                        bitmap.recycle();
                    }
                    image.close();
                }
                // 处理图像
                processImg(bitmap, image);
            }
        });

        ViewPort viewPort = mPreviewView.getViewPort();
        if (null == viewPort) {
            viewPortNull();
            return;
        }

        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .addUseCase(imageCapture)
                .setViewPort(viewPort)
                .build();

        mCamera = cameraProvider.bindToLifecycle(customLifecycle,
                cameraSelector, useCaseGroup);

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
    }

    /**
     * 加入供应商提供的CameraX拍照拓展
     * @param builder
     */
    @SuppressLint("RestrictedApi")
    private void addCameraXEx(ImageCapture.Builder builder, CameraSelector cameraSelector) {
        // 供应商拓展
        BokehImageCaptureExtender bokehImageCapture = BokehImageCaptureExtender.create(builder);
        if (bokehImageCapture.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            bokehImageCapture.enableExtension(cameraSelector);
        }
        try {
            // todo 不完善，狗日的不支持
            // 尝试调用光学防抖
            MutableConfig mutableConfig = builder.getMutableConfig();
            Config.Option<Object> config =
                    Config.Option.create(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE.getName(),
                            Object.class, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON);

            mutableConfig.insertOption(config, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 选择尺寸
     * @return
     */
    protected abstract Size chooseSize();

    /**
     * 相机尺寸错误
     */
    protected abstract void sizeError();

    /**
     * 选择最大的宽高
     * @return
     */
    protected Size chooseMaxSize() {
        // 选择使用图片分析的尺寸
        Size[] canUseSizes = new Size[]{new Size(mPreviewView.getWidth(),
                mPreviewView.getHeight())};
        try {
            int cameraId = -1;
            int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
            for (int i = 0; i <= numberOfCameras; i++) {
                android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(i, info);
                if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraId = i;
                    break;
                }
            }
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics =
                    cameraManager.getCameraCharacteristics(String.valueOf(cameraId));
            StreamConfigurationMap streamConfigurationMap = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            canUseSizes = streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Size useSize = new Size(mPreviewView.getWidth(),
                mPreviewView.getHeight());
        if (null != canUseSizes && 0 < canUseSizes.length) {
            //useSize = canUseSizes[0];
            for (Size size : canUseSizes) {
                if ((size.getWidth() / 4) == (size.getHeight() / 3)) {
                    useSize = size;
                    break;
                }
            }
        }

        // 转换宽高
        Size newSize = new Size(useSize.getHeight(), useSize.getWidth());
        return newSize;
    }

    /**
     * 获取相机可用的尺寸列表
     * @return
     */
    protected Size[] getCameraSizeArray() {
        Size[] canUseSizes = null;
        try {
            int cameraId = -1;
            int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
            for (int i = 0; i <= numberOfCameras; i++) {
                android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(i, info);
                if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraId = i;
                    break;
                }
            }
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics =
                    cameraManager.getCameraCharacteristics(String.valueOf(cameraId));
            StreamConfigurationMap streamConfigurationMap = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            canUseSizes = streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return canUseSizes;
    }

    /**
     * 从PreviewView中获取到ViewPort为null的情况
     */
    protected abstract void viewPortNull();

    /**
     * 转换YUV到Bitmap并旋转90度所用的时间
     * @param useTime
     */
    protected abstract void convertYUVRotateBitmapTime(long useTime);

    /**
     * 处理图像
     * @param bitmap
     */
    @SuppressLint("UnsafeExperimentalUsageError")
    private void processImg(Bitmap bitmap, ImageProxy imageProxy) {
        if (null == imageProxy) {
            return;
        }
        if (null == bitmap) {
            imageProxy.close();
            return;
        }

        this.imageProxy = imageProxy;
        handleBitmap = bitmap;
        cameraXAnalyzeBitmap(bitmap);
    }

    protected abstract void cameraXAnalyzeBitmap(Bitmap bitmap);

    /**
     * 分析完成回调
     */
    protected void analyzeFinish() {
        if (null != imageProxy) {
            try {
                imageProxy.close();
            } catch (Exception e) {
                PrintLineLog.e(CL_CAMERAX_TAG, e);
            }
        }
        if (null != handleBitmap) {
            handleBitmap.recycle();
            handleBitmap = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isPause) {
            customLifecycle.doOnStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPause) {
            customLifecycle.doOnResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        customLifecycle.doOnPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customLifecycle.doOnStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customLifecycle.doOnDestory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PERMISSION_CAMERA_CODE == requestCode) {
            if (null == permissions || 0 == permissions.length) {
                return;
            }
            boolean cameraPermissionOpen =
                    PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                            permissions[0]);
            if (!cameraPermissionOpen) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA_CODE);
            } else {
                mPreviewView.post(new Runnable() {
                    @Override
                    public void run() {
                        initCameraPreviewSize(resizePreviewViewWidthHeight());
                    }
                });
            }
        }
    }

    /**
     * 点击CameraX暂停或者恢复
     */
    protected void clickCameraXPauseOrContinue() {
        if (isPause) {
            isPause = false;
            customLifecycle.doOnStart();
            if (null != mIVPause) {
                mIVPause.setVisibility(View.INVISIBLE);
            }
            cameraXContinue();
        } else {
            isPause = true;
            customLifecycle.doOnStop();

            View resultView = pauseViewOnPreview();
            if (null == resultView) {
                Bitmap cameraBitmap = mPreviewView.getBitmap();
                if (null != mIVPause) {
                    mIVPause.setImageBitmap(cameraBitmap);
                }
            } else {
                Bitmap bitmap = Bitmap.createBitmap(resultView.getWidth(),
                        resultView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                resultView.draw(canvas);
                Bitmap cameraBitmap = mPreviewView.getBitmap();
                Bitmap mergeBitmap = BitmapUtil.mergeOnBackRectBitmap(cameraBitmap, bitmap);
                try {
                    bitmap.recycle();
                    cameraBitmap.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (null != mIVPause) {
                    mIVPause.setImageBitmap(mergeBitmap);
                }
            }

            if (null != mIVPause) {
                mIVPause.setVisibility(View.VISIBLE);
            }

            cameraXPause();
        }
    }

    /**
     * 获取暂停后需要驻留的视图，用以将结果绘制在Preview生成的Bitmap上
     * @return
     */
    @Nullable
    protected abstract View pauseViewOnPreview();

    /**
     * CameraX继续
     */
    protected abstract void cameraXContinue();

    /**
     * CameraX暂停
     */
    protected abstract void cameraXPause();

    /**
     * 调用拍照
     * @param path
     */
    protected void clickCameraXTakePhoto(final String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, takePictureExecutor,
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults outputFileResults) {
                        customLifecycle.doOnStop();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            if (null == bitmap) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        takePhotoError(new RuntimeException("BitmapFactory." +
                                                "decodeFile is null"));
                                    }
                                });
                                return;
                            }
                            // 华为手机，拍照已经旋转过了，所以
                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                // 说明需要旋转
                                bitmap = BitmapUtil.rotateBitmap(bitmap,
                                        90, true);
                            }

                            // 回调
                            takePhotoResult(bitmap);

                        } catch (final Exception e) {
                            PrintLineLog.e(CL_CAMERAX_TAG, e);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    takePhotoError(e);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull final ImageCaptureException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                takePhotoError(exception);
                            }
                        });
                    }
                });
    }

    /**
     * 拍照发生错误
     */
    protected abstract void takePhotoError(Exception exception);

    /**
     * 拍照结果
     * 注意，这是拍照线程池回调
     * @param bitmap
     */
    protected abstract void takePhotoResult(Bitmap bitmap);
}
