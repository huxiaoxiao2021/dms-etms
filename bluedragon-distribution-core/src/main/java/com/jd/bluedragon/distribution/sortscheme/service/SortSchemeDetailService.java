package com.jd.bluedragon.distribution.sortscheme.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public interface SortSchemeDetailService {

    SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetail(SortSchemeDetailRequest request, String url);

    SortSchemeDetailResponse<List<String>> findMixSiteBySchemeId2(SortSchemeDetailRequest request, String url);

    SortSchemeDetailResponse<List<String>> findChuteCodeBySchemeId2(SortSchemeDetailRequest request, String url);

    List<SortSchemeDetail> parseSortSchemeDetail2(Sheet file) throws DataFormatException, Exception;

    SortSchemeDetailResponse<List<SortSchemeDetail>> findBySchemeId2(SortSchemeDetailRequest request, String url);

    HSSFWorkbook createWorkbook(List<SortSchemeDetail> sortSchemeDetailList);
}
