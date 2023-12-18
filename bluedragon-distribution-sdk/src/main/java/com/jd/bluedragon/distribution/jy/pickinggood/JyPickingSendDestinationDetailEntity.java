package com.jd.bluedragon.distribution.jy.pickinggood;

import java.io.Serializable;
import java.util.Date;

/**
 * 空提提货发货流向批次明细
 */
public class JyPickingSendDestinationDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 创建场地id
     */
    private Long createSiteId;
    /**
     * 流向场地ID
     */
    private Long nextSiteId;
    /**
     * 发货批次号
     */
    private String sendCode;
    /**
     * 提货状态（0-待发货，1-发货中，2-待封车[发货完成]，3-已封车）
     */
    private Integer status;
    /**
     * 是否操作过封车标识：1-是，0-否
     */
    private Integer sealFlag;
    /**
     * 首次扫描时间
     */
    private Date firstScanTime;
    /**
     * 完成时间
     */
    private Date completeTime;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateSiteId() {
        return createSiteId;
    }

    public void setCreateSiteId(Long createSiteId) {
        this.createSiteId = createSiteId;
    }

    public Long getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Long nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSealFlag() {
        return sealFlag;
    }

    public void setSealFlag(Integer sealFlag) {
        this.sealFlag = sealFlag;
    }

    public Date getFirstScanTime() {
        return firstScanTime;
    }

    public void setFirstScanTime(Date firstScanTime) {
        this.firstScanTime = firstScanTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
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

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}