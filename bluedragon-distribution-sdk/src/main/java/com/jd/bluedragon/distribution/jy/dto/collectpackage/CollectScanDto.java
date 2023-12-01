package com.jd.bluedragon.distribution.jy.dto.collectpackage;

import java.io.Serializable;

public class CollectScanDto implements Serializable {

    /**
     * CollectPackageExcepScanEnum
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
