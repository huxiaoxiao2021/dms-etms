package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

public interface HideInfoService {
    /**
     * 根据weaybillSign设置微笑面单字段
     * @param waybillSign
     * @param waybill
     */
    void setHideInfo(String waybillSign,PrintWaybill waybill);
}
