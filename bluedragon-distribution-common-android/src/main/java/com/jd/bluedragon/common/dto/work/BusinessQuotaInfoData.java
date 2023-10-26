package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

public class BusinessQuotaInfoData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 指标的业务日期
     */
    private String businessDate;
    /**
     * 指标目标值
     */
    private String target;
    /**
     * 指标实际达成值
     */
    private String actual;

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }
}
