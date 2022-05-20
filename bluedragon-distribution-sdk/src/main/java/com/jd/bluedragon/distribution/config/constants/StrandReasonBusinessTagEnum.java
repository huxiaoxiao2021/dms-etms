package com.jd.bluedragon.distribution.config.constants;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.config.constants
 * @Description:
 * @date Date : 2022年05月12日 20:13
 */
public enum StrandReasonBusinessTagEnum {

    BUSINESS_TAG_DEFAULT(1,"默认"),
    BUSINESS_TAG_COLD(2,"冷链");
    StrandReasonBusinessTagEnum(Integer code,String name){
        this.code = code;
        this.name = name;
    }
    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
