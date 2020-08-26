package com.jd.bluedragon.common.dto.unloadCar;

/**
 * @author lijie
 * @date 2020/7/3 1:11
 */
public enum OperateTypeEnum {

    DELETE_HELPER(0,"删除"),

    INSERT_HELPER(1,"新增");

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

    OperateTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static OperateTypeEnum getEnum(int type) {
        for (OperateTypeEnum operateType : OperateTypeEnum.values()) {
            if (operateType.getType() == type) {
                return operateType;
            }
        }
        return null;
    }
}
