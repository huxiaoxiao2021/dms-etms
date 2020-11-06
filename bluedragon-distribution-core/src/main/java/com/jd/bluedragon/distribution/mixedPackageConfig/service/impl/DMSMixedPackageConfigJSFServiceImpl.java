package com.jd.bluedragon.distribution.mixedPackageConfig.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.TransportTypeEnum;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.DMSMixedPackageConfigJSFService;
import com.jd.bluedragon.distribution.mixedPackageConfig.service.MixedPackageConfigService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 集包地对外接口实现
 *
 * @author: hujiping
 * @date: 2020/10/28 16:36
 */
@Service("dmsMixedPackageConfigJSFService")
public class DMSMixedPackageConfigJSFServiceImpl implements DMSMixedPackageConfigJSFService {

    private static final Logger logger = LoggerFactory.getLogger(DMSMixedPackageConfigJSFServiceImpl.class);

    @Autowired
    private MixedPackageConfigService mixedPackageConfigService;

    /**
     * 查询面单集包地
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.CORE.DMSMixedPackageConfigJSFServiceImpl.queryMixedSiteCodeForPrint",
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<MixedSite> queryMixedSiteCodeForPrint(PrintQueryRequest request) {
        InvokeResult<MixedSite> result = new InvokeResult<MixedSite>();
        result.success();
        if(request == null || request.getOriginalDmsCode() == null
                || request.getDestinationDmsCode() == null
                || request.getTransportType() == null
                || !TransportTypeEnum.transportTypeMap.containsKey(request.getTransportType())){
            logger.warn("参数错误!");
            result.parameterError("参数错误");
            return result;
        }
        MixedSite mixedSite = mixedPackageConfigService.queryMixedSiteCodeForPrint(request);
        if(mixedSite == null){
            logger.warn("查询集包地为空，入参：【{}】", JsonHelper.toJsonMs(request));
            result.customMessage(600,"未维护混集包配置!");
            return result;
        }
        result.setData(mixedSite);
        return result;
    }
}
