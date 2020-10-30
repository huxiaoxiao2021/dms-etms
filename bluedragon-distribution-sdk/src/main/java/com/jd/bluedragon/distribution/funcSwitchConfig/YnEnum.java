package com.jd.bluedragon.distribution.funcSwitchConfig;


public enum YnEnum {
    //这里前端枚举与 liqinlin 分拣机开关刚好的相反
    // 对应分拣机打开开关---OFF
    YN_ON(1,"有效","不拦截"),
    // 对应分拣机关闭开关---ON
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
