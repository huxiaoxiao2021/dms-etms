package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
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
    public void cancelFeatherLetterByWaybillCode(String waybillCode) {
        WaybillUpdateBeforePickupDTO pickupDTO = new WaybillUpdateBeforePickupDTO();
        pickupDTO.setWaybillCode(waybillCode);
        pickupDTO.setRelievedSend(RelievedSendEnum.SUPPORT_NOT_USE.getValue());
        ResponseDTO responseDTO = waybillUpdateApi.updateBeforePickUpWithResult(pickupDTO);
        logger.info("取消鸡毛信服务结果responseDTO[{}]", JsonHelper.toJson(responseDTO));
    }
}
