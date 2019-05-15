package com.jd.bluedragon.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class LongHelper<E> {

    private static Logger logger = Logger.getLogger(LongHelper.class);

    public static Long strToLongOrNull(String value){
        if(StringUtils.isEmpty(value)){
            return null;
        }
        return Long.valueOf(value);
    }

    public static void main(String[] args) {

    }

}
