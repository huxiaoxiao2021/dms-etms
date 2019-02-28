package com.jd.bluedragon.utils;

/**
 * Created by yangbo7 on 2016/7/7.
 */
public class IntegerHelper {

    public static boolean compare(Integer a, Integer b) {
        if (a == null || b == null) {
            return false;
        }
        if (a == b) {
            return true;
        }
        if (a.intValue() == b.intValue()) {
            return true;
        }
        return false;
    }

}
