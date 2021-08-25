package com.jd.bluedragon.distribution.alliance;

import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("allianceBusiDeliveryServiceFoTransfer")
public class AllianceBusiDeliveryServiceImpl implements AllianceBusiDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(AllianceBusiDeliveryServiceImpl.class);

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;


    @Override
    public InvokeResult<Boolean> checkAllianceMoney(String waybillCode) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        boolean flag = false;
        try {
            // 加盟商余额校验
            if (allianceBusiDeliveryDetailService.checkExist(waybillCode)
                    && !allianceBusiDeliveryDetailService.checkMoney(waybillCode)) {
                flag = true;
            }
            result.setData(flag);
            return result;
        } catch (Exception e) {
            logger.error("盟商余额校验出现异常，请求参数waybillCode={},error=", waybillCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }


}
