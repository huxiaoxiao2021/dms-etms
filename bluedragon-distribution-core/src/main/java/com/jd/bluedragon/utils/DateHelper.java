package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static final String[] DATE_TIME_FORMAT = new String[]{
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyyMMddHHmmss",
            "yyyyMMddHHmmssSSS"
    };

    private static final Log LOGGER = LogFactory.getLog(DateHelper.class);

    /**
     * 一分钟的毫秒数
     */
    public static final long ONE_MINUTES_MILLI = 60 * 1000;

    /**
     * 十分钟的毫秒数
     */
    public static final long TEN_MINUTES = 10 * 60 * 1000;

    /**
     * 一小时的毫秒数
     */
    public static final long ONE_HOUR_MILLI = 60 * 60 * 1000;

    /**
     * 一天的毫秒数
     */
    public static final long ONE_DAY = 24 * 60 * 60 * 1000;

    public static final String DATE_FORMAT_YYYYMMDDHHmmssSSS = "yyyyMMddHHmmssSSS";

    public static final String DATE_FORMAT_YYYYMMDDHHmmssSS = "yyyyMMddHHmmssSS";

    public static final String DATE_FORMAT_YYYYMMDDHHmmss = "yyyyMMddHHmmss";

    public static final String DATE_FORMAT_YYYYMMDDHHmmss2 = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";

    public static final String DATE_FORMAT_HHmmss = "HH:mm:ss";
    /**
     * 日期-格式yyyy-MM-dd HH:mm
     */
    public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";


    public static Date add(final Date date, Integer field, Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(field, amount);

        return calendar.getTime();
    }

    public static Date addDate(final Date date, Integer days) {
        return DateHelper.add(date, Calendar.DATE, days);
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);

        return sdf.format(date);
    }

    /**
     * 将日期转换成为字符（yyyy-MM-dd）
     */
    public static String formatDate(Date date, String formatString) {
        if (date == null) {
            return "";
        }

        String format = formatString;
        if (StringHelper.isEmpty(formatString)) {
            format = Constants.DATE_FORMAT;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);
    }

    /**
     * 将日期转换成为字符（yyyy-MM-dd HH:mm:ss）
     *
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

        return sdf.format(date);
    }

    /**
     * 校正时间截
     *
     * @param source 原时间截
     * @return JAVA时间截
     */
    public static long adjustTimestampToJava(long source) {
        long target = source;
        int timeLength = String.valueOf(System.currentTimeMillis()).length();
        int clientTimeLength = String.valueOf(source).length();
        if (clientTimeLength > timeLength) {
            target = (source / (long) Math.pow(10, clientTimeLength - timeLength));
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));
            }
        } else if (timeLength > clientTimeLength) {
            target = (source * (long) Math.pow(10, timeLength - clientTimeLength));
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));
            }
        }
        return target;
    }

    public static void main(String[] args) {

        Date date = DateHelper.parseDate("2019-04-28 02:38:01", Constants.DATE_TIME_MS_FORMAT,Constants.DATE_TIME_FORMAT);
        System.out.println(date);

        long source = 14738233921256L;
        long target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));

        source = 14736094286801256L;
        target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));

        source = 14736094286801L;
        target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));

        source = 14736097046982760L;
        target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));

        source = 14736094604249413L;
        target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));

        source = 1473824034L;
        target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));

        source = 1473824030L;
        target = adjustTimestampToJava(source);
        System.out.println(MessageFormat.format("时间由{0}校正为{1},时间截由{2}校正为{3}", DateHelper.formatDateTimeMs(new Date(source)), DateHelper.formatDateTimeMs(new Date(target)), source, target));
        System.out.println(daysBetween(new Date(1570104779000l),new Date()));

    }

    /**
     * 将日期转换成为字符（yyyy-MM-dd HH:mm:ss.SSS）
     *
     * @param date
     * @return
     */
    public static String formatDateTimeMs(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_MS_FORMAT);

        return sdf.format(date);
    }

    public static Date getDateValue(Object object) {
        return ObjectHelper.isNotEmpty(object) ? (Date) object : null;
    }

    /**
     * 字符转换为日期（yyyy-MM-dd）
     *
     * @param dateString
     * @return
     */
    public static Date parseDate(String dateString) {
        return DateHelper.parseDate(dateString, Constants.DATE_FORMAT);
    }

    public static Date parseDate(String dateString, String format) {
        if (dateString == null || format == null) {
            return null;
        }

        try {
            return new SimpleDateFormat(format).parse(dateString);
        } catch (Exception e) {
            LOGGER.error("该日期格式无法解析，the date: " + dateString + "，the format: " + format, e);
            return null;
        }
    }
    public static Date parseDate(String dateString, String... formats) {
        if (dateString == null || formats == null) {
            return null;
        }
        for(String format : formats){
            try {
                Date parseDate = new SimpleDateFormat(format).parse(dateString);
                if(parseDate!=null){
                    return parseDate;
                }
            } catch (Exception e) {
                LOGGER.warn("该日期格式无法解析，the date: " + dateString + "，the format: " + format, e);
            }
        }
        return null;
    }

    /**
     * 字符转换为日期（yyyy-MM-dd HH:mm:ss）
     *
     * @param dateString
     * @return
     */
    public static Date parseDateTime(String dateString) {
        return DateHelper.parseDate(dateString, Constants.DATE_TIME_FORMAT);
    }

    /**
     * 匹配DATE_TIME_FORMAT数组中所有格式的日期
     *
     * @param dateString
     * @return
     */
    public static Date parseAllFormatDateTime(String dateString) {
        try {
            return DateUtils.parseDate(dateString, DateHelper.DATE_TIME_FORMAT);
        } catch (ParseException e) {
            throw new IllegalArgumentException("输入参数的日期格式无法解析，the date: " + dateString, e);
        }
    }

    public static Date toDate(Long date) {
        if (date == null) {
            return null;
        }
        return new Date(date);
    }

    public static Date getSeverTime(String sPdaTime) {
        Date serverTime = new Date();
        if (StringHelper.isEmpty(sPdaTime)) {
            return serverTime;
        }
        Date pdaTime = null;
        try {
            pdaTime = DateHelper.parseAllFormatDateTime(sPdaTime);
        } catch (IllegalArgumentException e) {
            LOGGER.error("解析PDA时间失败：" + sPdaTime, e);
        } finally {
            if (pdaTime != null) {
                Long interval = pdaTime.getTime() - serverTime.getTime();
                if (pdaTime.after(serverTime) && DateHelper.TEN_MINUTES < interval) {
                    pdaTime = serverTime;
                }
            }else{
                pdaTime = serverTime;
            }
        }
        return pdaTime;
    }

    /**
     * 比较2个时间
     * <p>1、同时为空返回0
     * <p>2、都不为空，返回o1.compareTo(o2)比较结果
     * <p>3、o1为空o2不为空，返回-1
     * <p>4、o2为空哦o1不为空，返回1
     *
     * @param date
     * @param date1
     * @return
     */
    public static int compare(Date date, Date date1) {
        return ObjectHelper.compare(date, date1);
    }

    /**
     * 比较参数时间 和 当前时间按days参数调整的后的时间
     *
     * @param date 比较时间
     * @param days 调整天数（正数往前调整、负数往后调整）
     * @return
     */
    public static int compareAdjustDate(final Date date, Integer days) {

        Date adjustDate = DateHelper.addDate(new Date(), days);

        return DateHelper.compare(date, adjustDate);
    }

    /**
     * 判断当前时间是否在基准时间前后rangeHours小时的范围内
     *
     * @param standDate
     * @param rangeHours
     * @return
     */
    public static boolean currentTimeIsRangeHours(final Date standDate, Integer rangeHours) {
        if (standDate == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(standDate);
        calendar.add(Calendar.HOUR_OF_DAY, rangeHours);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -2 * rangeHours);
        long startTime = calendar.getTimeInMillis();

        long currentTime = System.currentTimeMillis();

        return startTime <= currentTime && currentTime <= endTime;
    }

    /**
     * 计算两个日期相差的天数
     * @param start
     * @param end
     * @return
     */
    public static int daysBetween(Date start, Date end) {
        if(start == null || end == null){
            return 0;
        }
        java.util.Calendar calst = java.util.Calendar.getInstance();
        java.util.Calendar caled = java.util.Calendar.getInstance();
        calst.setTime(start);
        caled.setTime(end);
        //设置时间为0时
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calst.set(java.util.Calendar.MINUTE, 0);
        calst.set(java.util.Calendar.SECOND, 0);
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);
        caled.set(java.util.Calendar.MINUTE, 0);
        caled.set(java.util.Calendar.SECOND, 0);
        //得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
                .getTime().getTime() / 1000)) / 3600 / 24;

        return days;
    }
}
