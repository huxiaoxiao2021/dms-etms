package com.jd.bluedragon.utils;

/**
 * 打印标签来源
 * @author yanghongqiang
 *
 */
public enum OriginalType {

    /**分拣中心打印标签*/
    DMS("dms"),
    /**备件库打印标签*/
    SPWMS("SPWMS");

    private final String value;
    public String getValue(){
        return value;
    }
    private OriginalType(String value){
        this.value=value;
    }
}
