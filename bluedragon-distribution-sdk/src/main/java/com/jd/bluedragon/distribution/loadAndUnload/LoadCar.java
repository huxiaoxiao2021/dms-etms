package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务
 * @author: wuming
 * @create: 2020-10-15 20:22
 */
public class LoadCar extends DbEntity {

    /**
     * 车牌
     */
    private String licenseNumber;

    /**
     * 目的场地Id
     */
    private Long endSiteCode;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 创建人所属转运中心Id
     */
    private Long createSiteCode;

    /**
     * 创建人所属转运中心名称
     */
    private String createSiteName;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 装车任务状态：0-未开始，1-已开始，2-已完成
     */
    private Integer status;

    /**
     * 操作时间
     */
    private Date updateTime;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作人erp
     */
    private String operateUserErp;


    public LoadCar() {
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Long getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(Long endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }
}
