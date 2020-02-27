package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 根据标识隐藏寄件信息、收件信息
 * 收件信息判断waybill_sign第37位
 * 寄件信息判断waybill_sign第47位
 * Created by shipeilin on 2017/9/21.
 */
@Service("hideInfoComposeService")
public class HideInfoComposeServiceImpl implements  ComposeService {

    @Autowired
    HideInfoService hideInfoService;

    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode){
        String waybillSign  = waybill.getWaybillSign();
        String sendPay  = waybill.getSendPay();
        hideInfoService.setHideInfo(waybillSign,sendPay,waybill);
    }
}
