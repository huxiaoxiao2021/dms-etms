package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yanghongqiang on 2016/3/11.
 * 龙门架编号，操作时间，返回操作类型，操作人ID、姓名、操作站点ID，名称、批次号（可选）等其它信息
 * "siteCode":2015,"siteName":"北京双树分拣中心","staffName":"杨宏强","orgId":6,"dmsCode":"010F005"
 */
public class GantryDeviceConfigJsfRequest implements Serializable{

    private static final long serialVersionUID = -7364913389727514244L;

    public Long id;

    /*
    龙门架编号全国维一编号
     */
    private String machineId;

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
    操作类型,按位求于,1验货，2发货，4量方、称重。5验货+量方、称重。6发货+量方、称重
     */
    private Integer businessType;

    /*
    用文字表达的操作类型如：验货+量方,对应二进制101，10进至5.
     */
    private String businessTypeRemark;

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
    private String lockUserErp;

    /*
    锁定人姓名
     */
    private String lockUserName;

    /*
    数据库时间
     */
    private Date dbTime;

    private Integer yn;

    /*
    龙门架新老版本，0-老版本，1-新版本
    */
    private Byte version;
    /**
     * 业务类型：正向 逆向
     */
    private Integer waybillBusinessType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
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

    public String getBusinessTypeRemark() {
        return businessTypeRemark;
    }

    public void setBusinessTypeRemark(String operateTypeRmark) {
        this.businessTypeRemark = operateTypeRmark;
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

    public Date getDbTime() {
        return dbTime;
    }

    public void setDbTime(Date dbTime) {
        this.dbTime = dbTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public Integer getWaybillBusinessType() {
        return waybillBusinessType;
    }

    public void setWaybillBusinessType(Integer waybillBusinessType) {
        this.waybillBusinessType = waybillBusinessType;
    }

    @Override
    public String toString() {
        return "GantryDeviceConfigJsfRequest{" +
                "id=" + id +
                ", machineId='" + machineId + '\'' +
                ", gantrySerialNumber='" + gantrySerialNumber + '\'' +
                ", operateUserId=" + operateUserId +
                ", operateUserErp='" + operateUserErp + '\'' +
                ", operateUserName='" + operateUserName + '\'' +
                ", updateUserErp='" + updateUserErp + '\'' +
                ", updateUserName='" + updateUserName + '\'' +
                ", createSiteCode=" + createSiteCode +
                ", createSiteName='" + createSiteName + '\'' +
                ", businessType=" + businessType +
                ", businessTypeRemark='" + businessTypeRemark + '\'' +
                ", sendCode='" + sendCode + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", lockStatus=" + lockStatus +
                ", lockUserErp='" + lockUserErp + '\'' +
                ", lockUserName='" + lockUserName + '\'' +
                ", dbTime=" + dbTime +
                ", yn=" + yn +
                ", version=" + version +
                '}';
    }

}
