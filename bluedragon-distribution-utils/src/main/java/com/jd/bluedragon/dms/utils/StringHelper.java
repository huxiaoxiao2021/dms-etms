package com.jd.bluedragon.dms.utils;

public class StringHelper {


    public static boolean isNotEmpty(String s) {
        return !StringHelper.isEmpty(s);
    }

    public static Boolean isEmpty(String s) {
        if (s == null || s.trim().length() == 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
