package com.jd.bluedragon.distribution.jy.pickinggood;

import java.util.Date;

public class JyBizTaskPickingGoodExtendEntity {
    private Long id;

    private String bizId;

    private String sendCode;

    private Byte waitScanInitComplete;

    private Date waitScanInitCompleteTime;

    private Integer waitScanNum;

    private Integer waitScanPackageNum;

    private Integer waitScanBoxNum;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Byte getWaitScanInitComplete() {
        return waitScanInitComplete;
    }

    public void setWaitScanInitComplete(Byte waitScanInitComplete) {
        this.waitScanInitComplete = waitScanInitComplete;
    }

    public Date getWaitScanInitCompleteTime() {
        return waitScanInitCompleteTime;
    }

    public void setWaitScanInitCompleteTime(Date waitScanInitCompleteTime) {
        this.waitScanInitCompleteTime = waitScanInitCompleteTime;
    }

    public Integer getWaitScanNum() {
        return waitScanNum;
    }

    public void setWaitScanNum(Integer waitScanNum) {
        this.waitScanNum = waitScanNum;
    }

    public Integer getWaitScanPackageNum() {
        return waitScanPackageNum;
    }

    public void setWaitScanPackageNum(Integer waitScanPackageNum) {
        this.waitScanPackageNum = waitScanPackageNum;
    }

    public Integer getWaitScanBoxNum() {
        return waitScanBoxNum;
    }

    public void setWaitScanBoxNum(Integer waitScanBoxNum) {
        this.waitScanBoxNum = waitScanBoxNum;
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
}