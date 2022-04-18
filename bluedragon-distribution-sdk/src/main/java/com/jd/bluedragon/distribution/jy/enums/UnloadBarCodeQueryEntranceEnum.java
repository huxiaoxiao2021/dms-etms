package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName UnloadBarCodeQueryEntranceEnum
 * @Description
 * @Author wyh
 * @Date 2022/4/2 14:05
 **/
public enum UnloadBarCodeQueryEntranceEnum {

    TO_SCAN(1, "待扫"),
    INTERCEPT(2, "拦截"),
    MORE_SCAN(3, "多扫"),
    COMPLETE_PREVIEW(4, "卸车完成前预览"),
    ;

    private Integer code;

    private String name;

    UnloadBarCodeQueryEntranceEnum(Integer code, String name) {
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
