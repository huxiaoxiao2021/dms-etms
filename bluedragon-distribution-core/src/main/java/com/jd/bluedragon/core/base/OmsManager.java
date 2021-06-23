package com.jd.bluedragon.core.base;

import cn.jdl.oms.express.model.ModifyExpressOrderRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.CancelFeatherLetterRequest;


/**
 * @ClassName OmsManager
 * @Description
 * @Author wyh
 * @Date 2021/4/27 22:44
 **/
public interface OmsManager {

    /**
     *
     * @param request
     * @param omcOrderCode
     * @return
     */
    ModifyExpressOrderRequest makeCancelLetterRequest(CancelFeatherLetterRequest request, String omcOrderCode);

    /**
     * 取消鸡毛信
     * @param waybillCode
     * @param request
     * @return
     * @see
     */
    InvokeResult<String> cancelFeatherLetterByWaybillCode(final String waybillCode, ModifyExpressOrderRequest request);

}
