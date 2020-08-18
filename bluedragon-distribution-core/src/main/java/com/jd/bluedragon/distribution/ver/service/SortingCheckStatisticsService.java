package com.jd.bluedragon.distribution.ver.service;

import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;


/**
 * 记录日志
 */
public interface SortingCheckStatisticsService {

    void addSortingCheckStatisticsLog(PdaOperateRequest pdaOperateRequest, SortingJsfResponse sortingJsfResponse);


}
