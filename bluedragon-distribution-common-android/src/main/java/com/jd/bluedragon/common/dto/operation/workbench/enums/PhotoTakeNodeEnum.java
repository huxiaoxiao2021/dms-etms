package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/29 17:12
 * @Description: 货物拍照节点
 */
public enum PhotoTakeNodeEnum {

    UNLOAD_SCAN(1,"卸货扫描");


    private Integer code;

    private String desc;

    PhotoTakeNodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static String getDescByCode(Integer code) {
        for (PhotoTakeNodeEnum photoTakeNodeEnum : PhotoTakeNodeEnum.values()) {
            if (photoTakeNodeEnum.code.equals(code)) {
                return photoTakeNodeEnum.desc;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
