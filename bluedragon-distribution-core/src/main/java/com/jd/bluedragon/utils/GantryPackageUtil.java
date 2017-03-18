package com.jd.bluedragon.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhanglei51 on 2017/3/13.
 */
public class GantryPackageUtil {
    /**
     * 为了计算龙门架的流速 把一小时分为12个区域 每五分钟一个区域计算一次
     * 然后将区域数据写入到redis中，设置失效日期为4小时
     * 区域key例子：201703130101  表示时间区域为2017-03-13 01:00:00 —— 2017-03-13 01:05:00
     * */
    public static String getDateRegion(String gantryNumber,Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            //将日期转换为连续数字格式 如201703131648
            String coventDateStr = simpleDateFormat.format(date);

            //取出当前分钟数  48
            int curMinute = Integer.parseInt(coventDateStr.substring(coventDateStr.length()-2,coventDateStr.length()));

            String prefix = gantryNumber+coventDateStr.substring(0,coventDateStr.length()-2);

            //计算当前分钟数所属的区间
            int region = curMinute/5+1;

            //一位补0
            if(region < 10){
                return prefix+"0"+region;
            }else{
                return prefix+region;
            }

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取一天中的小时数
     * */
    public static Integer getDateHour(Date date){
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            //将日期转换为连续数字格式 如16
            return c.get(Calendar.HOUR_OF_DAY);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前日期 分钟对应12个区域中的id 返回1-12
     * */
    public static Integer getDateMinuteRegion(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            //将日期转换为连续数字格式 如201703131648
            String coventDateStr = simpleDateFormat.format(date);

            //取出当前分钟数  48
            int curMinute = Integer.parseInt(coventDateStr.substring(coventDateStr.length() - 2, coventDateStr.length()));
            //计算当前分钟数所属的区间
            int region = curMinute / 5 + 1;

            return region;
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取前缀 龙门架序列号+日期
     * */
    public static String getDatePrefix(String gantryNumber,Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return gantryNumber+simpleDateFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 修正长度  一位前面补0
     * */
    public static String fixIntLenth(int num){
        if(num >=0 && num < 10){
            return "0"+num;
        }else if(num >=10 && num < 100){
            return ""+num;
        }else{
            return "00";
        }
    }
}
