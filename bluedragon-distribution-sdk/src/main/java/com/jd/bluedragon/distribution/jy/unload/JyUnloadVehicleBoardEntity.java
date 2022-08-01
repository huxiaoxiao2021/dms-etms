package com.jd.bluedragon.distribution.jy.unload;

import java.util.Date;

public class JyUnloadVehicleBoardEntity {
    private Long id;

    private String unloadVehicleBizId;

    private String boardCode;

    private Long startSiteId;

    private String startSiteName;

    private Long endSiteId;

    private String endSiteName;

    private String goodsAreaCode;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    /**
     * 组板数量
     */
    private Integer boardCodeNum;

    /**
     * 子任务ID
     */
    private String unloadVehicleStageBizId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnloadVehicleBizId() {
        return unloadVehicleBizId;
    }

    public void setUnloadVehicleBizId(String unloadVehicleBizId) {
        this.unloadVehicleBizId = unloadVehicleBizId;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
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

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getBoardCodeNum() {
        return boardCodeNum;
    }

    public void setBoardCodeNum(Integer boardCodeNum) {
        this.boardCodeNum = boardCodeNum;
    }

    public String getUnloadVehicleStageBizId() {
        return unloadVehicleStageBizId;
    }

    public void setUnloadVehicleStageBizId(String unloadVehicleStageBizId) {
        this.unloadVehicleStageBizId = unloadVehicleStageBizId;
    }

}