package com.mark.media.mediaplayer.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mark.media.mediaplayer.R;
import com.mark.media.mediaplayer.utils.DataContants;
import com.mark.media.mediaplayer.utils.ShowToast;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by mark on 2016/10/11 0011.
 */

public class VideoUI extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private VideoView mVideoView;
    private TextView mDownloadRateView;
    private TextView mLoadRateView;
    private ProgressBar mProgress;
    private String mPath;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_video);
        mPath = DataContants.VEDIO_2_URL;

        initView();

        if (Vitamio.isInitialized(VideoUI.this)) {

            if (TextUtils.isEmpty(mPath)) {
                // Tell the user to provide a media file URL/path.
                ShowToast.show(VideoUI.this, "Please edit VideoBuffer Activity, and set path" + " variable to your media file URL/path");
                return;
            } else {
            /*
             * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
                uri = Uri.parse(mPath);
                mVideoView.setVideoURI(uri);
                mVideoView.setMediaController(new MediaController(this));
                mVideoView.requestFocus();

                mVideoView.setOnInfoListener(this);
                mVideoView.setOnBufferingUpdateListener(this);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // optional need Vitamio 4.0
                        mediaPlayer.setPlaybackSpeed(1.0f);
                    }
                });
            }

        }
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.video);

        //显示缓冲百分比的TextView
        mDownloadRateView = (TextView) findViewById(R.id.download_rate);
        //显示下载网速的TextView
        mLoadRateView = (TextView) findViewById(R.id.load_rate);

        // 进度条
        mProgress = (ProgressBar) findViewById(R.id.probar);
    }


    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mProgress.setVisibility(View.VISIBLE);
                    mDownloadRateView.setText("");
                    mLoadRateView.setText("");
                    mDownloadRateView.setVisibility(View.VISIBLE);
                    mLoadRateView.setVisibility(View.VISIBLE);
                }
                break;

            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                mProgress.setVisibility(View.GONE);
                mDownloadRateView.setVisibility(View.GONE);
                mLoadRateView.setVisibility(View.GONE);
                break;

            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                mDownloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mLoadRateView.setText(percent + "%");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
