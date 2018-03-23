package com.jd.bluedragon.distribution.popPrint.domain;

import java.io.Serializable;

/**
 *  POP打印回传运单 任务 BODY json串对应的实体类，
 *  其实可以用WaybillStatus 但是有些字段类型不一致，
 *  比如操作时间导致存入的json串是时间戳
 *  所以新建一个类，统一处理全部String类型
 */
public class PopAddPackStateTaskBody implements Serializable {

    

    private String sendCode;
    private String boxCode;
    private String waybillCode;
    private String packageCode;

    private String createSiteCode;
    private String createSiteName;

    private String operatorId;
    private String operator;
    private String operateType;
    private String operateTime;

    private String remark;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(String createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
