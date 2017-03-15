package com.mark.media.mediaplayer.bean;

import java.util.List;

/**
 * 电影的javaBean
 * Created by Mark on 2016/11/19 0003.
 */

public class FilmBean {

    public ControlBean control;
    public int status;
    public DataBean data;

    public class ControlBean {
        private int expires;
    }

    public class DataBean {

        public boolean hasNext;

        public List<MoviesBean> movies;

        public class MoviesBean {

            public boolean late;
            public int cnms;
            public int sn;
            public String showInfo;
            public String img;
            public double sc;
            public String ver;
            public String rt;
            public int dur;
            public int pn;
            public int preSale;
            public String vd;
            public String dir;
            public String star;
            public String cat;
            public int wish;
            public boolean is3d;
            public String src;
            public String scm;
            public boolean imax;
            public int snum;
            public String showDate;
            public String nm;
            public String time;
            public int id;
        }

    }
}
