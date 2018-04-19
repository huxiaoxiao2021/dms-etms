package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
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
	private static final String SITE_CHANGE_MSG_FORMAT = "预分拣站点变更为%s\n请操作包裹补打更换面单！";
    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    @Qualifier("waybillSiteChangeProducer")
    private DefaultJMQProducer waybillSiteChangeProducer;
    
    @Autowired
    private DmsOperateHintService dmsOperateHintService;
    
	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		logger.info("C网操作B2B传站业务，预分拣站点变更拦截");
		InterceptResult<String> result = context.getResult();
		WaybillPrintResponse printInfo = context.getResponse();
		String waybillCode = printInfo.getWaybillCode();
		String barCode = context.getRequest().getBarCode();
		String waybillSign = printInfo.getWaybillSign();
		//业务判断
		if(!BusinessHelper.isSignY(waybillSign, 1)
				&&BusinessHelper.isSignChar(waybillSign, 36, '0')){
			DmsWeightFlow totalWeightInfo = getTotalWeightByBarCode(barCode);
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
		        
		        int size = 0;
		        if(printInfo.getPackList() != null){
		        	size = printInfo.getPackList().size();
		        };
	            OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
	            originalOrderInfo.setWeight(context.getRequest().getWeightOperFlow().getWeight());
	            originalOrderInfo.setHeight(context.getRequest().getWeightOperFlow().getHigh());
	            originalOrderInfo.setLength(context.getRequest().getWeightOperFlow().getLength());
	            originalOrderInfo.setWidth(context.getRequest().getWeightOperFlow().getWidth());
	            originalOrderInfo.setWaybillCode(printInfo.getWaybillCode());
	            originalOrderInfo.setPackageCode(printInfo.getPackList().get(0).getPackageCode());
	            originalOrderInfo.setOriginalStationId(printInfo.getPrepareSiteCode());
	            originalOrderInfo.setOriginalStationName(baseStaffSiteOrgDto.getSiteName());
	            JdResult<BaseResponseIncidental<MediumStationOrderInfo>> mediumStationOrderInfo = preseparateWaybillManager.getMediumStation(originalOrderInfo);
	            MediumStationOrderInfo tmp = new MediumStationOrderInfo();
	            tmp.setMediumStationId(24);
	            tmp.setMediumStationName("石景山站");
	            mediumStationOrderInfo.getData().setData(tmp);
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
	                printInfo.setPrepareSiteCode(newPreSiteInfo.getMediumStationId());
	                printInfo.setPrepareSiteName(newPreSiteInfo.getMediumStationName());
	                printInfo.setRoad(newPreSiteInfo.getMediumStationRoad());
	                String siteChangeMsg = String.format(SITE_CHANGE_MSG_FORMAT, newPreSiteInfo.getMediumStationId());
	                context.appendMessage(siteChangeMsg);
	                result.toWeakSuccess(JdResult.CODE_SUC, siteChangeMsg);
	                sendSiteChangeMQ(context, printInfo);
	                sendSiteChangeHitMsg(printInfo,siteChangeMsg);
	            }
			}
		}
		return result;
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
		siteChangeHit.setHintCode(11);
		siteChangeHit.setHintMessage(msg);
		dmsOperateHintService.saveOrUpdate(siteChangeHit);
	}
	/**
	 * 根据包裹号/运单号获取运单总重量
	 * @param barCode
	 * @return
	 */
	private DmsWeightFlow getTotalWeightByBarCode(String barCode){
		DmsWeightFlow totalWeightInfo = new DmsWeightFlow();
		totalWeightInfo.setWeight(52.25);
		return totalWeightInfo;
	}
    /**
     * 发送外单中小件预分拣站点变更mq消息
     * @param context
     * @param commonWaybill
     */
    private void sendSiteChangeMQ(WaybillPrintContext context, PrintWaybill commonWaybill){
        SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
        siteChangeMqDto.setWaybillCode(commonWaybill.getWaybillCode());
        siteChangeMqDto.setPackageCode(commonWaybill.getPackList().get(0).getPackageCode());
        siteChangeMqDto.setNewSiteId(commonWaybill.getPrepareSiteCode());
        siteChangeMqDto.setNewSiteName(commonWaybill.getPrepareSiteName());
        siteChangeMqDto.setNewSiteRoadCode(commonWaybill.getRoad());
        siteChangeMqDto.setOperatorId(context.getRequest().getUserCode());
        siteChangeMqDto.setOperatorName(context.getRequest().getUserName());
        siteChangeMqDto.setOperatorSiteId(context.getRequest().getSiteCode());
        siteChangeMqDto.setOperatorSiteName(context.getRequest().getSiteName());
        siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
        try {
            waybillSiteChangeProducer.sendOnFailPersistent(commonWaybill.getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
            logger.info("发送外单中小件预分拣站点变更mq消息成功："+JsonHelper.toJsonUseGson(siteChangeMqDto));
        } catch (Exception e) {
            SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), siteChangeMqDto.getOperatorId().toString(), waybillSiteChangeProducer.getTopic(),
                    siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ);
            logger.error("发送外单中小件预分拣站点变更mq消息失败："+JsonHelper.toJsonUseGson(siteChangeMqDto), e);
        }
    }
}
