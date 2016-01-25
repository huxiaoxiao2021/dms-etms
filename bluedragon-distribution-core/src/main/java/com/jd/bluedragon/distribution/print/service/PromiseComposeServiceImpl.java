package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by wangtingwei on 2016/1/25.
 */
public class PromiseComposeServiceImpl implements  ComposeService {

    private static final Log log= LogFactory.getLog(PromiseComposeServiceImpl.class);

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
                waybill.setTimeCategory("4日达及以上");
        }

        try {
            if (request.getStartSiteType() != null && dmsCode != null
                    && SerialRuleUtil.isMatchReceiveWaybillNo(waybill.getWaybillCode())
                    && ((Constants.WAYBILL_SIGN_B!=waybill.getWaybillSign().charAt(1)&& NumberHelper.isNumber(waybill.getVendorId()))||Constants.WAYBILL_SIGN_B==waybill.getWaybillSign().charAt(0))) {

                log.debug("调用promise获取外单时效开始");

                OrderMarkingForeignRequest orderMarkingRequest = new OrderMarkingForeignRequest();
                if (Constants.WAYBILL_SIGN_B==waybill.getWaybillSign().charAt(0))
                    orderMarkingRequest.setOrderId(Constants.ORDER_TYPE_B_ORDERNUMBER);//纯外单订单号设置为0
                else
                    orderMarkingRequest.setOrderId(Long.parseLong(waybill.getVendorId()));//订单号
                orderMarkingRequest.setWaybillCode(waybill.getWaybillCode());//运单号
                orderMarkingRequest.setOpeSiteId(request.dmsCode.toString());//分拣中心ID
                orderMarkingRequest.setOpeSiteName(request.dmsName);//分拣中心名称

                orderMarkingRequest.setOpesiteType(request.getStartSiteType()
                        .equals(Constants.BASE_SITE_DISTRIBUTION_CENTER) ? Constants.PROMISE_DISTRIBUTION_CENTER
                        : request.getStartSiteType().equals(Constants.BASE_SITE_SITE) ? Constants.PROMISE_SITE
                        : Constants.PROMISE_DISTRIBUTION_B);
                orderMarkingRequest.setSource(Constants.DISTRIBUTION_SOURCE);
                orderMarkingRequest.setProvinceId(waybill.getProvinceId()==null?Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE:waybill.getProvinceId());//省
                orderMarkingRequest.setCityId(waybill.getCityId()==null?Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE:waybill.getCityId());//市
                orderMarkingRequest.setCountyId(waybill.getCountryId()==null?Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE:waybill.getCountryId());//县
                orderMarkingRequest.setTownId(waybill.getTownId()==null?Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE:waybill.getTownId());//镇
                orderMarkingRequest.setCurrentDate(new Date());//当前时间
//                }
                log.debug("调用promise获取外单时效传入参数" + orderMarkingRequest == null ? "" : JsonHelper.toJson(orderMarkingRequest));
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
