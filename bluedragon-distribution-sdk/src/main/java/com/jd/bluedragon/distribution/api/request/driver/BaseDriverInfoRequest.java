package com.jd.bluedragon.distribution.api.request.driver;

import com.jd.bluedragon.distribution.api.request.base.BaseRequest;

/**
 * 司机基础信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-17 15:35:33 周三
 */
public class BaseDriverInfoRequest extends BaseRequest {
    private static final long serialVersionUID = -5547919665057119562L;

    /**
     * 承运商编码
     */
    private String carrierCode;

    /**
     * 承运商性质
     */
    private Integer carrierClass;

    public BaseDriverInfoRequest() {
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public BaseDriverInfoRequest setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
        return this;
    }

    public Integer getCarrierClass() {
        return carrierClass;
    }

    public BaseDriverInfoRequest setCarrierClass(Integer carrierClass) {
        this.carrierClass = carrierClass;
        return this;
    }
}
