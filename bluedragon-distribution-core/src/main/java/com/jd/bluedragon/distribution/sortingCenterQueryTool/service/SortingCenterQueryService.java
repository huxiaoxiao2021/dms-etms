package com.jd.bluedragon.distribution.sortingCenterQueryTool.service;

import com.jd.bluedragon.distribution.api.request.SortingCenterQueryRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2016/10/17.
 */
public interface SortingCenterQueryService {

    InvokeResult countNumFromThreeTables (SortingCenterQueryRequest request, String url);

    List<Object> queryDetailsFromThreeTables (Map<String,Object> request, String url);
}
