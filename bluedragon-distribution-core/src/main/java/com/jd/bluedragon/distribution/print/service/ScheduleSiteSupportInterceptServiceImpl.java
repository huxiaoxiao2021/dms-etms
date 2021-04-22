package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.ScheduleSiteSupportInterceptHandler;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/21 16:12
 */
@Service("scheduleSiteSupportInterceptService")
public class ScheduleSiteSupportInterceptServiceImpl implements ScheduleSiteSupportInterceptService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleSiteSupportInterceptHandler.class);


    @Autowired
    private BaseMinorManager baseMinorManager;

    /**
     * 预分拣目的站点滑道信息校验
     * @param prepareSiteCode  预分拣目的站点
     * @param startSiteCode  预分拣始发站点
     * @return
     */
    @Override
    public InvokeResult<String> checkCrossInfo(String waybillSign, String sendPay , String waybillCode, Integer prepareSiteCode, Integer startSiteCode) {
        InvokeResult<String> result = new InvokeResult<>();
        try {
            //获取始发道口类型
            Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybillSign, sendPay);
            BaseDmsStore baseDmsStore = new BaseDmsStore();
            JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore,prepareSiteCode,startSiteCode,originalCrossType);
            if(!jdResult.isSucceed()) {
                LOGGER.warn("com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo-->预分拣站点滑道信息返回失败 :{}，运单号:{}", jdResult.getMessage(),waybillCode);
                result.customMessage(JdResponse.CODE_TARGET_SITE_NO_ROUTE_CONFIRM, JdResponse.MESSAGE_TARGET_SITE_NO_ROUTE_CONFIRM);
                return result;
            }

            if(jdResult.getData()==null || StringUtils.isEmpty(jdResult.getData().getDestinationCrossCode())){
                LOGGER.warn("com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo-->预分拣站点没有滑道信息 :{}，运单号:{}", jdResult.getMessage(),waybillCode);
                result.customMessage(JdResponse.CODE_TARGET_SITE_NO_ROUTE_CONFIRM, JdResponse.MESSAGE_TARGET_SITE_NO_ROUTE_CONFIRM);
                return result;
            }
        }catch (Exception e){
            LOGGER.error("com.jd.bluedragon.distribution.print.service.ScheduleSiteSupportInterceptServiceImpl.checkCrossInfo-->获取预分拣滑道信息获取异常 waybillCode：{},prepareSiteCode:{},startSiteCode:{}",
                    waybillCode,prepareSiteCode,startSiteCode);
            result.customMessage(JdResponse.CODE_BASIC_SITE_CODE_ERROR, JdResponse.MESSAGE_BASIC_SITE_CODE_ERRPR);
            return result;
        }
        return result;
    }
}
    
