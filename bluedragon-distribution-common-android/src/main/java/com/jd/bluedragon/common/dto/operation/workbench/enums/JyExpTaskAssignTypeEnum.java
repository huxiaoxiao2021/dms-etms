package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/6/1 15:33
 * @Description:
 */
public enum JyExpTaskAssignTypeEnum {

     RECEIVE(0,"取件"),
     ASSIGN(1,"指派"),

    ;


    private final int code;

    private final String text;

    JyExpTaskAssignTypeEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
