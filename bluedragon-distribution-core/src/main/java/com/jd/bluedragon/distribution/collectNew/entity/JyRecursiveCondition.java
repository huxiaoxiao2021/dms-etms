package com.jd.bluedragon.distribution.collectNew.entity;

import java.io.Serializable;

public class JyRecursiveCondition implements Serializable {

    private static final long serialVersionUID = 2529892464587997330L;

    private String sendVehicleBizId;

    private Integer queryCount;

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public Integer getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(Integer queryCount) {
        this.queryCount = queryCount;
    }
}
