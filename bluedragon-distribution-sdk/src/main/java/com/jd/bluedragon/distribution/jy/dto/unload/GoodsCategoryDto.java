package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
public class GoodsCategoryDto implements Serializable {
    private static final long serialVersionUID = 7431307375891856971L;
    private Integer type;
    private String name;
    private Integer count;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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
