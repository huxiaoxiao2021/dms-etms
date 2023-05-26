package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePdaDto;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumablePackConfirmRequest;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumablePDAService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableService;
import com.jd.bluedragon.distribution.jy.dto.User;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("doWaybillConsumablePackConfirm|德邦包装确认请求参数:request={}", JsonHelper.toJson(request));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();
        try {
            WaybillConsumablePackConfirmReq confirmReq = new WaybillConsumablePackConfirmReq();
            confirmReq.setUser(getUser(request.getUser()));
            confirmReq.setBusinessCode(request.getBusinessCode());
            // 查询运单耗材信息
            JdCResponse<List<WaybillConsumablePackConfirmRes>> consumableResponse = waybillConsumablePDAService.getWaybillConsumableInfo(confirmReq);
            if (!JdCResponse.CODE_SUCCESS.equals(consumableResponse.getCode())) {
                LOGGER.warn("doWaybillConsumablePackConfirm|查询耗材信息失败:request={},response={}", JsonHelper.toJson(request), JsonHelper.toJson(consumableResponse));
                result.setCode(consumableResponse.getCode());
                result.setMessage(consumableResponse.getMessage());
                return result;
            }
            List<WaybillConsumablePackConfirmRes> consumableList = consumableResponse.getData();
            LOGGER.info("测试指定1:consumableList={}", JsonHelper.toJson(consumableList));
            if (CollectionUtils.isEmpty(consumableList)) {
                LOGGER.warn("doWaybillConsumablePackConfirm|查询耗材信息为空:request={},response={}", JsonHelper.toJson(request), JsonHelper.toJson(consumableResponse));
                result.parameterError("查询耗材信息为空");
                return result;
            }
            // 组装转换耗材请求参数
            List<WaybillConsumablePdaDto> waybillConsumablePdaRequestList = getWaybillConsumablePdaDtoList(consumableList, request.getConfirmVolume());
            LOGGER.info("测试指定2:waybillConsumablePdaRequestList={}", JsonHelper.toJson(waybillConsumablePdaRequestList));
            confirmReq.setWaybillConsumableDtoList(waybillConsumablePdaRequestList);
            // 执行耗材确认
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

    private List<WaybillConsumablePdaDto> getWaybillConsumablePdaDtoList(List<WaybillConsumablePackConfirmRes> consumableList, Double confirmVolume) {
        List<WaybillConsumablePdaDto> waybillConsumablePdaRequestList = new ArrayList<>();
        for (WaybillConsumablePackConfirmRes confirmRes : consumableList) {
            WaybillConsumablePdaDto pdaRequest = new WaybillConsumablePdaDto();
            pdaRequest.setConsumableCode(confirmRes.getConsumableCode());
            pdaRequest.setConsumableTypeCode(confirmRes.getConsumableTypeCode());
            pdaRequest.setConfirmQuantity(confirmRes.getReceiveQuantity());
            // 优先使用参数中的确认体积
            if (confirmVolume != null) {
                pdaRequest.setConfirmVolume(confirmVolume);
            } else {
                // 参数没传，默认使用上游下发的体积
                if (confirmRes.getVolume() != null) {
                    pdaRequest.setConfirmVolume(Double.valueOf(confirmRes.getVolume().toPlainString()));
                }
            }
            waybillConsumablePdaRequestList.add(pdaRequest);
        }
        return waybillConsumablePdaRequestList;
    }

    private com.jd.bluedragon.common.dto.base.request.User getUser(User user) {
        com.jd.bluedragon.common.dto.base.request.User newUser = new com.jd.bluedragon.common.dto.base.request.User();
        newUser.setUserCode(user.getUserCode());
        newUser.setUserErp(user.getUserErp());
        newUser.setUserName(user.getUserName());
        return newUser;
    }

}
