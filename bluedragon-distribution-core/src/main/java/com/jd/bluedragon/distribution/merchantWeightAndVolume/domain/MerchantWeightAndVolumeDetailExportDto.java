package com.jd.bluedragon.distribution.merchantWeightAndVolume.domain;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/9 11:25
 */
public class MerchantWeightAndVolumeDetailExportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商家ID
     * */
    private Integer merchantId;
    /**
     * 商家编码
     * */
    private String merchantCode;
    /**
     * 商家名称
     * */
    private String merchantName;
    /**
     * 操作区域编码
     * */
    private Integer operateOrgCode;
    /**
     * 操作区域名称
     * */
    private String operateOrgName;
    /**
     * 操作站点编码
     * */
    private Integer operateSiteCode;
    /**
     * 操作站点名称
     * */
    private String operateSiteName;
    /**
     * 创建人ERP
     * */
    private String createErp;
    /**
     * 创建人
     * */
    private String createUserName;

    /**
     * 创建时间
     */
    private String createTime;

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Integer getOperateOrgCode() {
        return operateOrgCode;
    }

    public void setOperateOrgCode(Integer operateOrgCode) {
        this.operateOrgCode = operateOrgCode;
    }

    public String getOperateOrgName() {
        return operateOrgName;
    }

    public void setOperateOrgName(String operateOrgName) {
        this.operateOrgName = operateOrgName;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
    
