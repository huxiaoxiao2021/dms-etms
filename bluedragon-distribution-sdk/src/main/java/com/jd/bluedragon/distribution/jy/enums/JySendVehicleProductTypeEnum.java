package com.jd.bluedragon.distribution.jy.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/2 10:28
 * @Description:
 */
public enum JySendVehicleProductTypeEnum {

    NONE("NONE", "其他", 99),
    FAST("FAST", "特快送", 1),
    FRESH("FRESH", "生鲜", 2),
    TIKTOK("TIKTOK", "抖音", 3),
    DEWU("DEWU", "得物", 4),
    MEDICINE("MEDICINE", "医药", 5),
    KA("KA", "KA", 6),
    EASYFROZEN("EASYFROZEN", "易冻品", 7),
    LUXURY("LUXURY", "奢侈品", 8),
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
