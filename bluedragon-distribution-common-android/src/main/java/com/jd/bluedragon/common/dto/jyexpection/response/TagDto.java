package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;

/**
 * 按网格统计
 */
public class TagDto implements Serializable {

    private Integer code;

    private String name;

    private String style;

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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
