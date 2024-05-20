package com.jd.common.limiter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by tianye13 on 2016/10/7.
 */
public class DateHelper {
    public static final String DefaultTime = "1998-06-18 00:00:00";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_DD = "yyyy-MM-dd";

    private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

    @SuppressWarnings("rawtypes")
    private static ThreadLocal threadLocal = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };

    @SuppressWarnings("rawtypes")
    private static ThreadLocal threadLocaldd = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_DD);
        }
    };


    public static DateFormat getDateFormat() {
        return (DateFormat) threadLocal.get();
    }


    public static DateFormat getDateFormatDD() {
        return (DateFormat) threadLocaldd.get();
    }

    /**
     * @param t
     * @return 格式化时间
     */
    public static Date formatDates(Date t) {

        try {
            String timeDate = getDateFormat().format(t);
            return DateHelper.getString2Date(timeDate);
        } catch (Exception e) {
            logger.error("fromatDates error:", e);
            return t;
        }

    }


    /**
     * @param t
     * @return 格式化时间为yyyy-mm-dd
     */
    public static Date formatDatesDD(Date t) {

        try {
            String timeDate = getDateFormatDD().format(t);
            return DateHelper.getString2DateDD(timeDate);
        } catch (Exception e) {
            logger.error("fromatDatesDD error:", e);
            return t;
        }

    }

    /**
     * 将字符串日期yyyy-MM-dd HH:mm:ss转换为java.util.Date
     *
     * @param strDate
     * @return
     */
    public final static Date getString2Date(String strDate) {
        Date date = null;
        if (strDate != null && !"".equals(strDate)) {
            try {
                date = getDateFormat().parse(strDate);
            } catch (ParseException e) {
                date = defaultTime();
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 将日期转换为yyyy-MM-dd 串
     *
     * @return
     */
    public final static String getDate2String(Date date) {
        String str = null;
        if (date != null) {
            str = getDateFormatDD().format(date);
        }
        return str;
    }

    /**
     * 将日期转换为yyyy-MM-dd HH:mm:ss 串
     *
     * @return
     */
    public final static String getDateDD2String(Date date) {
        String str = null;
        if (date != null) {
            str = getDateFormat().format(date);
        }
        return str;
    }

    /**
     * 将字符串日期yyyy-MM-dd HH转换为java.util.Date
     *
     * @param strDate
     * @return
     */
    public final static Date getString2DateDD(String strDate) {
        Date date = null;
        if (strDate != null && !"".equals(strDate)) {
            try {
                date = getDateFormatDD().parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 获取默认时间
     *
     * @return
     */
    public static Date defaultTime() {
        Date d = new Date();
        try {
            d = getDateFormat().parse(DefaultTime);
        } catch (Exception e) {
            logger.error("defaultTime error", e);
        }
        return d;
    }

    public static boolean isBeforeDefaultTime(Date timeSpan) {
        boolean flag = false;
        try {
            flag = timeSpan.before(defaultTime());
        } catch (Exception e) {
            logger.error("isBeforeDefaultTime error:", e);
        }
        return flag;
    }

    /**
     * 设置日志的时分秒
     *
     * @param date
     * @param h
     * @param m
     * @param s
     * @return
     */
    public static Date setDateHMS(Date date, int h, int m, int s) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, s);
        return c.getTime();
    }

    public static Date getBeginTimeOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    public static Date getAddDays(int days) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date getAddMonth(int month) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Date getAddHours(int hours) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public static boolean checkLocalTimeState(Date pdaTime) {
        try {
            if (pdaTime.after(getAddMonth(-1)) && pdaTime.before(getAddHours(1))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("checkLocalTimeState error", e);
            return false;
        }
    }

    /**
     * 增加天
     * @param time
     * @param day
     * @return
     */
    public static Date addDay(Date time, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DAY_OF_YEAR,day);
        return calendar.getTime();
    }

    public static Integer getHour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static Integer getMin(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static Integer getSecond(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    public static String dateToString(Date date) {
        return getDateFormat().format(date);
    }
}
