package com.jd.bluedragon.utils;

import com.jd.common.util.StringUtils;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberHelper {
	private static final Logger log = LoggerFactory.getLogger(NumberHelper.class);
	
    public static DecimalFormat doubleFormat = new DecimalFormat("#.00");    //保留两位小数
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("0.00");   //保留两位小数
    /**
     * cm3和m3的转换值
     */
    public static final long CM3_M3_MAGNIFICATION = 1000000;

    /**
     * 立方厘米转换成立方米 小数点后保留2位
     * @param cm3
     * @return
     */
    public static Double cm3ToM3(Double cm3){
        if(cm3 == null){
            return 0.00;
        }
        Double m3 = cm3 / CM3_M3_MAGNIFICATION;
        return doubleFormat(m3);
    }

    public static Double getDoubleValue(Object object) {
        return ObjectHelper.isNotEmpty(object) ? Double.valueOf(object.toString()) : 0.0D;
    }

    public static Long getLongValue(Object object) {
        return ObjectHelper.isNotEmpty(object) ? Long.valueOf(object.toString()) : 0L;
    }

    public static Integer getIntegerValue(Object object) {
        return ObjectHelper.isNotEmpty(object) ? Integer.valueOf(object.toString()) : 0;
    }

    public static boolean isPositiveNumber(Integer number) {
        if (ObjectHelper.isEmpty(number)) {
            return false;
        }

        return NumberHelper.isPositiveNumber(Long.valueOf(number));
    }
    /**
     * 判断字符串是否为数值类型,包含整数和浮点数
     * eg:+1,+1.,-1,-1.,+1.0,-1.0,+1,0.0,001,009,001.0
     * @param numberStr
     * @return
     */
    public static boolean isBigDecimal(String numberStr) {
        if(null==numberStr){
            return false;
        }
        Pattern pattern = Pattern.compile("^((\\+|-)?\\d+)(\\.\\d*)?");
        Matcher matcher = pattern.matcher(numberStr);
        return matcher.matches();
    }

    /**
     * 如果是null 返回0，如果非数字 返回null
     * @param value
     * @return
     */
    public static BigDecimal parseBigDecimalNullToZero(String value) {
        if(null == value){
            return BigDecimal.ZERO;
        }
        if(NumberUtils.isNumber(value)){
            new BigDecimal(value);
        }
        return null;
    }

    /**
     * 判断是否正整数
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        if(null==number){
            return false;
        }
        Pattern pattern1 = Pattern.compile("[1-9]\\d*");
        Matcher matcher1 = pattern1.matcher(number);
        if (matcher1.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isPositiveNumber(Long number) {
        if (number != null && number > 0) {
            return true;
        }

        return false;
    }

    public static boolean isNumberUpZero(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        if (Integer.parseInt(str) > 0) {
            return true;
        }
        return false;
    }
    /**
     * 返回结果a是否大于b,返回 a>b 的值
     * @param a
     * @param b
     * @return
     */
    public static boolean gt(Number a,Number b) {
        if (a != null && b != null) {
            return a.doubleValue() > b.doubleValue();
        }
        return false;
    }
    /**
     * 返回结果a是否大于等于b,返回 a>=b 的值
     * @param a
     * @param b
     * @return
     */
    public static boolean gte(Number a,Number b) {
        if (a != null && b != null) {
            return a.doubleValue() >= b.doubleValue();
        }
        return false;
    }
    /**
     * 返回结果a是否大于0
     * @param a 数字类型的对象，支持 Byte,Double,Float,Integer,Long,Short
     * @return
     */
    public static boolean gt0(Number a) {
        if (a != null) {
            return a.doubleValue() > 0;
        }
        return false;
    }
    /**
     * 返回结果：字符串是否大于0的数值
     * @param numStr-数字型的字符串
     * @return
     */
    public static boolean gt0(String numStr) {
        if (isBigDecimal(numStr)) {
        	BigDecimal decimal = new BigDecimal(numStr);
            return decimal.compareTo(BigDecimal.ZERO)==1;
        }
        return false;
    }
    /**
     * 返回结果a是否小于b,返回 a<b 的值
     * @param a
     * @param b
     * @return
     */
    public static boolean lt(Number a,Number b) {
    	if (a != null && b != null) {
            return a.doubleValue() < b.doubleValue();
        }
        return false;
    }
    /**
     * 返回结果a是否小于等于b,返回 a<=b 的值
     * @param a
     * @param b
     * @return
     */
    public static boolean lte(Number a,Number b) {
    	if (a != null && b != null) {
            return a.doubleValue() <= b.doubleValue();
        }
        return false;
    }

    /**
     * 返回格式化后的double，四舍五入，保留小数点后两位。
     *
     * @param num
     * @return
     */
    public static Double doubleFormat(Double num) {
    	if (num != null) {
            return Double.valueOf(doubleFormat.format(num));
        }
        return null;
    }
    /**
     * 将字符串转换为Integer类型，传入参数为空/转换异常返回null
     * @param str
     * @return
     */
    public static Integer convertToInteger(String str) {
        return convertToInteger(str,null);
    }
    /**
     * 将字符串转换为Integer类型，传入参数为空/转换异常返回指定的值：defaultVal
     * @param str
     * @param defaultVal 默认返回值
     * @return
     */
    public static Integer convertToInteger(String str,Integer defaultVal) {
    	if (str != null && str.length() > 0 && StringUtils.isNumeric(str)) {
            try {
				return Integer.valueOf(str);
			} catch (NumberFormatException e) {
				log.warn("fail to convertToInteger! input:{}", str);
			}
        }
        return defaultVal;
    }
    /**
     * 返回格式化后的金额（保留2位小数）
     * @param val
     * @return
     */
	public static String formatMoney(Double val) {
		if(val != null){
			return MONEY_FORMAT.format(val);
		}
		return null;
	}
    /**
     * 返回格式化后的金额（保留2位小数）
     * @param doubleStr
     * @return
     */
	public static String formatMoney(String doubleStr) {
		if(doubleStr != null && isBigDecimal(doubleStr)){
			return MONEY_FORMAT.format(new BigDecimal(doubleStr));
		}
		return null;
	}
    /**
     * 返回格式化后的金额（保留2位小数）
     * @param doubleStr
     * @return
     */
	public static String formatMoney(BigDecimal val) {
		if(val != null){
			return MONEY_FORMAT.format(val);
		}
		return null;
	}

    /**
     * 获取数字的最高位
     * @param num 数字
     * @return
     */
    public static String getFirstDigit(String num) {
        if (StringHelper.isEmpty(num) || !NumberHelper.isNumberUpZero(num)) {
            return "0";
        }
        return num.substring(0,1);
    }

    /**
     * 取余保留位数
     * @return 返回余数
     */
    public static int remainderByNum(int num, int mod) {
        return num % mod;
    }

    /**
     * 输入项是正整数
     * @param input
     * @return
     */
    public static boolean isPositiveNumber(String input) {
        String pattern = "^[1-9]\\d*$";
        return input.matches(pattern);
    }

    public static void main(String[] args) {
        Double d1 = 7000000.0;
        System.out.println(cm3ToM3(d1));
        Double d2 = 77000000.0;
        System.out.println(cm3ToM3(d2));
        Double d3 = 7700000.0;
        System.out.println(cm3ToM3(d3));
        Double d4 = 770000.0;
        System.out.println(cm3ToM3(d4));
        Double d5 = 77000.0;
        System.out.println(cm3ToM3(d5));
        Double d6 = 7700.0;
        System.out.println(cm3ToM3(d6));
        Double d7 = 0.0;
        System.out.println(cm3ToM3(d7));
        Double d8 = null;
        System.out.println(cm3ToM3(d8));

    }
}
