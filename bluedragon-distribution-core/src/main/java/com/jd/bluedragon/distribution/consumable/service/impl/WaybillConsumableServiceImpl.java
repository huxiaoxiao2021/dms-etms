package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumablePackConfirmRequest;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumablePDAService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 包装确认
 */
@Service("waybillConsumableService")
public class WaybillConsumableServiceImpl implements WaybillConsumableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaybillConsumableServiceImpl.class);

    @Autowired
    private WaybillConsumablePDAService waybillConsumablePDAService;

    /**
     * 耗材打包确认（绑定运单打包人 + 耗材确认）
     */
    @Override
    @JProfiler(jKey = "DMSWEB.WaybillConsumableServiceImpl.doWaybillConsumablePackConfirm",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> doWaybillConsumablePackConfirm(WaybillConsumablePackConfirmRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            WaybillConsumablePackConfirmReq confirmReq = new WaybillConsumablePackConfirmReq();
            BeanUtils.copyProperties(request, confirmReq);
            JdCResponse<Boolean> response = waybillConsumablePDAService.doWaybillConsumablePackConfirm(confirmReq);
            result.setCode(response.getCode());
            result.setMessage(response.getMessage());
            result.setData(response.getData());
        } catch (Exception e) {
            LOGGER.error("doWaybillConsumablePackConfirm|包装耗材确认出现异常:request={}", JsonHelper.toJson(request), e);
            result.error();
        }
        return result;
    }
}
