package com.jd.bluedragon.distribution.busineCode.sendCode.contant;

public enum HugeSendCodeResponseEnum {
    NOT_DMS_POSITIVE(4001, "非分拣中心正向批次号"),
    NO_SEND(4002, "无发货数据"),
    SEND_PROCESSING(4003, "发货数据有积压未处理完");
    private int code;

    private String desc;

    HugeSendCodeResponseEnum(){

    }

    HugeSendCodeResponseEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
