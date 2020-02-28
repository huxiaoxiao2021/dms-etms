package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.log.BizOperateTypeConstants;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.weight.service.WeightService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import com.jd.preseparate.vo.BaseResponseIncidental;
import com.jd.preseparate.vo.MediumStationOrderInfo;
import com.jd.preseparate.vo.OriginalOrderInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 纯外单中小件二次预分拣服务
 * Created by shipeilin on 2018/2/1.
 */
@Service("preSortingSecondService")
public class PreSortingSecondServiceImpl implements PreSortingSecondService{
    private static final Logger log = LoggerFactory.getLogger(PreSortingSecondServiceImpl.class);

    @Autowired
	private LogEngine logEngine;

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /* MQ消息生产者： topic:bd_waybill_original_site_change*/
    @Autowired
    @Qualifier("waybillSiteChangeProducer")
    private DefaultJMQProducer waybillSiteChangeProducer;
    
    @Autowired
    private DmsOperateHintService dmsOperateHintService;

    @Autowired
    private WeightService weightService;
    
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;
    
    @Autowired
    private PrintRecordService printRecordService;
    /**
     * 2次预分拣变更提示信息
     */
    private static final String SITE_CHANGE_MSG ="单号‘%s’由中件站配送，请务必更换包裹标签";
    
    private static final String SITE_CHANGE_MSG_FORMAT = "预分拣站点变更为【%s】，请操作包裹补打更换面单！";

