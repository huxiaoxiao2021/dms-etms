package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检超标上传的图片类型
 *
 * @author: hujiping
 * @date: 2021/12/16 17:56
 */
public enum SpotCheckPicTypeEnum {

    PIC_PANORAMA(0, "全景"),
    PIC_WEIGHT(1, "重量"),
    PIC_FACE(2, "面单"),
    PIC_LENGTH(3, "长"),
    PIC_WIDTH(4, "宽"),
    PIC_HEIGHT(5, "高");

    private Integer code;

    private String name;

    SpotCheckPicTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String analysisNameFromCode(int code){
        for (SpotCheckPicTypeEnum value : SpotCheckPicTypeEnum.values()) {
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
