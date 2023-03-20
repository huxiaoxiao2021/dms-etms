package com.jd.bluedragon.distribution.recycle.material.enums;

public enum PrintTypeEnum {
    PRINT(1,"打印"),
    REPRINT(2,"补打印"),
    DISABLE_AKBOX(3,"作废周转筐");
    private int code;

    private String name;

    PrintTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
