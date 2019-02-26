package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.waybill.handler.C2cInterceptHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintMessages;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.distribution.waybill.service.LabelPrinting;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.preseparate.vo.MediumStationOrderInfo;
import com.jd.preseparate.vo.OriginalOrderInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 面单打印冗余服务
 * Created by shipeilin on 2018/1/31.
 */
@Service("wayBillPrintRedundanceService")
public class WayBillPrintRedundanceServiceImpl implements WayBillPrintRedundanceService {

    private static final Log logger= LogFactory.getLog(WayBillPrintRedundanceServiceImpl.class);

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private AirTransportService airTransportService;

    @Autowired
    private LabelPrinting labelPrinting;

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
    @Autowired
    private PreSortingSecondService preSortingSecondService;

    /* MQ消息生产者： topic:bd_waybill_original_site_change*/
    @Autowired
    @Qualifier("waybillSiteChangeProducer")
    private DefaultJMQProducer waybillSiteChangeProducer;
    @Autowired
    @Qualifier("thirdOverRunInterceptHandler")
    private InterceptHandler<WaybillPrintContext,String> thirdOverRunInterceptHandler;
    @Autowired
    private C2cInterceptHandler c2cInterceptHandler;
    @Autowired
    @Qualifier("templateSelectService")
    private TemplateSelectService templateSelectService;
    /**
     * 2次预分拣变更提示信息
     */
    private static final String SITE_CHANGE_MSG ="单号‘%s’由中件站配送，请务必更换包裹标签";

