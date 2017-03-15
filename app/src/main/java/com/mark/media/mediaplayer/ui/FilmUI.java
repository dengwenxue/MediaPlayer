package com.mark.media.mediaplayer.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mark.media.mediaplayer.R;
import com.mark.media.mediaplayer.utils.DataContants;

/**
 * Created by mark on 2016/12/18.
 */

public class FilmUI extends Activity {
    private static final String TAG = NewsDetailsUI.class.getSimpleName();
    private WebView mFilm;
    private ProgressBar mLoadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_film);

        Intent intent = getIntent();
        String url = intent.getStringExtra(DataContants.FILMURL);

        mFilm = (WebView) findViewById(R.id.wv_ui_film);
        mLoadingPB = (ProgressBar) findViewById(R.id.pb_loading);

        WebSettings settings = mFilm.getSettings();

        // 设置WebView的属性
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持)
        settings.setJavaScriptEnabled(true);// 支持js功能
        settings.setUseWideViewPort(true);// 双击缩放

        mFilm.loadUrl(url);

        //
        mFilm.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "开始加载页面...");
                mLoadingPB.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "完成页面加载...");
                mLoadingPB.setVisibility(View.GONE);
            }

            // 所有链接跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("跳转链接:" + url);
                view.loadUrl(url);// 在跳转链接时强制在当前webview中加载
                return true;
            }
        });

        // mWebView.goBack();//跳到上个页面
        // mWebView.goForward();//跳到下个页面

        mFilm.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // 进度发生变化
                System.out.println("进度:" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // 网页标题
                System.out.println("网页标题:" + title);
            }
        });
    }

}
