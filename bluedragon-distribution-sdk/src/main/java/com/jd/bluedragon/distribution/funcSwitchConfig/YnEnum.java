package com.jd.bluedragon.distribution.funcSwitchConfig;

public enum YnEnum {
    YN_ON(1,"有效","不拦截"),
    YN_OFF(0,"无效","拦截");

    private  String text;
    private  int code;
    private  String message;


    YnEnum( int code,String text,String message) {
        this.text = text;
        this.code = code;
        this.message = message;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
