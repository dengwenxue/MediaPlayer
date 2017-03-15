package com.mark.media.mediaplayer.bean;

import java.util.List;

/**
 * 封装了科技新闻的javabean
 * Created by Mark on 2016/6/27.
 */
public class NewsBean {

    public int error_code;
    public String reason;

    public List<ResultBean> result;

    public class ResultBean {
        public String ctime;
        public String title;
        public String description;
        public String picUrl;
        public String url;
    }
}
