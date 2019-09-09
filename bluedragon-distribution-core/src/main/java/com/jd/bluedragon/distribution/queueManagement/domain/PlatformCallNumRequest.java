package com.jd.bluedragon.distribution.queueManagement.domain;

public class PlatformCallNumRequest {
    /**
     * 月台编码
     */
    private String platformCode;

    /**
     * 流向编码
     */
    private String flowCode;

    /**
     * 车型
     */
    private String carType;

    /**
     * 作业类型
     */
    private int platformWorkTypeEnum;

    /**
     * 操作人信息
     */
    private Operator operatorInfo;

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public int getPlatformWorkTypeEnum() {
        return platformWorkTypeEnum;
    }

    public void setPlatformWorkTypeEnum(int platformWorkTypeEnum) {
        this.platformWorkTypeEnum = platformWorkTypeEnum;
    }

    public Operator getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(Operator operatorInfo) {
        this.operatorInfo = operatorInfo;
    }
}
