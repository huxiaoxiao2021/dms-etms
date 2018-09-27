package com.jd.bluedragon.distribution.performance.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * @ClassName: PerformanceCondition
 * @Description: 加履交接单打印查询条件
 * @author: hujiping
 * @date: 2018/8/17 14:09
 */
public class PerformanceCondition extends BasePagerCondition {
    private static final long serialVersionUID = 1L;

    /** 履约单号 */
    private String performanceCode;

    /** 运单号或包裹号 */
    private String waybillorPackCode;

    public String getPerformanceCode() {
        return performanceCode;
    }

    public void setPerformanceCode(String performanceCode) {
        this.performanceCode = performanceCode;
    }

    public String getWaybillorPackCode() {
        return waybillorPackCode;
    }

    public void setWaybillorPackCode(String waybillorPackCode) {
        this.waybillorPackCode = waybillorPackCode;
    }

    @Override
    public String toString() {
        return "PerformanceCondition{" +
                "performanceCode='" + performanceCode + '\'' +
                ", waybillorPackCode='" + waybillorPackCode + '\'' +
                '}';
    }
}
