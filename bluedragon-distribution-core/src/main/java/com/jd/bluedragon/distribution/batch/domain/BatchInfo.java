package com.jd.bluedragon.distribution.batch.domain;

import java.util.Date;

/**
 * Created by yanghongqiang on 14-8-1.
 */
public class BatchInfo {
    /**
     * 全局唯一ID
     */
    private Long id;

    /**
     * 波次号
     */
    private String batchCode;

    /**
     * 创建站点编号
     */
    private Integer createSiteCode;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人编号
     */
    private Integer createUserCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后操作人编号
     */
    private Integer updateUserCode;

    /**
     * 最后操作人
     */
    private String updateUser;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 是否删除 '0' 删除 '1' 使用
     */
    private Integer yn;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchCode() {
        return this.batchCode;
    }

    public void setBatchCode(String code) {
        batchCode = code;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    public Date getCreateTime() {
        return this.createTime == null ? null : (Date) this.createTime.clone();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? null : (Date) createTime.clone();
    }

    public Integer getUpdateUserCode() {
        return this.updateUserCode;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public Date getUpdateTime() {
        return this.updateTime == null ? null : (Date) this.updateTime.clone();
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? null : (Date) updateTime.clone();
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        BatchInfo other = (BatchInfo) obj;
        if (this.batchCode == null) {
            return this.batchCode == other.batchCode;
        }

        return this.batchCode.equals(other.batchCode);
    }

    @Override
    public int hashCode() {
        return 360 + this.batchCode.hashCode();
    }
}
