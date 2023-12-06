package com.jd.bluedragon.distribution.jy.pickinggood;

import java.util.Date;

/**
 * 空提提货任务扩展表
 */
public class JyBizTaskPickingGoodExtendEntity {
    private Long id;

    private String bizId;
    /**
     * 待提批次号
     */
    private String sendCode;
    /**
     * 待提数据初始化完成标识：1 初始化完成
     */
    private Integer waitScanInitComplete;
    /**
     * 待提数据初始化完成时间
     */
    private Date waitScanInitCompleteTime;
    /**
     * 待扫总件数
     */
    private Integer waitScanNum;
    /**
     * 待扫包裹数
     */
    private Integer waitScanPackageNum;
    /**
     * 待扫箱数
     */
    private Integer waitScanBoxNum;

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

    public Integer getWaitScanInitComplete() {
        return waitScanInitComplete;
    }

    public void setWaitScanInitComplete(Integer waitScanInitComplete) {
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