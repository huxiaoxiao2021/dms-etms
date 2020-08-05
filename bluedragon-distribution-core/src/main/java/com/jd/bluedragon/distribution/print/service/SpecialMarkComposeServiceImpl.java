package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.distribution.waybill.service.LabelPrintingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 合成特殊标记
 * Created by wangtingwei on 2015/12/24.
 */
@Service("specialMarkComposeService")
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
        if(BusinessUtil.isUrban(waybill.getWaybillSign(), waybill.getSendPay())){
        	boolean isMarked = false;
        	TransbillM transbillM = transbillMService.getByWaybillCode(waybill.getWaybillCode());
            if(transbillM != null){
            	if(TRANS_MODE_ZHI.equals(transbillM.getRequireTransMode())){
            		waybill.appendSpecialMark(CITY_DISTRIBUTION_ZHI);
            		isMarked = true;
            	}else if(TRANS_MODE_JI.equals(transbillM.getRequireTransMode())){
            		if(BusinessUtil.isSignChar(waybill.getSendPay(), POSITION_124, CHAR_3)){
                        waybill.appendSpecialMark(CITY_DISTRIBUTION_JI);
                    }else {
                        waybill.appendSpecialMark(CITY_DISTRIBUTION_CHENG);
                    }
            		isMarked = true;
            	}
            }
            //判断是否完成城配打标
            if(!isMarked){
            	if(BusinessUtil.isSignChar(waybill.getSendPay(), POSITION_124, CHAR_3)){
                    waybill.appendSpecialMark(CITY_DISTRIBUTION_JI);
                }else {
                    waybill.appendSpecialMark(CITY_DISTRIBUTION_CHENG);
                }
            }
        }

        //新通路面单代配站点--sendPay第148-149 = 39且146=1--追打"代"
        if(BusinessHelper.isNewPathWay(waybill.getSendPay())){
            waybill.appendSpecialMark(SPECIAL_MARK_HELP_DELIVERY);
        }

        if(null!=targetSiteCode&&targetSiteCode>0){
            waybill.appendSpecialMark(SPECIAL_MARK_LOCAL_SCHEDULE);
        }
        if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
        	if(!BusinessUtil.isSignChar(waybill.getSendPay(),22,'5') || BusinessUtil.isZiTiByWaybillSign(waybill.getWaybillSign())){
        	    if (!BusinessUtil.isBusinessNet(waybill.getWaybillSign())) {
                    waybill.appendSpecialMark(SPECIAL_MARK_ARAYACAK_SITE);
                }
        	}
        }
        if(BusinessUtil.isSignY(waybill.getSendPay(),135)){
            waybill.appendSpecialMark(SPECIAL_MARK_VALUABLE);
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),12,'9')) {
            waybill.appendSpecialMark(SPECIAL_MARK_CROWD_SOURCING);
        }
        if(BusinessUtil.isSignY(waybill.getWaybillSign(),24)) {
            waybill.appendSpecialMark(SPECIAL_MARK_PUBLIC_WELFARE);
        }

        //安利--waybillSign第27位等于1的为允许半收的订单，包裹标签打“半”
        if(BusinessUtil.isSignY(waybill.getWaybillSign(), 27)){
            waybill.appendSpecialMark(ALLOW_HALF_ACCEPT);
        }
        //分拣补打的运单和包裹小标签上添加“尊”字样:waybillsign 第35为1 打“尊”逻辑 2017年9月21日17:59:39
        if(BusinessUtil.isSignY(waybill.getWaybillSign(), 35)){
            waybill.appendSpecialMark(SPECIAL_MARK_SENIOR);
        }
        if(waybill.getIsSelfService() || BusinessUtil.isZiTiGuiByWaybillSign(waybill.getWaybillSign())){//城配与配送方式柜互斥，优先城配
            if (!BusinessUtil.isBusinessNet(waybill.getWaybillSign())) {
                waybill.appendSpecialMark(SPECIAL_MARK_ARAYACAK_CABINET);
            }
        }
        //城配标和柜冲突处理
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_ZHI, SPECIAL_MARK_ARAYACAK_CABINET);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_JI, SPECIAL_MARK_ARAYACAK_CABINET);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_CHENG, SPECIAL_MARK_ARAYACAK_CABINET);
        //waybill_sign标识位，第七十九位为2，打提字标,C网覆盖掉B,B网不覆盖。也就是说B网不处理冲突。tangcq2018年12月18日14:34:13
        if(!BusinessUtil.isSignChar(waybill.getWaybillSign(), 79,'2')){
            //城配标和提冲突处理
            waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_ZHI, SPECIAL_MARK_ARAYACAK_SITE);
            waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_JI, SPECIAL_MARK_ARAYACAK_SITE);
            waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_CHENG, SPECIAL_MARK_ARAYACAK_SITE);
        }
        /* 众邮面单不打印“店”字 */
        if (BusinessUtil.isZiTiDianByWaybillSign(waybill.getWaybillSign()) && !BusinessUtil.isBusinessNet(waybill.getWaybillSign())) {
            waybill.appendSpecialMark(SPECIAL_MARK_ARAYACAK_DIAN);
        }
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_ZHI, SPECIAL_MARK_ARAYACAK_DIAN);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_JI, SPECIAL_MARK_ARAYACAK_DIAN);
        waybill.dealConflictSpecialMark(CITY_DISTRIBUTION_CHENG, SPECIAL_MARK_ARAYACAK_DIAN);

        //城配标和运输产品互斥，如果显示【B】字标，那么在显示【特惠送】的位置显示为空
        if(StringHelper.isNotEmpty(waybill.getSpecialMark()) && waybill.getSpecialMark().contains(CITY_DISTRIBUTION_CHENG)){
            waybill.setTransportMode("");
        }

        //waybill_sign第54位等于4 且 第40位等于2或3时显示 【医药】，并且和B互斥--显示医药，则不显示B
        if(BusinessUtil.isBMedicine(waybill.getWaybillSign()) && BusinessUtil.isSignInChars(waybill.getWaybillSign(),WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_2,WaybillSignConstants.CHAR_40_3)){
            waybill.appendSpecialMark(SPECIAL_MARK_MEDICINE);
            waybill.dealConflictSpecialMark(SPECIAL_MARK_MEDICINE,CITY_DISTRIBUTION_CHENG);
        }

        //“半”与“航”互斥，且“航”字为大
        waybill.dealConflictSpecialMark(SPECIAL_MARK_AIRTRANSPORT, ALLOW_HALF_ACCEPT);
        //处理标记冲突，安和众
        waybill.dealConflictSpecialMark(SPECIAL_MARK_VALUABLE, SPECIAL_MARK_CROWD_SOURCING);

        //港澳售进合包,sendpay第108位为1或2或3时，且senpay第124位为4时，视为是全球售合包订单，面单上打印"合"
        if (BusinessUtil.isSignChar(waybill.getSendPay(),124,'4')
                && BusinessUtil.isSignInChars(waybill.getSendPay(),108,'1','2','3')) {
            waybill.appendSpecialMark(SPECIAL_MARK_SOLD_INTO_PACKAGE);
        }
        if(BusinessUtil.isC2CJZD(waybill.getWaybillSign())){
            waybill.appendSpecialMark(TextConstants.TEXT_JZD_SPECIAL_MARK);
        }
        //Sendpay第307位=1，面单打印“车”标记
        if(BusinessUtil.isWrcps(waybill.getSendPay())){
            waybill.appendSpecialMark(TextConstants.WRCPS_FLAG);
        }        
    }
}
