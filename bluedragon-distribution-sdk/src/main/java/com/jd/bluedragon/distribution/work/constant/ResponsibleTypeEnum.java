package com.jd.bluedragon.distribution.work.constant;

public enum ResponsibleTypeEnum {
    WORK_GRID_SIGN(1,"网格签到人"),
    SUPPLIER(2, "外包商"),
    WORK_GRID_OWNER(3,"网格组长");
    
    private Integer code;

    /**
     * 名称
     */
    private String name;

    ResponsibleTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
