package com.jd.bluedragon.distribution.sortscheme.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

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

}
