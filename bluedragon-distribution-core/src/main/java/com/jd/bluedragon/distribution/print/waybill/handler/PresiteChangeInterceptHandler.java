package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.weight.service.WeightService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.preseparate.vo.BaseResponseIncidental;
import com.jd.preseparate.vo.MediumStationOrderInfo;
import com.jd.preseparate.vo.OriginalOrderInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
/**
 * @ClassName: PresiteChangeInterceptHandler
 * @Description: C网操作B2B传站业务，预分拣站点变更拦截服务
 * @author: wuyoude
 * @date: 2018年4月18日 上午10:37:54
 */
@Service
public class PresiteChangeInterceptHandler implements InterceptHandler<WaybillPrintContext,String>{
	private static final Log logger= LogFactory.getLog(PresiteChangeInterceptHandler.class);
	private static final String SITE_CHANGE_MSG_FORMAT = "预分拣站点变更为【%s】，请操作包裹补打更换面单！";
    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    @Qualifier("waybillSiteChangeProducer")
    private DefaultJMQProducer waybillSiteChangeProducer;
    
    @Autowired
    private DmsOperateHintService dmsOperateHintService;

    @Autowired
    private WeightService weightService;
    
    @Autowired
    private RedisManager redisManager;
    
	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		logger.info("C网操作B2B传站业务，预分拣站点变更拦截");
		InterceptResult<String> result = context.getResult();
		WaybillPrintResponse printInfo = context.getResponse();
		String waybillCode = printInfo.getWaybillCode();
		String waybillSign = printInfo.getWaybillSign();
		//包裹数量
		int packageNum = 0;
        if(printInfo.getPackList() != null){
        	packageNum = printInfo.getPackList().size();
        };
		//业务判断
		if(!BusinessHelper.isSignY(waybillSign, 1)
				&&BusinessHelper.isSignChar(waybillSign, 36, '0')){
			boolean isPackageReprint = WaybillPrintOperateTypeEnum
					.PACKAGE_AGAIN_PRINT_TYPE.equals(context.getRequest().getOperateType());
			//包裹补打业务-存在补打提醒信息才会处理
			if(isPackageReprint){
				if(!this.dmsOperateHintService.hasNeedReprintHintMsg(waybillCode)){
					return result;
				}
			}
			WeightOperFlow totalWeightInfo = getTotalWeight(context);
			if(totalWeightInfo != null){
				if(null==printInfo.getPrepareSiteCode()
		                ||(ComposeService.PREPARE_SITE_CODE_NOTHING.equals(printInfo.getPrepareSiteCode())
		                || ComposeService.PREPARE_SITE_CODE_EMS_DIRECT.equals(printInfo.getPrepareSiteCode()))){
		            return result;
		        }
		        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(printInfo.getPrepareSiteCode());
		        if(baseStaffSiteOrgDto == null){
		            return result;
		        }
		        
	            OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
	            originalOrderInfo.setWeight(totalWeightInfo.getWeight());
	            originalOrderInfo.setHeight(totalWeightInfo.getHigh());
	            originalOrderInfo.setLength(totalWeightInfo.getLength());
	            originalOrderInfo.setWidth(totalWeightInfo.getWidth());
	            originalOrderInfo.setWaybillCode(printInfo.getWaybillCode());
	            originalOrderInfo.setPackageCode(printInfo.getPackList().get(0).getPackageCode());
	            originalOrderInfo.setOriginalStationId(printInfo.getPrepareSiteCode());
	            originalOrderInfo.setOriginalStationName(baseStaffSiteOrgDto.getSiteName());
	            originalOrderInfo.setProvinceId(context.getBigWaybillDto().getWaybill().getProvinceId());
	            originalOrderInfo.setCityId(context.getBigWaybillDto().getWaybill().getCityId());
	            originalOrderInfo.setCountyId(context.getBigWaybillDto().getWaybill().getCountryId());
	            originalOrderInfo.setTownId(context.getBigWaybillDto().getWaybill().getTownId());
	            JdResult<BaseResponseIncidental<MediumStationOrderInfo>> mediumStationOrderInfo = preseparateWaybillManager.getMediumStation(originalOrderInfo);
	            //接口调用失败/返回站点为空，直接通过不强制拦截
	            if(!mediumStationOrderInfo.isSucceed()
	            		|| mediumStationOrderInfo.getData() == null
	            		|| mediumStationOrderInfo.getData().getData() == null){
	                return result;
	            }
	            MediumStationOrderInfo newPreSiteInfo = mediumStationOrderInfo.getData().getData();
	            //预分拣站点变更处理逻辑
	            if(newPreSiteInfo.getMediumStationId()!=null
	            	&& !newPreSiteInfo.getMediumStationId().equals(printInfo.getPrepareSiteCode())){
	                logger.warn(String.format("C网操作B2B传站业务:%s预分拣站点变更：%s->%s",waybillCode,
	                		printInfo.getPrepareSiteCode(),newPreSiteInfo.getMediumStationId()));
	                //一单一件/包裹补打，设置打印站点为新站点
	                if(packageNum==1||isPackageReprint){
	                	printInfo.setPrepareSiteCode(newPreSiteInfo.getMediumStationId());
		                printInfo.setPrepareSiteName(newPreSiteInfo.getMediumStationName());
		                printInfo.setRoad(newPreSiteInfo.getMediumStationRoad());
	                }
	                //一单一件，发送站点变更mq
	                if(packageNum==1){
		                sendSiteChangeMQ(context, newPreSiteInfo);
	                }else{
		                String siteChangeMsg = String.format(SITE_CHANGE_MSG_FORMAT, newPreSiteInfo.getMediumStationId()
		                		+newPreSiteInfo.getMediumStationName());
		                context.appendMessage(siteChangeMsg);
		                result.toWeakSuccess(JdResult.CODE_SUC, siteChangeMsg);
		                //包裹补打-发送mq并且标记打印记录,所有包裹打印完毕，清除提醒信息
		                if(isPackageReprint){
		                	dealPackageReprint(context, newPreSiteInfo);
		                }else{
		                	sendSiteChangeHitMsg(printInfo,siteChangeMsg);
		                }
	                }
	            }
			}
		}
		return result;
	}
	/**
	 * 处理包裹补打操作-第一次补打发送mq并且标记打印记录,所有包裹打印完毕后清除补打提醒信息
	 * @param context
	 * @param newPreSiteInfo
	 */
	private void dealPackageReprint(WaybillPrintContext context,
			MediumStationOrderInfo newPreSiteInfo) {
		WaybillPrintResponse printInfo = context.getResponse();
		String barCode = context.getRequest().getBarCode();
		String waybillCode = printInfo.getWaybillCode();
		//判断是否按运单补打
		boolean isPrintByWaybill = waybillCode.equals(barCode);
		//按运单补打,则关闭提醒信息
		boolean needCloseHintMsg = isPrintByWaybill;
		String reprintRecordsKey = "reprintRecordsKey"+waybillCode;
		Map<String,String> reprintRecords = redisManager.hgetall(reprintRecordsKey);
		if(!isPrintByWaybill){
			//按包裹补打，不包含本次补打，存储一条补打记录
			if(!reprintRecords.containsKey(barCode)){
				redisManager.hset(reprintRecordsKey, barCode, Constants.STRING_FLG_TRUE);
				//判断是否已补打完所有包裹
				if(reprintRecords.size()==(printInfo.getPackList().size()-1)){
					needCloseHintMsg = true;
				}
			}
		}else{
			needCloseHintMsg = true;
		}
		if(needCloseHintMsg){
			sendSiteChangeMQ(context, newPreSiteInfo);
			DmsOperateHint siteChangeHit = new DmsOperateHint();
			siteChangeHit.setDmsSiteCode(printInfo.getOriginalDmsCode());
			siteChangeHit.setDmsSiteName(printInfo.getOriginalDmsName());
			siteChangeHit.setWaybillCode(printInfo.getWaybillCode());
			siteChangeHit.setHintCode(DmsOperateHint.HINT_CODE_NEED_REPRINT);
			siteChangeHit.setIsEnable(Constants.INTEGER_FLG_FALSE);
			logger.warn("关闭包裹补打提醒："+siteChangeHit.getWaybillCode());
			dmsOperateHintService.saveOrUpdate(siteChangeHit);
		}
	}
	/**
	 * 新增一条站点变更提示信息
	 * @param printInfo
	 * @param msg
	 */
	private void sendSiteChangeHitMsg(WaybillPrintResponse printInfo,String msg) {
		DmsOperateHint siteChangeHit = new DmsOperateHint();
		siteChangeHit.setDmsSiteCode(printInfo.getOriginalDmsCode());
		siteChangeHit.setDmsSiteName(printInfo.getOriginalDmsName());
		siteChangeHit.setWaybillCode(printInfo.getWaybillCode());
		siteChangeHit.setHintCode(DmsOperateHint.HINT_CODE_NEED_REPRINT);
		siteChangeHit.setHintName(DmsOperateHint.HINT_NAME_NEED_REPRINT);
		siteChangeHit.setHintMessage(msg);
		siteChangeHit.setIsEnable(Constants.INTEGER_FLG_TRUE);
		logger.warn("新增一条包裹补打提醒信息："+siteChangeHit.getWaybillCode());
		dmsOperateHintService.saveOrUpdate(siteChangeHit);
	}
	/**
	 * 根据包裹号/运单号获取运单总重量
	 * @param barCode
	 * @return
	 */
	private WeightOperFlow getTotalWeight(WaybillPrintContext context){
		WeightOperFlow totalWeightInfo = new WeightOperFlow();
		String waybillCode = context.getBigWaybillDto().getWaybill().getWaybillCode();
		List<DeliveryPackageD> packageList = context.getBigWaybillDto().getPackageList();
		int packageNum = packageList.size();
		//一单一件返回当前称重信息/取当前包裹的分拣中心称重信息
		if(packageNum == 1){
			if(context.getRequest().getWeightOperFlow()!=null){
				return context.getRequest().getWeightOperFlow();
			}else{
				return weightService.getDmsWeightByPackageCode(packageList.get(0).getPackageBarcode());
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
					packageCode = context.getResponse().getPackList().get(packageNum-1).getPackageCode();
					for(PrintPackage printPackage:context.getResponse().getPackList()){
						if(printPackage.getIsPrintPack() == null || !printPackage.getIsPrintPack()){
							packageCode = printPackage.getPackageCode();
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
    /**
     * 发送外单中小件预分拣站点变更mq消息
     * @param context
     * @param commonWaybill
     */
    private void sendSiteChangeMQ(WaybillPrintContext context, MediumStationOrderInfo newPreSiteInfo){
        SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
        siteChangeMqDto.setWaybillCode(context.getResponse().getWaybillCode());
        siteChangeMqDto.setPackageCode(context.getResponse().getPackList().get(0).getPackageCode());
        siteChangeMqDto.setNewSiteId(newPreSiteInfo.getMediumStationId());
        siteChangeMqDto.setNewSiteName(newPreSiteInfo.getMediumStationName());
        siteChangeMqDto.setNewSiteRoadCode(newPreSiteInfo.getMediumStationRoad());
        siteChangeMqDto.setOperatorId(context.getRequest().getUserCode());
        siteChangeMqDto.setOperatorName(context.getRequest().getUserName());
        siteChangeMqDto.setOperatorSiteId(context.getRequest().getSiteCode());
        siteChangeMqDto.setOperatorSiteName(context.getRequest().getSiteName());
        siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
        try {
            waybillSiteChangeProducer.sendOnFailPersistent(context.getResponse().getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
            logger.warn("发送预分拣站点变更mq消息成功："+JsonHelper.toJsonUseGson(siteChangeMqDto));
        } catch (Exception e) {
            logger.error("发送预分拣站点变更mq消息失败："+JsonHelper.toJsonUseGson(siteChangeMqDto), e);
        }finally{
        	SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), String.valueOf(siteChangeMqDto.getOperatorId()), waybillSiteChangeProducer.getTopic(),
                    siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ);
        }
    }
}
