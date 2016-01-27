package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.waybill.service.LabelPrintingService;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 合成特殊标记
 * Created by wangtingwei on 2015/12/24.
 */
public class SpecialMarkComposeServiceImpl implements ComposeService {
    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        StringBuilder builder=new StringBuilder();
        if(null!=targetSiteCode&&targetSiteCode>0){
            builder.append(SPECIAL_MARK_LOCAL_SCHEDULE);
        }
        if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
            if(waybill.getSendPay().charAt(21)!='5'){
                builder.append(SPECIAL_MARK_ARAYACAK_SITE);
            }
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().charAt(11)=='9') {
            builder.append(SPECIAL_MARK_CROWD_SOURCING);
        }

        if(waybill.getIsAir()){
            builder.append(SPECIAL_MARK_AIRTRANSPORT);
        }
        if(waybill.getIsSelfService()){
            builder.append(SPECIAL_MARK_ARAYACAK_CABINET);
        }
        waybill.setSpecialMark(builder.toString());
    }
}
