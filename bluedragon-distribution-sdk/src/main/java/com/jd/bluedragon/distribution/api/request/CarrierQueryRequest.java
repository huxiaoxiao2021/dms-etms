package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

/**
 * 承运商查询条件
 *
 * @author hujiping
 * @date 2021/6/23 9:49 下午
 */
public class CarrierQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 承运商id
     */
    private String carrierId;
    /**
     * 承运商编码
     */
    private String carrierCode;
    /**
     * 承运商名称
     */
    private String carrierName;
    /**
     * 承运商类型
     */
    private Integer carrierType;

    public String getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Integer getCarrierType() {
        return carrierType;
    }

    public void setCarrierType(Integer carrierType) {
        this.carrierType = carrierType;
    }
}
