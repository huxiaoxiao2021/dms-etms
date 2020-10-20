package com.jd.bluedragon.distribution.bagException.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.box.request.BoxCollectionReportRequest;
import com.jd.bluedragon.common.dto.box.request.QueryBoxCollectionReportRequest;
import com.jd.bluedragon.common.dto.box.response.QueryBoxCollectionReportResponse;

/**
 * 集包异常举报
 *
 * @author fanggang7
 * @time 2020-09-27 17:00:52 周日
 */
public interface CollectionBagExceptionReport4PdaService {

    /**
     * 查询集包建箱是否有异常
     * @return QueryBoxCollectionReportResponse
     * @author fanggang7
     * @time 2020-09-23 21:26:39 周三
     */
    JdCResponse<QueryBoxCollectionReportResponse> queryBagCollectionHasException(QueryBoxCollectionReportRequest query);

    /**
     * 举报集包异常
     * @return QueryBoxCollectionReportResponse
     * @author fanggang7
     * @time 2020-09-23 21:26:39 周三
     */
    JdCResponse<Boolean> reportBagCollectionException(BoxCollectionReportRequest reportRequest);
}
