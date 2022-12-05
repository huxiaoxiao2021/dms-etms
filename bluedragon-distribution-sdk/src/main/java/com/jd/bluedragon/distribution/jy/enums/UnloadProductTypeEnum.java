package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName UnloadProductTypeEnum
 * @Description 卸车产品类型
 * @Author wyh
 * @Date 2022/4/2 14:05
 **/
public enum UnloadProductTypeEnum {

    NONE("NONE", "其他", 99),
    FRESH("FRESH", "生鲜", 3),
    KA("KA", "KA", 5),
    MEDICINE("MEDICINE", "医药", 4),
    FAST("FAST", "特快送", 2),
    TIKTOK("TIKTOK", "抖音", 1)
    ;

    private String code;

    private String name;

    private Integer displayOrder;

    UnloadProductTypeEnum(String code, String name, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public static String getNameByCode(String code) {
        for (UnloadProductTypeEnum productType : UnloadProductTypeEnum.values()) {
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
