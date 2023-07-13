package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumablePackConfirmRequest;

public interface WaybillConsumableService {

    /**
     * 包装耗材确认
     * @param waybillConsumablePackConfirmDto 耗材信息
     * @return 是否成功
     */
    InvokeResult<Boolean> doWaybillConsumablePackConfirm(WaybillConsumablePackConfirmRequest waybillConsumablePackConfirmDto);

}
