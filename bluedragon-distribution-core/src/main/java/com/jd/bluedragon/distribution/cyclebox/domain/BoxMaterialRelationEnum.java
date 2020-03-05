package com.jd.bluedragon.distribution.cyclebox.domain;

/**
 * 循环集包袋枚举
 *
 * @author: hujiping
 * @date: 2020/2/21 14:30
 */
public enum BoxMaterialRelationEnum {

    SEND(1, "发货"),

    SEND_CANCEL(2, "取消发货"),

    INSPECTION(3, "验货"),

    SEND_SITE(4, "站点发货");

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

    BoxMaterialRelationEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static BoxMaterialRelationEnum getEnum(int type) {
        for (BoxMaterialRelationEnum status : BoxMaterialRelationEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
