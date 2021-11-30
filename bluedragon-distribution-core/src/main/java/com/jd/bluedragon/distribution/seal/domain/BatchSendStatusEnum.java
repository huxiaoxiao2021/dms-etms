package com.jd.bluedragon.distribution.seal.domain;

/**
 * 租户列表
 * Created by jinjingcheng on 2018/4/22.
 */
public enum BatchSendStatusEnum {
    USED(0, "已使用"),
    UNUSED(1, "取消使用");

    private Integer code;
    private String name;

    BatchSendStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
