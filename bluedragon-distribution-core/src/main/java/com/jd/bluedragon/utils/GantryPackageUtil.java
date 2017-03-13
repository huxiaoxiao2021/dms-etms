package com.jd.bluedragon.utils;

import java.text.SimpleDateFormat;
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
    public static String getDateRegion(Date date){
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            //将日期转换为连续数字格式 如201703131648
            String coventDateStr = simpleDateFormat1.format(date);

            //取出当前分钟数  48
            int curMinute = Integer.parseInt(coventDateStr.substring(coventDateStr.length()-2,coventDateStr.length()));

            String prefix = coventDateStr.substring(0,coventDateStr.length()-2);

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
}
