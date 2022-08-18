package com.jd.bluedragon.distribution.print.domain;

/**
 * 面单输出方式枚举
 *
 * @author hujiping
 * @date 2022/8/15 5:26 PM
 */
public enum SurfaceOutputTypeEnum {

    OUTPUT_TYPE_PRINT(0, "打印"),
    OUTPUT_TYPE_PREVIEW(1, "预览");

    SurfaceOutputTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

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
