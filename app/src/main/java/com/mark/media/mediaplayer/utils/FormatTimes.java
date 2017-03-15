package com.mark.media.mediaplayer.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换
 * Created by Mark on 2016/6/28.
 */
public class FormatTimes {
    /************
     * 当前日期转换为标准时间格式
     ******************
     * @param ctime*/
    public static String getNowDate(String ctime) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);

        // 时区转换
//        ParsePosition pos = new ParsePosition(8);
//        Date currentDate = formatter.parse(dateString, pos);

        return dateString;
    }

    /*************
     * 毫秒转换为"yyyy-MM-dd HH:mm:ss"格式的时间
     **********/
    public static String getNowDate(long currentTime) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);

        // 时区转换
        ParsePosition pos = new ParsePosition(8);
        Date currentDate = formatter.parse(dateString, pos);

        return dateString;
    }
}
