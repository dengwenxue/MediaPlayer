package com.mark.media.mediaplayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mark.media.mediaplayer.adapter.FilmAdapter;
import com.mark.media.mediaplayer.service.MusicService;
import com.mark.media.mediaplayer.ui.FilmUI;
import com.mark.media.mediaplayer.ui.NewsUI;
import com.mark.media.mediaplayer.ui.VideoUI;
import com.mark.media.mediaplayer.utils.CacheUtils;
import com.mark.media.mediaplayer.utils.FormatTimes;
import com.mark.media.mediaplayer.utils.ShowToast;
import com.mark.media.mediaplayer.widget.OnItemClickListener;
import com.youth.banner.loader.ImageLoader;
import com.mark.media.mediaplayer.utils.DataContants;
import com.mark.media.mediaplayer.widget.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mark.media.mediaplayer.bean.FilmBean;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MediaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOG_TAG = MediaActivity.class.getSimpleName();

    // 延时时间
    private static final long DELAY = 2000;

    public final static int ALBUM_REQUEST_CODE = 1000;
    public final static int CAMERA_REQUEST_CODE = 1001;

    private static final int UPDATEUI = 1 << 0;
    public static String SAVED_IMAGE_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/MediaPlayer/camera/";// 拍照路径
    String cameraPath;

    // 广告图片
    String img1 = "http://a1.att.hudong.com/30/32/19300001024098134992322076458.jpg";
    String img2 = "http://twimg.edgesuite.net/images/ReNews/20150819/640_a48504cd68fa5fc7519dadfa9c886d42.jpg";
    String img3 = "http://gb.cri.cn/mmsource/images/2014/12/18/18/14818525325832551462.jpg";
    String img4 = "http://nicepic2u.com/wp-content/uploads/2015/01/wonder-girls-1-510x300.jpg";

    // 控件
    private Toolbar mToolbar;
    private RoundedImageView mRoundedImg;
    private RotateAnimation mRotateAnimation;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;

    // 数据
    List<String> imagesUrl = new ArrayList<>();
    private List<FilmBean.DataBean.MoviesBean> mMoviesData = new ArrayList<>();

    private FilmBean mFilmBean;
    private FilmAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        // String channel = ManifestUtil.getMetaDataFromAppication(this, "UMENG_CHANNEL");

        setupToolbar();

        setupBanner();

        setupCollapsingToolbar();

        setupDrawer();

        setupFloatingActionButton();

        setupNavigationView();

        setupContent();

    }

    /**
     * OKHttp加载网络数据
     */
    private void loadData() {
        // 三级缓存
        // 1.获取缓存
        String cache = CacheUtils.getCache(DataContants.MAOYAN_API, getApplication());

        // 2.缓存存在
        if (!TextUtils.isEmpty(cache)) {
            parseJsonData(cache);
        }

        // 3.缓存不存在，网络获取
        getNetData();
    }

    /**
     * 获取网络数据
     */
    private void getNetData() {
        // 创建一个requestqueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // 创建一个StringRequest
        StringRequest stringRequest = new StringRequest(DataContants.MAOYAN_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                // 请求成功
                System.out.println("Result:" + result);

                // 设置缓存
                CacheUtils.setCache(DataContants.MAOYAN_API, result, getApplication());

                // 业务逻辑
                parseJsonData(result);

                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ShowToast.show(MediaActivity.this, "网络请求失败" + volleyError.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    /**
     * 解析json数据
     *
     * @param jsonStr
     */
    private void parseJsonData(String jsonStr) {

        Gson gson = new Gson();
        mFilmBean = gson.fromJson(jsonStr, FilmBean.class);

        mMoviesData = mFilmBean.data.movies;

        // 設置數據
        mAdapter = new FilmAdapter(MediaActivity.this, mMoviesData);
        mRecyclerView.setAdapter(mAdapter);

        // 点击事件
        mAdapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String filmUrl = "http://www.iqiyi.com/dianying/";
                Intent intent_film = new Intent(MediaActivity.this, FilmUI.class);
                intent_film.putExtra(DataContants.FILMURL, filmUrl);
                startActivity(intent_film);
            }
        });
    }

    /**
     * 設置具體内容
     */
    private void setupContent() {
        // 設置圓形圖片
        mRoundedImg = (RoundedImageView) findViewById(R.id.riv_head_img);

        // 設置頭像動畫
        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setDuration(3000);
        mRotateAnimation.setRepeatCount(Integer.MAX_VALUE);
        mRoundedImg.startAnimation(mRotateAnimation);

        // 點擊，網絡加載圖片
        mRoundedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImg();
            }
        });

        // 设置RecyclerView
        setupRecyclerView();
        loadData();
    }

    /**
     * 加载图片
     */
    private void loadImg() {
        Picasso.with(MediaActivity.this).load(DataContants.IMGURL[new Random().nextInt(DataContants.IMGURL.length)])
                .placeholder(R.mipmap.girl)
                .error(R.mipmap.error).
                fit().priority(Picasso.Priority.HIGH)
                .into(mRoundedImg);
        ShowToast.show(MediaActivity.this, "点我啊...");
    }

    /**
     * 设置猫眼电影查询
     */
    private void setupRecyclerView() {
        // 1.初始化控件
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.media_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.media_recyclerview);

        // 2.设置刷新的实现
        mRefresh.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        mRefresh.setOnRefreshListener(this);

        // 3.设置RecyclerView
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        //把GridLayoutManager设置给RecyclerView
        mRecyclerView.setLayoutManager(manager);

    }

    /**
     * 设置NavigationView
     */
    private void setupNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 设置FloatingActionButton
     */
    private void setupFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "先看看有哪些最新科技吧!", Snackbar.LENGTH_LONG)
                        .setAction("点击阅读", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MediaActivity.this, NewsUI.class);
                                startActivity(intent);
                            }
                        })
                        .setActionTextColor(Color.BLUE).show();
            }
        });
    }

    /**
     * 设置抽屉
     */
    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * 设置图片与ToolBar的效果
     */
    private void setupCollapsingToolbar() {
        CollapsingToolbarLayout collapsingToolBar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);

        collapsingToolBar.setTitleEnabled(false);
    }

    /**
     * 设置广告轮播
     */
    private void setupBanner() {
        // 添加图片
        imagesUrl.add(img1);
        imagesUrl.add(img2);
        imagesUrl.add(img3);
        imagesUrl.add(img4);

        Banner banner = (Banner) findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(imagesUrl);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        // banner.setBannerTitles(Arrays.asList(titles));
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1.加载图片
                mRefresh.setRefreshing(false);
                loadImg();

                // 2.内容刷新
                mAdapter.notifyDataSetChanged();

            }
        }, DELAY);
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Picasso 加载图片简单用法
            Picasso.with(context).load((String) path).fit().priority(Picasso.Priority.HIGH).into(imageView);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，方便fresco自定义ImageView
        @Override
        public ImageView createImageView(Context context) {
            return super.createImageView(context);
        }
    }

    /**
     * 设置Toolbar
     */
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MediaPlayer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.media, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent(this, MusicService.class);

        //noinspection SimplifiableIfStatement
        if (id == R.id.start_music) {// 播放音乐
            startService(intent);
        } else if (id == R.id.stop_music) {// 停止音乐
            stopService(intent);
        } else if (id == R.id.play_vedio) {// 播放视频
            Intent intent_play_video = new Intent(MediaActivity.this, VideoUI.class);
            startActivity(intent_play_video);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            // 指定相机拍摄照片保存地址
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/mnt/sdcard/DCIM/Album/" + FormatTimes.getNowDate(System.currentTimeMillis()) + ".png")));
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }

            // 系统扫描图片
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File("/mnt/sdcard/DCIM/Album/" + FormatTimes.getNowDate(System.currentTimeMillis()) + ".png")));
            sendBroadcast(intent);

        } else if (id == R.id.nav_gallery) {
            // 查看系统图片
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, ALBUM_REQUEST_CODE);

        } else if (id == R.id.nav_slideshow) {
            // 打开视频
            Intent intent_play_video = new Intent(MediaActivity.this, VideoUI.class);
            startActivity(intent_play_video);
        } else if (id == R.id.nav_manage) {
            // 打开系统设置界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            // 一键分享
            showShare();

        } else if (id == R.id.nav_send) {
            // 邮件
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            //设置文本格式
            emailIntent.setType("text/plain");
            //设置对方邮件地址
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
            //设置标题内容
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "2524066607@qq.com");
            //设置邮件文本内容
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "2524066607@qq.com");
            startActivity(Intent.createChooser(emailIntent, "Choose Email Client"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Log.d(LOG_TAG, "path=" + cameraPath);
            }

            if (requestCode == ALBUM_REQUEST_CODE) {
                try {
                    Uri uri = data.getData();
                    final String absolutePath = getAbsolutePath(MediaActivity.this, uri);
                    Log.d(LOG_TAG, "path=" + absolutePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getAbsolutePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 一鍵分享
     */
    private void showShare() {
        //ShareSDK初始化
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我爱京东");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("我爱京东");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止shareSDK
        ShareSDK.stopSDK(this);
    }

    /**
     * 点击屏幕停止动画,松开开始动画
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                mRoundedImg.clearAnimation();
                break;

            case MotionEvent.ACTION_UP:// 松开
                mRoundedImg.startAnimation(mRotateAnimation);
                break;
        }

        return super.onTouchEvent(event);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ShowToast.show(MediaActivity.this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
