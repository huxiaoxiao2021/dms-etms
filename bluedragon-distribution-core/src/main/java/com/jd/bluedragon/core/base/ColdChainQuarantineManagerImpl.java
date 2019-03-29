package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.sdk.modules.quarantine.ColdChainQuarantineJsfService;
import com.jd.bluedragon.sdk.modules.quarantine.dto.BaseResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("coldChainQuarantineManager")
public class ColdChainQuarantineManagerImpl implements  ColdChainQuarantineManager{
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ColdChainQuarantineJsfService coldChainQuarantineJsfService;

    /**
     * 判断运单在当前操作单位是否需要录入检疫证票号
     * @param waybillCode
     * @param siteCode
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "ColdChainQuarantineManagerImpl.isWaybillNeedAddQuarantine", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean isWaybillNeedAddQuarantine(String waybillCode, Integer siteCode) {
        logger.info("查询是否需要录入检疫证票号...");
        try {
            if (StringUtils.isBlank(waybillCode) || siteCode == null) {
                return false;
            }
            //调jsf
            BaseResult<Boolean> result = coldChainQuarantineJsfService.isWaybillNeedAddQuarantine(waybillCode, siteCode);
            logger.info("查询是否需要录入检疫证票号.waybillCode:" + waybillCode + ",siteCode:" + siteCode + ".结果为:" + JSON.toJSONString(result));
            return result.getData();
        } catch (Exception e) {
            logger.error("查询是否需要录入检疫证票号异常，waybillCode：" + waybillCode + ",siteCode:" + siteCode, e);
        }
        return false;
    }
}
