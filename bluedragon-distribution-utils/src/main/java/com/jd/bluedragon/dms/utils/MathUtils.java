package com.jd.bluedragon.dms.utils;

import java.math.BigDecimal;

/**
 * 数学计算工具
 */
public class MathUtils {

    /***
     * 长宽高(cm) 转换成立方米
     * @param length
     * @param width
     * @param high
     * @param scale 保留几位小数
     * @return
     */
    public static Double mul(double length, double width, double high,int scale) {
        //length*width*high
        BigDecimal b1 = new BigDecimal(Double.toString(length));
        BigDecimal b2 = new BigDecimal(Double.toString(width));
        BigDecimal b3 = new BigDecimal(Double.toString(high));
        BigDecimal dividenum = new BigDecimal(Double.toString(1000000));
        return b1.multiply(b2).multiply(b3).divide(dividenum, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 保留多位小数
     *
     * @param source
     * @param scale 保留几位小数
     * @return
     */
    public static Double keepScale(double source, int scale) {
        BigDecimal b = BigDecimal.valueOf((float) source * 100);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
