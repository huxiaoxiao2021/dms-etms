package com.jd.bluedragon.distribution.waybill.service;

/**
 * Created by yanghongqiang on 2015/11/30.
 */

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.fce.dos.service.contract.OrderMarkingService;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import com.jd.ql.basic.domain.BaseDmsStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 分拣中心包裹标签打印
 * @author eason
 *
 */
@Service("dmsLablePrintingService")
public class DmsLablePrintingServiceImpl extends AbstractLabelPrintingServiceTemplate {

    public static final Log log = LogFactory.getLog(DmsLablePrintingServiceImpl.class);

    public static final String LOG_PREFIX="包裹标签打印-分拣[DmsLablePrintingServiceImpl] ";

    @Autowired
    private OrderMarkingService orderMarkingService;
    /**
     * 初始化基础资料对象
     */
    @Override
    public BaseDmsStore initBaseVo(LabelPrintingRequest request) {
        BaseDmsStore baseDmsStore = new BaseDmsStore();
        baseDmsStore.setStoreId(request.getStoreCode());//库房编号
        baseDmsStore.setCky2(request.getCky2());//cky2
        baseDmsStore.setOrgId(request.getOrgCode());//机构编号
        baseDmsStore.setDmsId(request.getDmsCode());//分拣中心编号

        return baseDmsStore;
    }

    /**
     * 包裹标签在运单范围内的扩展
     */
    @Override
    public LabelPrintingResponse waybillExtensional(
            LabelPrintingRequest request, LabelPrintingResponse labelPrinting,
            Waybill waybill) {
        //现场调度时 预分拣站点为空，以现场调度的为准
        if(request.getPreSeparateCode()==null){
            labelPrinting.setPrepareSiteCode(waybill.getOldSiteId());
            labelPrinting.setPrintAddress(waybill.getReceiverAddress());
        }else{
            labelPrinting.setPrepareSiteCode(request.getPreSeparateCode());
            labelPrinting.setPrepareSiteName(request.getPreSeparateName());
            //如果调度的新地址为空，则应当显示老地址
            labelPrinting.setPrintAddress(StringHelper.isEmpty(waybill.getNewRecAddr())?waybill.getReceiverAddress():waybill.getNewRecAddr());
        }

        //自提订单---打提字，并且地址不显示
        StringBuilder specialMark = new StringBuilder(StringHelper.isEmpty(labelPrinting.getSpecialMark())?"":labelPrinting.getSpecialMark());
        log.debug(new StringBuilder(LOG_PREFIX).append("waybill---distanceType").append(waybill.getDistanceType()).append("sendpay ").append(waybill.getSendPay()));
        if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
            if(waybill.getSendPay().charAt(21)!='5'){
                labelPrinting.setPrintAddress("");
                specialMark.append(LabelPrintingService.SPECIAL_MARK_ARAYACAK_SITE);
            }
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().charAt(11)=='9') {
            specialMark.append(LabelPrintingService.SPECIAL_MARK_CROWD_SOURCING);
        }
        labelPrinting.setSpecialMark(specialMark.toString());
        // 外单多时效打标
        if(StringHelper.isNotEmpty(waybill.getWaybillSign())) {
            if(waybill.getWaybillSign().charAt(15)=='0')
                labelPrinting.setTimeCategory("");
            if(waybill.getWaybillSign().charAt(15)=='1')
                labelPrinting.setTimeCategory("当日达");
            if(waybill.getWaybillSign().charAt(15)=='2')
                labelPrinting.setTimeCategory("次日达");
            if(waybill.getWaybillSign().charAt(15)=='3')
                labelPrinting.setTimeCategory("隔日达");
            if(waybill.getWaybillSign().charAt(15)=='4')
                labelPrinting.setTimeCategory("次晨达");
        }

        try {
            if (request != null && request.getStartSiteType() != null && request.dmsCode != null
                    && waybill != null && StringHelper.isNotEmpty(request.getWaybillCode())
                    && SerialRuleUtil.isMatchReceiveWaybillNo(request.getWaybillCode())
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
                    labelPrinting.setPromiseText(orderMarkingForeignResponse.getPromiseMsg());
                    labelPrinting.setTimeCategory(orderMarkingForeignResponse.getSendpayDesc());
                } else {
                    log.error("调用promise接口获取外单时效失败：" + orderMarkingForeignResponse == null ? "" : orderMarkingForeignResponse.toString());
                }
                log.debug("调用promise获取外单时效返回数据" + orderMarkingForeignResponse == null ? "" : JsonHelper.toJson(orderMarkingForeignResponse.toString()));

            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            log.error("外单调用promise接口异常" + e.toString() + (request == null ? "" : JsonHelper.toJson(request)),e);
        }
        //加promise调用获取时效具体信息
        return labelPrinting;
    }
}
