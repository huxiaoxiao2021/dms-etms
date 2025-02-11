package com.jd.bluedragon.distribution.fBarCode.domain;

import java.util.Date;

/**
 * Created by yanghongqiang on 14-8-1.
 */
public class FBarCode {
    /** 全局唯一ID */
    private Long id;

    /** 箱号 */
    private String code;

    /** 箱号集合 */
    private String codes;

    /** 箱号类型 */
    private String type;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;

    /** 最后操作人编号 */
    private Integer updateUserCode;

    /** 最后操作人 */
    private String updateUser;

    /** 最后修改时间 */
    private Date updateTime;

    /** 打印次数 */
    private Integer times;

    /** 打印数量 */
    private Integer quantity;

    /** 状态 '0' 新增 '1’打印完毕 */
    private Integer status;

    private String statuses;

    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;


    public void setStatus(int status) {
        this.status = status;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
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

    public Integer getTimes() {
        return this.times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatuses() {
        return this.statuses;
    }

    public void setStatuses(String statuses) {
        this.statuses = statuses;
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCodes() {
        return this.codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        FBarCode other = (FBarCode) obj;
        if (this.code == null) {
            return this.code == other.code;
        }

        return this.code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return 360 + this.code.hashCode();
    }
}
