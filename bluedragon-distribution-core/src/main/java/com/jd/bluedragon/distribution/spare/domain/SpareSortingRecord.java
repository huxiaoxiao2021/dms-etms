package com.jd.bluedragon.distribution.spare.domain;

import java.util.Date;

/*
 * 备件库分拣记录
 * */
public class SpareSortingRecord {

    /*
    * 主键
    * */
    private Long id;

    /*
     * 箱号
     * */
    private String boxCode;

    /*
     * 运单号
     * */
    private String waybillCode;

    /*
     * 始发站点
     * */
    private Integer createSiteCode;

    /*
     * 始发站点名称
     * */
    private String createSiteName;

    /*
     * 目的站点
     * */
    private Integer receiveSiteCode;

    /*
     * 目的站点名称
     * */
    private String receiveSiteName;

    /*
     * 责任主体
     * */
    private String dutyCode;

    /*
     * 责任主体名称
     * */
    private String dutyName;

    /*
     * 破损原因
     * */
    private String spareReason;

    private Integer createUserCode;

    private String createUser;

    private Date createTime;

    private Integer updateUserCode;

    private String updateUser;

    private Date updateTime;

    private Byte isDelete;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode == null ? null : boxCode.trim();
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode == null ? null : waybillCode.trim();
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
        this.createSiteName = createSiteName == null ? null : createSiteName.trim();
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName == null ? null : receiveSiteName.trim();
    }

    public String getDutyCode() {
        return dutyCode;
    }

    public void setDutyCode(String dutyCode) {
        this.dutyCode = dutyCode == null ? null : dutyCode.trim();
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName == null ? null : dutyName.trim();
    }

    public String getSpareReason() {
        return spareReason;
    }

    public void setSpareReason(String spareReason) {
        this.spareReason = spareReason == null ? null : spareReason.trim();
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
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}