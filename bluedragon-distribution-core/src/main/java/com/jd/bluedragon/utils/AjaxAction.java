package com.jd.bluedragon.utils;

public interface AjaxAction {
    
    public static final String OPERATE_SUCCESS = "1";
    public static final String OPERATE_FAIL = "0";
    
    String process();
}
