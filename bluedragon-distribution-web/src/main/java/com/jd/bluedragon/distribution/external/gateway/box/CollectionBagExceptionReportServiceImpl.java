package com.jd.bluedragon.distribution.external.gateway.box;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.box.request.BoxCollectionReportRequest;
import com.jd.bluedragon.common.dto.box.request.QueryBoxCollectionReportRequest;
import com.jd.bluedragon.common.dto.box.response.QueryBoxCollectionReportResponse;
import com.jd.bluedragon.distribution.bagException.service.CollectionBagExceptionReport4PdaService;
import com.jd.bluedragon.external.gateway.box.CollectionBagExceptionReportGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 集包异常举报接口
 *
 * @author fanggang7
 * @time 2020-09-23 21:22:12 周三
 */
@Service("collectionBagExceptionReportGatewayService")
@Slf4j
public class CollectionBagExceptionReportServiceImpl implements CollectionBagExceptionReportGatewayService {

    @Autowired
    private CollectionBagExceptionReport4PdaService collectionBagExceptionReport4PdaService;

    /**
     * 查询集包建箱是否有异常
     *
     * @param query 请求参数
     * @return QueryBoxCollectionReportResponse
     * @author fanggang7
     * @time 2020-09-23 21:26:39 周三
     */
    @Override
    public JdCResponse<QueryBoxCollectionReportResponse> queryBagCollectionHasException(QueryBoxCollectionReportRequest query) {
        return collectionBagExceptionReport4PdaService.queryBagCollectionHasException(query);
    }

    /**
     * 举报集包异常
     *
     * @param reportRequest 请求参数
     * @return QueryBoxCollectionReportResponse
     * @author fanggang7
     * @time 2020-09-23 21:26:39 周三
     */
    @Override
    public JdCResponse<Boolean> reportBagCollectionException(BoxCollectionReportRequest reportRequest) {
        return collectionBagExceptionReport4PdaService.reportBagCollectionException(reportRequest);
    }
}
