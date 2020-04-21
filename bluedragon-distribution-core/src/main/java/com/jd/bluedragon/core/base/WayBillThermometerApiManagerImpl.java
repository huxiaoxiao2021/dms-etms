package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ccmp.ctm.api.WayBillThermometerApi;
import com.jd.ccmp.ctm.dto.CommonDto;
import com.jd.ccmp.ctm.dto.WaybillRequest;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lijie
 * @date 2020/4/4 23:26
 */
@Service("wayBillThermometerApiManager")
public class WayBillThermometerApiManagerImpl implements WayBillThermometerApiManager{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WayBillThermometerApi wayBillThermometerApi;

    @JProfiler(jKey = "DMSWEB.WayBillThermometerApiManagerImpl.bindWaybillPackage", mState = {JProEnum.TP})
    @Override
    public ColdChainOperationResponse bindWaybillPackage(WaybillRequest request) {

        ColdChainOperationResponse response = new ColdChainOperationResponse();

        logger.info("包裹：{}绑定温度计：{}",request.getPackageCode(),request.getTemperDeviceID());

        CommonDto<String> result = null;
        result = wayBillThermometerApi.bindWaybillPackage(request);
        if(result == null){
            response.setCode(JdResponse.CODE_OK_NULL);
            response.setMessage(JdResponse.MESSAGE_OK_NULL);
            return response;
        }

        if(result.getCode() == 1){
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
            response.setData(result.getData());
            return response;
        }

        if(result.getCode() == 0 || result.getCode() == -1){
            logger.warn("包裹：{}绑定温度计：{}失败！",request.getPackageCode(),request.getTemperDeviceID());
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            response.setData(result.getData());
        }

        return response;
    }
}
