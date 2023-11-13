package com.jd.bluedragon.distribution.jy.dto.userSign;

import java.io.Serializable;

public class UserJobType implements Serializable {
    private static final long serialVersionUID = -6228085788350466310L;

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
