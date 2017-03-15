package com.mark.media.mediaplayer.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * 缓存图片
 * Created by Mark on 2016/6/25.
 */
@SuppressLint("NewApi")
public class BitmapCache implements ImageLoader.ImageCache {

    private int maxSize = 10 * 1024 * 1024;
    private final LruCache<String, Bitmap> mCache;

    public BitmapCache() {
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {

        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }
}