    @Override
    public InterceptResult<String> getWaybillPack(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<String>();
        Integer startDmsCode = context.getRequest().getDmsSiteCode();
        String barCode = context.getRequest().getBarCode();
        Integer packOpeFlowFlg = context.getRequest().getPackOpeFlowFlg();
        // 判断传入参数
        if (startDmsCode == null || startDmsCode.equals(0) || StringUtils.isEmpty(barCode)) {
            logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-" + barCode + "】获取运单包裹信息接口 --> 传入参数非法");
            result.toError(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            return result;
        }
        // 转换运单号
        String waybillCode = WaybillUtil.getWaybillCode(barCode);
        // 调用服务
        try {
            Waybill waybill = loadBasicWaybillInfo(context,waybillCode,packOpeFlowFlg);
            if (waybill == null) {
            	result.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
        		logger.warn("调用运单接口获取运单数据为空，waybillCode："+waybillCode);
        		return result;
            }else{
                //调用分拣接口获得基础资料信息
                context.setWaybill(waybill);
                result = preSortingSecondService.preSortingAgain(context);//处理是否触发2次预分拣
                if(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT_TYPE.equals(context.getRequest().getOperateType())){
                    // C2C运单打印面单校验揽收完成
                    InterceptResult<String> c2cInterceptResult =c2cInterceptHandler.handle(context);
                    if(!c2cInterceptResult.isSucceed()){
                        return c2cInterceptResult;
                    }
                	InterceptResult<String> overRunInterceptResult =thirdOverRunInterceptHandler.handle(context);
                    if(!overRunInterceptResult.isSucceed()){
                    	return overRunInterceptResult;
                    }
                }
                InterceptResult<String> temp = setBasicMessageByDistribution(context);
                if(temp.getStatus() > result.getStatus()){
                    result = temp;
                }
                logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");

                //获取模板
                templateSelectService.handle(context);
            }
        } catch (Exception e) {
            // 调用服务异常
            logger.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
            result.toError(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }
    /**
     * 获取运单信息
     * @param waybillCode 运单号
     * @param packOpeFlowFlg 是否获取称重信息
     * @return 运单实体
     */
    private Waybill loadBasicWaybillInfo(WaybillPrintContext context,String waybillCode,Integer packOpeFlowFlg) {
    	BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(waybillCode);
    	if (baseEntity == null 
    			||baseEntity.getResultCode() != 1
    			||baseEntity.getData()==null) {
    		return null;
    	}
    	context.setBigWaybillDto(baseEntity.getData());
    	boolean loadPweight = Constants.INTEGER_FLG_TRUE.equals(packOpeFlowFlg);
    	Waybill waybill = this.waybillCommonService.convWaybillWS(baseEntity.getData(), true, true,true,loadPweight);
        if (waybill == null) {
            return waybill;
        }
        // 增加SOP订单EMS全国直发
        if (Constants.POP_SOP_EMS_CODE.equals(waybill.getSiteCode())) {
            waybill.setSiteName(Constants.POP_SOP_EMS_NAME);
        }

        this.setWaybillStatus(waybill);
        return waybill;
    }

    /**
     * 设置打印数据基础信息
     * @param context 上下文
     */
    private InterceptResult<String> setBasicMessageByDistribution(WaybillPrintContext context) {
        Waybill waybill = context.getWaybill();
        Integer localSchedule = context.getRequest().getTargetSiteCode();
        Boolean nopaperFlg = context.getRequest().getNopaperFlg();
        Integer startSiteType = context.getRequest().getStartSiteType();
        Integer startDmsCode = context.getRequest().getDmsSiteCode();
        InterceptResult<String> result = new InterceptResult<String>();
        try {
            LabelPrintingRequest request = new LabelPrintingRequest();
            request.setWaybillCode(waybill.getWaybillCode());
            request.setDmsCode(startDmsCode);
            request.setStartSiteType(startSiteType);
            if (localSchedule!=null && !localSchedule.equals(0))
                request.setLocalSchedule(1);
            else
                request.setLocalSchedule(0);
            request.setCky2(waybill.getCky2());
            request.setOrgCode(waybill.getOrgId());
            //是否航空
            if(checkAireSigns(waybill)){
                request.setAirTransport(true);
            }
            request.setStoreCode(waybill.getStoreId());
            // 是否调度
            // request.setPreSeparateCode(waybill.getOldCode());
            if (localSchedule!=null && !localSchedule.equals(0))
                request.setPreSeparateCode(localSchedule);// 调度站点
            // 是否DMS调用
            request.setOriginalType(OriginalType.DMS.getValue());
            //是否有纸化
            if(nopaperFlg){
                request.setLabelType(LableType.PAPERLESS.getLabelPaper());
            }else {
                request.setLabelType(LableType.PAPER.getLabelPaper());
            }

            BaseResponseIncidental<LabelPrintingResponse> response = labelPrinting.dmsPrint(request,context);
            if(response==null || response.getData()==null){
                logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空response对象");
                result.toError(JdResponse.CODE_PARAM_ERROR, "根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空response对象");
                return result;
            }

            LabelPrintingResponse labelPrinting = response.getData();
            if(labelPrinting==null){
                logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空labelPrinting对象");
                result.toError(JdResponse.CODE_PARAM_ERROR, "根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空labelPrinting对象");
                return result;
            }

            if (response != null) {
                waybill.setCrossCode(String.valueOf(labelPrinting.getOriginalCrossCode()));
                waybill.setTrolleyCode(String.valueOf(labelPrinting.getOriginalTabletrolley()));
                waybill.setTargetDmsCode(labelPrinting.getPurposefulDmsCode());
                waybill.setTargetDmsName(String.valueOf(labelPrinting.getPurposefulDmsName()));
                waybill.setTargetDmsDkh(String.valueOf(labelPrinting.getPurposefulCrossCode()));
                waybill.setTargetDmsLch(String.valueOf(labelPrinting.getPurposefulTableTrolley()));
                waybill.setAddress(labelPrinting.getOrderAddress());
                waybill.setJsonData(response.getJsonData());
                waybill.setRoad(labelPrinting.getRoad());
                result.toSuccess();
                if(labelPrinting.getRoad()==null|| labelPrinting.getRoad().isEmpty()){
                    logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印路区信息为空");
                }
            }
        } catch (Throwable e) {
            logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息接口 --> 异常", e);
            result.toError(JdResponse.CODE_SERVICE_ERROR, "根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息接口异常");
        }
        return result;
    }

    /**
     * 设置运单装态
     * @param waybill 运单实体
     */
    private void setWaybillStatus(Waybill waybill) {
        if (waybill == null || StringUtils.isBlank(waybill.getWaybillCode())) {
            return;
        }

        Boolean isDelivery = waybill.isDelivery();
        if (isDelivery) {
            waybill.setStatusAndMessage(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        } else {
            waybill.setStatusAndMessage(SortingResponse.CODE_293040, SortingResponse.MESSAGE_293040);
        }

        // 验证运单号，是否锁定、删除等
        com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse cancelWaybill = null;
        try {
            cancelWaybill = WaybillCancelClient.getWaybillResponse(waybill.getWaybillCode());
        } catch (Exception e) {
            logger.error("WaybillResource --> setWaybillStatus get cancelWaybill Error:", e);
        }

        if (cancelWaybill != null) {
            if (SortingResponse.CODE_29300.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29300, SortingResponse.MESSAGE_29300);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293000, SortingResponse.MESSAGE_293000);
                }
            } else if (SortingResponse.CODE_29302.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29302, SortingResponse.MESSAGE_29302);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293020, SortingResponse.MESSAGE_293020);
                }
            } else if (SortingResponse.CODE_29301.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29301, SortingResponse.MESSAGE_29301);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293010, SortingResponse.MESSAGE_293010);
                }
            } else if (SortingResponse.CODE_29303.equals(cancelWaybill.getCode())) {
                waybill.setStatusAndMessage(SortingResponse.CODE_29303, SortingResponse.MESSAGE_29303);
            }
        }
    }

    /**
     * 根据基础资料调用设置航空标识
     * @param waybill 运单数据
     * @return 布尔值
     */
    private boolean checkAireSigns(Waybill waybill) {
        // 设置航空标识
        boolean signs = false;
        if (waybill.getBusiId() != null && !waybill.getBusiId().equals(0)) {
            logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId() + "-" + "-" + waybill.getSiteCode() + "】根据基础资料调用设置航空标识");
            signs = this.airTransportService.getAirSigns(waybill.getBusiId());
        }
        return signs;
    }

    /**
     * 一单一件且重新称重或量方的触发二次预分拣
     * @param context 上下文
     * @return 处理结果，处理是否通过
     */
    private InterceptResult<String> preSortingAgain(WaybillPrintContext context){
        InterceptResult<String> interceptResult = context.getResult();
        Waybill waybill = context.getWaybill();
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
            JdResult<com.jd.preseparate.vo.BaseResponseIncidental<MediumStationOrderInfo>> mediumStationOrderInfo = preseparateWaybillManager.getMediumStation(originalOrderInfo);
            //接口调用失败/返回站点ID为空，直接通过不强制拦截
            if(mediumStationOrderInfo.isSucceed()
            		&& mediumStationOrderInfo.getData() != null
            		&& mediumStationOrderInfo.getData().getData() != null
            		&& mediumStationOrderInfo.getData().getData().getMediumStationId() != null){
	            MediumStationOrderInfo newPreSiteInfo = mediumStationOrderInfo.getData().getData();
	            //新预分拣站点不同于原站点则提示换单并设置为新的预分拣站点
	            if(!newPreSiteInfo.getMediumStationId().equals(oldPrepareSiteCode)){
	            	//换站点了
                    waybill.setSiteCode(newPreSiteInfo.getMediumStationId());
                    waybill.setSiteName(newPreSiteInfo.getMediumStationName());
                    waybill.setRoad(newPreSiteInfo.getMediumStationRoad());
                    context.appendMessage(String.format(SITE_CHANGE_MSG, context.getRequest().getBarCode()));
                    context.setStatus(InterceptResult.STATUS_WEAK_PASSED);
                    interceptResult.toWeakSuccess(JdResult.CODE_SUC, String.format(SITE_CHANGE_MSG, context.getRequest().getBarCode()));
                    sendSiteChangeMQ(context);
                    return interceptResult;
	            }
            }
        }else{
            interceptResult.toSuccess();
        }
        return interceptResult;
    }

    /**
     * 发送外单中小件预分拣站点变更mq消息
     * @param context
     */
    private void sendSiteChangeMQ(WaybillPrintContext context){
        SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
        Waybill waybill = context.getWaybill();
        siteChangeMqDto.setWaybillCode(waybill.getWaybillCode());
        siteChangeMqDto.setPackageCode(waybill.getPackList().get(0).getPackCode());
        siteChangeMqDto.setNewSiteId(waybill.getSiteCode());
        siteChangeMqDto.setNewSiteName(waybill.getSiteName());
        siteChangeMqDto.setNewSiteRoadCode(waybill.getRoad());
        siteChangeMqDto.setOperatorId(context.getRequest().getUserCode());
        siteChangeMqDto.setOperatorName(context.getRequest().getUserName());
        siteChangeMqDto.setOperatorSiteId(context.getRequest().getSiteCode());
        siteChangeMqDto.setOperatorSiteName(context.getRequest().getSiteName());
        siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
        try {
            waybillSiteChangeProducer.send(waybill.getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
            logger.info("发送外单中小件预分拣站点变更mq消息成功："+JsonHelper.toJsonUseGson(siteChangeMqDto));
        } catch (JMQException e) {
            SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), siteChangeMqDto.getOperatorId().toString(), waybillSiteChangeProducer.getTopic(),
                    siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ);
            logger.error("发送外单中小件预分拣站点变更mq消息失败："+JsonHelper.toJsonUseGson(siteChangeMqDto), e);
        }
    }
}
