package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class ExcepScanDto implements Serializable {
    private static final long serialVersionUID = 653616219093419933L;
    /**
     * UnloadBarCodeQueryEntranceEnum
     * 扫描异常类型: 1待扫 2拦截 3多扫
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
