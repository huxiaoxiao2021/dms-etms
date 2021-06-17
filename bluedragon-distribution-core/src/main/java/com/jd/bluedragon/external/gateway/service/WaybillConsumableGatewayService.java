package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;

/**
 * @Author zhengchengfa
 * @Description //运单耗材服务： 转运木质耗材打包确认  B网
 * @date 20210617
 **/
public interface WaybillConsumableGatewayService {
    /**
     * 查询运单耗材信息接口
     * @param waybillConsumablePackConfirmReq
     * @return
     */
    JdCResponse<WaybillConsumablePackConfirmRes> getWaybillConsumableInfo(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq);

    /**
     *  耗材打包确认（绑定运单打包人 + 耗材确认）
     * @param waybillConsumablePackConfirmReq
     * @return
     */
    JdCResponse<Boolean> doWaybillConsumablePackConfirm(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq);


    /**
     * 校验是否可修改耗材数量
     * @param waybillConsumablePackConfirmReq
     * @return
     */
    JdCResponse<Boolean> canModifyConsumableNum(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq);



}
