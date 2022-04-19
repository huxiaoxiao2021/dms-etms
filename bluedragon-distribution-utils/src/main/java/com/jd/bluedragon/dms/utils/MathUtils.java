package com.jd.bluedragon.dms.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        BigDecimal b = BigDecimal.valueOf((float) source);
        return b.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    private static final Pattern PATTERN_BIGDECIMAL = Pattern.compile("^((\\+|-)?\\d+)(\\.\\d*)?");
    public static long objToZeroLong(Object value) {
        return (value == null || "".equals(value)) ? 0 : Long.parseLong(value.toString());
    }

    public static int objToInt(Object value) {
        if(value == null || "".equals(value)){
            return 0;
        }
        if(value instanceof String){
            return Integer.parseInt((String)value);
        }
        return Integer.parseInt(value.toString());
    }

    public static double objToDoubleOrZero(Object value) {
        if(value == null || "".equals(value)){
            return 0;
        }
        if(value instanceof Double){
            return (Double)value;
        }
        return Double.valueOf((String)value);
    }

    public static float objToFloatOrZero(Object value) {
        if(value == null || "".equals(value)){
            return 0;
        }
        if(value instanceof Float){
            return (Float)value;
        }
        return Float.parseFloat(value.toString());
    }

    public static Double objToDoubleOrNull(Object value) {
        if(value == null || "".equals(value)){
            return null;
        }
        if(value instanceof Double){
            return (Double)value;
        }
        if(!isBigDecimal((String)value)){
            return null;
        }
        return Double.valueOf((String)value);
    }

    public static Double strToDouble(String value) {
        if(StringUtils.isEmpty(value)){
            return null;
        }
        if(!isBigDecimal(value)){
            return null;
        }
        return Double.parseDouble(value);
    }

    /**
     * 去掉小数部分取整，也就是正数取左边，负数取右边，相当于向原点靠近的方向取整\
     * 1.1->1   1.5->1   1.8->1   -1.1->-1   -1.5->-1   -1.8>-1
     * @param dividend 被除数
     * @param molecule 分子
     * @return 结果
     */
    public static Double divideDown(Double dividend, Integer molecule) {
        if(dividend == null || molecule == null){
            return null;
        }

        BigDecimal bigDividend = new BigDecimal(dividend);
        BigDecimal bigMolecule = new BigDecimal(molecule);
        return bigDividend.divide(bigMolecule,2, RoundingMode.DOWN).doubleValue();
    }

    /**
     * 判断字符串是否为数值类型,包含整数和浮点数
     * eg:+1,+1.,-1,-1.,+1.0,-1.0,+1,0.0,001,009,001.0
     * @param numberStr 字符串
     * @return true 是数字，false不是
     */
    public static boolean isBigDecimal(String numberStr) {
        if(null==numberStr){
            return false;
        }
        Matcher matcher = PATTERN_BIGDECIMAL.matcher(numberStr);
        return matcher.matches();
    }
}
