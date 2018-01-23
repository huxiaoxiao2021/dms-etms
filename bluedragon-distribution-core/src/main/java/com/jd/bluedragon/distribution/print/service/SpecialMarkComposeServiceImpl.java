package com.jd.bluedragon.distribution.print.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.distribution.waybill.service.LabelPrintingService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 合成特殊标记
 * Created by wangtingwei on 2015/12/24.
 */
public class SpecialMarkComposeServiceImpl implements ComposeService {
    @Autowired
    private TransbillMService transbillMService;
    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        /**
         *城配/中石化打标逻辑合并
          若订单sendpay146=1或者waybillsign36=1,则为城配订单
				若配送方式字段（transbill_m.require_trans_mode）为仓库直发，则标识位打直字
				若配送方式字段（transbill_m.require_trans_mode）为分拣集货，且订单sendpay124=3，则标识位打集字
				若配送方式字段（transbill_m.require_trans_mode）为分拣集货，且订单sendpay124！=3，则标识位打城字
         */
        if(BusinessHelper.isUrban(waybill.getWaybillSign(), waybill.getSendPay())){
        	boolean isMarked = false;
        	TransbillM transbillM = transbillMService.getByWaybillCode(waybill.getWaybillCode());
            if(transbillM != null){
            	if(TRANS_MODE_ZHI.equals(transbillM.getRequireTransMode())){
            		waybill.appendSpecialMark(CITY_DISTRIBUTION_ZHI);
            		isMarked = true;
            	}else if(TRANS_MODE_JI.equals(transbillM.getRequireTransMode())){
            		if(BusinessHelper.isSignChar(waybill.getSendPay(), POSITION_124, CHAR_3)){
                        waybill.appendSpecialMark(CITY_DISTRIBUTION_JI);
                    }else {
                        waybill.appendSpecialMark(CITY_DISTRIBUTION_CHENG);
                    }
            		isMarked = true;
            	}
            }
            //判断是否完成城配打标
            if(!isMarked){
            	if(BusinessHelper.isSignChar(waybill.getSendPay(), POSITION_124, CHAR_3)){
                    waybill.appendSpecialMark(CITY_DISTRIBUTION_JI);
                }else {
                    waybill.appendSpecialMark(CITY_DISTRIBUTION_CHENG);
                }
            }
        }
        if(null!=targetSiteCode&&targetSiteCode>0){
            waybill.appendSpecialMark(SPECIAL_MARK_LOCAL_SCHEDULE);
        }
        if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
        	if(waybill.getSendPay().charAt(21)!='5'){
        		waybill.appendSpecialMark(SPECIAL_MARK_ARAYACAK_SITE);
        	}
        }
        if(waybill.getSendPay().length()>134&&waybill.getSendPay().charAt(134)=='1'){
            waybill.appendSpecialMark(SPECIAL_MARK_VALUABLE);
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().length() >11 && waybill.getWaybillSign().charAt(11)=='9') {
            waybill.appendSpecialMark(SPECIAL_MARK_CROWD_SOURCING);
        }
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().length() >23 && waybill.getWaybillSign().charAt(23)=='1') {
            waybill.appendSpecialMark(SPECIAL_MARK_PUBLIC_WELFARE);
        }

        //安利--waybillSign第27位等于1的为允许半收的订单，包裹标签打“半”
        if(waybill.getWaybillSign().length() > 26 && waybill.getWaybillSign().charAt(26) == '1'){
            waybill.appendSpecialMark(ALLOW_HALF_ACCEPT);
        }
        //分拣补打的运单和包裹小标签上添加“尊”字样:waybillsign 第35为1 打“尊”逻辑 2017年9月21日17:59:39
        if(waybill.getWaybillSign().length() > 34 && waybill.getWaybillSign().charAt(34) == '1'){
            waybill.appendSpecialMark(SPECIAL_MARK_SENIOR);
        }
        //当前打“空”的逻辑不变，“空”字变为“航”，同时增加waybillsign 第31为1 打“航”逻辑。Waybillsign标识 2017年8月22日16:23:47
        if(waybill.getWaybillSign().length() > 30 && waybill.getWaybillSign().charAt(30) == '1'){
            waybill.appendSpecialMark(SPECIAL_MARK_AIRTRANSPORT);
        }else {
            if(waybill.getIsAir()){
                waybill.appendSpecialMark(SPECIAL_MARK_AIRTRANSPORT);
            }
        }
        if(waybill.getIsSelfService()){//城配与配送方式柜互斥，优先城配
            waybill.appendSpecialMark(SPECIAL_MARK_ARAYACAK_CABINET);
        }
        //城配标和柜冲突处理
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_ZHI, SPECIAL_MARK_ARAYACAK_CABINET);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_JI, SPECIAL_MARK_ARAYACAK_CABINET);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_CHENG, SPECIAL_MARK_ARAYACAK_CABINET);
      //城配标和提冲突处理
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_ZHI, SPECIAL_MARK_ARAYACAK_SITE);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_JI, SPECIAL_MARK_ARAYACAK_SITE);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_CHENG, SPECIAL_MARK_ARAYACAK_SITE);

        //城配标和运输产品互斥，如果显示【B】字标，那么在显示【特惠送】的位置显示为空
        if(StringHelper.isNotEmpty(waybill.getSpecialMark()) && waybill.getSpecialMark().contains(CITY_DISTRIBUTION_CHENG)){
            waybill.setTransportMode("");
        }

        //“半”与“航”互斥，且“航”字为大
        waybill.dealConflictSpecialMark(SPECIAL_MARK_AIRTRANSPORT, ALLOW_HALF_ACCEPT);
        //处理标记冲突，安和众
        waybill.dealConflictSpecialMark(SPECIAL_MARK_VALUABLE, SPECIAL_MARK_CROWD_SOURCING);

        //港澳售进合包,sendpay第108位为1或2或3时，且senpay第124位为4时，视为是全球售合包订单，面单上打印"合"
        if (StringHelper.isNotEmpty(waybill.getSendPay()) && waybill.getSendPay().length() > 123 && waybill.getSendPay().charAt(123) == '4'
                && (waybill.getSendPay().charAt(107) == '1' || waybill.getSendPay().charAt(107) == '2' || waybill.getSendPay().charAt(107) == '3')) {
            waybill.appendSpecialMark(SPECIAL_MARK_SOLD_INTO_PACKAGE);
        }
    }
}
