package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;

public interface HideInfoService {
    /**
     * 根据weaybillSign设置微笑面单字段
     * @param waybillSign
     * @param waybill
     */
    void setHideInfo(String waybillSign,String sendPay,BasePrintWaybill waybill);
}
