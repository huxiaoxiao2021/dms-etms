package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @ClassName BarCodeLabelOptionEnum
 * @Description
 * @Author wyh
 * @Date 2022/4/10 13:54
 **/
public enum BarCodeLabelOptionEnum {

    PRODUCT_TYPE(1, "产品类型"),
    INTERCEPT(2, "拦截"),
    SEND_FORCE_SEND(3, "强扫")
    ;

    private Integer code;

    private String name;

    BarCodeLabelOptionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (BarCodeLabelOptionEnum labelOptionEnum : BarCodeLabelOptionEnum.values()) {
            if (labelOptionEnum.code.equals(code)) {
                return labelOptionEnum.name;
            }
        }
        return "";
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
