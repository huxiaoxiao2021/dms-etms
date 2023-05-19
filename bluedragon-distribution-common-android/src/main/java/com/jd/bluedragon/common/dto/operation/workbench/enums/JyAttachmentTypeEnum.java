package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 拣运-附件类型枚举
 *
 * @author hujiping
 * @date 2023/3/30 2:26 PM
 */
public enum JyAttachmentTypeEnum {

    FILE(0, "文档"),
    PICTURE(1, "图片"),
    MUSIC(2, "音频"),
    VIDEO(3, "视频"),
    ;


    private final int code;

    private final String desc;

    JyAttachmentTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return desc;
    }
    
}
