package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class WaitScanDto implements Serializable {
    private static final long serialVersionUID = -115634624256034024L;
    private String barCode;
    private Integer type;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
