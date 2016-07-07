package com.jd.bluedragon.distribution.sortscheme.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;

import java.util.List;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public interface SortSchemeDetailService {

    SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetail(SortSchemeDetailRequest request, String url);
}
