package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.fce.dos.service.contract.OrderMarkingService;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangtingwei on 2016/1/25.
 * 
 * 逻辑迁移到PromiseWaybillHandler
 */
@Service("promiseComposeService")
@Deprecated
public class PromiseComposeServiceImpl implements  ComposeService {

    private static final Logger log= LoggerFactory.getLogger(PromiseComposeServiceImpl.class);

    
    
    @Autowired
    private OrderMarkingService orderMarkingService;
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    @Qualifier("waybillQueryManager")
    private WaybillQueryManager waybillQueryManager;
    /**
     * 传入参数dmsCode为null时，调用接口传值为0
     */
    private static final String STR_DMS_CODE_NULL = "0";

    @Override
    public void handle(PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        // 外单多时效打标
        if(StringHelper.isNotEmpty(waybill.getWaybillSign()) && waybill.getWaybillSign().length() > 15) {
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
        	if(BusinessUtil.isB2b(waybill.getWaybillSign())&&"true".equals(PropertiesHelper.newInstance().getValue("isRoutePredictDateEnabled"))){
        		
        		 Integer configType = Constants.ROUTE_INTER_CONFIG_TYPE_QUAN_LIUCHENG_LVYUELV;//路由接口配置类型
        		 Integer bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B;
        		if(BusinessUtil.isSignInChars(waybill.getWaybillSign(), 40, '2', '5')){//纯外（纯配）
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CHUN_WAI_B2B;
        		}else if(BusinessUtil.isSignInChars(waybill.getWaybillSign(), 40, '3')){//仓配零担
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B;
        		}else{//1,4 整车
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_ZHENG_CHE_B2B;
        		}
        		
                BaseStaffSiteOrgDto startSite=baseMajorManager.getBaseSiteBySiteId(dmsCode);
                BaseStaffSiteOrgDto toSite=baseMajorManager.getBaseSiteBySiteId(waybill.getPrepareSiteCode());
                String routeTimeText = null;
                if(startSite!=null&&toSite!=null&&startSite.getCityId()!=null&&toSite.getCityId()!=null)
                	routeTimeText = vrsRouteTransferRelationManager.queryRoutePredictDate(configType, bizzType, startSite.getCityId().toString(), toSite.getCityId().toString(), new Date());
                if(StringHelper.isNotEmpty(routeTimeText)){
                	waybill.setPromiseText(routeTimeText);
                }else{
                	waybill.setPromiseText("");
                }
        	}else if (WaybillUtil.isBusiWaybillCode(waybill.getWaybillCode())
                    && ((!BusinessUtil.isSignChar(waybill.getWaybillSign(),2,Constants.WAYBILL_SIGN_B)&& NumberHelper.isNumber(waybill.getOrderCode()))||BusinessUtil.isSignChar(waybill.getWaybillSign(),1,Constants.WAYBILL_SIGN_B))) {

                log.debug("调用promise获取外单时效开始");

                OrderMarkingForeignRequest orderMarkingRequest = new OrderMarkingForeignRequest();
                if (BusinessUtil.isSignChar(waybill.getWaybillSign(),1,Constants.WAYBILL_SIGN_B))
                    orderMarkingRequest.setOrderId(Constants.ORDER_TYPE_B_ORDERNUMBER);//纯外单订单号设置为0
                else
                    orderMarkingRequest.setOrderId(Long.parseLong(waybill.getOrderCode()));//订单号
                orderMarkingRequest.setWaybillCode(waybill.getWaybillCode());//运单号
                String dmsCodeStr = STR_DMS_CODE_NULL;
                if(dmsCode != null){
                	dmsCodeStr = dmsCode.toString();
                }
                orderMarkingRequest.setOpeSiteId(dmsCodeStr);//分拣中心ID
                orderMarkingRequest.setOpeSiteName(dmsCodeStr);//分拣中心名称
                orderMarkingRequest.setOpesiteType( Constants.PROMISE_DISTRIBUTION_CENTER);
                orderMarkingRequest.setSource(Constants.DISTRIBUTION_SOURCE);
                orderMarkingRequest.setProvinceId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//省
                orderMarkingRequest.setCityId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//市
                orderMarkingRequest.setCountyId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//县
                orderMarkingRequest.setTownId(Constants.DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE);//镇
                orderMarkingRequest.setCurrentDate(new Date());//当前时间

                log.debug("调用promise获取外单时效传入参数" +JsonHelper.toJson(orderMarkingRequest));
                OrderMarkingForeignResponse orderMarkingForeignResponse = orderMarkingService.orderMarkingServiceForForeign(orderMarkingRequest);
                if (orderMarkingForeignResponse != null && orderMarkingForeignResponse.getResultCode() >= 1) {
                    waybill.setPromiseText(orderMarkingForeignResponse.getPromiseMsg());
                    waybill.setTimeCategory(orderMarkingForeignResponse.getSendpayDesc());
                } else {
                    log.warn("调用promise接口获取外单时效失败：{}" , JsonHelper.toJson(orderMarkingForeignResponse));
                }
                log.debug("调用promise获取外单时效返回数据{}"  , JsonHelper.toJson(orderMarkingForeignResponse));

                //C2C面单预计送达时间从运单获取REQUIRE_TIME
                if(BusinessUtil.isSignChar(waybill.getWaybillSign(),29,'8')){
                    String foreCastTime = "";
                    BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybill.getWaybillCode(), true,false, false, false);
                    BigWaybillDto data = baseEntity.getData();
                    if(data != null && data.getWaybill() != null && data.getWaybill().getRequireTime() != null){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        foreCastTime = sdf.format(data.getWaybill().getRequireTime());
                    }
                    waybill.setPromiseText(foreCastTime);
                }
            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            log.error("外单调用promise接口异常" +waybill.getWaybillCode(),e);
        }
    }
}
