package com.app.owenfastfood.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.app.owenfastfood.constant.AboutUsConfig;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.databinding.ActivityIntroBinding;
import com.app.owenfastfood.prefs.DataStoreManager;
import com.app.owenfastfood.utils.StringUtil;

@SuppressLint("CustomSplashScreen")
public class LoadingActivity extends BaseActivity {
    private ActivityIntroBinding mActivitySplashBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySplashBinding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(mActivitySplashBinding.getRoot());
        initUi();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::goToNextActivity, 2000);
    }

    private void initUi() {
        mActivitySplashBinding.tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        mActivitySplashBinding.tvAboutUsSlogan.setText(AboutUsConfig.ABOUT_US_SLOGAN);
    }

    private void goToNextActivity() {
        if (DataStoreManager.getUser() != null && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            GlobalFunction.gotoMainActivity(this);
        } else {
            GlobalFunction.startActivity(this, LoginActivity.class);
        }
        finish();
    }
}