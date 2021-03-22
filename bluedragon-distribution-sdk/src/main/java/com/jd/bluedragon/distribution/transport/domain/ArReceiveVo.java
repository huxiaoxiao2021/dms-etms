package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;

/**
 * 空铁提货
 */
public class ArReceiveVo {

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 操作人姓名
     */
    private Integer createUser;

    /**
     * 操作站点ID
     */
    private Integer createSiteCode;

    /**
     * 包裹/箱号
     */
    private String boxCode;

    /**
     * 1-装箱；2-原包
     */
    private Short boxingType;

    /**
     * 操作时间
     */
    private Date operateTime;


    private Date createTime;

    private Date updateTime;

    private Integer yn;

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Short getBoxingType() {
        return boxingType;
    }

    public void setBoxingType(Short boxingType) {
        this.boxingType = boxingType;
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
