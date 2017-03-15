package com.mark.media.mediaplayer.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 显示Toast
 * Created by Mark on 2016/6/25.
 */
public class ShowToast {
    public static void show(final Activity context, final String str) {
        if (Thread.currentThread().getName().equals("main")) {
            // 主线程
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } else {
            // 其他线程
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
