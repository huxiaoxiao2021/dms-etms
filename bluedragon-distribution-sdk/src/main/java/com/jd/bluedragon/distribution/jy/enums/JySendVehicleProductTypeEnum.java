package com.jd.bluedragon.distribution.jy.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/2 10:28
 * @Description:
 */
public enum JySendVehicleProductTypeEnum {

    NONE("NONE", "其他", 99),
    FAST("FAST", "特快送", 1),
    AIR_TRANSPORT("AIR_TRANSPORT", "航空件", 2),
    FRESH("FRESH", "生鲜", 3),
    FRESH_SPECIAL("FRESH_SPECIAL", "生鲜特保", 4),
    TIKTOK("TIKTOK", "抖音", 5),
    DEWU("DEWU", "得物", 6),
    TEAN("TEAN","特安",7),
    KA("KA", "KA", 8),
    MEDICINE("MEDICINE", "医药", 9),
            ;

    private String code;

    private String name;

    private Integer displayOrder;

    JySendVehicleProductTypeEnum(String code, String name, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public static String getNameByCode(String code) {
        for (JySendVehicleProductTypeEnum productType : JySendVehicleProductTypeEnum.values()) {
            if (productType.code.equals(code)) {
                return productType.name;
            }
        }
        return "";
    }

    public static JySendVehicleProductTypeEnum getEnumByCode(String code) {
        for (JySendVehicleProductTypeEnum value : JySendVehicleProductTypeEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
