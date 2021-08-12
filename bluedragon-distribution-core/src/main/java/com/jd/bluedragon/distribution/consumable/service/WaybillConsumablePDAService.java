package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;

import java.util.List;

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
     *  查询运单耗材信息接口(一个运单可能存在关联多个耗材场景)
     * @param waybillConsumablePackConfirmReq
     * @return
     */
    JdCResponse<List<WaybillConsumablePackConfirmRes>> getWaybillConsumableInfo(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq);
}
