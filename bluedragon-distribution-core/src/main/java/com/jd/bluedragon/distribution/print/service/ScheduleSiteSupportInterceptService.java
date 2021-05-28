package com.jd.bluedragon.distribution.print.service;


import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface ScheduleSiteSupportInterceptService {

    /**
     * 获取滑道信息
     * @param waybillSign
     * @param sendPay
     * @param waybillCode  运单号
     * @param prepareSiteCode  预分拣目的站点
     * @param startSiteCode  预分拣始发站点
     * @return
     */
    InvokeResult<String> checkCrossInfo(String waybillSign, String sendPay, String waybillCode , Integer prepareSiteCode, Integer startSiteCode);
}
