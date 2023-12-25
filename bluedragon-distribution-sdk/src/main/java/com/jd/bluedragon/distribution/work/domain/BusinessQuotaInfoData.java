package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;

public class BusinessQuotaInfoData implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 指标的业务日期
     */
    private String quotaAchieveInfo;
    /**
     * 指标目标值
     */
    private String target;
    /**
     * 指标实际达成值
     */
    private String actual;

    public String getQuotaAchieveInfo() {
        return quotaAchieveInfo;
    }

    public void setQuotaAchieveInfo(String quotaAchieveInfo) {
        this.quotaAchieveInfo = quotaAchieveInfo;
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
