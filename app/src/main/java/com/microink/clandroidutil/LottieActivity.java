package com.microink.clandroidutil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.bumptech.glide.Glide;
import com.microink.clandroid.android.http.OkHttpUtil;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Cass
 * @version v1.0
 * @Date 2020/9/23 9:08 AM
 */
public class LottieActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnShowGif;
    private Button mBtnShowLottie;
    private Button mBtnShowSVGA;
    private ImageView mIV;
    private LottieAnimationView mLottieView;
    private SVGAImageView mSVGA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);

        initView();
    }

    private void initView() {
        mIV = findViewById(R.id.iv_main);
        mLottieView = findViewById(R.id.lottie_main);
        mSVGA = findViewById(R.id.svga_main);
        mBtnShowGif = findViewById(R.id.btn_gif_main);
        mBtnShowLottie = findViewById(R.id.btn_lottie_main);
        mBtnShowSVGA = findViewById(R.id.btn_svga_main);

        mBtnShowGif.setOnClickListener(this);
        mBtnShowLottie.setOnClickListener(this);
        mBtnShowSVGA.setOnClickListener(this);
    }

    private void showGif() {
        // 设置圆角 5.0以上
        // 1. 设置背景shape为圆角
        // 2. 设置setClipToOutline(true)
        //mIV.setClipToOutline(true);
        mIV.setVisibility(View.VISIBLE);
        mLottieView.setVisibility(View.GONE);
        mSVGA.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.big2_2).into(mIV);
    }

    private void showLottie() {
        mIV.setVisibility(View.GONE);
        mLottieView.setVisibility(View.VISIBLE);
        mSVGA.setVisibility(View.GONE);
        try {
            //InputStream fis = getAssets().open("loading/data.json");
            LottieTask<LottieComposition> s = LottieCompositionFactory.fromAsset(
                    this, "big2/data.json");
            mLottieView.setImageAssetsFolder("big2/images");
            s.addListener(new LottieListener<LottieComposition>() {
                @Override
                public void onResult(LottieComposition result) {
                    mLottieView.setComposition(result);
                    mLottieView.setRepeatCount(LottieDrawable.INFINITE);
                    mLottieView.playAnimation();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSVGA() {
        mIV.setVisibility(View.GONE);
        mLottieView.setVisibility(View.GONE);
        mSVGA.setVisibility(View.VISIBLE);
        SVGAParser.Companion.shareParser().init(this);
        SVGAParser.Companion.shareParser().decodeFromAssets("big2.svga",
                new SVGAParser.ParseCompletion() {

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onComplete(SVGAVideoEntity svgaVideoEntity) {
                        mSVGA.setVideoItem(svgaVideoEntity);
                        mSVGA.startAnimation();
                    }
                });
    }

    private void loadURL() {
        String url = "https://www.baidu.com";
        OkHttpUtil.OkHttpUtilBuilder builder =
                OkHttpUtil.OkHttpUtilBuilder.requestUlr(url);
        builder.postJsonRequestCallString(new OkHttpUtil.OkHttpUtilStringCallback() {
            @Override
            public void onFailed(int code, Exception e, @Nullable okhttp3.Call call) {
                Log.i("LottieActivity", "onFailed()");
            }

            @Override
            public void onResponse(int code, String response, @Nullable okhttp3.Call call) {
                Log.i("LottieActivity", "onResponse()");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gif_main:
                //showGif();
                loadURL();
                break;
            case R.id.btn_lottie_main:
                showLottie();
                break;
            case R.id.btn_svga_main:
                showSVGA();
                break;
        }
    }
}
