package com.jd.bluedragon.utils;

public class BooleanHelper {
    
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
    
    public static Boolean isFlase(Boolean object) {
        if (object == null || !object) {
            return Boolean.TRUE;
        }
        
        return false;
    }
    
    public static Boolean isNotEmpty(Object object) {
        if (object == null) {
            return Boolean.FALSE;
        }
        
        return Boolean.TRUE;
    }
    
    public static Boolean isTrue(Boolean object) {
        if (object != null && object) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
}
