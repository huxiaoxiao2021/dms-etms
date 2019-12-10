package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.service.PreSortingSecondService;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.distribution.waybill.service.LabelPrinting;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.LableType;
import com.jd.bluedragon.utils.OriginalType;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
/**
 * 
 * @ClassName: SitePlateBasicHandler
 * @Description: 
 * @author: wuyoude
 * @date: 2019年5月24日 下午3:32:53
 *
 */
@Service("sitePlateBasicHandler")
public class SitePlateBasicHandler implements Handler<WaybillPrintContext,JdResult<String>> {
    private static final Logger log = LoggerFactory.getLogger(SitePlateBasicHandler.class);

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

    @Autowired
    @Qualifier("thirdOverRunInterceptHandler")
    private InterceptHandler<WaybillPrintContext,String> thirdOverRunInterceptHandler;
    @Autowired
    private C2cInterceptHandler c2cInterceptHandler;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<String>();
        Integer startDmsCode = context.getRequest().getDmsSiteCode();
        String barCode = context.getRequest().getBarCode();
        Integer packOpeFlowFlg = context.getRequest().getPackOpeFlowFlg();
        // 判断传入参数
        if (startDmsCode == null || startDmsCode.equals(0) || StringUtils.isEmpty(barCode)) {
            log.warn("根据初始分拣中心-运单号/包裹号【{}-{}】获取运单包裹信息接口 --> 传入参数非法",startDmsCode , barCode );
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
        		log.warn("调用运单接口获取运单数据为空，waybillCode：{}",waybillCode);
        		return result;
            }else{
                //调用分拣接口获得基础资料信息
                context.setWaybill(waybill);
                result = preSortingSecondService.preSortingAgain(context);//处理是否触发2次预分拣
                // C2C运单打印面单校验揽收完成
                InterceptResult<String> c2cInterceptResult =c2cInterceptHandler.handle(context);
                if(!c2cInterceptResult.isSucceed()){
                    return c2cInterceptResult;
                }
                if(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT_TYPE.equals(context.getRequest().getOperateType())){
                	InterceptResult<String> overRunInterceptResult =thirdOverRunInterceptHandler.handle(context);
                    if(!overRunInterceptResult.isSucceed()){
                    	return overRunInterceptResult;
                    }
                }
                InterceptResult<String> temp = setBasicMessageByDistribution(context);
                if(temp.getStatus() > result.getStatus()){
                    result = temp;
                }
                log.info("运单号【{}】调用根据运单号获取运单包裹信息接口成功",waybillCode);
            }
        } catch (Exception e) {
            // 调用服务异常
            log.error("根据运单号【{}】 获取运单包裹信息接口 --> 异常",waybillCode, e);
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
            //是否有纸化,改为通过paperSizeCode来判断,兼容旧逻辑
            if(Boolean.FALSE.equals(context.getRequest().getNopaperFlg())
            		||DmsPaperSize.PAPER_SIZE_CODE_1005.equals(context.getRequest().getPaperSizeCode())){
                request.setLabelType(LableType.PAPER.getLabelPaper());
            }else {
            	request.setLabelType(LableType.PAPERLESS.getLabelPaper());
            }

            BaseResponseIncidental<LabelPrintingResponse> response = labelPrinting.dmsPrint(request,context);
            if(response==null || response.getData()==null){
                log.warn("根据运单号【{}】 获取预分拣的包裹打印信息为空response对象",waybill.getWaybillCode());
                result.toError(JdResponse.CODE_PARAM_ERROR, "根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空response对象");
                return result;
            }

            LabelPrintingResponse labelPrinting = response.getData();
            if(labelPrinting==null){
                log.warn("根据运单号【{}】 获取预分拣的包裹打印信息为空labelPrinting对象",waybill.getWaybillCode());
                result.toError(JdResponse.CODE_PARAM_ERROR, "根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空labelPrinting对象");
                return result;
            }

            //设置路由信息
            String waybillSign = "";
            if(context.getWaybill() != null && StringUtils.isNotBlank(context.getWaybill().getWaybillSign())){
                waybillSign = context.getWaybill().getWaybillSign();
            }
            Integer originalDmsCode = labelPrinting.getOriginalDmsCode();
            Integer destinationDmsCode = labelPrinting.getPurposefulDmsCode();
            waybillCommonService.loadWaybillRouter(labelPrinting,originalDmsCode,destinationDmsCode,waybillSign);

            if (response != null) {
            	waybill.setAddress(labelPrinting.getOrderAddress());
                result.toSuccess();
            }
        } catch (Throwable e) {
            log.error("根据运单号【{}】 获取包裹打印信息接口 --> 异常",waybill.getWaybillCode(), e);
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
            log.error("WaybillResource --> setWaybillStatus get cancelWaybill Error:{}",waybill.getWaybillCode(), e);
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
            log.debug("B商家ID-初始分拣中心-目的站点【{}-{}】根据基础资料调用设置航空标识",waybill.getBusiId(), waybill.getSiteCode());
            signs = this.airTransportService.getAirSigns(waybill.getBusiId());
        }
        return signs;
    }
}
