package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.fce.dos.service.contract.OrderMarkingService;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by wangtingwei on 2016/1/25.
 */
public class PromiseComposeServiceImpl implements  ComposeService {

    private static final Log log= LogFactory.getLog(PromiseComposeServiceImpl.class);

    @Autowired
    private OrderMarkingService orderMarkingService;

    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        // 外单多时效打标
        if(StringHelper.isNotEmpty(waybill.getWaybillSign())) {
            if(waybill.getWaybillSign().charAt(15)=='0')
                waybill.setTimeCategory("");
            if(waybill.getWaybillSign().charAt(15)=='1')
                waybill.setTimeCategory("当日达");
            if(waybill.getWaybillSign().charAt(15)=='2')
                waybill.setTimeCategory("次日达");
            if(waybill.getWaybillSign().charAt(15)=='3')
                waybill.setTimeCategory("隔日达");
            if(waybill.getWaybillSign().charAt(15)=='4')
                waybill.setTimeCategory("次晨达");
        }

        try {
            if (SerialRuleUtil.isMatchReceiveWaybillNo(waybill.getWaybillCode())
                    && ((Constants.WAYBILL_SIGN_B!=waybill.getWaybillSign().charAt(1)&& NumberHelper.isNumber(waybill.getOrderCode()))||Constants.WAYBILL_SIGN_B==waybill.getWaybillSign().charAt(0))) {

                log.debug("调用promise获取外单时效开始");

                OrderMarkingForeignRequest orderMarkingRequest = new OrderMarkingForeignRequest();
                if (Constants.WAYBILL_SIGN_B==waybill.getWaybillSign().charAt(0))
                    orderMarkingRequest.setOrderId(Constants.ORDER_TYPE_B_ORDERNUMBER);//纯外单订单号设置为0
                else
                    orderMarkingRequest.setOrderId(Long.parseLong(waybill.getOrderCode()));//订单号
                orderMarkingRequest.setWaybillCode(waybill.getWaybillCode());//运单号
                orderMarkingRequest.setOpeSiteId(dmsCode.toString());//分拣中心ID
                orderMarkingRequest.setOpeSiteName(dmsCode.toString());//分拣中心名称

                orderMarkingRequest.setOpesiteType( Constants.PROMISE_DISTRIBUTION_CENTER);
                orderMarkingRequest.setSource(Constants.DISTRIBUTION_SOURCE);
                orderMarkingRequest.setProvinceId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//省
                orderMarkingRequest.setCityId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//市
                orderMarkingRequest.setCountyId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//县
                orderMarkingRequest.setTownId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//镇
                orderMarkingRequest.setCurrentDate(new Date());//当前时间
//                }
                log.debug("调用promise获取外单时效传入参数" +JsonHelper.toJson(orderMarkingRequest));
                OrderMarkingForeignResponse orderMarkingForeignResponse = orderMarkingService.orderMarkingServiceForForeign(orderMarkingRequest);
                if (orderMarkingForeignResponse != null && orderMarkingForeignResponse.getResultCode() >= 1) {
                    waybill.setPromiseText(orderMarkingForeignResponse.getPromiseMsg());
                    waybill.setTimeCategory(orderMarkingForeignResponse.getSendpayDesc());
                } else {
                    log.error("调用promise接口获取外单时效失败：" + orderMarkingForeignResponse == null ? "" : orderMarkingForeignResponse.toString());
                }
                log.debug("调用promise获取外单时效返回数据" + orderMarkingForeignResponse == null ? "" : JsonHelper.toJson(orderMarkingForeignResponse.toString()));

            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            log.error("外单调用promise接口异常" + e.toString() + waybill.getWaybillCode(),e);
        }
    }
}
