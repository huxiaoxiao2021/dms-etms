package com.jd.bluedragon.distribution.print.service;

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
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.jmq.common.exception.JMQException;
import com.jd.preseparate.vo.BaseResponseIncidental;
import com.jd.preseparate.vo.MediumStationOrderInfo;
import com.jd.preseparate.vo.OriginalOrderInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 纯外单中小件二次预分拣服务
 * Created by shipeilin on 2018/2/1.
 */
@Service("preSortingSecondService")
public class PreSortingSecondServiceImpl implements PreSortingSecondService{
    private static final Log logger= LogFactory.getLog(PreSortingSecondServiceImpl.class);

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /* MQ消息生产者： topic:bd_waybill_original_site_change*/
    @Autowired
    @Qualifier("waybillSiteChangeProducer")
    private DefaultJMQProducer waybillSiteChangeProducer;
    /**
     * 2次预分拣变更提示信息
     */
    private static final String SITE_CHANGE_MSG ="单号‘%s’由中件站配送，请务必更换包裹标签";

    /**
     * 包裹重量体积的默认值0
     */
    private static final Double DOUBLE_ZERO = 0.0;

    /**
     * 一单一件且重新称重或量方的触发二次预分拣
     * @param context 上下文
     * @param commonWaybill 运单实体
     * @return 处理结果，处理是否通过
     */
    @Override
    public InterceptResult<String> preSortingAgain(WaybillPrintContext context, PrintWaybill commonWaybill){
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        //如果预分拣站点为0超区或者999999999EMS全国直发，则无法触发二次预分拣
        if(null==commonWaybill.getPrepareSiteCode()
                ||(ComposeService.PREPARE_SITE_CODE_NOTHING.equals(commonWaybill.getPrepareSiteCode())
                || ComposeService.PREPARE_SITE_CODE_EMS_DIRECT.equals(commonWaybill.getPrepareSiteCode()))){
            interceptResult.toSuccess();
            return interceptResult;
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(commonWaybill.getPrepareSiteCode());
        if(baseStaffSiteOrgDto == null){
            // interceptResult.toError(InterceptResult.CODE_ERROR, "查询预分拣站点为空："+commonWaybill.getPrepareSiteCode());    //查询不到预分拣站点，不做处理直接返回
            interceptResult.toSuccess();
            return interceptResult;
        }
        
        int size = 0;
        if(commonWaybill.getPackList() != null){
        	size = commonWaybill.getPackList().size();
        };
        if(Integer.valueOf(0).equals(context.getRequest().getTargetSiteCode()) && size == 1 && BusinessHelper.isExternal(commonWaybill.getWaybillSign()) && hasWeightOrVolume(context)){    //一单一件 纯外单 上传了新的体积或重量
            OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
            originalOrderInfo.setWeight(context.getRequest().getWeightOperFlow().getWeight());
            originalOrderInfo.setHeight(context.getRequest().getWeightOperFlow().getHigh());
            originalOrderInfo.setLength(context.getRequest().getWeightOperFlow().getLength());
            originalOrderInfo.setWidth(context.getRequest().getWeightOperFlow().getWidth());
            originalOrderInfo.setWaybillCode(commonWaybill.getWaybillCode());
            originalOrderInfo.setPackageCode(commonWaybill.getPackList().get(0).getPackageCode());
            originalOrderInfo.setOriginalStationId(commonWaybill.getPrepareSiteCode());
            originalOrderInfo.setOriginalStationName(baseStaffSiteOrgDto.getSiteName());
            //originalOrderInfo.setOriginalRoad(commonWaybill.getRoad());    //commonWaybill.getRoad()查不到时可能设置为"0",接口非必要字段，这里不传该参数
            originalOrderInfo.setSystemCode("DMS");
            JdResult<BaseResponseIncidental<MediumStationOrderInfo>> mediumStationOrderInfo = preseparateWaybillManager.getMediumStation(originalOrderInfo);
            //接口调用失败/返回站点为空，直接通过不强制拦截
            if(!mediumStationOrderInfo.isSucceed()
            		|| mediumStationOrderInfo.getData() == null
            		|| mediumStationOrderInfo.getData().getData() == null){
                interceptResult.toSuccess();
                return interceptResult;
            }
            MediumStationOrderInfo newPreSiteInfo = mediumStationOrderInfo.getData().getData();
            //新预分拣站点不同于原站点则提示换单并设置为新的预分拣站点
            if(newPreSiteInfo.getMediumStationId()!=null
            	&& !newPreSiteInfo.getMediumStationId().equals(commonWaybill.getPrepareSiteCode())){
            	//换站点了
                logger.info("中小件二次预分拣换预分拣站点了："+newPreSiteInfo.getMediumStationId());
                commonWaybill.setPrepareSiteCode(newPreSiteInfo.getMediumStationId());
                commonWaybill.setPrepareSiteName(newPreSiteInfo.getMediumStationName());
                commonWaybill.setRoad(newPreSiteInfo.getMediumStationRoad());
                context.appendMessage(String.format(SITE_CHANGE_MSG, context.getRequest().getBarCode()));
                interceptResult.toWeakSuccess(JdResult.CODE_SUC, String.format(SITE_CHANGE_MSG, context.getRequest().getBarCode()));
                sendSiteChangeMQ(context, commonWaybill);
            }
        }else{
            interceptResult.toSuccess();
        }
        return interceptResult;
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
            waybillSiteChangeProducer.send(commonWaybill.getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
            logger.info("发送外单中小件预分拣站点变更mq消息成功："+JsonHelper.toJsonUseGson(siteChangeMqDto));
        } catch (JMQException e) {
            SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), siteChangeMqDto.getOperatorId().toString(), waybillSiteChangeProducer.getTopic(),
                    siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ);
            logger.error("发送外单中小件预分拣站点变更mq消息失败："+JsonHelper.toJsonUseGson(siteChangeMqDto), e);
        }
    }

    /**
     * 判断是否上传了体积或者重量(重量不为0 或者 长宽高都不为0)
     * @param context 请求上下文
     * @return 是否上传体积或重量
     */
    private boolean hasWeightOrVolume(WaybillPrintContext context){
    	if(context.getRequest().getWeightOperFlow()==null){
    		return false;
    	}
        if(!DOUBLE_ZERO.equals(context.getRequest().getWeightOperFlow().getWeight()) ||
                (!DOUBLE_ZERO.equals(context.getRequest().getWeightOperFlow().getWidth()) &&
                        !DOUBLE_ZERO.equals(context.getRequest().getWeightOperFlow().getLength()) &&
                        !DOUBLE_ZERO.equals(context.getRequest().getWeightOperFlow().getHigh()))){
            return true;
        }
        return false;
    }
}
