package com.jd.bluedragon.distribution.abnormalDispose.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月19日 11时:35分
 */
public class AbnormalDisposeInspection {
    private String waveBusinessId;
    private Date sealVehicleDate;//解封车时间
    private String waybillCode;
    private Integer prevAreaId;//上级区域
    private String prevAreaName;//上级区域
    private String prevSiteCode;//上级站点
    private String prevSiteName;//上级站点
    private Integer endCityId;//目的城市
    private String endCityName;//目的城市
    private String endSiteCode;//目的站点
    private String endSiteName;//目的站点
    private String isDispose;//是否提报异常
    private String qcCode;//质控系统异常编码

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

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }

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
    public Date getSealVehicleDate() {
        return sealVehicleDate;
    }

    public void setSealVehicleDate(Date sealVehicleDate) {
        this.sealVehicleDate = sealVehicleDate;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPrevAreaId() {
        return prevAreaId;
    }

    public void setPrevAreaId(Integer prevAreaId) {
        this.prevAreaId = prevAreaId;
    }

    public String getPrevAreaName() {
        return prevAreaName;
    }

    public void setPrevAreaName(String prevAreaName) {
        this.prevAreaName = prevAreaName;
    }

    public String getPrevSiteCode() {
        return prevSiteCode;
    }

    public void setPrevSiteCode(String prevSiteCode) {
        this.prevSiteCode = prevSiteCode;
    }

    public String getPrevSiteName() {
        return prevSiteName;
    }

    public void setPrevSiteName(String prevSiteName) {
        this.prevSiteName = prevSiteName;
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

    public String getQcCode() {
        return qcCode;
    }

    public void setQcCode(String qcCode) {
        this.qcCode = qcCode;
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
