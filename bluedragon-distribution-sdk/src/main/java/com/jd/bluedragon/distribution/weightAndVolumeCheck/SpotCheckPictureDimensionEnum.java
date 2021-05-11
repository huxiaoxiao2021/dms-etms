package com.jd.bluedragon.distribution.weightAndVolumeCheck;

/**
 * 抽检上传图片维度
 *
 * @author hujiping
 * @date 2021/5/10 11:22 上午
 */
public enum SpotCheckPictureDimensionEnum {

    PICTURE_WEIGHT(1, "重量"),
    PICTURE_LENGTH(2, "长"),
    PICTURE_WIDTH(3, "宽"),
    PICTURE_HEIGHT(4, "高"),
    PICTURE_PAGER(5, "面单");

    private Integer code;

    private String name;

    SpotCheckPictureDimensionEnum(Integer code, String name) {
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
