package com.jd.bluedragon.distribution.qualityControl.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dudong on 2014/12/9.
 */
public class QualityControl implements Serializable {
    private static final long serialVersionUID = 5064869768953952133L;

    public static final Integer QC_REVERSE = 110; // 分拣中心退货
    public static final Integer QC_SPARE = 140; // 分拣中心逆向备件库分拣破损
    public static final Integer QC_FXM = 5003; // 配送外呼
    public static final String SYSTEM_NAME = "DMS";

    private String waybillCode;//运单号
    private String typeCode; //三级编码
    private Date createTime;//创建时间
    private Integer createUserId;//创建人id
    private String createUserName;//创建人姓名
    private Integer blameDept;//责任部门
    private String blameDeptName;//责任部门
    private Integer messageType; //责任类型
    private String boxCode; //箱号
    private String systemName; //来源系统 DMS
    private String extraCode; //额外标识码
    private String returnState; // 是否需要返回处理信息

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getBlameDept() {
        return blameDept;
    }

    public void setBlameDept(Integer blameDept) {
        this.blameDept = blameDept;
    }

    public String getBlameDeptName() {
        return blameDeptName;
    }

    public void setBlameDeptName(String blameDeptName) {
        this.blameDeptName = blameDeptName;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getExtraCode() {
        return extraCode;
    }

    public void setExtraCode(String extraCode) {
        this.extraCode = extraCode;
    }

    public String getReturnState() {
        return returnState;
    }

    public void setReturnState(String returnState) {
        this.returnState = returnState;
    }
}
