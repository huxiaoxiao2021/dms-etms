package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 20:07
 * @Description:
 */
public class JySendPredictAggsRequest implements Serializable {

    /**
     * 业务主键
     */
    private String bizId;

    /**
     *场地id
     */
    private Long siteId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 波次标识 1 当前波次 2 上波次
     */
    private Integer flag;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
