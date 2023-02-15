package com.jd.bluedragon.common.dto.comboard.request;

import java.io.Serializable;

public class ExcepScanDto implements Serializable {
    private static final long serialVersionUID = 6317689039225362482L;
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
