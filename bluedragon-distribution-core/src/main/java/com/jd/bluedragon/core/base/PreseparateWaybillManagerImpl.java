package com.jd.bluedragon.core.base;

import com.jd.bluedragon.preseparate.jsf.CommonOrderServiceJSF;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.preseparate.vo.PsOrderSeparateVo;
import com.jd.preseparate.vo.external.ExternalOrderDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by wangtingwei on 2015/10/28.
 */
@Service("preseparateWaybillManager")
public class PreseparateWaybillManagerImpl implements PreseparateWaybillManager {

    private static final Log logger= LogFactory.getLog(PreseparateWaybillManagerImpl.class);

    @Autowired
    @Qualifier("preseparateOrderService")
    private CommonOrderServiceJSF preseparateOrderService;

    @Override
    public Integer getPreseparateSiteId(String waybillCode) throws Exception {
        Integer siteId=null;
        if(SerialRuleUtil.isMatchWaybillNo(waybillCode)){
            PsOrderSeparateVo domain=preseparateOrderService.getPreSeparateOrderByOrderId(waybillCode.trim());
            if(null!=domain){
                if(logger.isDebugEnabled()){
                    logger.debug(JsonHelper.toJson(domain));
                }
                siteId=domain.getPartnerId();
            }
        }else {

            ExternalOrderDto domain = preseparateOrderService.getPreSeparateExternalByOrderId(waybillCode.trim());
            if(null!=domain){
                if(logger.isDebugEnabled()){
                    logger.debug(JsonHelper.toJson(domain));
                }
                siteId=domain.getPartnerId();
            }
        }
        return siteId;
    }
}
