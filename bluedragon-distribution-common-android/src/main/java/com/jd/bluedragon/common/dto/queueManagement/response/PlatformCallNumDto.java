package com.jd.bluedragon.common.dto.queueManagement.response;

import java.io.Serializable;

public class PlatformCallNumDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String queueTaskCode;
    private String carCode;
    private String carTypeValue;

    public String getQueueTaskCode() {
        return queueTaskCode;
    }

    public void setQueueTaskCode(String queueTaskCode) {
        this.queueTaskCode = queueTaskCode;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public String getCarTypeValue() {
        return carTypeValue;
    }

    public void setCarTypeValue(String carTypeValue) {
        this.carTypeValue = carTypeValue;
    }
}
