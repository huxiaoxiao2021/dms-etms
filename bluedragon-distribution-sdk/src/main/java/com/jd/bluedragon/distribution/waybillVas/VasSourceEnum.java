package com.jd.bluedragon.distribution.waybillVas;

/**
 * 增值复制值定义
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-22 21:26:52 周三
 */
public enum VasSourceEnum {


    /**
     * 现有历史自定义
     */
    OLD_SELT_DEFINIED(1, "现有历史自定义"),

    /**
     * 新增自定义
     */
    ELT_DEFINIED(2, "新增自定义"),

    /**
     * 产品中心定义
     */
    PRODUCT_CENTER(3, "产品中心定义"),

    /**
     * 仓产品
     */
    STORE_PRODUCT(4, "仓产品"),
    ;

    private Integer code;

    private String name;

    private VasSourceEnum(Integer code, String name) {
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
