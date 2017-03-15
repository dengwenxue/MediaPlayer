package com.mark.media.mediaplayer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.mark.media.mediaplayer.MediaActivity;
import com.mark.media.mediaplayer.R;

/**
 * 欢迎页面的实现
 */
public class WelcomeUI extends Activity {

    private static final int ANIM_DELAY = 2000;
    private RelativeLayout mWelcomeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_welcome);

        mWelcomeLayout = (RelativeLayout) findViewById(R.id.welcome);

        // 设置补间动画
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(ANIM_DELAY);
        mWelcomeLayout.startAnimation(animation);

        // 设置监听
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 停顿2s,再跳转
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(WelcomeUI.this, MediaActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, ANIM_DELAY);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
