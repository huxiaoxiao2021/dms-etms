package com.jd.bluedragon.distribution.sortscheme.service;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.utils.ExportByPOIUtil;
import com.jd.bluedragon.utils.RestHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Service("sortSchemeService")
public class SortSchemeServiceImpl implements SortSchemeService {

    @Override
    public SortSchemeResponse pageQuerySortScheme(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse<Pager<List<SortScheme>>>>() {
                });
    }

    @Override
    public SortSchemeResponse addSortScheme2(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse<String>>() {
                });
    }

    @Override
    public SortSchemeResponse<String> deleteById2(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse<String>>() {
                });
    }

    @Override
    public SortSchemeResponse<String> disableById2(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse<String>>() {
                });
    }

    @Override
    public SortSchemeResponse<String> ableById2(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse<String>>() {
                });
    }

    @Override
    public SortSchemeResponse<SortScheme> findById2(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse<SortScheme>>() {
                });
    }

    @Override
    public SortSchemeResponse importSortSchemeDetail2(SortSchemeRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeResponse>() {
                });
    }

}






























