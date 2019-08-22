package com.jd.bluedragon.distribution.waybill.service;

/**
 * Created by yanghongqiang on 2015/11/30.
 */

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
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

import java.text.SimpleDateFormat;
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
            labelPrinting.setPrintSiteName(request.getPreSeparateName());
            //如果调度的新地址为空，则应当显示老地址
            labelPrinting.setPrintAddress(StringHelper.isEmpty(waybill.getNewRecAddr())?waybill.getReceiverAddress():waybill.getNewRecAddr());
        }

        //自提订单---打提字，并且地址不显示
        log.debug(new StringBuilder(LOG_PREFIX).append("waybill---distanceType").append(waybill.getDistanceType()).append("sendpay ").append(waybill.getSendPay()));
        if(waybill.getDistributeType()!=null && waybill.getDistributeType().equals(LabelPrintingService.ARAYACAK_SIGN) && waybill.getSendPay().length()>=50){
            if(waybill.getSendPay().charAt(21)!='5'){
                labelPrinting.setPrintAddress("");
                labelPrinting.appendSpecialMark(LabelPrintingService.SPECIAL_MARK_ARAYACAK_SITE);
            }
        }
        // 众包--运单 waybillSign 第 12位为 9--追打"众"字
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),12,'9')) {
        	labelPrinting.appendSpecialMark(LabelPrintingService.SPECIAL_MARK_CROWD_SOURCING);
        }
        //分拣补打的运单和包裹小标签上添加“尊”字样:waybillsign 第35为1 打“尊”逻辑 2017年9月21日17:59:39
        if(BusinessUtil.isSignY(waybill.getWaybillSign(),35)){
        	labelPrinting.appendSpecialMark(SPECIAL_MARK_SENIOR);
        }

        //港澳售进合包,sendpay第108位为1或2或3时，且senpay第124位为4时，视为是全球售合包订单，面单上打印"合"
        if (BusinessUtil.isSignChar(waybill.getSendPay(),124,'4')
                && BusinessUtil.isSignInChars(waybill.getSendPay(),108,'1','2','3')) {
            labelPrinting.appendSpecialMark(SPECIAL_MARK_SOLD_INTO_PACKAGE);
        }

        //waybill_sign第54位等于4 且 第40位等于2或3时显示 【医药】，并且和B互斥--显示医药，则不显示B
        if(BusinessUtil.isBMedicine(waybill.getWaybillSign()) && BusinessUtil.isSignInChars(waybill.getWaybillSign(),WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_2,WaybillSignConstants.CHAR_40_3)){
            labelPrinting.appendSpecialMark(SPECIAL_MARK_MEDICINE);
            labelPrinting.dealConflictSpecialMark(SPECIAL_MARK_MEDICINE,CITY_DISTRIBUTION_CHENG);
        }

        // 外单多时效打标
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().length() > 15) {
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
            if(waybill.getWaybillSign().charAt(15)=='5')
                labelPrinting.setTimeCategory("4日达");
            if(waybill.getWaybillSign().charAt(15)=='6')
                labelPrinting.setTimeCategory("");
            if(waybill.getWaybillSign().charAt(15)=='9')
                labelPrinting.setTimeCategory("外单京准达");
        }

        try {
            if (request != null && request.getStartSiteType() != null && request.dmsCode != null
                    && waybill != null && StringHelper.isNotEmpty(request.getWaybillCode())
                    && WaybillUtil.isBusiWaybillCode(request.getWaybillCode())
                    && ((!BusinessUtil.isSignChar(waybill.getWaybillSign(),2,Constants.WAYBILL_SIGN_B)&& NumberHelper.isNumber(waybill.getVendorId()))||BusinessUtil.isSignChar(waybill.getWaybillSign(),1,Constants.WAYBILL_SIGN_B))) {

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
                    log.warn("调用promise接口获取外单时效失败，返回结果：" + JsonHelper.toJson(orderMarkingForeignResponse));
                }
                log.debug("调用promise获取外单时效返回数据" + JsonHelper.toJson(orderMarkingForeignResponse));

                //C2C面单预计送达时间从运单获取REQUIRE_TIME
                if(BusinessUtil.isSignChar(waybill.getWaybillSign(),29,'8')){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String foreCastTime = "";
                    if(waybill.getRequireTime() != null){
                        foreCastTime = sdf.format(waybill.getRequireTime());
                    }
                    labelPrinting.setPromiseText(foreCastTime);
                }
            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            log.error("外单调用promise接口异常" + e.toString() + (request == null ? "" : JsonHelper.toJson(request)),e);
        }
        //加promise调用获取时效具体信息
        return labelPrinting;
    }
}
