package com.jd.bluedragon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by zhangzhen10 on 2017/12/12.
 */
public class ReflectUtils {
    private static Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 根据属性名获取属性值
     */
    public static Object getFieldValueByName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, null);
            return value;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据方法名及传入的参数，得到返回值
     */
    public static Object invokeMethodByMethodName(String methodName, Object invokeObject, Object param) {
        try {
            Method method = invokeObject.getClass().getDeclaredMethod(methodName, param.getClass());
            Object value = method.invoke(invokeObject, param);
            return value;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
