package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName UnloadBarCodeQueryEntranceEnum
 * @Description
 * @Author wyh
 * @Date 2022/4/2 14:05
 **/
public enum UnloadBarCodeScanTypeEnum {

    TO_SCAN(1, "待扫"),
    SCANNED(2, "已扫"),
    LOCAL_MORE_SCAN(3, "本场地多扫"),
    OUT_MORE_SCAN(4, "非本场地多扫"),
    ;

    private Integer code;

    private String name;

    UnloadBarCodeScanTypeEnum(Integer code, String name) {
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
