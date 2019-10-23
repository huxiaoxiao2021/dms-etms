package com.jd.bluedragon.distribution.queueManagement.domain;

public class Operator {
    /**
     * 操作人erp
     */
    private String operatorUserErp;

    /**
     * 操作人名称
     */
    private String operatorUserName;

    /**
     * 操作人工作区
     */
    private String currentStationCode;

    /**
     * 操作人工作区类型
     */
    private int resourceTypeEnum;

    public String getOperatorUserErp() {
        return operatorUserErp;
    }

    public void setOperatorUserErp(String operatorUserErp) {
        this.operatorUserErp = operatorUserErp;
    }

    public String getOperatorUserName() {
        return operatorUserName;
    }

    public void setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
    }

    public String getCurrentStationCode() {
        return currentStationCode;
    }

    public void setCurrentStationCode(String currentStationCode) {
        this.currentStationCode = currentStationCode;
    }

    public int getResourceTypeEnum() {
        return resourceTypeEnum;
    }

    public void setResourceTypeEnum(int resourceTypeEnum) {
        this.resourceTypeEnum = resourceTypeEnum;
    }
}
