package com.jd.bluedragon.utils;

public class ObjectHelper {
    
    public static Boolean isEmpty(Boolean object) {
        if (object == null || !object) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    public static Boolean isEmpty(Object object) {
        if (object == null) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    public static Boolean isNotEmpty(Object object) {
        if (object == null) {
            return Boolean.FALSE;
        }
        
        return Boolean.TRUE;
    }
}
