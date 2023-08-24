package com.jd.bluedragon.core.base;

import com.jd.ql.basic.dto.CommonDto;
import com.jd.ql.erp.dto.forcedelivery.price.WaybillPriceRequestDto;
import com.jd.ql.erp.dto.forcedelivery.price.WaybillPriceResponseDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/25 13:56
 * @Description:
 */
public interface WaybillForceDeliveryApiManager {

    /**
     * 妥投校验接口
     * @param requestDto
     * @return
     */
    Boolean forceDeliveryCheck(WaybillPriceRequestDto requestDto);
}
