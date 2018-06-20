package com.jd.bluedragon.distribution.abnormalDispose.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 已验货未发货的运单
 * @date 2018年06月19日 11时:35分
 */
public class AbnormalDisposeSend {
    private Date inspectionDate;//验货时间
    private String inspectionSiteCode;//验货分拣中心
    private String inspectionSiteName;//验货分拣中心
    private String waybillCode;
    private Integer nextAreaId;//下级区域
    private String nextAreaName;//下级区域
    private String nextSiteCode;//下级站点
    private String nextSiteName;//下级站点
    private Integer endCityId;//目的城市
    private String endCityName;//目的城市
    private String endSiteCode;//目的站点
    private String endSiteName;//目的站点
    private String isDispose;//是否提报异常
    private String abnormalType;//异常类型 0异常 1外呼
    private String abnormalReason1;//一级原因描述

    /**
     * 创建人code
     */
    private Integer createUserCode;

    /**
     * 创建人ERP
     */
    private String createUser;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 更新人code
     */
    private Integer updateUserCode;

    /**
     * 更新人ERP
     */
    private String updateUser;

    /**
     * 更新人名称
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    Date createTime;
    /**
     * 更新时间
     */
    Date updateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAbnormalReason1() {
        return abnormalReason1;
    }

    public void setAbnormalReason1(String abnormalReason1) {
        this.abnormalReason1 = abnormalReason1;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionSiteCode() {
        return inspectionSiteCode;
    }

    public void setInspectionSiteCode(String inspectionSiteCode) {
        this.inspectionSiteCode = inspectionSiteCode;
    }

    public String getInspectionSiteName() {
        return inspectionSiteName;
    }

    public void setInspectionSiteName(String inspectionSiteName) {
        this.inspectionSiteName = inspectionSiteName;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getNextAreaId() {
        return nextAreaId;
    }

    public void setNextAreaId(Integer nextAreaId) {
        this.nextAreaId = nextAreaId;
    }

    public String getNextAreaName() {
        return nextAreaName;
    }

    public void setNextAreaName(String nextAreaName) {
        this.nextAreaName = nextAreaName;
    }

    public String getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(String nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public String getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(String endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getIsDispose() {
        return isDispose;
    }

    public void setIsDispose(String isDispose) {
        this.isDispose = isDispose;
    }

    public String getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(String abnormalType) {
        this.abnormalType = abnormalType;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }
}
