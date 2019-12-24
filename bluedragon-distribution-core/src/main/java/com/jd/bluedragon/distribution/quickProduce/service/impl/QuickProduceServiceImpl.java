package com.jd.bluedragon.distribution.quickProduce.service.impl;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.receive.api.dto.OrderMsgDTO;
import com.jd.ql.dms.receive.api.jsf.GetOrderMsgServiceJsf;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 快生项目获取运单信息
 */
@Service("quickProduceService")
public class QuickProduceServiceImpl implements QuickProduceService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    @Autowired
    @Qualifier("waybillCommonService")
    private WaybillCommonService waybillCommonService;

    /**
     * 快生项目获取运单信息
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMSWEB.QuickProduceServiceImpl.getQuickProduceWabill",mState = {JProEnum.TP})
    @Override
    public QuickProduceWabill getQuickProduceWabill(String waybillCode) {
        if(StringHelper.isEmpty(waybillCode))
            return null;
        QuickProduceWabill quickProduceWabill=new QuickProduceWabill();
        Waybill waybill=null;
        if (WaybillUtil.isJDWaybillCode(waybillCode)) {//自营定单
            waybill = getWabillFromOom(waybillCode);
        }
        else if(WaybillUtil.isWaybillCode(waybillCode)) {//外单
            waybill = getQuickProduceWabillFromDrec(waybillCode);
        }
        else {//正则漏掉单号
            waybill = getWabillFromOom(waybillCode);
            if(waybill==null)
                waybill=getQuickProduceWabillFromDrec(waybillCode);
        }
        if (waybill==null) {
            log.warn("快生服务接口获取运单信息为空,单号为:{}",waybillCode);
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

        Waybill waybill= null;
        OrderMsgDTO orderMsgDTO= getOrderMsgServiceJsf.getOrderAllMsgByDeliveryId(waybillCode);
        if(orderMsgDTO==null) {
            log.warn("闪购从外单获取运单数据为空，单号为：{}",waybillCode);
            return waybill;
        }
        waybill=new Waybill();
        waybill.setReceiverName(orderMsgDTO.getReceiveName());
        waybill.setReceiverMobile(orderMsgDTO.getReceiveMobile());
        waybill.setReceiverTel(orderMsgDTO.getReceiveTel());
        if (orderMsgDTO.getCollectionMoney()!=null) {
            waybill.setRecMoney(orderMsgDTO.getCollectionMoney());
        }
        //waybill.setSendPay(orderMsgDTO.sendp);
        waybill.setAddress(orderMsgDTO.getReceiveAdress());
        //waybill.setAirSigns(orderMsgDTO.getAreaCityId());
        waybill.setWaybillCode(waybillCode);
        waybill.setPaymentType(orderMsgDTO.getCollectionValue());
        if (orderMsgDTO.getPreallocation() != null)
            waybill.setSiteCode(orderMsgDTO.getPreallocation().getSiteId());
        return waybill;
    }


    private  Waybill getWabillFromOom(String waybillCode){
        Waybill waybill=null;
        try {//本机调试无host配置，台账接口错误，暂时加try，catch

            Long orderId = waybillCommonService.findOrderIdByWaybillCode(waybillCode);
            if(orderId == null){
                return waybill;
            }
            waybill = orderWebService.getWaybillByOrderId(orderId);
            if (waybill != null) {
                OrderBankResponse orderBankResponse = orderBankService.getOrderBankResponse(orderId.toString());
                if (orderBankResponse != null&&orderBankResponse.getShouldPay()!=null) {
                    waybill.setRecMoney(Double.parseDouble(orderBankResponse.getShouldPay().toString()));
                }
            }

        }
        catch (Exception ex){
            log.error("getWabillFromOom:{}",waybillCode,ex);
        }
        return waybill;
    }
}
