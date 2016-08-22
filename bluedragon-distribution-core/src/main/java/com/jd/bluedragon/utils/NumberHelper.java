package com.jd.bluedragon.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberHelper {

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

    public static boolean isStringNumber(String number) {
        if(null==number){
            return false;
        }
        Pattern pattern1 = Pattern.compile("[1-9]\\d*\\.?\\d+");
        Matcher matcher1 = pattern1.matcher(number);
        if (matcher1.matches()) {
            return true;
        }
        return false;
    }

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
}
