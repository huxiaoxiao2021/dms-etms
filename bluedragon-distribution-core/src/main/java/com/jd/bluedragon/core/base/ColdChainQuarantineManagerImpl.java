package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.sdk.modules.quarantine.ColdChainQuarantineJsfService;
import com.jd.bluedragon.sdk.modules.quarantine.dto.BaseResult;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("coldChainQuarantineManager")
public class ColdChainQuarantineManagerImpl implements  ColdChainQuarantineManager{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ColdChainQuarantineJsfService coldChainQuarantineJsfService;

    /**
     * 判断运单在当前操作单位是否需要录入检疫证票号
     * @param waybillCode
     * @param siteCode
     * @return
     */
    public Boolean isWaybillNeedAddQuarantine(String waybillCode, Integer siteCode) {
        log.debug("查询是否需要录入检疫证票号...");
        CallerInfo info = Profiler.registerInfo("ColdChainQuarantineManagerImpl.isWaybillNeedAddQuarantine", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            if (StringUtils.isBlank(waybillCode) || siteCode == null) {
                return false;
            }
            //调jsf
            BaseResult<Boolean> result = coldChainQuarantineJsfService.isWaybillNeedAddQuarantine(waybillCode, siteCode);
            if(log.isDebugEnabled()){
                log.debug("查询是否需要录入检疫证票号.waybillCode:{},siteCode:{}.结果为:{}" ,waybillCode,siteCode, JSON.toJSONString(result));
            }
            return result.getData();
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("查询是否需要录入检疫证票号异常，waybillCode：{},siteCode:{}" ,waybillCode, siteCode, e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return false;
    }
}
