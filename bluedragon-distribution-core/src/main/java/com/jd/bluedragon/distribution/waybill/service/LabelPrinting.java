package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
public interface LabelPrinting {
    /**
     * 分拣中心打印接口
     * @param request
     */
    public BaseResponseIncidental<LabelPrintingResponse> dmsPrint(LabelPrintingRequest request);

//    /**
//     * 备件库打印接口
//     * @param request
//     */
//    public BaseResponseIncidental<LabelPrintingResponse> spwmsPrint(LabelPrintingRequest request);
}
