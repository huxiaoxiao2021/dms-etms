package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

public class SendExcepScanDto implements Serializable {

    private static final long serialVersionUID = -1616340253019876145L;
    /**
     * com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum
     */
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
