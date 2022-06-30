package com.jd.bluedragon.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    /**
     * 时间格式：yyyyMMddHHmmss'.
     */
    public static String DEFAULT_PATTERN = "yyyyMMddHHmmss";

    /**
     * 时间格式：yyyyMMdd'.
     */
    public static String yyyyMMdd = "yyyyMMdd";

    /**
     * 时间格式：yyyy-MM-dd'.
     */
    public static String yyyy_MM_dd = "yyyy-MM-dd";

    /**
     * 时间格式：HH:mm:ss'.
     */
    public static String HH_mm_ss = "HH:mm:ss";

    /**
     * 时间格式：yyyy-MM-dd' 'HH:mm:ss'.
     */
    public static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd' 'HH:mm:ss";

    /**
     * 时间格式：yyyy/MM/dd' 'HH:mm:ss'.
     */
    public static String yyyy_M_d_HH_mm_ss = "yyyy/MM/dd HH:mm:ss";

    /**
     * 时间格式：yyyy/MM/dd' 'HH:mm:ss'.
     */
    public static String yyyy_M_d_HH_mm = "yyyy/MM/dd HH:mm";
    public static String yyyy_m_d_HH_mm = "yyyy/M/d HH:mm";
    public static String yyyy_mm_d_HH_mm = "yyyy/MM/d HH:mm";
    public static String yyyy_m_dd_HH_mm = "yyyy/M/dd HH:mm";

    public static String HH_mm = "HH:mm";


    /**
     * 时间格式：yyyy-MM-dd' 'HH:mm'.
     */
    public static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd' 'HH:mm";

    /**
     * 获取当前时间(格式:yyyyMMddHHmmss)
     *
     * @return
     */
    public static String getCurrDate() {
        return getCurrDate(DEFAULT_PATTERN);
    }


    public static String getCurrTime() {
        return getCurrDate(HH_mm);
    }

    public static String dayForWeek(String day) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date tmpDate = null;
        try {
            tmpDate = format.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();

        String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};

        try {

            cal.setTime(tmpDate);

        } catch (Exception e) {

            e.printStackTrace();

        }

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。

        if (w < 0)

            w = 0;

        return weekDays[w];

    }

    public static int getDifferBetweenTimes(String t1, String t2) {
        SimpleDateFormat df = new SimpleDateFormat(HH_mm);

        Date sd1 = null;
        Date sd2 = null;
        try {
            sd1 = df.parse(t1);
            sd2 = df.parse(t2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long long1 = sd1.getTime();
        long long2 = sd2.getTime();
        int min = (int) (long2 - long1) / 1000 / 60;
        return min;
    }

    /**
     * 字符串转日期，默认格式为yyyyMMddHHmmss
     *
     * @param str
     * @return
     */
    public static Date strToDate(String str) {
        return strToDate(str, DEFAULT_PATTERN);
    }

    /**
     * 日期格式化，默认格式为yyyyMMddHHmmss
     *
     * @param date 日期
     * @return
     */
    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    /**
     * 字符串转日期
     *
     * @param str     时间
     * @param pattern 格式
     * @return
     */
    public static Date strToDate(String str, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期格式化
     *
     * @param date    日期
     * @param pattern 格式
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        return sdf.format(date);
    }

    /**
     * 获取当前时间
     *
     * @param pattern 格式
     * @return
     */
    public static String getCurrDate(String pattern) {
        long d = System.currentTimeMillis();
        Date date = new Date(d);

        return format(date, pattern);
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public static Date getSystemDate() {
        long d = System.currentTimeMillis();
        Date date = new Date(d);

        return date;
    }

    /**
     * 格式化时间
     *
     * @param strTime
     * @param pattern
     * @return
     */
    public static String getTime(String strTime, String pattern) {
        SimpleDateFormat s = new SimpleDateFormat(pattern);
        try {
            return String.valueOf(s.parse(strTime).getTime());// 时间戳
        } catch (ParseException e) {
            e.printStackTrace();
            return String.valueOf(System.currentTimeMillis());
        }
    }

    /**
     * 格式化日期格式
     *
     * @param date
     * @return
     */
    public static final String date2string(Date date, String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        return sdf.format(date);
    }

    /**
     * 获取本地日期时间.
     *
     * @return 本地日期时间yyyyMMddHHmmss
     */
    public static String getLocalFullDateTime14() {
        return date2string(new Date(), DEFAULT_PATTERN);
    }


    /**
     * 返回指定格式时间
     *
     * @param mask
     * @return
     */
    public static final String now2string(String mask) {
        return date2string(new Date(), mask);
    }

    /**
     * @param date
     * @param field
     * @param amount
     * @return 对指定的日期做加减运算；<br>
     * 减：add(new Date(), Calendar.DATE, -1)返回昨天的日期<br>
     * 加：add(new Date(), Calendar.YEAR, 1)返回一年后的今天
     */
    public static Date add(Date date, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, amount);
        return c.getTime();
    }


    /**
     * 返回需要格式的字符串时间
     *
     * @param str      字符串时间
     * @param patternS 字符串格式
     * @param patternE 格式化后的格式
     * @return
     */
    public static String strToStr(String str, String patternS, String patternE) {

        return format(strToDate(str, patternS), patternE);

    }
}
