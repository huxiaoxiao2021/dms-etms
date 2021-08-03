package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

public interface DeliveryJsfService {
    /**
     * 检验 条码是否已经在createSiteCode发货
     * @param barcode 包裹号 或箱号
     * @param createSiteCode 场地
     * @return
     */
    InvokeResult<Boolean> checkIsSend(String barcode, Integer createSiteCode);
}
