package com.jd.bluedragon.distribution.merchantWeightAndVolume.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/11/5 15:55
 */
public class MerchantWeightAndVolumeCondition extends BasePagerCondition {
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
     * 区域编码
     * */
    private Integer orgCode;
    /**
     * 站点编码
     * */
    private Integer siteCode;
    /**
     * 录入商家编码
     * */
    private String inputMerchantCode;
    /**
     * 录入站点编码
     * */
    private Integer inputSiteCode;
    /**
     * 登录站点编码
     * */
    private Integer loginSiteCode;
    /**
     * 登录人ERP
     * */
    private Integer loginErp;

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

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getInputMerchantCode() {
        return inputMerchantCode;
    }

    public void setInputMerchantCode(String inputMerchantCode) {
        this.inputMerchantCode = inputMerchantCode;
    }

    public Integer getInputSiteCode() {
        return inputSiteCode;
    }

    public void setInputSiteCode(Integer inputSiteCode) {
        this.inputSiteCode = inputSiteCode;
    }

    public Integer getLoginSiteCode() {
        return loginSiteCode;
    }

    public void setLoginSiteCode(Integer loginSiteCode) {
        this.loginSiteCode = loginSiteCode;
    }

    public Integer getLoginErp() {
        return loginErp;
    }

    public void setLoginErp(Integer loginErp) {
        this.loginErp = loginErp;
    }
}
