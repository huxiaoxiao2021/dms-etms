package com.jd.bluedragon.distribution.queueManagement.domain;

/**
 * 作业状态修改请求对象
 */
public class PlatformWorkRequest {
    /**
     * 月台编码
     */
    private String platformCode;

    /**
     * 排队任务编码
     */
    private String queueTaskCode;

    /**
     * 作业操作类型
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

    public String getQueueTaskCode() {
        return queueTaskCode;
    }

    public void setQueueTaskCode(String queueTaskCode) {
        this.queueTaskCode = queueTaskCode;
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
