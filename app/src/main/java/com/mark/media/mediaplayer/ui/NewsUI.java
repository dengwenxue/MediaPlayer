package com.mark.media.mediaplayer.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mark.media.mediaplayer.R;
import com.mark.media.mediaplayer.adapter.NewsAdapter;
import com.mark.media.mediaplayer.bean.NewsBean;
import com.mark.media.mediaplayer.utils.CacheUtils;
import com.mark.media.mediaplayer.utils.DataContants;
import com.mark.media.mediaplayer.widget.OnItemClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻页面
 * Created by Mark on 2016/6/27.
 */
public class NewsUI extends Activity implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener, View.OnClickListener {

    private static final String TAG = NewsUI.class.getSimpleName();
    private NewsAdapter mNewsAdapter;
    private RequestQueue mRequestQueue;
    private NewsBean mBean;
    private List<NewsBean.ResultBean> mNewsData;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_news);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr_news);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mBack = (ImageView) findViewById(R.id.iv_ui_news_back);

        mRequestQueue = Volley.newRequestQueue(this);

        // 2.设置刷新的实现
        mRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        mRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        initData();

        mBack.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mNewsData = new ArrayList<>();

        // 1.获取缓存
        String cache = CacheUtils.getCache(DataContants.CACHE, NewsUI.this);
        // 2.缓存存在
        if (!TextUtils.isEmpty(cache)) {
            processJson(cache);
        }

        // 3.缓存不存在
        getAndProcessNewsDatasFromNet();
    }


    /**
     * 获取并处理json数据
     *
     * @param
     */
    private void getAndProcessNewsDatasFromNet() {

        JsonObjectRequest jsonRequest = new JsonObjectRequest(DataContants.NEWSINTERFACE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理json数据
                        processJson(response + "");

                        // 设置缓存
                        CacheUtils.setCache(DataContants.CACHE, response + "", NewsUI.this);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, volleyError.getMessage(), volleyError);
                    }
                });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(jsonRequest);
    }

    private void processJson(String json) {
        Gson gson = new Gson();
        mBean = gson.fromJson(json, NewsBean.class);

        // 结果数据
        mNewsData = mBean.result;

        // 设置数据
        mNewsAdapter = new NewsAdapter(NewsUI.this, mNewsData);
        mRecyclerView.setAdapter(mNewsAdapter);

        // 设置点击事件
        mNewsAdapter.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
                // 刷新
                mNewsAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onItemClick(View view, int position) {
        String newsUrl = mNewsData.get(position).url;

        Intent intent = new Intent(NewsUI.this, NewsDetailsUI.class);
        intent.putExtra(DataContants.NEWSURL, newsUrl);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.iv_ui_news_back){
            this.finish();
        }
    }
}
