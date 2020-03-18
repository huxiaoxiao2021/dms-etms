package com.jd.bluedragon.common.dto.queueManagement.response;

import java.io.Serializable;

public class CarTypeInfoDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String carType;
    private String carTypeName;

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }
}
