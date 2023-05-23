package com.jd.bluedragon.distribution.jy.constants;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-23 16:02
 */
public enum JyMixScanTaskCompleteEnum {

    DOING(0,"未完成"),
    COMPLETE(1,"完成");
    
    private Integer code;
    private String msg;

    JyMixScanTaskCompleteEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
