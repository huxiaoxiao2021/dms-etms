package com.jd.bluedragon.distribution.jy.service.transfer.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteMatchQo;
import com.jdl.basic.api.service.transferDp.ConfigTransferDpBusinessApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.jy.service.transfer.manager
 * @ClassName: JYTransferConfigProxy
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/18 21:27
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class JYTransferConfigProxy {

    @Autowired
    private ConfigTransferDpBusinessApi configTransferDpBusinessApi;

    @Cache(key = "DMS.JYTransferConfigProxy.queryMatchConditionRecord@args0@args1", memoryEnable = true, memoryExpiredTime = 1 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 1 * 60 * 1000)
    public ConfigTransferDpSite queryMatchConditionRecord( Integer handoverSiteCode, Integer preSortSiteCode) {
        ConfigTransferDpSiteMatchQo var1 = new ConfigTransferDpSiteMatchQo();
        var1.setHandoverSiteCode(handoverSiteCode);
        var1.setPreSortSiteCode(preSortSiteCode);
        if (log.isInfoEnabled()) {
            log.info("查询中转场地配置信息，请求参数：{}", JsonHelper.toJson(var1));
        }
        CallerInfo callerInfo = Profiler.registerInfo("DMS.WEB.JYTransferConfigProxy.queryMatchConditionRecord", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            Result<ConfigTransferDpSite> result = configTransferDpBusinessApi.queryMatchConditionRecord(var1);
            if (log.isInfoEnabled()) {
                log.info("查询中转场地配置信息，请求参数:{}，查询结果:{}", JsonHelper.toJson(var1), JsonHelper.toJson(result));
            }
            if (result == null || result.isFail()) {
                log.error("查询中转场地配置信息，请求参数:{}，查询结果:{}", JsonHelper.toJson(var1), JsonHelper.toJson(result));
            }
            return result == null? null : result.getData();
        } catch (RuntimeException e) {
            Profiler.functionError(callerInfo);
            log.error("查询中转场地配置信息异常，请求参数：{}", JsonHelper.toJson(var1), e);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }

        return null;
    }

    public boolean isMatchConfig(ConfigTransferDpSite configTransferDpSite, String waybillSign) {
        if (BusinessHelper.isDPWaybill1(waybillSign) && configTransferDpSite != null) {
            return true;
        }
        if (BusinessHelper.isDPWaybill2(waybillSign) && configTransferDpSite != null
                && configTransferDpSite.getEffectiveStartTime().before(new Date())
                && configTransferDpSite.getEffectiveStopTime().after(new Date())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否需要进行中转
     *  目前需要有中转配置的是德邦春节业务
     * @param waybillSign
     * @param createSiteCode
     * @param preSiteCode
     * @return
     */
    public boolean isNeedTransfer(String waybillSign, Integer createSiteCode, Integer preSiteCode) {
        ConfigTransferDpSiteMatchQo siteMatchQo = new ConfigTransferDpSiteMatchQo();
        siteMatchQo.setHandoverSiteCode(createSiteCode);
        siteMatchQo.setPreSortSiteCode(preSiteCode);
        ConfigTransferDpSite configTransferDpSite = this.queryMatchConditionRecord(siteMatchQo);
        return isMatchConfig(configTransferDpSite, waybillSign);
    }

}
