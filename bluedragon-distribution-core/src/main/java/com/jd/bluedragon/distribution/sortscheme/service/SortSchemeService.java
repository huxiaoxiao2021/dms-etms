package com.jd.bluedragon.distribution.sortscheme.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;

import java.util.List;

/**
 * Created by yangbo7 on 2016/6/22.
 */

public interface SortSchemeService {

    SortSchemeResponse<Pager<List<SortScheme>>> pageQuerySortScheme(SortSchemeRequest request, String url);

    SortSchemeResponse<String> addSortScheme2(SortSchemeRequest request, String url);

    SortSchemeResponse<String> deleteById2(SortSchemeRequest request, String url);

    SortSchemeResponse<String> disableById2(SortSchemeRequest request, String url);

    SortSchemeResponse<String> ableById2(SortSchemeRequest request, String url);

    SortSchemeResponse<SortScheme> findById2(SortSchemeRequest request, String url);

    SortSchemeResponse importSortSchemeDetail2(SortSchemeRequest request, String url);

    SortSchemeResponse<String> ableAutoSendById(SortSchemeRequest request, String url);

    SortSchemeResponse<String> disableAutoSendById(SortSchemeRequest request, String url);

    SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetail(SortSchemeDetailRequest request, String url);

    SortSchemeResponse<List<SortScheme>> queryBySiteCode(SortSchemeRequest request,String url);

}
