package com.jd.bluedragon.distribution.work.constant;

public enum ViolentSortingResponsibleStatusEnum {
    NOT_FOUND(0,"未找到责任人"),
    DETERMINED(1,"已定责");
    /**
     * 编码
     */
    private Integer code;

    /**
     * 名称
     */
    private String name;
    

    ViolentSortingResponsibleStatusEnum(Integer code, String name) {
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