    /**
     * 一单一件且重新称重或量方的触发二次预分拣
     * @param context 上下文
     * @return 处理结果，处理是否通过
     */
    @Override
    public InterceptResult<String> preSortingAgain(WaybillPrintContext context){
        InterceptResult<String> interceptResult = context.getResult();
        Waybill waybill = context.getWaybill();
		InterceptResult<String> result = context.getResult();
		String waybillCode = waybill.getWaybillCode();
		String waybillSign = waybill.getWaybillSign();
		
        Integer oldPrepareSiteCode = waybill.getSiteCode();
        //指定目的站点targetSiteCode>0或预分拣站点<=0或999999999EMS全国直发，则无法触发二次预分拣
        if(NumberHelper.gt0(context.getRequest().getTargetSiteCode())
        		||!NumberHelper.gt0(oldPrepareSiteCode)
                || ComposeService.PREPARE_SITE_CODE_EMS_DIRECT.equals(oldPrepareSiteCode)){
            interceptResult.toSuccess();
            return interceptResult;
        }
        BaseStaffSiteOrgDto oldPreSiteInfo = baseMajorManager.getBaseSiteBySiteId(oldPrepareSiteCode);
        if(oldPreSiteInfo == null){
            interceptResult.toSuccess();
            return interceptResult;
        }
        int packageNum = waybill.getPackageNum();
        //一单一件 纯外单，上传了新的体积或重量 ，走原中小件分离逻辑
        if(packageNum == 1 
        		&& BusinessUtil.isExternal(waybill.getWaybillSign())
        		&& BusinessHelper.hasWeightOrVolume(context.getRequest())){
            OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
            originalOrderInfo.setWeight(context.getRequest().getWeightOperFlow().getWeight());
            originalOrderInfo.setHeight(context.getRequest().getWeightOperFlow().getHigh());
            originalOrderInfo.setLength(context.getRequest().getWeightOperFlow().getLength());
            originalOrderInfo.setWidth(context.getRequest().getWeightOperFlow().getWidth());
            originalOrderInfo.setWaybillCode(waybill.getWaybillCode());
            originalOrderInfo.setPackageCode(waybill.getPackList().get(0).getPackCode());
            originalOrderInfo.setOriginalStationId(oldPrepareSiteCode);
            originalOrderInfo.setOriginalStationName(oldPreSiteInfo.getSiteName());
            JdResult<BaseResponseIncidental<MediumStationOrderInfo>> mediumStationOrderInfo = preseparateWaybillManager.getMediumStation(originalOrderInfo);
            //接口调用失败/返回站点ID为空/小于等于0，直接通过不强制拦截
            if(mediumStationOrderInfo.isSucceed()
            		&& mediumStationOrderInfo.getData() != null
            		&& mediumStationOrderInfo.getData().getData() != null
            		&& NumberHelper.gt0(mediumStationOrderInfo.getData().getData().getMediumStationId())){
	            MediumStationOrderInfo newPreSiteInfo = mediumStationOrderInfo.getData().getData();
	            //新预分拣站点不同于原站点则提示换单并设置为新的预分拣站点
	            if(!newPreSiteInfo.getMediumStationId().equals(oldPrepareSiteCode)){
	            	//换站点了
	                log.warn("中小件二次预分拣换预分拣站点了：{}", newPreSiteInfo.getMediumStationId());
	                this.resetPresiteInfo(context, newPreSiteInfo);
	                context.appendMessage(String.format(SITE_CHANGE_MSG, context.getRequest().getBarCode()));
	                context.setStatus(InterceptResult.STATUS_WEAK_PASSED);
	                interceptResult.toWeakSuccess(JdResult.CODE_SUC, String.format(SITE_CHANGE_MSG, context.getRequest().getBarCode()));
	                sendSiteChangeMQ(context, newPreSiteInfo);
	                return interceptResult;
	            }
            }
        }
        Integer operateType = context.getRequest().getOperateType();
		boolean isPackageReprint = WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT_TYPE.equals(operateType);
		//只处理平台打印、包裹补打、站点平台打印
        if(!WaybillPrintOperateTypeEnum.PLATE_PRINT_TYPE.equals(operateType)
        		&&!WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT_TYPE.equals(operateType)
        		&&!isPackageReprint){
        	return interceptResult;
        }
		//业务判断
		if(!BusinessUtil.isSignY(waybillSign, 1)
				&&BusinessUtil.isSignChar(waybillSign, 36, '0')){
			//包裹补打业务-存在补打提醒信息才会处理
			if(isPackageReprint){
				return this.dealPackageReprint(context);
			}
			WeightOperFlow totalWeightInfo = getTotalWeight(context);
			if(totalWeightInfo != null){
	            OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
	            originalOrderInfo.setWeight(totalWeightInfo.getWeight());
	            originalOrderInfo.setBulk(totalWeightInfo.getVolume());
	            originalOrderInfo.setWaybillCode(waybill.getWaybillCode());
	            originalOrderInfo.setPackageCode(waybill.getPackList().get(0).getPackCode());
	            originalOrderInfo.setOriginalStationId(oldPrepareSiteCode);
	            originalOrderInfo.setOriginalStationName(oldPreSiteInfo.getSiteName());
	            originalOrderInfo.setProvinceId(context.getBigWaybillDto().getWaybill().getProvinceId());
	            originalOrderInfo.setCityId(context.getBigWaybillDto().getWaybill().getCityId());
	            originalOrderInfo.setCountyId(context.getBigWaybillDto().getWaybill().getCountryId());
	            originalOrderInfo.setTownId(context.getBigWaybillDto().getWaybill().getTownId());
	            JdResult<BaseResponseIncidental<MediumStationOrderInfo>> mediumStationOrderInfo = preseparateWaybillManager.getMediumStation(originalOrderInfo);
	            //接口调用失败/返回站点ID为空/小于等于0，直接通过不强制拦截
	            if(mediumStationOrderInfo.isSucceed()
	            		&& mediumStationOrderInfo.getData() != null
	            		&& mediumStationOrderInfo.getData().getData() != null
	            		&& NumberHelper.gt0(mediumStationOrderInfo.getData().getData().getMediumStationId())){
		            MediumStationOrderInfo newPreSiteInfo = mediumStationOrderInfo.getData().getData();
		            //新预分拣站点不同于原站点则提示换单并设置为新的预分拣站点
		            if(!newPreSiteInfo.getMediumStationId().equals(oldPrepareSiteCode)){
		                log.warn("C网操作B2B传站业务:{}预分拣站点变更：{}->{}",waybillCode,
								oldPrepareSiteCode,newPreSiteInfo.getMediumStationId());
		                //一单一件，设置打印站点为新站点,发送站点变更mq
		                if(packageNum == 1){
			                this.resetPresiteInfo(context, newPreSiteInfo);
			                this.sendSiteChangeMQ(context, newPreSiteInfo);
		                }else{
			                String siteChangeMsg = String.format(SITE_CHANGE_MSG_FORMAT, newPreSiteInfo.getMediumStationId()
			                		+newPreSiteInfo.getMediumStationName());
			                context.appendMessage(siteChangeMsg);
			                context.setStatus(InterceptResult.STATUS_WEAK_PASSED);
			                result.toWeakSuccess(siteChangeMsg);
			                //发送包裹补打提醒信息
			                this.sendSiteChangeHitMsg(context,siteChangeMsg,newPreSiteInfo);
		                }
		            }
	            }
			}
		}
        return interceptResult;
    }
    /**
     * 根据新预分拣站点重新设置waybill及response预分拣站点
     * @param context
     * @param newPreSiteInfo
     */
    private void resetPresiteInfo(WaybillPrintContext context, MediumStationOrderInfo newPreSiteInfo){
    	if(context.getResponse()!=null){
    		context.getResponse().setPrepareSiteCode(newPreSiteInfo.getMediumStationId());
        	context.getResponse().setPrepareSiteName(newPreSiteInfo.getMediumStationName());
        	context.getResponse().setPrintSiteName(newPreSiteInfo.getMediumStationName());
        	context.getResponse().setRoad(newPreSiteInfo.getMediumStationRoad());
        	context.getResponse().setRoadCode(newPreSiteInfo.getMediumStationRoad());
    	}
    	//站点平台及驻场打印
    	context.getWaybill().setSiteCode(newPreSiteInfo.getMediumStationId());
    	context.getWaybill().setSiteName(newPreSiteInfo.getMediumStationName());
    	context.getWaybill().setRoad(newPreSiteInfo.getMediumStationRoad());
    }
    /**
     * 发送外单中小件预分拣站点变更mq消息
     * @param context
     * @param newPreSiteInfo
     */
    private void sendSiteChangeMQ(WaybillPrintContext context, MediumStationOrderInfo newPreSiteInfo){
		long startTime=new Date().getTime();

		SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
        siteChangeMqDto.setWaybillCode(context.getWaybill().getWaybillCode());
        siteChangeMqDto.setPackageCode(context.getWaybill().getPackList().get(0).getPackCode());
        siteChangeMqDto.setNewSiteId(newPreSiteInfo.getMediumStationId());
        siteChangeMqDto.setNewSiteName(newPreSiteInfo.getMediumStationName());
        siteChangeMqDto.setNewSiteRoadCode(newPreSiteInfo.getMediumStationRoad());
        siteChangeMqDto.setOperatorId(context.getRequest().getUserCode());
        siteChangeMqDto.setOperatorName(context.getRequest().getUserName());
        siteChangeMqDto.setOperatorSiteId(context.getRequest().getSiteCode());
        siteChangeMqDto.setOperatorSiteName(context.getRequest().getSiteName());
        siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
        try {
            waybillSiteChangeProducer.sendOnFailPersistent(context.getWaybill().getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
            log.warn("发送预分拣站点变更mq消息成功：{}", JsonHelper.toJsonUseGson(siteChangeMqDto));
        } catch (Exception e) {
            log.error("发送预分拣站点变更mq消息失败：{}", JsonHelper.toJsonUseGson(siteChangeMqDto), e);
        }finally{

			long endTime = new Date().getTime();

			JSONObject request=new JSONObject();
			request.put("waybillCode",siteChangeMqDto.getWaybillCode());
			request.put("packageCode",siteChangeMqDto.getPackageCode());
			request.put("operatorName",siteChangeMqDto.getOperatorName());

			JSONObject response=new JSONObject();
			response.put("keyword2", String.valueOf(siteChangeMqDto.getOperatorId()));
			response.put("keyword3", waybillSiteChangeProducer.getTopic());
			response.put("keyword4", siteChangeMqDto.getOperatorSiteId().longValue());
			response.put("content", JsonHelper.toJsonUseGson(siteChangeMqDto));

			BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
					.bizType(BizOperateTypeConstants.SORTING_PRE_SORTING_SITE_CHANGE.getBizTypeCode())
					.operateType(BizOperateTypeConstants.SORTING_PRE_SORTING_SITE_CHANGE.getOperateTypeCode())
					.methodName("PreSortingSecondServiceImpl#sendSiteChangeMQ")
					.operateRequest(request)
					.operateResponse(response)
					.processTime(endTime,startTime)
					.build();

			logEngine.addLog(businessLogProfiler);


        	SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), String.valueOf(siteChangeMqDto.getOperatorId()), waybillSiteChangeProducer.getTopic(),
                    siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ);
        }
    }
	/**
	 * 处理包裹补打操作-第一次补打发送mq并且标记打印记录,所有包裹打印完毕后清除补打提醒信息
	 * @param context
	 */
	private InterceptResult<String> dealPackageReprint(WaybillPrintContext context) {
		Waybill waybill = context.getWaybill();
		String barCode = context.getRequest().getBarCode();
		String waybillCode = waybill.getWaybillCode();
		DmsOperateHint dmsOperateHint = this.dmsOperateHintService.getNeedReprintHint(waybillCode);
		if(dmsOperateHint != null){
			MediumStationOrderInfo newPreSiteInfo = JsonHelper.fromJson(
					dmsOperateHint.getHintContent(), MediumStationOrderInfo.class);
			//判断是否按运单补打
			boolean isPrintByWaybill = waybillCode.equals(barCode);
			//一单一件设置为按运单打印
			if(waybill.getPackageNum() == 1){
				isPrintByWaybill = true;
			}
			//按运单补打,则关闭提醒信息
			boolean needCloseHintMsg = isPrintByWaybill;
			boolean needSendMq = false;
			Set<String> reprintRecords = this.printRecordService.getHasReprintPackageCodes(waybillCode);
			if(!isPrintByWaybill){
				//按包裹补打，不包含本次补打，存储一条补打记录
				if(reprintRecords == null || !reprintRecords.contains(barCode)){
					this.printRecordService.saveReprintRecord(barCode);
					//判断是否已补打完所有包裹
					if(reprintRecords != null && reprintRecords.size()==(waybill.getPackageNum()-1)){
						needCloseHintMsg = true;
					}
				}
			}else{
				needCloseHintMsg = true;
			}
			if(reprintRecords == null||reprintRecords.isEmpty()){
				needSendMq = true;
			}
			if(needCloseHintMsg){
				log.warn("关闭包裹补打提醒：{}",waybill.getWaybillCode());
				dmsOperateHint.setIsEnable(Constants.INTEGER_FLG_FALSE);
				dmsOperateHintService.saveOrUpdate(dmsOperateHint);
				log.warn("清除包裹补打记录：{}",waybill.getWaybillCode());
				this.printRecordService.deleteReprintRecordsByWaybillCode(waybillCode);
			}
			//发送站点变更的mq给运单
			if(needSendMq){
				log.warn("包裹补打-发送站点变更的mq：{}",waybill.getWaybillCode());
				sendSiteChangeMQ(context, newPreSiteInfo);
			}
			this.resetPresiteInfo(context, newPreSiteInfo);
		}
		return context.getResult();
	}
	/**
	 * 新增一条站点变更提示信息
	 * @param context
	 * @param msg
	 */
	private void sendSiteChangeHitMsg(WaybillPrintContext context,String msg, MediumStationOrderInfo newPreSiteInfo) {
		DmsOperateHint siteChangeHit = new DmsOperateHint();
		siteChangeHit.setDmsSiteCode(context.getRequest().getDmsSiteCode());
		siteChangeHit.setDmsSiteName(context.getRequest().getSiteName());
		siteChangeHit.setWaybillCode(context.getWaybill().getWaybillCode());
		siteChangeHit.setHintType(DmsOperateHint.HINT_TYPE_SYS);
		siteChangeHit.setHintCode(DmsOperateHint.HINT_CODE_NEED_REPRINT);
		siteChangeHit.setHintName(DmsOperateHint.HINT_NAME_NEED_REPRINT);
		siteChangeHit.setHintMessage(msg);
		siteChangeHit.setHintContent(JsonHelper.toJson(newPreSiteInfo));
		siteChangeHit.setIsEnable(Constants.INTEGER_FLG_TRUE);
		log.warn("新增一条包裹补打提醒信息：{}",siteChangeHit.getWaybillCode());
		dmsOperateHintService.saveOrUpdate(siteChangeHit);
	}
	/**
	 * 根据包裹号/运单号获取运单总重量
	 * @param context
	 * @return
	 */
	private WeightOperFlow getTotalWeight(WaybillPrintContext context){
		WeightOperFlow totalWeightInfo = new WeightOperFlow();
		String waybillCode = context.getWaybill().getWaybillCode();
		List<Pack> packageList = context.getWaybill().getPackList();
		int packageNum = context.getWaybill().getPackageNum();
		//一单一件返回当前称重信息/取当前包裹的分拣中心称重信息
		if(packageNum == 1){
			if(BusinessHelper.hasWeightOrVolume(context.getRequest())){
				return context.getRequest().getWeightOperFlow();
			}else{
				return weightService.getDmsWeightByPackageCode(packageList.get(0).getPackCode());
			}
		}
		Map<String, WeightOperFlow> weightInfos = weightService.getDmsWeightsByWaybillCode(waybillCode);
		String barCode = context.getRequest().getBarCode();
		//判断是否按运单补打
		boolean isPrintByWaybill = waybillCode.equals(barCode);
		if(weightInfos!=null && weightInfos.size()>=(packageList.size()-1)){
			if(context.getRequest().getWeightOperFlow()!=null){
				String packageCode = null;
				if(isPrintByWaybill){
					packageCode = context.getWaybill().getPackList().get(packageNum-1).getPackCode();
					for(Pack pack:context.getWaybill().getPackList()){
						if(pack.getIsPrintPack()!=Waybill.IS_PRINT_PACK){
							packageCode = pack.getPackCode();
							break;
						}
					}
				}else{
					packageCode = barCode;
				}
				weightInfos.put(packageCode, context.getRequest().getWeightOperFlow());
			}
			//包裹集齐，累加重量和体积，作为调用预分拣的参数
			if(weightInfos.size()==packageList.size()){
				double weightSum = 0.0;
				double volumeSum = 0.0;
				for(String key:weightInfos.keySet()){
					WeightOperFlow weightInfo = weightInfos.get(key);
					weightSum += weightInfo.getWeight();
					volumeSum += weightInfo.getVolume();
				}
				totalWeightInfo.setWeight(weightSum);
				totalWeightInfo.setVolume(volumeSum);
				return totalWeightInfo;
			}
		}
		return null;
	}
}
