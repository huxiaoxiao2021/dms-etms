package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class GoodsCategoryDto implements Serializable {
    private static final long serialVersionUID = -6293792134242023592L;
    private String type;
    private String name;
    private Integer count;
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
