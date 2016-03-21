package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdObject;

import java.util.Date;

/**
 * Created by yanghongqiang on 2016/3/15.
 */
public class GantryDeviceConfig extends JdObject {
    /**
     * 自动发货
     */
    public static final int AUTO_INSPECTION=1;

    /**
     * 自动发货
     */
    public static final int AUTO_SEND=2;
    /**
     * 测量体积
     */
    public static final int AUTO_MEASURE=4;

    public Long id;

    /*
    龙门架编号全国维一编号
     */
    private Integer machineId;

    /*
    龙门架序列号,
     */
    private String gantrySerialNumber;

    /**
     * 操作人id
     */
    private int operateUserId;

    /*
    *操作人erp帐户
     */
    private String operateUserErp;

    /*
    *操作人姓名
    */
    private String operateUserName;


    /*
    *结束批次人erp帐户
     */
    private String updateUserErp;

    /*
    *结束批次人姓名
    */
    private String updateUserName;

    /*
    操作人所属站点
     */
    private Integer createSiteCode;

    /*
    操作人所属分拣中心
     */
    private String createSiteName;

    /*
    操作类型,按位求于,1验货，2发货，4量方。5验货+量方。6发货+量方
     */
    private Integer businessType;

    /*
    用文字表达的操作类型如：验货+量方,对应二进制101，10进至5.
     */
    private String operateTypeRemark;

    /*
    批次号
     */
    private String sendCode;

    /*
    批次号开始时间
     */
    private Date startTime;

    /*
    批次号结束时间
     */
    private Date endTime;

    /*
    q锁定状态
     */
    private Integer lockStatus;

    /*
    锁定人ERP帐户
     */
    private  String lockUserErp;

    /*
    锁定人姓名
     */
    private String lockUserName;

    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public String getGantrySerialNumber() {
        return gantrySerialNumber;
    }

    public void setGantrySerialNumber(String gantrySerialNumber) {
        this.gantrySerialNumber = gantrySerialNumber;
    }

    public int getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(int operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getOperateTypeRemark() {
        return operateTypeRemark;
    }

    public void setOperateTypeRemark(String operateTypeRemark) {
        this.operateTypeRemark = operateTypeRemark;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Integer lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getLockUserErp() {
        return lockUserErp;
    }

    public void setLockUserErp(String lockUserErp) {
        this.lockUserErp = lockUserErp;
    }

    public String getLockUserName() {
        return lockUserName;
    }

    public void setLockUserName(String lockUserName) {
        this.lockUserName = lockUserName;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
