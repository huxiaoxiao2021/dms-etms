package com.jd.bluedragon.distribution.merchantWeightAndVolume.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/11/5 14:35
 */
public class MerchantWeightAndVolumeDetail extends DbEntity {
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
     * 操作省区编码
     * */
    private String operateProvinceAgencyCode;
    /**
     * 操作省区名称
     * */
    private String operateProvinceAgencyName;
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
     * 操作省区编码
     * */
    private String operateProvinceAgencyCode;
    /**
     * 操作省区名称
     * */
    private String operateProvinceAgencyName;
    /**
     * 操作枢纽编码
     */
    private String operateAreaHubCode;
    /**
     * 操作枢纽名称
     */
    private String operateAreaHubName;

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

    public String getOperateProvinceAgencyCode() {
        return operateProvinceAgencyCode;
    }

    public void setOperateProvinceAgencyCode(String operateProvinceAgencyCode) {
        this.operateProvinceAgencyCode = operateProvinceAgencyCode;
    }

    public String getOperateProvinceAgencyName() {
        return operateProvinceAgencyName;
    }

    public void setOperateProvinceAgencyName(String operateProvinceAgencyName) {
        this.operateProvinceAgencyName = operateProvinceAgencyName;
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

    public String getOperateProvinceAgencyCode() {
        return operateProvinceAgencyCode;
    }

    public void setOperateProvinceAgencyCode(String operateProvinceAgencyCode) {
        this.operateProvinceAgencyCode = operateProvinceAgencyCode;
    }

    public String getOperateProvinceAgencyName() {
        return operateProvinceAgencyName;
    }

    public void setOperateProvinceAgencyName(String operateProvinceAgencyName) {
        this.operateProvinceAgencyName = operateProvinceAgencyName;
    }

    public String getOperateAreaHubCode() {
        return operateAreaHubCode;
    }

    public void setOperateAreaHubCode(String operateAreaHubCode) {
        this.operateAreaHubCode = operateAreaHubCode;
    }

    public String getOperateAreaHubName() {
        return operateAreaHubName;
    }

    public void setOperateAreaHubName(String operateAreaHubName) {
        this.operateAreaHubName = operateAreaHubName;
    }
}
