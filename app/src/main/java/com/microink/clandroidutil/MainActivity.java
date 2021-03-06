package com.microink.clandroidutil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
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
import com.microink.clandroid.android.executor.AppExecutors;
import com.microink.clandroid.android.log.PrintLineLog;
import com.microink.clandroid.android.screen.ScreenUtil;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout mCLAll;
    private Button mBtnShowGif;
    private Button mBtnShowLottie;
    private Button mBtnShowSVGA;
    private Button mBtnJump;
    private ImageView mIV;
    private LottieAnimationView mLottieView;
    private SVGAImageView mSVGA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        int activityHeight = ScreenUtil.getActivityHeight(this);
        int screenHeight = ScreenUtil.getScreenHeight(this);
        //Log.i("Cass", "");
    }

    private void initView() {
        mCLAll = findViewById(R.id.cl_main);
        mIV = findViewById(R.id.iv_main);
        mLottieView = findViewById(R.id.lottie_main);
        mSVGA = findViewById(R.id.svga_main);
        mBtnShowGif = findViewById(R.id.btn_gif_main);
        mBtnShowLottie = findViewById(R.id.btn_lottie_main);
        mBtnShowSVGA = findViewById(R.id.btn_svga_main);
        mBtnJump = findViewById(R.id.btn_jump_main);

        mBtnShowGif.setOnClickListener(this);
        mBtnShowLottie.setOnClickListener(this);
        mBtnShowSVGA.setOnClickListener(this);
        mBtnJump.setOnClickListener(this);

        PrintLineLog.i("Test", true);

        PrintLineLog.i("Haahahahah", true);
    }

    private void showGif() {
        // 设置圆角 5.0以上
        // 1. 设置背景shape为圆角
        // 2. 设置setClipToOutline(true)
        //mIV.setClipToOutline(true);
        Glide.with(this).load(R.drawable.loading_gif).into(mIV);
    }

    private void showLottie() {
        try {
            //InputStream fis = getAssets().open("loading/data.json");
            LottieTask<LottieComposition> s = LottieCompositionFactory.fromAsset(
                    this, "loading2/data.json");
            mLottieView.setImageAssetsFolder("loading2/images");
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
        SVGAParser.Companion.shareParser().init(this);
        SVGAParser.Companion.shareParser().decodeFromAssets("loading_svga.svga",
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gif_main:
                showGif();
                break;
            case R.id.btn_lottie_main:
                showLottie();
                break;
            case R.id.btn_svga_main:
                showSVGA();
                break;
            case R.id.btn_jump_main:
                Intent intent = new Intent(this, LottieActivity.class);
                startActivity(intent);
                break;
        }
    }
}