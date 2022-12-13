package com.jd.bluedragon.distribution.consumer.send;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jsf.address.DmsGbAddressLevelsResponse;
import com.jd.bluedragon.core.jsf.address.GbDistrictJDDistrictMapServiceManager;
import com.jd.bluedragon.core.jsf.eclp.ThirdJsfInterfaceManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.send.domain.DmsSendToEmsWaybillInfoMq;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyRequest;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyRequestItem;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesCompanyResponse;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesOperType;
import com.jd.bluedragon.external.crossbow.postal.domain.TracesOperTypeMap;
import com.jd.bluedragon.external.crossbow.postal.domain.WaybillInfoRequest;
import com.jd.bluedragon.external.crossbow.postal.domain.WaybillInfoResponse;
import com.jd.bluedragon.external.crossbow.postal.enums.DoTypeEnum;
import com.jd.bluedragon.external.crossbow.postal.enums.InsuranceFlagEnum;
import com.jd.bluedragon.external.crossbow.postal.enums.InternationalTypeEnum;
import com.jd.bluedragon.external.crossbow.postal.enums.ProductTypeEnum;
import com.jd.bluedragon.external.crossbow.postal.enums.TraceTypeEnum;
import com.jd.bluedragon.external.crossbow.postal.manager.EmsTracesCompanyManager;
import com.jd.bluedragon.external.crossbow.postal.manager.EmsWaybillInfoManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dbs.util.CollectionUtils;
import com.jd.etms.third.service.domain.ReceiptStateParameter;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 分拣发邮政运单mq消费逻辑
 * @author wuyoude
 */
