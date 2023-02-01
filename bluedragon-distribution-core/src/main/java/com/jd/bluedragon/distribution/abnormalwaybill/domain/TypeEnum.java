//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jd.bluedragon.distribution.abnormalwaybill.domain;

public enum TypeEnum {
    SYS_AUTO(1, "需要补打"),
    MANAL(2, "人工处理"),;

    private int code;
    private String desc;

    private TypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
