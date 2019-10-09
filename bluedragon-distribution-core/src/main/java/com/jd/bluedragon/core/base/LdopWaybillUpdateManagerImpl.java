package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.enums.RelievedSendEnum;
import com.jd.ldop.center.api.update.WaybillUpdateApi;
import com.jd.ldop.center.api.update.dto.WaybillUpdateBeforePickupDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/8/20
 */
@Service("ldopWaybillUpdateManager")
public class LdopWaybillUpdateManagerImpl implements LdopWaybillUpdateManager{

    private static final Logger logger = LoggerFactory.getLogger(LdopWaybillUpdateManagerImpl.class);

    @Autowired
    private WaybillUpdateApi waybillUpdateApi;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.LdopWaybillUpdateManagerImpl.cancelFeatherLetterByWaybillCode",mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> cancelFeatherLetterByWaybillCode(String waybillCode) {
        InvokeResult<String> invokeResult = new InvokeResult<>();
        WaybillUpdateBeforePickupDTO pickupDTO = new WaybillUpdateBeforePickupDTO();
        pickupDTO.setWaybillCode(waybillCode);
        pickupDTO.setRelievedSend(RelievedSendEnum.SUPPORT_NOT_USE.getValue());
        ResponseDTO responseDTO = waybillUpdateApi.updateBeforePickUpWithResult(pickupDTO);
        if(responseDTO == null){
            logger.error("取消鸡毛信服务失败-接口返回空waybillCode[{}]",waybillCode);
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage("取消鸡毛信失败,请求返回空！");
            return invokeResult;
        }
        if(!Objects.equals(responseDTO.getStatusCode(),ResponseDTO.SUCCESS_CODE)){
            logger.error("取消鸡毛信服务失败waybillCode[{}]responseDTO[{}]",waybillCode, JsonHelper.toJson(responseDTO));
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage(responseDTO.getStatusMessage());
            return invokeResult;
        }
        invokeResult.success();
        return invokeResult;
    }
}
