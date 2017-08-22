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

        //城配--sendPay第146位为1，且124位为3追打“集”；sendPay第146位为1，且124位不为3追打“城”
        if(waybill.getSendPay().length()>=145 && waybill.getSendPay().charAt(145) == '1'){
            if(waybill.getSendPay().charAt(123) == '3'){
                builder.append(CITY_DISTRIBUTION_JI);
            }else {
                builder.append(CITY_DISTRIBUTION_CHENG);
            }
        }
        if(null!=targetSiteCode&&targetSiteCode>0){
            builder.append(SPECIAL_MARK_LOCAL_SCHEDULE);
        }
        if(builder.indexOf(CITY_DISTRIBUTION_JI) < 0 && builder.indexOf(CITY_DISTRIBUTION_CHENG) < 0){//城配与配送方式提互斥，优先城配
            if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
                if(waybill.getSendPay().charAt(21)!='5'){
                    builder.append(SPECIAL_MARK_ARAYACAK_SITE);
                }

            }
        }
        if(waybill.getSendPay().length()>134&&waybill.getSendPay().charAt(134)=='1'){
            builder.append(SPECIAL_MARK_VALUABLE);
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().length() >11 && waybill.getWaybillSign().charAt(11)=='9') {
            builder.append(SPECIAL_MARK_CROWD_SOURCING);
        }
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().length() >23 && waybill.getWaybillSign().charAt(23)=='1') {
            builder.append(SPECIAL_MARK_PUBLIC_WELFARE);
        }

        if(waybill.getIsAir()){
            builder.append(SPECIAL_MARK_AIRTRANSPORT);
        }
        //安利--waybillSign第27位等于1的为允许半收的订单，包裹标签打“半”
        if(waybill.getWaybillSign().length() > 26 && waybill.getWaybillSign().charAt(26) == '1'){
            builder.append(ALLOW_HALF_ACCEPT);
        }
        //当前打“空”的逻辑不变，“空”字变为“航”，同时增加waybillsign 第31为1 打“航”逻辑。Waybillsign标识 2017年8月22日16:23:47
        if(waybill.getWaybillSign().length() > 30 && waybill.getWaybillSign().charAt(30) == '1'){
            builder.append(SPECIAL_MARK_AIRTRANSPORT);
        }
        if((builder.indexOf(CITY_DISTRIBUTION_JI) < 0 && builder.indexOf(CITY_DISTRIBUTION_CHENG) < 0) && waybill.getIsSelfService()){//城配与配送方式柜互斥，优先城配
            builder.append(SPECIAL_MARK_ARAYACAK_CABINET);
        }

        //“半”与“空”互斥，且“空”字为大
        if( builder.indexOf(SPECIAL_MARK_AIRTRANSPORT) >= 0 && builder.indexOf(ALLOW_HALF_ACCEPT) >= 0){
            builder.deleteCharAt(builder.indexOf(ALLOW_HALF_ACCEPT));
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
