package com.jd.bluedragon.distribution.quickProduce.service.impl;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.utils.NumberHelper;
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

    @Autowired
    private BaseService baseService;


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
        joinDetail.setDeclaredValue(waybill.getRecMoney());
        joinDetail.setDistributeStoreId(waybill.getDistributeStoreId());
        joinDetail.setPrice(waybill.getRecMoney());
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
        joinDetail.setReceiverZipCode(waybill.getReceiverZipCode());
        joinDetail.setProvinceName(waybill.getProvinceName());
        joinDetail.setCityName(waybill.getCityName());
        joinDetail.setCountryName(waybill.getCountryName());
    }

    private Waybill getQuickProduceWabillFromDrec(String waybillCode) {

        Waybill waybill= new Waybill();
        OrderMsgDTO orderMsgDTO= getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId(waybillCode);
        waybill.setReceiverName(orderMsgDTO.getReceiveName());
        waybill.setReceiverMobile(orderMsgDTO.getReceiveMobile());
        waybill.setReceiverTel(orderMsgDTO.getReceiveTel());
        if(NumberHelper.isNumber(orderMsgDTO.getGoodsMoney())) {
            waybill.setRecMoney(Double.parseDouble(orderMsgDTO.getGoodsMoney()));
        }
        //waybill.setSendPay(orderMsgDTO.sendp);
        waybill.setAddress(orderMsgDTO.getAdress());
        //waybill.setAirSigns(orderMsgDTO.getAreaCityId());
        waybill.setWaybillCode(orderMsgDTO.getOrderId());
        return waybill;
    }


    private  Waybill getWabillFromOom(String waybillCode){
        Waybill waybill=null;
        try {//本机调试无host配置，台账接口错误，暂时加try，catch
            if (!NumberHelper.isNumber(waybillCode)) {
                waybill = orderWebService.getWaybillByOrderId(Long.parseLong(waybillCode));
                if (waybill != null) {
                    OrderBankResponse orderBankResponse = orderBankService.getOrderBankResponse(waybillCode);
                    if (orderBankResponse != null&&orderBankResponse.getShouldPay()!=null) {
                        waybill.setRecMoney(Double.parseDouble(orderBankResponse.getShouldPay().toString()));
                    }
                }
            }
        }
        catch (Exception ex){
            logger.debug(ex);
        }
        return waybill;
    }
}
