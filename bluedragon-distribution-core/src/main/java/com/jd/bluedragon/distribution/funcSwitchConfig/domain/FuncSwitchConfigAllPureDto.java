package com.jd.bluedragon.distribution.funcSwitchConfig.domain;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/16 22:58
 */
public class FuncSwitchConfigAllPureDto {
    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 始发站点
     */
    private Integer createSiteCode;

    /**
     * 青龙业主号
     */
    private String customerCode;

    /**
     * 运单标位
     */
    private String waybillSign;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }
}
    
