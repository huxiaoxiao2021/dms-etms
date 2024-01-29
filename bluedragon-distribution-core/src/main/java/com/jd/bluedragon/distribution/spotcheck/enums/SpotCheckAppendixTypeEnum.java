package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检超标附件类型
 */
public enum SpotCheckAppendixTypeEnum {

    REPORT_PICTURE(1, "举报图片"),
    REPORT_VIDEO(2, "举报视频"),
    ESCALATION_PICTURE(3, "升级图片"),
    ESCALATION_VIDEO(4, "升级视频");

    private Integer code;

    private String name;

    SpotCheckAppendixTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String analysisNameFromCode(int code){
        for (SpotCheckAppendixTypeEnum value : SpotCheckAppendixTypeEnum.values()) {
            if(value.getCode() == code){
                return value.getName();
            }
        }
        return null;
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
