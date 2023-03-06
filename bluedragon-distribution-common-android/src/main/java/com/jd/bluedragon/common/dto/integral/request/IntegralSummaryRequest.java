package com.jd.bluedragon.common.dto.integral.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.integral.constants.RankingTimeRangeScopeEnum;

import java.io.Serializable;

/**
 * @ClassName IntegralSummaryRequest
 * @Description 积分汇总页查询参数。层级下钻
 * @Author wyh
 * @Date 2023/3/6 16:01
 **/
public class IntegralSummaryRequest extends BaseReq implements Serializable {

    private static final long serialVersionUID = -6051001368608203945L;

    /**
     * 查询时间范围
     */
    private RankingTimeRangeScopeEnum timeRangeScope;

    public RankingTimeRangeScopeEnum getTimeRangeScope() {
        return timeRangeScope;
    }

    public void setTimeRangeScope(RankingTimeRangeScopeEnum timeRangeScope) {
        this.timeRangeScope = timeRangeScope;
    }
}
