package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;

/**
 * @program: ql-dms-distribution
 * @description: 装卸公司、装卸商
 * @author: caozhixing3
 * @date: 2024-03-28 13:53
 **/
public class StevedoringMerchant implements Serializable {

    private static final long serialVersionUID = 7274400728122958189L;
    /**
     * 装卸商编码
     */
    private String merchantCode;
    /**
     * 装卸商名称
     */
    private Long merchantName;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Long getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(Long merchantName) {
        this.merchantName = merchantName;
    }
}
