package com.jd.bluedragon.core.jsf.collectpackage;

import com.jd.bluedragon.common.dto.collectpackage.request.StatisticsUnderFlowQueryReq;
import com.jd.bluedragon.core.jsf.collectpackage.dto.ListTaskStatisticQueryDto;
import com.jd.bluedragon.core.jsf.collectpackage.dto.StatisticsUnderTaskQueryDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.udata.query.api.dto.ApiDataQueryRequest;
import com.jd.udata.query.api.dto.ApiDataQueryResult;
import com.jd.udata.query.api.dto.ApiTopologyQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
/**
 * @author weixiaofeng12
 * @description 查询集装统计数据能力
 * @date 2024-03-11 17:56
 */
@Service("collectLoadingManger")
@Slf4j
public class CollectLoadingMangerImpl extends CollectPackageMangerImpl{

    @Override
    public ApiDataQueryRequest buildListTaskStatisticUDataQueryParams(ListTaskStatisticQueryDto dto) {
        ApiDataQueryRequest apiDataQueryRequest = super.buildListTaskStatisticUDataQueryParams(dto);
        apiDataQueryRequest.setApiName("uat-CollectPackageAgg");
        return apiDataQueryRequest;
    }

    @Override
    public ApiTopologyQueryResult callUdataQueryAPi(ApiDataQueryRequest queryParams) {
        log.info("collectLoadingManger invoke udata api request :{}", JsonHelper.toJson(queryParams));
        ApiTopologyQueryResult apiTopologyQueryResult = null;
        try {
            ApiDataQueryResult apiDataQueryResult = apiQueryService.apiDataQuery(queryParams);
            converNormalRs2TopologyRs(apiTopologyQueryResult, apiDataQueryResult);
            return apiTopologyQueryResult;
        } catch (Exception e) {
            log.error("collectLoadingManger invoke udata api exception",e);
        }
        log.info("collectLoadingManger invoke udata api response :{}", JsonHelper.toJson(apiTopologyQueryResult));
        return apiTopologyQueryResult;
    }

    @Override
    public ApiDataQueryRequest buildQueryTaskFlowStatisticQueryParams(StatisticsUnderTaskQueryDto dto) {
        ApiDataQueryRequest apiDataQueryRequest = super.buildQueryTaskFlowStatisticQueryParams(dto);
        apiDataQueryRequest.setApiName("uat-CollectPackageAggFlow");
        return apiDataQueryRequest;
    }

    @Override
    public ApiDataQueryRequest buildListPackageUnderFlowUDataQueryParams(StatisticsUnderFlowQueryReq request) {
        ApiDataQueryRequest apiDataQueryRequest = super.buildListPackageUnderFlowUDataQueryParams(request);
        apiDataQueryRequest.setApiName("uat-CollectPackageDetail");
        apiDataQueryRequest.setCurrPage(request.getPageNo());
        apiDataQueryRequest.setPageSize(request.getPageSize());

        Map<String, Object> params = new HashMap<>(8);
        params.put("bizId", request.getBizId());
        params.put("endSiteId",request.getEndSiteId());
        apiDataQueryRequest.setParams(params);
        return apiDataQueryRequest;
    }

    private static void converNormalRs2TopologyRs(ApiTopologyQueryResult apiTopologyQueryResult, ApiDataQueryResult apiDataQueryResult) {
        apiTopologyQueryResult.setCode(apiDataQueryResult.getCode());
        apiTopologyQueryResult.setStatus(apiDataQueryResult.getStatus());
        apiTopologyQueryResult.setDataList(apiDataQueryResult.getData());
    }
}
