package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.ql.erp.dto.forcedelivery.price.WaybillPriceRequestDto;
import com.jd.ql.erp.dto.forcedelivery.price.WaybillPriceResponseDto;
import com.jd.ql.erp.jsf.WaybillForceDeliveryApi;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/25 14:00
 * @Description:
 */
@Service
public class WaybillForceDeliveryApiManagerImpl implements WaybillForceDeliveryApiManager{

    private final static Logger log = LoggerFactory.getLogger(WaybillForceDeliveryApiManagerImpl.class);


    @Autowired
    private WaybillForceDeliveryApi waybillForceDeliveryApiService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "WaybillForceDeliveryApiManagerImpl.forceDeliveryCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean forceDeliveryCheck(WaybillPriceRequestDto requestDto) {
        log.info("forceDeliveryCheck -妥投检验接口入参-{}", JSON.toJSONString(requestDto));
        try{
            CommonDto<WaybillPriceResponseDto> responseDtoCommonDto = waybillForceDeliveryApiService.forceDeliveryCheck(requestDto);
            log.info("forceDeliveryCheck -妥投检验接口出参-{}", JSON.toJSONString(responseDtoCommonDto));
            if(responseDtoCommonDto != null && CommonDto.CODE_SUCCESS == responseDtoCommonDto.getCode()){
                return true;
            }
        }catch (Exception e){
            log.error("forceDeliveryCheck -妥投检验接口异常! 入参-{}，异常信息-{}",JSON.toJSONString(requestDto),e.getMessage(),e);
        }
        return false;
    }
}
