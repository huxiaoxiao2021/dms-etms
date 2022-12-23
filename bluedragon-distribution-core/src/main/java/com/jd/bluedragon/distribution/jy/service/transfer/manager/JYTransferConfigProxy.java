package com.jd.bluedragon.distribution.jy.service.transfer.manager;

import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
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

    public ConfigTransferDpSite queryMatchConditionRecord(ConfigTransferDpSiteMatchQo var1) {
         if (log.isInfoEnabled()) {
             log.info("查询中转场地配置信息，请求参数：{}", JsonHelper.toJson(var1));
         }
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
            log.error("查询中转场地配置信息异常，请求参数：{}", JsonHelper.toJson(var1), e);
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

}
