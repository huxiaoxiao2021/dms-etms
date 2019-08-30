package com.jd.bluedragon.utils;

import net.sf.json.util.JSONStringer;

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

    public static int integerToInt(Integer value){
        if(value == null){
            return 0;
        }
        return value.intValue();
    }

}
