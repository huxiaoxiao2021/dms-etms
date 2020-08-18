package com.jd.bluedragon.distribution.ver.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.log.impl.LogEngineImpl;
import com.jd.bluedragon.distribution.ver.service.SortingCheckStatisticsService;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SortingCheckStatisticsServiceImpl implements SortingCheckStatisticsService {


    @Autowired
    LogEngine logEngine;

    public void addSortingCheckStatisticsLog(PdaOperateRequest pdaOperateRequest, SortingJsfResponse sortingJsfResponse) {

        //日志
        JSONObject request = new JSONObject();
        request.put("operatorCode", pdaOperateRequest.getOperateUserCode());
        request.put("pakcageCode", pdaOperateRequest.getPackageCode());
        request.put("boxCode", pdaOperateRequest.getBoxCode());
        request.put("siteCode", pdaOperateRequest.getCreateSiteCode());
        request.put("siteName", pdaOperateRequest.getCreateSiteName());
        request.put("operatorName", pdaOperateRequest.getOperateUserName());

        request.put("responseCode", sortingJsfResponse.getCode());
        request.put("responseMessage", sortingJsfResponse.getMessage());

        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_VERINWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_VER_FILTER_STATISTICS);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_VER_FILTER_STATISTICS);
        businessLogProfiler.setTimeStamp(new Date().getTime());
        businessLogProfiler.setRequestTime(new Date().getTime());
        businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));

        logEngine.addLog(businessLogProfiler);

    }


}
