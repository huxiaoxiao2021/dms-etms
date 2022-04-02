package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName ProductTypeEnum
 * @Description 卸车产品类型
 * @Author wyh
 * @Date 2022/4/2 14:05
 **/
public enum ProductTypeEnum {

    NONE("NONE", "其他", 99),
    FRESH("FRESH", "生鲜", 1),
    KA("KA", "KA", 2),
    MEDICINE("MEDICINE", "医药", 3),
    FAST("FAST", "特快送", 4),
    ;

    private String code;

    private String name;

    private Integer displayOrder;

    ProductTypeEnum(String code, String name, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
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
