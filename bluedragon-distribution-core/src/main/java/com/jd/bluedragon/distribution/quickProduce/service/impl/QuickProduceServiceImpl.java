package com.jd.bluedragon.distribution.quickProduce.service.impl;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.receive.api.dto.OrderMsgDTO;
import com.jd.ql.dms.receive.api.jsf.GetOrderMsgServiceJsf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 快生项目获取运单信息
 */
@Service("quickProduceService")
public class QuickProduceServiceImpl implements QuickProduceService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    GetOrderMsgServiceJsf getOrderMsgServiceJsf;

    /**
     * 订单中间键ws封装类
     */
    @Autowired
    private OrderWebService orderWebService;

    /**
     * 台帐系统ws封装类
     */
    @Autowired
    private OrderBankService orderBankService;


    /**
     * 快生项目获取运单信息
     * @param waybillCode
     * @return
     */
    @Override
    public QuickProduceWabill getQuickProduceWabill(String waybillCode) {
        if(StringHelper.isEmpty(waybillCode))
            return null;
        QuickProduceWabill quickProduceWabill=new QuickProduceWabill();
        Waybill waybill=null;
        if (SerialRuleUtil.isMatchWaybillNo(waybillCode)) {//自营定单
            waybill = getWabillFromOom(waybillCode);
        }
        else if(SerialRuleUtil.isMatchAllWaybillNo(waybillCode)) {//外单
            waybill = getQuickProduceWabillFromDrec(waybillCode);
        }
        else {//正则漏掉单号
            waybill = getWabillFromOom(waybillCode);
            if(waybill==null)
                waybill=getQuickProduceWabillFromDrec(waybillCode);
        }
        if (waybill==null) {
            logger.error("快生服务接口获取运单信息为空,单号为"+waybillCode);
            return null;
        }

        quickProduceWabill.setWaybill(waybill);
        this.converseWaybill(quickProduceWabill,waybill);//设置joindetail对象

        return quickProduceWabill;
    }

    private void converseWaybill(QuickProduceWabill quickProduceWabill, Waybill waybill) {
        JoinDetail joinDetail =new JoinDetail();
        quickProduceWabill.setJoinDetail(joinDetail);
        joinDetail.setDeclaredValue(Double.parseDouble(waybill.getRecMoney()));
        joinDetail.setDistributeStoreId(waybill.getDistributeStoreId());
        joinDetail.setPrice(Double.parseDouble(waybill.getRecMoney()));
        joinDetail.setGoodNumber(waybill.getQuantity());
        joinDetail.setGoodWeight(waybill.getWeight());
        joinDetail.setPayment(waybill.getPaymentType());
        joinDetail.setReceiverAddress(waybill.getAddress());
        joinDetail.setReceiverMobile(waybill.getReceiverMobile());
        joinDetail.setReceiverName(waybill.getReceiverName());
        joinDetail.setReceiverTel(waybill.getReceiverTel());
        joinDetail.setWaybillType(waybill.getType());
        joinDetail.setSendPay(waybill.getSendPay());
        joinDetail.setOldSiteId(waybill.getSiteCode());
    }

    private Waybill getQuickProduceWabillFromDrec(String waybillCode) {

        Waybill waybill= new Waybill();
        OrderMsgDTO orderMsgDTO= getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId(waybillCode);
        waybill.setReceiverName(orderMsgDTO.getReceiveName());
        waybill.setReceiverMobile(orderMsgDTO.getReceiveMobile());
        waybill.setReceiverTel(orderMsgDTO.getReceiveTel());
        waybill.setRecMoney(orderMsgDTO.getGoodsMoney());
        //waybill.setSendPay(orderMsgDTO.sendp);
        waybill.setAddress(orderMsgDTO.getAdress());
        //waybill.setAirSigns(orderMsgDTO.getAreaCityId());
        return null;
    }


    private  Waybill getWabillFromOom(String waybillCode){
        Waybill waybill= orderWebService.getWaybillByOrderId(Long.parseLong(waybillCode));
        if (waybill != null) {
            BigDecimal pay = orderBankService.getOrderBankResponse(waybillCode).getShouldPay();
            waybill.setRecMoney(pay == null ? null : pay.toString());
        }
        return waybill;
    }
}
