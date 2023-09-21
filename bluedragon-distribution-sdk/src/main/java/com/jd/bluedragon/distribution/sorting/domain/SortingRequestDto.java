package com.jd.bluedragon.distribution.sorting.domain;

import java.io.Serializable;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.sorting.domain
 * @Description:
 * @date Date : 2023年09月19日 18:34
 */
public class SortingRequestDto implements Serializable {

    private static final long serialVersionUID = 4831337335421636691L;
    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 创建站点编号
     */
    private Integer createSiteCode;

    /**
     * 创建站点名称
     */
    private String createSiteName;

    /**
     * 接收站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 接收站点名称
     */
    private String receiveSiteName;

    /**
     * 创建人编号
     */
    private Integer createUserCode;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private String operateTime;
    /**
     * 是否需要校验绑定集包袋编号
     * 1是，2否
     */
    private Integer needBindMaterialFlag;
    /**
     * 集包袋编号
     */
    private String materialCode;
    /**
     * 业务类型
     */
    private Integer businessType;
    /**
     *  是否取消分拣：0-正常分拣;1-取消分拣
     */
    private Integer isCancel;
    /**
     * form C# pda 报丢分拣的标识：默认 0 ；0表示正常分拣，1表示报丢分拣，C# 没有设置值，默认0
     */
    private Integer isLoss;
    /**
     * form C# pda逆向分拣标示（1：报损 2：三方七折退备件库），C# 没有设置值 默认0
     */
    private Integer featureType;
    //======================================

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getNeedBindMaterialFlag() {
        return needBindMaterialFlag;
    }

    public void setNeedBindMaterialFlag(Integer needBindMaterialFlag) {
        this.needBindMaterialFlag = needBindMaterialFlag;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
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
}
