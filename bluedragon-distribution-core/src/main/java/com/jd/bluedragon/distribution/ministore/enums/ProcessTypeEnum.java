package com.jd.bluedragon.distribution.ministore.enums;

public enum ProcessTypeEnum {
    SEND_JIEHUOCANG(1,"接货仓发货"),
    INSPECTION_SORT_CENTER(2,"分拣中心验货"),
    SEND_SORT_CENTER(3,"分拣中心发货"),
    BACK_INSPECTION_SORT_CENTER(4,"逆向-分拣中心验货"),
    BACK_SEND_SORT_CENTER(5,"逆向-分拣中心发货");
    private Integer type;
    private String msg;

    ProcessTypeEnum(Integer type, String msg) {
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
