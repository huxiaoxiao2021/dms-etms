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
        if(waybill.getSendPay().length()>134&&waybill.getSendPay().charAt(134)=='1'){
            builder.append(SPECIAL_MARK_VALUABLE);
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().charAt(11)=='9') {
            builder.append(SPECIAL_MARK_CROWD_SOURCING);
        }
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().charAt(23)=='1') {
            builder.append(SPECIAL_MARK_PUBLIC_WELFARE);
        }

        if(waybill.getIsAir()){
            builder.append(SPECIAL_MARK_AIRTRANSPORT);
        }
        if(waybill.getIsSelfService()){
            builder.append(SPECIAL_MARK_ARAYACAK_CABINET);
        }
        if(waybill.getSendPay().charAt(146) == '1'){
            if(waybill.getSendPay().charAt(124) == '3'){
                builder.append(CITY_DISTRIBUTION_JI);
            }else {
                builder.append(CITY_DISTRIBUTION_CHENG);
            }
        }

        int index=-1;
        if(((index=builder.indexOf(SPECIAL_MARK_CROWD_SOURCING))>=0)
                &&(builder.indexOf(SPECIAL_MARK_VALUABLE)>=0))
        {
            builder.deleteCharAt(index);
        }
        waybill.setSpecialMark(builder.toString());
    }
}
