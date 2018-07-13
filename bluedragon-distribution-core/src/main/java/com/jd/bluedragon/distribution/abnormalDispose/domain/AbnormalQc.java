package com.jd.bluedragon.distribution.abnormalDispose.domain;

import java.util.Date;

/**
 * Created by hujiping on 2018/6/27.
 * 质控工单记录表
 */
public class AbnormalQc {

    private Integer id;
    private String waveBusinessId;
    private String waybillCode;
    private Integer createUserCode;
    private String createUserErp;
    private String createUser;
    private Integer createSiteCode;
    private String createSiteName;
    private String qcCode;
    private Date operateTime;
    private Date createTime;
    private Date updateTime;
    private Integer yn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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
        this.createSiteName = createSiteName;
    }

    public String getQcCode() {
        return qcCode;
    }

    public void setQcCode(String qcCode) {
        this.qcCode = qcCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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
}
