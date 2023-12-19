package com.jd.bluedragon.common.dto.work;

public enum WorkGridCandidateStatusEnum {
    NORMAL(1,"正常"), 
    //未排班 或已离职
    ABNORMAL(2,"不正常");
    private Integer code;
    private String name;

    WorkGridCandidateStatusEnum() {
    }

    WorkGridCandidateStatusEnum(Integer code, String name) {
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
