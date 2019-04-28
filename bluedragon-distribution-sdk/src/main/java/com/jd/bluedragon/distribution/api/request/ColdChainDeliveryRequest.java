package com.jd.bluedragon.distribution.api.request;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainDeliveryRequest
 * @date 2019/4/9
 */
public class ColdChainDeliveryRequest extends DeliveryRequest {

    /**
     * 运输计划编码
     */
    private String transPlanCode;

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }
}
