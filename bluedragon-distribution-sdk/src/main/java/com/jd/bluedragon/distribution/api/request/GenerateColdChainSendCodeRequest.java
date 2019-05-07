package com.jd.bluedragon.distribution.api.request;

/**
 * @author lixin39
 * @Description 生成冷链发货批次号
 * @ClassName GenerateColdChainSendCodeRequest
 * @date 2019/3/30
 */
public class GenerateColdChainSendCodeRequest extends GenerateSendCodeRequest {

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
