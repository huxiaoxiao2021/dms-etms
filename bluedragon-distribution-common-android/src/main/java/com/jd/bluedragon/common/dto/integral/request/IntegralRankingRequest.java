package com.jd.bluedragon.common.dto.integral.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.integral.constants.RankingRegionScopeEnum;
import com.jd.bluedragon.common.dto.integral.constants.RankingTimeRangeScopeEnum;

import java.io.Serializable;

/**
 * @ClassName IntegralRankingRequest
 * @Description 积分排行榜请求参数
 * @Author wyh
 * @Date 2023/3/6 14:36
 **/
public class IntegralRankingRequest extends BaseReq implements Serializable {

    private static final long serialVersionUID = 6663570873037190094L;

    /**
     * 查询地区范围
     */
    private RankingRegionScopeEnum regionScope;

    /**
     * 查询时间范围
     */
    private RankingTimeRangeScopeEnum timeRangeScope;

    private Integer pageNumber;

    private Integer pageSize;

    public RankingRegionScopeEnum getRegionScope() {
        return regionScope;
    }

    public void setRegionScope(RankingRegionScopeEnum regionScope) {
        this.regionScope = regionScope;
    }

    public RankingTimeRangeScopeEnum getTimeRangeScope() {
        return timeRangeScope;
    }

    public void setTimeRangeScope(RankingTimeRangeScopeEnum timeRangeScope) {
        this.timeRangeScope = timeRangeScope;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
