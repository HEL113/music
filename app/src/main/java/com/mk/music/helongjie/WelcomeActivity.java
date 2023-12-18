package com.mk.music.helongjie;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    private static final int DELAY_TIME = 3000; // 延迟时间，单位为毫秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        TextView welcomeText = findViewById(R.id.welcome_text);
        RelativeLayout backgroundLayout = findViewById(R.id.background_layout);

        ObjectAnimator textFadeOutAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 1.0f, 0.0f);
        textFadeOutAnimation.setDuration(4000); // 文字渐变消失的时间，单位为毫秒

        ObjectAnimator backgroundFadeOutAnimation = ObjectAnimator.ofFloat(backgroundLayout, "alpha", 1.0f, 0.0f);
        backgroundFadeOutAnimation.setDuration(4000); // 背景渐变消失的时间，单位为毫秒

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textFadeOutAnimation, backgroundFadeOutAnimation);
        animatorSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToLoginActivity();
            }
        }, DELAY_TIME);
    }
    @Override
    public void onBackPressed() {
        // 按下返回键时不执行任何操作
    }
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 添加页面渐变的切换动画
        finish();
    }
}