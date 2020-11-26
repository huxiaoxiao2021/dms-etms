package com.jd.bluedragon.common.dto.sorting.request;

import java.io.Serializable;

/**
 * 分拣 建包 task body对象
 */
public class PackSortTaskBody implements Serializable {
    private static final long serialVersionUID = -1L;

    /*
    收货单位编号（站点ID、三方物流ID）
     */
    private Integer receiveSiteCode;
    /*
    收货单位名称（站点名称、三方物流名称）
     */
    private String receiveSiteName;
    /*
    箱号
     */
    private String boxCode;
    /*
    包裹号
     */
    private String packageCode;
    /*
    业务类型
     */
    private Integer businessType;
    /*
    是否取消分拣：0-正常分拣;1-取消分拣
     */
    private Integer isCancel;
    /*
    操作人编号
     */
    private Integer userCode;
    /*
    操作人姓名
     */
    private String userName;
    /*
    分拣中心id
     */
    private Integer siteCode;
    /*
       分拣中心name
     */
    private String siteName;
    /**
     * form C# pda 报丢分拣的标识：默认 0 ；0表示正常分拣，1表示报丢分拣，C# 没有设置值，默认0
     */
    private Integer isLoss;
    /**
     * form C# pda逆向分拣标示（1：报损 2：三方七折退备件库），C# 没有设置值 默认0
     */
    private Integer featureType;
//    private String bsendCode;  C#pda端 传的是空字符串，后端没用到
    /*
    操作时间
     */
    private String operateTime;
//    private String reverseType; C#pda端有上传后端没用发下使用

    /**
     * 分拣来源字段
     */
    private Integer bizSource;

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
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

    public Integer getIsLoss() {
        return isLoss;
    }

    public void setIsLoss(Integer isLoss) {
        this.isLoss = isLoss;
    }

    public Integer getFeatureType() {
        return featureType;
    }

    public void setFeatureType(Integer featureType) {
        this.featureType = featureType;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