@Service("dmsSendToEmsWaybillInfoConsumer")
public class DmsSendToEmsWaybillInfoConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DmsSendToEmsWaybillInfoConsumer.class);
    
	private static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("0.00");
	
    @Autowired
    @Qualifier("emsWaybillInfoManager")
    private EmsWaybillInfoManager emsWaybillInfoManager;
    
    @Autowired
    @Qualifier("emsTracesCompanyManager")
    private EmsTracesCompanyManager emsTracesCompanyManager;
    
    @Autowired
    @Qualifier("thirdJsfInterfaceManager")
    private ThirdJsfInterfaceManager thirdJsfInterfaceManager;
    
    @Autowired
    @Qualifier("gbDistrictJDDistrictMapServiceManager")
    private GbDistrictJDDistrictMapServiceManager gbDistrictJDDistrictMapServiceManager;
    
    @Autowired
    private WaybillTraceManager waybillTraceManager;
    
    @Autowired
    private WaybillPackageManager waybillPackageManager;
    
    
    @Autowired
    @Qualifier("tracesOperTypeMap")
    private TracesOperTypeMap tracesOperTypeMap;
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Value("${dms.external.postal.dataSource:JD}")
    private String dataSource;
    
    private static final Integer STATUS_NOTICE_ECLP = -2200;
    
    @Override
    public void consume(Message message) throws Exception{
        CallerInfo info = Profiler.registerInfo("dmsSendToEmsWaybillInfoConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("发货明细消息dmsSendToEmsWaybillInfoConsumer-消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            // 将mq消息体转换成SendDetail对象
            DmsSendToEmsWaybillInfoMq sendDataMq = JsonHelper.fromJson(message.getText(), DmsSendToEmsWaybillInfoMq.class);
            if (sendDataMq == null || StringHelper.isEmpty(sendDataMq.getPackageBarcode())) {
                log.warn("dmsSendToEmsWaybillInfoConsumer:消息体[{}]转换实体失败或没有合法的包裹号",message.getText());
                return;
            }
            //组装发邮政运单信息传输接口数据，并发送给邮管局
            boolean sendSuc = buildAndSendWaybillInfoToEms(sendDataMq);
            if(sendSuc) {
                //发送成功，通知eclp
            	sendNoticeToEclp(sendDataMq);
                //组装发邮政运单信息传输接口数据，并发送给邮管局
            	buildAndSendWaybillTracesToEms(sendDataMq);
            }

        }catch(Exception e){
            Profiler.functionError(info);
            log.error("分拣发邮政运单mq消费失败:"+message.getText(), e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }
    /**
     * 组装发邮政运单信息传输接口数据，并发送给邮管局
     * @param sendDetail
     */
    private boolean buildAndSendWaybillInfoToEms(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	WaybillInfoRequest request = new WaybillInfoRequest();
    	request.setDoType(DoTypeEnum.A.toString());
    	request.setWaybillNo(sendDataMq.getPackageBarcode());
    	request.setProductCode(ProductTypeEnum.STANDARD.getCode());
    	request.setIsInternational(InternationalTypeEnum.INTERNAL.getCode());
    	if(sendDataMq.getBusiId() != null) {
    		request.setPickupAttribute(sendDataMq.getBusiId().toString());
    	}
    	//取包裹重量
    	DeliveryPackageD packageData = waybillPackageManager.getPackageInfoByPackageCode(sendDataMq.getPackageBarcode());
    	Double weight = 0d;
    	if(packageData != null) {
    		if(NumberHelper.gt0(packageData.getAgainWeight())) {
    			weight = packageData.getAgainWeight();
    		}else if(NumberHelper.gt0(packageData.getGoodWeight())) {
    			weight = packageData.getGoodWeight();
    		}
    		//单位转成g
    		weight = weight * 1000;
    	}
    	request.setRealWeight(WEIGHT_FORMAT.format(weight));
    	//设置收寄件人信息
    	request.setSenderLinker(sendDataMq.getConsigner());
    	request.setSenderMobile(sendDataMq.getConsignerMobile());
    	request.setSenderAddress(sendDataMq.getConsignerAddress());
    	request.setReceiverLinker(sendDataMq.getReceiverName());
    	request.setReceiverMobile(sendDataMq.getReceiverMobile());
    	request.setReceiverAddress(sendDataMq.getReceiverAddress());

    	request.setInsuranceFlag(InsuranceFlagEnum.N.getCode());
    	
    	request.setSenderRegionCode(getSenderRegionCode(sendDataMq));
    	request.setReceiverRegionCode(getReceiverRegionCode(sendDataMq));
    	
    	log.info("分拣发邮政运单信息request:{}",JsonHelper.toJson(request));
    	WaybillInfoResponse waybillResponse= emsWaybillInfoManager.doRestInterface(request);
    	log.info("分拣发邮政运单信息response:{}",JsonHelper.toJson(waybillResponse));
    	if(waybillResponse != null && waybillResponse.checkSucceed()) {
    		return true;
    	}
    	return false;
    }
    private Integer getReceiverJdCode(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	if(NumberHelper.gt0(sendDataMq.getReceiverTownId())) {
    		return sendDataMq.getReceiverTownId();
    	}
    	if(NumberHelper.gt0(sendDataMq.getReceiverCountryId())) {
    		return sendDataMq.getReceiverCountryId();
    	}
    	if(NumberHelper.gt0(sendDataMq.getReceiverCityId())) {
    		return sendDataMq.getReceiverCityId();
    	}
    	if(NumberHelper.gt0(sendDataMq.getReceiverProvinceId())) {
    		return sendDataMq.getReceiverProvinceId();
    	}
    	return null;
    }
    private String getReceiverRegionCode(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	JdResult<DmsGbAddressLevelsResponse> gbResult= this.gbDistrictJDDistrictMapServiceManager.getGBDistrictByJDCode(getReceiverJdCode(sendDataMq));
    	if(gbResult != null 
    			&& gbResult.isSucceed()
    			&& gbResult.getData() != null) {
    		return gbResult.getData().getGbCode();
    		
    	}else {
    		log.warn("获取收件人12位区划编码失败:{}",JsonHelper.toJson(gbResult));
    	}
		return null;
	}
    /**
     * 优先取TownId
     * @param sendDataMq
     * @return
     */
	private Integer getSenderJdCode(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	if(NumberHelper.gt0(sendDataMq.getConsignerTownId())) {
    		return sendDataMq.getConsignerTownId();
    	}
    	if(NumberHelper.gt0(sendDataMq.getConsignerCountryId())) {
    		return sendDataMq.getConsignerCountryId();
    	}
    	if(NumberHelper.gt0(sendDataMq.getConsignerCityId())) {
    		return sendDataMq.getConsignerCityId();
    	}
    	if(NumberHelper.gt0(sendDataMq.getConsignerProvinceId())) {
    		return sendDataMq.getConsignerProvinceId();
    	}
    	return null;
	}
	private String getSenderRegionCode(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	JdResult<DmsGbAddressLevelsResponse> gbResult= this.gbDistrictJDDistrictMapServiceManager.getGBDistrictByJDCode(getSenderJdCode(sendDataMq));
    	if(gbResult != null 
    			&& gbResult.isSucceed()
    			&& gbResult.getData() != null) {
    		return gbResult.getData().getGbCode();
    		
    	}else {
    		log.warn("获取寄件人12位区划编码失败:{}",JsonHelper.toJson(gbResult));
    	}
		return null;
	}

	/**
     * 发送成功，通知eclp
     * @param sendDetail
     */
    private void sendNoticeToEclp(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	List<ReceiptStateParameter> list = new ArrayList<ReceiptStateParameter>();
    	ReceiptStateParameter param = new ReceiptStateParameter();
		//查询站点
		BaseStaffSiteOrgDto preSiteData = baseMajorManager.getBaseSiteBySiteId(sendDataMq.getPreSiteCode());
		if(preSiteData != null) {
			param.setOrgId(preSiteData.getOrgId());
			param.setOrgName(preSiteData.getOrgName());
			param.setZdId(preSiteData.getSiteCode());
			param.setZdName(preSiteData.getSiteName());
			param.setZdType(preSiteData.getSiteType());
		}
		param.setOrderId(sendDataMq.getWaybillCode());
		param.setShipId(sendDataMq.getPackageBarcode());
		param.setState(STATUS_NOTICE_ECLP);
		if(sendDataMq.getOperatorId() != null){
			param.setOperatorId(sendDataMq.getOperatorId());
		}
		param.setOperatorName(sendDataMq.getOperatorName());
    	list.add(param);
    	JdResult<List<ReceiptStateParameter>> eclpResult = thirdJsfInterfaceManager.partnerReceiptState(list);
    	if(!eclpResult.isSucceed()) {
    		log.warn("分拣发邮政通知eclp失败:{}",JsonHelper.toJson(eclpResult));
    	}
    }
    /**
     * 组装发邮政运单信息传输接口数据，并发送给邮管局
     * @param sendDetail
     */
    private void buildAndSendWaybillTracesToEms(DmsSendToEmsWaybillInfoMq sendDataMq) {
    	
    	BaseEntity<List<PackageState>> traceData = waybillTraceManager.getPkStateByPCode(sendDataMq.getPackageBarcode());
    	if(traceData == null || CollectionUtils.isEmpty(traceData.getData())) {
    		log.warn("包裹号{}全程跟踪为空，不发送邮政:",sendDataMq.getPackageBarcode());
    		return;
    	}
    	TracesCompanyRequest request = new TracesCompanyRequest();
    	List<TracesCompanyRequestItem> traces = new ArrayList<TracesCompanyRequestItem>();
    	for(PackageState item: traceData.getData()) {
    		if(tracesOperTypeMap.getStateCodeToEmsMap().containsKey(item.getState())) {
    			traces.add(toEmsTrace(item));
    		}
    	}
    	request.setTraces(traces);
    	log.info("分拣发邮政全程跟踪信息request:{}",JsonHelper.toJson(request));
    	TracesCompanyResponse tracesResponse= emsTracesCompanyManager.doRestInterface(request);
    	log.info("分拣发邮政全程跟踪信息response:{}",JsonHelper.toJson(tracesResponse));
    }
    /**
     * 转换成外部全程跟踪对象
     * @param packageState
     * @return
     */
	private TracesCompanyRequestItem toEmsTrace(PackageState packageState) {
		TracesCompanyRequestItem trace = new TracesCompanyRequestItem();
		trace.setWaybillNo(packageState.getPackageBarcode());
		trace.setOpTime(packageState.getCreateTime().getTime()/1000);
		trace.setOpCode(tracesOperTypeMap.getStateCodeToEmsMap().get(packageState.getState()).getCode());
		trace.setOpName(tracesOperTypeMap.getStateCodeToEmsMap().get(packageState.getState()).getName());
		trace.setOpDesc(packageState.getRemark());
		//查询操作网点对应的省市
		BaseStaffSiteOrgDto operatorSite = baseMajorManager.getBaseSiteBySiteId(packageState.getOperatorSiteId());
		if(operatorSite != null) {
			trace.setOpOrgProvName(operatorSite.getProvinceName());
			trace.setOpOrgCity(operatorSite.getCityName());
			trace.setOpOrgName(operatorSite.getSiteName());
		}
		if(packageState.getOperatorUserId() != null) {
			trace.setOperatorNo(packageState.getOperatorUserId().toString());
		}
		trace.setOperatorName(packageState.getOperatorUser());
		trace.setDataSource(dataSource);
		trace.setTraceType(TraceTypeEnum.INTERNAL.getCode());
		return trace;
	}
}
