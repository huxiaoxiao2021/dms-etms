package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.merchant.api.common.dto.Result;
import com.jd.merchant.api.staging.dto.StagingCheckReq;
import com.jd.merchant.api.staging.ws.StagingServiceWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: bluedragon-distribution
 * @description: 暂存预约校验
 * @author: wuming
 * @create: 2021-01-25 11:01
 */
@Service("waybillStagingCheckManager")
public class WaybillStagingCheckManagerImpl implements WaybillStagingCheckManager {

    private final Logger log = LoggerFactory.getLogger(WaybillStagingCheckManagerImpl.class);

    @Resource
    private StagingServiceWS stagingServiceWS;

    @JProfiler(jKey = "DMSCORE.WaybillStagingCheckManagerImpl.stagingCheck", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public boolean stagingCheck(String packageCode, Integer operateSiteCode) {
        try {
            StagingCheckReq req = new StagingCheckReq();
            req.setCurrentSiteCode(operateSiteCode);
            req.setPackageCode(packageCode);
            Result<Boolean> result = stagingServiceWS.stagingCheck(req);
            if (null != result && Constants.STAGING_CHECK_SUCCESS_CODE.equals(result.getCode()) && result.getData()) {
                return true;
            }
        } catch (Exception e) {
            log.error("WaybillStagingCheckManagerImpl.stagingCheck.error-packageCode={}", packageCode, e);
        }
        return false;
    }
}
