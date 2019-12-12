package com.jd.bluedragon.common.dto.abnormal;

/**
 * 处理（责任）部门信息
 *
 */
public class DutyDepartmentInfo {

    private String code;

    private String name;

    private Integer type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
