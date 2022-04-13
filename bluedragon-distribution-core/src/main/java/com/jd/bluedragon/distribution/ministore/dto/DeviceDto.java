package com.jd.bluedragon.distribution.ministore.dto;

import java.util.Date;

public class DeviceDto {
    private String storeCode;
    private String iceBoardCode;
    private String iceBoardCode1;
    private String iceBoardCode2;
    private String boxCode;
    private Byte state;
    private Boolean occupiedFlag;
    private Date startTime;
    private Date endTime;
    private String createUser;
    private Long createUserCode;
    private String updateUser;
    private Long updateUserCode;
    private Long createSiteCode;
    private String createSiteName;
    private Long receiveSiteCode;
    private String receiveSiteName;
    private Long miniStoreBindRelationId;
    private String errMsg;
    private Integer unboxCount;

    public String getIceBoardCode() {
        return iceBoardCode;
    }

    public void setIceBoardCode(String iceBoardCode) {
        this.iceBoardCode = iceBoardCode;
    }

    public Integer getUnboxCount() {
        return unboxCount;
    }

    public void setUnboxCount(Integer unboxCount) {
        this.unboxCount = unboxCount;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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

    public Long getMiniStoreBindRelationId() {
        return miniStoreBindRelationId;
    }

    public void setMiniStoreBindRelationId(Long miniStoreBindRelationId) {
        this.miniStoreBindRelationId = miniStoreBindRelationId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
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

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
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
}
