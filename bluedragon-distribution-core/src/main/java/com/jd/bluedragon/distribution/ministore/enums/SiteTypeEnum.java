package com.jd.bluedragon.distribution.ministore.enums;

public enum SiteTypeEnum {
    FENJIAN(64,"分拣中心"),
    JIEHUOCANG(6430,"接货仓");

    private Integer type;
    private String msg;

    SiteTypeEnum(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
