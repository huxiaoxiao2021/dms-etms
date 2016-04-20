package com.jd.bluedragon.utils;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

/**
 * Created by wangtingwei on 2016/4/19.
 */
public  class LogHelper {

    public static void errorUseCurrentStackTrace(Log logger,String message){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(message);
        for (StackTraceElement element : stes) {
            stringBuilder.append(element);
            stringBuilder.append("\n");
        }
        logger.error(stringBuilder.toString());
    }


    public static void errorUseCurrentStackTrace(Logger logger,String message){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(message);
        for (StackTraceElement element : stes) {
            stringBuilder.append(element);
            stringBuilder.append("\n");
        }
        logger.error(stringBuilder.toString());
    }


}
