package com.jd.bluedragon.distribution.ministore.domain;

import java.util.Date;

public class MiniStoreBindRelation {
    private Long id;

    private String storeCode;

    private String boxCode;

    private String iceBoardCode1;

    private String iceBoardCode2;

    private Byte state;

    private Boolean occupiedFlag;

    private String des;

    private Integer sortCount;

    private Integer unboxCount;

    private Long createSiteCode;

    private String createSiteName;

    private Long receiveSiteCode;

    private String receiveSiteName;

    private String createUser;

    private Long createUserCode;

    private String updateUser;

    private Long updateUserCode;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getIceBoardCode1() {
        return iceBoardCode1;
    }

    public void setIceBoardCode1(String iceBoardCode1) {
        this.iceBoardCode1 = iceBoardCode1;
    }

    public String getIceBoardCode2() {
        return iceBoardCode2;
    }

    public void setIceBoardCode2(String iceBoardCode2) {
        this.iceBoardCode2 = iceBoardCode2;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Boolean getOccupiedFlag() {
        return occupiedFlag;
    }

    public void setOccupiedFlag(Boolean occupiedFlag) {
        this.occupiedFlag = occupiedFlag;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Integer getSortCount() {
        return sortCount;
    }

    public void setSortCount(Integer sortCount) {
        this.sortCount = sortCount;
    }

    public Integer getUnboxCount() {
        return unboxCount;
    }

    public void setUnboxCount(Integer unboxCount) {
        this.unboxCount = unboxCount;
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

    public Long getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Long receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Long createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Long getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Long updateUserCode) {
        this.updateUserCode = updateUserCode;
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

    @Override
    public String toString() {
        return "MiniStoreBindRelation{" +
                "id=" + id +
                ", storeCode='" + storeCode + '\'' +
                ", boxCode='" + boxCode + '\'' +
                ", iceBoardCode1='" + iceBoardCode1 + '\'' +
                ", iceBoardCode2='" + iceBoardCode2 + '\'' +
                ", state=" + state +
                ", occupiedFlag=" + occupiedFlag +
                ", des='" + des + '\'' +
                ", sortCount=" + sortCount +
                ", unboxCount=" + unboxCount +
                ", createSiteCode=" + createSiteCode +
                ", createSiteName='" + createSiteName + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createUserCode=" + createUserCode +
                ", updateUser='" + updateUser + '\'' +
                ", updateUserCode=" + updateUserCode +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}