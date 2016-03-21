package com.jd.bluedragon.distribution.api.response;

import java.util.Date;

/**
 * Created by yanghongqiang on 2016/3/20.
 */
public class GantryDevice {
    /**龙门架设备编号*/
    private Long machineId;

    /**龙门架设备序列号*/
    private String serialNumber;

    /**龙门架所在机构编号*/
    private Integer orgCode;

    /**龙门架所在机构名称*/
    private String orgName;

    /**龙门架所在站点编号*/
    private Integer siteCode;

    /**龙门架所在站点名称*/
    private String siteName;

    /**龙门架供应商名称*/
    private String supplier;

    /**龙门架型号*/
    private String modelNumber;

    /**龙门架类型*/
    private Integer type;

    /**备注*/
    private String mark;

    /**创建时间*/
    private Date createTime;

    /**更新时间*/
    private Date updateTime;

    /**操作人名称*/
    private String operateName;

    /**是否有效*/
    private Integer yn;

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
