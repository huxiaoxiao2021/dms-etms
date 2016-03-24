package com.jd.bluedragon.distribution.gantry.domain;

import com.jd.bluedragon.distribution.api.request.GantryDeviceRequest;

import java.util.Date;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/10
 */
public class GantryDevice {

    /**龙门架设备编号*/
    private Long machineId;

    /**龙门架授权码*/
    private String token;

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
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static GantryDevice fromGantryRequest(GantryDeviceRequest request){
        GantryDevice device = new GantryDevice();
        device.setMachineId(request.getMachineId());
        device.setSerialNumber(request.getSerialNumber());
        device.setOrgCode(request.getOrgCode());
        device.setOrgName(request.getOrgName());
        device.setSiteCode(request.getSiteCode());
        device.setSiteName(request.getSiteName());
        device.setSupplier(request.getSupplier());
        device.setModelNumber(request.getModelNumber());
        device.setType(request.getType());
        device.setMark(request.getMark());
        device.setCreateTime(request.getCreateTime());
        device.setUpdateTime(request.getUpdateTime());
        device.setOperateName(request.getOperateName());
        return device;
    }
}
