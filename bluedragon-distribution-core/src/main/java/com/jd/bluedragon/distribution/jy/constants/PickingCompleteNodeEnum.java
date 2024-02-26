package com.jd.bluedragon.distribution.jy.constants;

public enum PickingCompleteNodeEnum {

    COMPLETE_BTN(1, "完成按钮触发"),
    EXCEPTION_BTN(2, "异常按钮触发"),
    WAIT_SCAN_0(3, "非自建任务待提为0时触发"),
    TIME_EXECUTE(4, "自建任务定时执行触发"),

    ;

    private int code;
    private String msg;

    PickingCompleteNodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
