package com.jd.bluedragon.distribution.print.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.fce.dos.service.contract.OrderMarkingService;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * Created by wangtingwei on 2016/1/25.
 * 
 * promise 路由将合并，因此两者对时效的处理也一并放在此处
 */
@Service("promiseComposeService")
public class PromiseComposeServiceImpl implements  ComposeService {

    private static final Log log= LogFactory.getLog(PromiseComposeServiceImpl.class);

    
    
    @Autowired
    private OrderMarkingService orderMarkingService;
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
    	log.info("获取时效信息1"+JsonHelper.toJson(waybill));
    	log.info("获取时效信息2"+dmsCode);
    	log.info("获取时效信息3"+targetSiteCode);
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
            if(waybill.getWaybillSign().charAt(15)=='5')
                waybill.setTimeCategory("4日达");
            if(waybill.getWaybillSign().charAt(15)=='6')
                waybill.setTimeCategory("");
            if(waybill.getWaybillSign().charAt(15)=='9')
                waybill.setTimeCategory("外单京准达");
        }

        try {
        	log.info("获取时效信息3"+PropertiesHelper.newInstance().getValue("isRoutePredictDateEnabled"));
        	//如果是B网订单取路由时效数据,否则取promise数据
        	//40位不为0是快运0默认、1整车、2是纯配快运零担
        	//http://cf.jd.com/pages/viewpage.action?pageId=31916460
        	if(BusinessHelper.isB2b(waybill.getWaybillSign())&&"true".equals(PropertiesHelper.newInstance().getValue("isRoutePredictDateEnabled"))){
        		
        		 Integer configType = Constants.ROUTE_INTER_CONFIG_TYPE_QUAN_LIUCHENG_LVYUELV;//路由接口配置类型
        		 Integer bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B;
        		if(BusinessHelper.isSignInChars(waybill.getWaybillSign(), 40, '2', '5')){//纯外（纯配）
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CHUN_WAI_B2B;
        		}else if(BusinessHelper.isSignInChars(waybill.getWaybillSign(), 40, '3')){//仓配零担
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B;
        		}else{//1,4 整车
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_ZHENG_CHE_B2B;
        		}
        		
                BaseStaffSiteOrgDto startSite=baseMajorManager.getBaseSiteBySiteId(dmsCode);
                BaseStaffSiteOrgDto toSite=baseMajorManager.getBaseSiteBySiteId(targetSiteCode);
                String routeTimeText = null;
                if(startSite!=null&&toSite!=null)
                	routeTimeText = vrsRouteTransferRelationManager.queryRoutePredictDate(configType, bizzType, startSite.getCityId().toString(), toSite.getCityId().toString(), new Date());
                if(StringHelper.isNotEmpty(routeTimeText)){
                	waybill.setPromiseText(routeTimeText);
                }else{
                	waybill.setPromiseText("");
                }
        	}else if (SerialRuleUtil.isMatchReceiveWaybillNo(waybill.getWaybillCode())
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
                    log.warn("调用promise接口获取外单时效失败：" + orderMarkingForeignResponse == null ? "" : orderMarkingForeignResponse.toString());
                }
                log.debug("调用promise获取外单时效返回数据" + orderMarkingForeignResponse == null ? "" : JsonHelper.toJson(orderMarkingForeignResponse.toString()));

            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            log.error("外单调用promise接口异常" +waybill.getWaybillCode(),e);
        }
    }
}
