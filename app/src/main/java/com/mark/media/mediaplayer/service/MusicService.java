package com.mark.media.mediaplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mark.media.mediaplayer.R;

/**
 * 服务控制音乐
 * Created by mark on 2016/6/27.
 */
public class MusicService extends Service {

    private static final String TAG = MusicService.class.getSimpleName();
    private MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = MediaPlayer.create(this, R.raw.daybyday);

        mPlayer.setLooping(true);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "开始听歌啦...");
        mPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "关闭啦...");
        mPlayer.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
