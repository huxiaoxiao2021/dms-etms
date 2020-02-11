package com.jd.bluedragon.common.dto.abnormal;

/**
 * 处理（责任）部门类型
 *
 */
public enum DutyDepartmentTypeEnum {

    DISTRIBUTION_SITE(0, "分拣、配送节点"),

    WAREHOUSE(1, "仓");

    private int type;

    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DutyDepartmentTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static DutyDepartmentTypeEnum getEnum(int type) {
        for (DutyDepartmentTypeEnum status : DutyDepartmentTypeEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
