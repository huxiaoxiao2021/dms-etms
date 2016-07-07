package com.jd.bluedragon.distribution.sortscheme.service;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.utils.RestHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Service("sortSchemeDetailService")
public class SortSchemeDetailServiceImpl implements SortSchemeDetailService {

    @Override
    public SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetail(SortSchemeDetailRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>>>() {
                });
    }

}
