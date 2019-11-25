package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.fce.dos.service.contract.OrderMarkingService;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
/**
 * 
 * @ClassName: PromiseWaybillHandler
 * @Description: 时效处理逻辑单元
 * @author: wuyoude
 * @date: 2018年1月29日 上午10:54:45
 *
 */
@Service
public class PromiseWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(PromiseWaybillHandler.class);
    @Autowired
    private OrderMarkingService orderMarkingService;
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    /**
     * 传入参数dmsCode为null时，调用接口传值为0
     */
    private static final String STR_DMS_CODE_NULL = "0";
	
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("获取时效信息");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		//调用逻辑设置时效信息
		handle(context,context.getBasePrintWaybill(), dmsCode, targetSiteCode);
		return context.getResult();
	}
	/**
	 * 旧逻辑，迁移来自PromiseComposeServiceImpl
	 * @param context
	 * @param basePrintWaybill
	 * @param dmsCode
	 * @param targetSiteCode
	 */
	private void handle(WaybillPrintContext context,
			BasePrintWaybill basePrintWaybill, Integer dmsCode,
			Integer targetSiteCode) {
		Waybill waybillData = context.getBigWaybillDto().getWaybill();
		String waybillSign = waybillData.getWaybillSign();
        // 外单多时效打标
        if(StringHelper.isNotEmpty(waybillSign) && waybillSign.length() > 15) {
            if(waybillSign.charAt(15)=='0')
                basePrintWaybill.setTimeCategory("");
            if(waybillSign.charAt(15)=='1')
                basePrintWaybill.setTimeCategory("当日达");
            if(waybillSign.charAt(15)=='2')
                basePrintWaybill.setTimeCategory("次日达");
            if(waybillSign.charAt(15)=='3')
                basePrintWaybill.setTimeCategory("隔日达");
            if(waybillSign.charAt(15)=='4')
                basePrintWaybill.setTimeCategory("次晨达");
            if(waybillSign.charAt(15)=='5')
                basePrintWaybill.setTimeCategory("4日达");
            if(waybillSign.charAt(15)=='6')
                basePrintWaybill.setTimeCategory("");
            if(waybillSign.charAt(15)=='9')
                basePrintWaybill.setTimeCategory("外单京准达");
        }

        try {
        	logger.info("获取时效信息3"+PropertiesHelper.newInstance().getValue("isRoutePredictDateEnabled"));
        	//如果是B网订单取路由时效数据,否则取promise数据
        	//40位不为0是快运0默认、1整车、2是纯配快运零担
        	//http://cf.jd.com/pages/viewpage.action?pageId=31916460
        	if(BusinessUtil.isB2b(waybillSign)&&"true".equals(PropertiesHelper.newInstance().getValue("isRoutePredictDateEnabled"))){
        		
        		 Integer configType = Constants.ROUTE_INTER_CONFIG_TYPE_QUAN_LIUCHENG_LVYUELV;//路由接口配置类型
        		 Integer bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B;
        		if(BusinessUtil.isSignInChars(waybillSign, 40, '2', '5')){//纯外（纯配）
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CHUN_WAI_B2B;
        		}else if(BusinessUtil.isSignInChars(waybillSign, 40, '3')){//仓配零担
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B;
        		}else{//1,4 整车
        			bizzType = Constants.ROUTE_INTER_BIZZ_TYPE_ZHENG_CHE_B2B;
        		}
        		
                BaseStaffSiteOrgDto startSite=baseMajorManager.getBaseSiteBySiteId(dmsCode);
                BaseStaffSiteOrgDto toSite=baseMajorManager.getBaseSiteBySiteId(basePrintWaybill.getPrepareSiteCode());
                String routeTimeText = null;
                if(startSite!=null&&toSite!=null&&startSite.getCityId()!=null&&toSite.getCityId()!=null)
                	routeTimeText = vrsRouteTransferRelationManager.queryRoutePredictDate(configType, bizzType, startSite.getCityId().toString(), toSite.getCityId().toString(), new Date());
                if(StringHelper.isNotEmpty(routeTimeText)){
                	basePrintWaybill.setPromiseText(routeTimeText);
                }else{
                	basePrintWaybill.setPromiseText("");
                }
        	}else if (WaybillUtil.isBusiWaybillCode(basePrintWaybill.getWaybillCode())
                    && ((!BusinessUtil.isSignChar(waybillSign,2,Constants.WAYBILL_SIGN_B)&& NumberHelper.isNumber(basePrintWaybill.getOrderCode()))||BusinessUtil.isSignChar(waybillSign,1,Constants.WAYBILL_SIGN_B))) {

                logger.debug("调用promise获取外单时效开始");

                OrderMarkingForeignRequest orderMarkingRequest = new OrderMarkingForeignRequest();
                if (BusinessUtil.isSignChar(waybillSign,1,Constants.WAYBILL_SIGN_B))
                    orderMarkingRequest.setOrderId(Constants.ORDER_TYPE_B_ORDERNUMBER);//纯外单订单号设置为0
                else
                    orderMarkingRequest.setOrderId(Long.parseLong(basePrintWaybill.getOrderCode()));//订单号
                orderMarkingRequest.setWaybillCode(basePrintWaybill.getWaybillCode());//运单号
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

                logger.debug("调用promise获取外单时效传入参数" +JsonHelper.toJson(orderMarkingRequest));
                OrderMarkingForeignResponse orderMarkingForeignResponse = orderMarkingService.orderMarkingServiceForForeign(orderMarkingRequest);
                if (orderMarkingForeignResponse != null && orderMarkingForeignResponse.getResultCode() >= 1) {
                    basePrintWaybill.setPromiseText(orderMarkingForeignResponse.getPromiseMsg());
                    basePrintWaybill.setTimeCategory(orderMarkingForeignResponse.getSendpayDesc());
                } else {
                    logger.warn("调用promise接口获取外单时效失败：" + JsonHelper.toJson(orderMarkingForeignResponse));
                }
                logger.debug("调用promise获取外单时效返回数据"  + JsonHelper.toJson(orderMarkingForeignResponse));

                //C2C面单预计送达时间从运单获取REQUIRE_TIME
                if(BusinessUtil.isSignChar(waybillSign,29,'8')){
                    String foreCastTime = "";
                    if(waybillData.getRequireTime() != null){
                        foreCastTime =  DateHelper.formatDate(waybillData.getRequireTime()
        						,DateHelper.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM);
                    }
                    basePrintWaybill.setPromiseText(foreCastTime);
                }
            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            logger.error("外单调用promise接口异常" +basePrintWaybill.getWaybillCode(),e);
        }
        this.dealJZD(waybillSign, waybillData, basePrintWaybill);
    }
	/**
	 * 处理sop京准达时效信息
	 * @param waybillSign
	 * @param waybillData
	 * @param basePrintWaybill
	 */
	public void dealJZD(String waybillSign,Waybill waybillData,BasePrintWaybill basePrintWaybill){
        //waybill_sign  第31位等于6时，打印【准】字,预计送达时间字段读取运单系统的预计送达时段,时效显示： 预计送达时段为开始时间+终止时间
		if(BusinessUtil.isSopJZD(waybillSign)||BusinessUtil.isC2CJZD(waybillSign)){
			//promiseText临时变量
			String promiseText = null;
			String startTimeStr = null;
			String endTimeStr =  null;
			if(waybillData.getRequireStartTime() != null){
				startTimeStr = DateHelper.formatDate(waybillData.getRequireStartTime(),DateHelper.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM);
				promiseText = startTimeStr;
			}
			if(waybillData.getRequireTime() != null){
				endTimeStr = DateHelper.formatDate(waybillData.getRequireTime()
						,DateHelper.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM);
				if(startTimeStr == null){
					promiseText = endTimeStr;
				}else if(startTimeStr.substring(0, 10).equals(endTimeStr.substring(0, 10))){
					promiseText += "-" + endTimeStr.substring(11);
				}else{
					promiseText += "-" + endTimeStr;
				}
			}
			if(promiseText != null){
			    if(BusinessUtil.isSopJZD(waybillSign)) {
                    basePrintWaybill.setPromiseText(promiseText);
                }
			    if(BusinessUtil.isC2CJZD(waybillSign)){
                    basePrintWaybill.setPromiseText(TextConstants.TEXT_JZD + promiseText);
                }
			}
		}
	}
}
