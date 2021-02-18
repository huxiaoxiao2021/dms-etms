package com.jd.bluedragon.utils;

import net.sf.json.util.JSONStringer;

/**
 * Created by yangbo7 on 2016/7/7.
 */
public class IntegerHelper {

    public static int integerToInt(Integer value){
        if(value == null){
            return 0;
        }
        return value.intValue();
    }

}
