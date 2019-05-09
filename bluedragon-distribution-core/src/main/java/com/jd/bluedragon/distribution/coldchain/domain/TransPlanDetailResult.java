package com.jd.bluedragon.distribution.coldchain.domain;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName TransPlanResult
 * @date 2019/4/8
 */
public class TransPlanDetailResult {

    /**
     * 运输计划编码
     */
    private String transPlanCode;

    /**
     * 运单号列表
     */
    private List<String> waybills;

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }

    public List<String> getWaybills() {
        return waybills;
    }

    public void setWaybills(List<String> waybills) {
        this.waybills = waybills;
    }
}
