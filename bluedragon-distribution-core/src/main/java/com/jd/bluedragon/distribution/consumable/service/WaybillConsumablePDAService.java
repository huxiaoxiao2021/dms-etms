package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210617
 **/
public interface WaybillConsumablePDAService {
    /**
     * 耗材打包确认（绑定运单打包人 + 耗材确认）
     * @param waybillConsumablePackConfirmReq
     * @return
     */
    JdCResponse<Boolean> doWaybillConsumablePackConfirm(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq);

    /**
     *  查询运单耗材信息接口
     * @param waybillConsumablePackConfirmReq
     * @return
     */
    JdCResponse<WaybillConsumablePackConfirmRes> getWaybillConsumableInfo(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq);
}
