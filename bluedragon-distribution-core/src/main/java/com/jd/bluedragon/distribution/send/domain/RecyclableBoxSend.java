package com.jd.bluedragon.distribution.send.domain;

public class RecyclableBoxSend {

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 错误代码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public RecyclableBoxSend(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
