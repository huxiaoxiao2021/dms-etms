package com.jd.bluedragon.distribution.waybill.domain;

import java.util.Date;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/4/28
 */
public class FreshWaybill {
    /**主键*/
    private Long id;
    /**包裹号*/
    private String packageCode;
    /**保温箱型号 1XPS材质24L/2折叠VIP材质15L/3折叠VIP材质25L/4折叠VIP材质55L*/
    private Integer boxType;
    /**冰板类型 10℃冷藏冰板/2-12℃冷冻冰板*/
    private Integer slabType;
    /**冰板数量*/
    private Integer slabNum;
    /**商品温度*/
    private Double packageTemp;
    /**冰板温度*/
    private Double slabTemp;
    /**创建时间*/
    private Date createTime;
    /**修改时间*/
    private Date updateTime;
    /**操作人编码*/
    private Integer userCode;
    /**操作人名称*/
    private String userName;
    /**操作人分拣中心*/
    private Integer userDmsId;
    /**操作人所在分拣中心名称*/
    private String userDmsName;
    /**操作人所属机构*/
    private Integer userOrgId;
    /**操作人所属机构名称*/
    private String userOrgName;

    /**是否有效*/
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getBoxType() {
        return boxType;
    }

    public void setBoxType(Integer boxType) {
        this.boxType = boxType;
    }

    public Integer getSlabType() {
        return slabType;
    }

    public void setSlabType(Integer slabType) {
        this.slabType = slabType;
    }

    public Integer getSlabNum() {
        return slabNum;
    }

    public void setSlabNum(Integer slabNum) {
        this.slabNum = slabNum;
    }

    public Double getPackageTemp() {
        return packageTemp;
    }

    public void setPackageTemp(Double packageTemp) {
        this.packageTemp = packageTemp;
    }

    public Double getSlabTemp() {
        return slabTemp;
    }

    public void setSlabTemp(Double slabTemp) {
        this.slabTemp = slabTemp;
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

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserDmsId() {
        return userDmsId;
    }

    public void setUserDmsId(Integer userDmsId) {
        this.userDmsId = userDmsId;
    }

    public String getUserDmsName() {
        return userDmsName;
    }

    public void setUserDmsName(String userDmsName) {
        this.userDmsName = userDmsName;
    }

    public Integer getUserOrgId() {
        return userOrgId;
    }

    public void setUserOrgId(Integer userOrgId) {
        this.userOrgId = userOrgId;
    }

    public String getUserOrgName() {
        return userOrgName;
    }

    public void setUserOrgName(String userOrgName) {
        this.userOrgName = userOrgName;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}