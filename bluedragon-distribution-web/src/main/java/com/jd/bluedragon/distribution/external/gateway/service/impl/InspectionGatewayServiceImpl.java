package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.easyFreeze.EasyFreezeSiteDto;
import com.jd.bluedragon.common.dto.inspection.request.InspectionRequest;
import com.jd.bluedragon.common.dto.inspection.response.ConsumableRecordResponseDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckWaybillTypeRequest;
import com.jd.bluedragon.common.dto.inspection.response.InspectionResultDto;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.waybill.request.ThirdWaybillReq;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillRouteLinkQueryManager;
import com.jd.bluedragon.core.jsf.easyFreezeSite.EasyFreezeSiteManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.HintCheckRequest;
import com.jd.bluedragon.distribution.api.request.ThirdWaybillRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionCheckResult;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.rest.allianceBusi.AllianceBusiResouse;
import com.jd.bluedragon.distribution.rest.inspection.InspectionResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.InspectionGatewayService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.api.waybillroutelink.resp.WaybillRouteLinkResp;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.distribution.loadAndUnload.service.impl.UnloadCarServiceImpl.EXPRESS_CENTER_SITE_ID;

/**
 * 验货相关接口 发布物流网关
 *
 * @author : xumigen
 * @date : 2019/6/14
 */
public class InspectionGatewayServiceImpl implements InspectionGatewayService {

    @Autowired
    @Qualifier("inspectionResource")
    private InspectionResource inspectionResource;

    @Autowired
    private DmsPackingConsumableService dmsPackingConsumableService;

    @Autowired
    @Qualifier("waybillResource")
    private WaybillResource waybillResource;

    @Resource
    private AllianceBusiResouse allianceBusiResouse;

    @Autowired
    protected BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Autowired
    @Qualifier("storagePackageMService")
    private StoragePackageMService storagePackageMService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
    @Autowired
    private WaybillRouteLinkQueryManager waybillRouteManager;

    @Autowired
    private EasyFreezeSiteManager easyFreezeSiteManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    private final static Logger log = LoggerFactory.getLogger(InspectionGatewayServiceImpl.class);

    @Override
    @BusinessLog(sourceSys = 1, bizType = 500, operateType = 50011)
    @JProfiler(jKey = "DMSWEB.InspectionGatewayServiceImpl.getStorageCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<InspectionResultDto> getStorageCode(String packageBarOrWaybillCode, Integer siteCode) {
        JdCResponse<InspectionResultDto> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        if (StringUtils.isEmpty(packageBarOrWaybillCode)) {
            jdCResponse.toFail("单号不能为空");
            return jdCResponse;
        }
        if (siteCode == null) {
            jdCResponse.toFail("站点不能为空");
            return jdCResponse;
        }
        JdResponse<InspectionResult> response = inspectionService.getStorageCode(packageBarOrWaybillCode, siteCode);
        if (!Objects.equals(response.getCode(), JdResponse.CODE_SUCCESS)) {
            jdCResponse.toError(response.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(response.getMessage());
        if (response.getData() != null) {
            InspectionResultDto dto = new InspectionResultDto();
            dto.setStorageCode(response.getData().getStorageCode());
            dto.setHintMessage(response.getData().getHintMessage());
            dto.setTabletrolleyCode(response.getData().getTabletrolleyCode());
            jdCResponse.setData(dto);
        }
        return jdCResponse;
    }

    public JdCResponse<ConsumableRecordResponseDto> isExistConsumableRecord(String waybillCode) {
        JdCResponse<ConsumableRecordResponseDto> jdCResponse = new JdCResponse<>();
        ConsumableRecordResponseDto consumableRecordResponseDto = new ConsumableRecordResponseDto();
        jdCResponse.setData(consumableRecordResponseDto);
        jdCResponse.toSucceed();
        if (StringUtils.isEmpty(waybillCode)) {
            jdCResponse.toFail("单号不能为空");
            return jdCResponse;
        }

        JdResponse<Boolean> jdResponse = dmsPackingConsumableService.getConfirmStatusByWaybillCode(waybillCode);
        if (jdCResponse.isSucceed() && jdResponse.getData() != null) {
            consumableRecordResponseDto.setExistConsumableRecord(jdResponse.getData());
            consumableRecordResponseDto.setHintMessage(jdResponse.getMessage());
        }

        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.InspectionGatewayServiceImpl.hintCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<InspectionCheckResultDto> hintCheck(HintCheckRequest request) {

        JdCResponse<InspectionCheckResultDto> resultDto = new JdCResponse<InspectionCheckResultDto>();
        resultDto.toSucceed();
        InspectionCheckResultDto inspectionCheckResultDto = new InspectionCheckResultDto();
        resultDto.setData(inspectionCheckResultDto);

        // 老验货需校验菜单是否可用
        if(!request.getNewInspectionCheck()){
            ImmutablePair<Boolean, String> checkResult = baseService.checkMenuIsAvailable(Constants.MENU_CODE_INSPECTION, request.getCreateSiteCode());
            if(!checkResult.getLeft()){
                resultDto.toError(checkResult.getRight());
                return resultDto;
            }
        }

        //获取储位号
        JdCResponse<InspectionResultDto> response = this.getStorageCode(request.getPackageCode(), request.getCreateSiteCode());
        inspectionCheckResultDto.setInspectionResultDto(response.getData());
        if (!Objects.equals(response.getCode(), JdResponse.CODE_SUCCESS)) {
            resultDto.toError(response.getMessage());
            return resultDto;
        }

        //运单是否存在待确认的包装任务
        String waybillCode = request.getPackageCode();
        if (WaybillUtil.isPackageCode(request.getPackageCode())) {
            waybillCode = WaybillUtil.getWaybillCode(request.getPackageCode());
        }
        JdCResponse<ConsumableRecordResponseDto> jdCResponse = this.isExistConsumableRecord(waybillCode);
        inspectionCheckResultDto.setConsumableRecordResponseDto(jdCResponse.getData());

        return resultDto;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.InspectionGatewayServiceImpl.getThirdWaybillPackageCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> getThirdWaybillPackageCode(ThirdWaybillReq request) {
        JdCResponse<String> result = new JdCResponse<>();
        result.toSucceed();
        if (null == request || StringUtils.isBlank(request.getThirdWaybillCode())) {
            result.toFail("运单号不能为空");
            return result;
        }

        ThirdWaybillRequest waybillRequest = new ThirdWaybillRequest();
        waybillRequest.setThirdWaybillCode(request.getThirdWaybillCode());
        waybillRequest.setUserErp(request.getUser().getUserErp());
        waybillRequest.setUserCode(request.getUser().getUserCode());
        waybillRequest.setUserName(request.getUser().getUserName());
        waybillRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
        waybillRequest.setSiteName(request.getCurrentOperate().getSiteName());
        InvokeResult<String> invokeResult = waybillResource.getPackageCodeByThirdWaybill(waybillRequest);
        if (!Objects.equals(invokeResult.getCode(), JdResponse.CODE_SUCCESS)) {
            result.toError(result.getMessage());
            return result;
        }

        result.toSucceed(result.getMessage());
        result.setData(invokeResult.getData());

        return result;
    }

    @Override
    public JdVerifyResponse checkWaybillType(InspectionCheckWaybillTypeRequest request) {
        JdVerifyResponse jdVerifyResponse = new JdVerifyResponse();
        if (StringUtils.isEmpty(request.getWaybillCode())) {
            jdVerifyResponse.toError("参数不能为空！");
            return jdVerifyResponse;
        }
        com.jd.bluedragon.distribution.wss.dto.BaseEntity<Boolean> result = allianceBusiResouse.checkMoney(request.getWaybillCode());
        if (result.getCode() != BaseEntity.CODE_SUCCESS) {
            jdVerifyResponse.toError(result.getMessage());
            return jdVerifyResponse;
        }
        //不充足就是需要拦截
        if (!result.getData()) {
            jdVerifyResponse.toSuccess();
            jdVerifyResponse.addInterceptBox(0, "加盟商预付款余额不足，请联系加盟商处理！");
            return jdVerifyResponse;
        }
        jdVerifyResponse.toSuccess(result.getMessage());
        return jdVerifyResponse;
    }

    /**
     * 判断是否是快运站点
     *
     * @param dmsSiteId
     * @return
     */
    private boolean isExpressCenterSite(Integer dmsSiteId) {
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(dmsSiteId);
        if (siteOrgDto == null) {
            return false;
        }
        if (EXPRESS_CENTER_SITE_ID.equals(siteOrgDto.getSubType())) {
            return true;
        }
        return false;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.InspectionGatewayServiceImpl.checkBeforeInspection", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<InspectionCheckResultDto> checkBeforeInspection(InspectionRequest request) {
        JdVerifyResponse<InspectionCheckResultDto> response = new JdVerifyResponse<>();
        response.toSuccess();

        String barCode = request.getBarCode();

        // 拦截消息客户端弹窗并震动，提示消息客户端文字提示，警告消息客户端只弹窗不震动

        // 加盟商余额校验
        checkAllianceMoney(response, request);

        // 暂存校验
        tempStorageCheck(request, response);

        //易冻品校验
        //easyFrozenremindByWaybillCode(request,response);

        // 提示语校验
        HintCheckRequest hintCheckRequest = new HintCheckRequest();
        hintCheckRequest.setPackageCode(barCode);
        hintCheckRequest.setCreateSiteCode(request.getCreateSiteCode());
        hintCheckRequest.setNewInspectionCheck(true);

        JdCResponse<InspectionCheckResultDto> hintCheckResult = hintCheck(hintCheckRequest);
        if (!Objects.equals(hintCheckResult.getCode(), BaseEntity.CODE_SUCCESS)) {
            response.toError(hintCheckResult.getMessage());
            return response;
        }
        else {
            response.setData(hintCheckResult.getData());

            if (StringUtils.isNotBlank(hintCheckResult.getData().getInspectionResultDto().getHintMessage())) {
                response.addWarningBox(0, hintCheckResult.getData().getInspectionResultDto().getHintMessage());
            }
            if (StringUtils.isNotBlank(hintCheckResult.getData().getConsumableRecordResponseDto().getHintMessage())) {
                response.addWarningBox(0, hintCheckResult.getData().getConsumableRecordResponseDto().getHintMessage());
            }

            // 拦截校验
            checkWaybillCancel(request, response);
        }

        return response;
    }

    private void checkWaybillCancel(InspectionRequest request, JdVerifyResponse<InspectionCheckResultDto> response) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setPackageCode(request.getBarCode());
        pdaOperateRequest.setBusinessType(request.getBusinessType());
        pdaOperateRequest.setCreateSiteCode(request.getCreateSiteCode());
        pdaOperateRequest.setCreateSiteName(request.getCreateSiteName());
        pdaOperateRequest.setOperateUserCode(request.getOperateUserCode());
        pdaOperateRequest.setOperateUserName(request.getOperateUserName());
        pdaOperateRequest.setOperateTime(request.getOperateTime());
        pdaOperateRequest.setOperateType(request.getOperateType());

        JdCancelWaybillResponse cancelWaybillResponse = waybillService.dealCancelWaybill(pdaOperateRequest);
        if (!Objects.equals(JdResponse.CODE_SUCCESS, cancelWaybillResponse.getCode())) {
            response.addWarningBox(0, cancelWaybillResponse.getMessage());
        }
    }

    /**
     * 暂存校验
     * @param request
     * @param response
     */
    private void tempStorageCheck(InspectionRequest request, JdVerifyResponse<InspectionCheckResultDto> response) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        if (StringUtils.isBlank(waybillCode)) {
            return;
        }

        InvokeResult<Boolean> tempStorageResult = storagePackageMService.checkIsNeedStorage(waybillCode, request.getCreateSiteCode());
        if (tempStorageResult.getCode() == 201) {
            if (tempStorageResult.getData()) {
                response.addWarningBox(0, tempStorageResult.getMessage());
            }
            else {
                response.addPromptBox(0, tempStorageResult.getMessage());
            }
        }
        else if (response.getCode() == JdCResponse.CODE_FAIL
                || response.getCode() == JdCResponse.CODE_ERROR) {
            response.addWarningBox(0, tempStorageResult.getMessage());
        }
    }

    /**
     * 加盟商余额校验
     * @param response
     * @param request
     * @return
     */
    private void checkAllianceMoney(JdVerifyResponse<InspectionCheckResultDto> response, InspectionRequest request) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        if (StringUtils.isNotBlank(waybillCode)) {
            if (!allianceBusiDeliveryDetailService.checkExist(waybillCode)) {
                if (!allianceBusiDeliveryDetailService.checkMoney(waybillCode)) {
                    response.addWarningBox(0, InspectionCheckResult.ALLIANCE_INTERCEPT_MESSAGE);
                }
            }
        }
    }


    private void easyFrozenremindByWaybillCode(InspectionRequest request, JdVerifyResponse<InspectionCheckResultDto> response) {
        log.info("卸车岗易冻品提醒校验入参-{}", JSON.toJSONString(request));
        Date operateTime = DateHelper.parseDateTime(request.getOperateTime());
        if(operateTime == null){
            log.warn("入参不能为空！");
            return;
        }
        String waybillCode =request.getBarCode();
        try{
            //根据运单获取waybillSign
            com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> dataByChoice = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
            log.info("JyUnloadVehicleServiceImpl-easyFrozenremindByWaybillCode-根据运单号获取运单标识接口请求成功!返回waybillsign数据:{}",dataByChoice.getData());
            if(dataByChoice == null
                    || dataByChoice.getData() == null
                    || dataByChoice.getData().getWaybill() == null
                    || org.apache.commons.lang3.StringUtils.isBlank(dataByChoice.getData().getWaybill().getWaybillSign())) {
                log.warn("查询运单waybillSign失败!");
                return ;
            }
            String waybillSign = dataByChoice.getData().getWaybill().getWaybillSign();
            // 根据waybillSign判断自营单 OR 外单
            if(!BusinessUtil.isSelf(waybillSign)){
                log.info("外单号--");
                //通过waybillsign判断此运单是否包含增值服务
                if(!BusinessUtil.isVasWaybill(waybillCode)){
                    log.warn("此运单不包含增值服务!");
                    return ;
                }
                //判断增值服务是否包含易冻品增值服务
                boolean isEasyFrozen = waybillService.isEasyFrozenVosWaybill(waybillSign);
                if(!isEasyFrozen){
                    log.warn("此运单不包含易冻品增值服务");
                    return ;
                }
                //根据当前操作场地和操作时间 去匹配易冻品指定场地配置
                boolean checkEasyFreezeConf = checkEasyFreezeSiteConf(request.getCreateSiteCode(),operateTime);
                if(checkEasyFreezeConf){
                    if(goodsResidencetimeOverThreeHours(waybillCode,operateTime)){
                        response.addWarningBox(0,InvokeResult.EASY_FROZEN_TIPS_STORAGE_MESSAGE);
                        return ;
                    }
                    response.addWarningBox(0,InvokeResult.EASY_FROZEN_TIPS_MESSAGE);
                    return ;
                }
            }else {
                log.info("自营单号--");
                return ;
            }
        }catch (Exception e){
            log.error("卸车岗易冻品提醒校验异常-{}",e.getMessage(),e);
        }
        return ;
    }

    /**
     * 判断货物滞留时间是否超过三小时 true：超过三小时
     */
    private boolean goodsResidencetimeOverThreeHours(String waybillCode,Date scanTime){
        Date planSendvehicleTime = getWaybillRoutePlanSendvehicleTime(waybillCode);
        if(planSendvehicleTime == null){
            return false;
        }
        int miniDiff = DateHelper.getMiniDiff(scanTime, planSendvehicleTime);
        int goodsResidenceTime = uccPropertyConfiguration.getGoodsResidenceTime();
        //使用分钟更精确些
        if(miniDiff > (goodsResidenceTime * 60)){
            return true;
        }
        return false;
    }
    /**
     * 根据运单获取运单在分拣中心计划发车时间
     * @return
     */
    private Date getWaybillRoutePlanSendvehicleTime(String waybillCode){

        List<WaybillRouteLinkResp> waybillRoutes = waybillRouteManager.queryCustomWaybillRouteLink(waybillCode);
        if(CollectionUtils.isNotEmpty(waybillRoutes)){
            for (WaybillRouteLinkResp route:waybillRoutes) {
                //判断是否是分拣发货操作类型
                if(Constants.SORT_SEND_VEHICLE.equals(route.getOperateType())){
                    return route.getPlanOperateTime();
                }
            }
        }
        return null;
    }

    /**
     * 判断当前站点是否满足易冻品配置 true：满足 false:不满足
     * @param siteCode
     * @return
     */
    private boolean checkEasyFreezeSiteConf(Integer siteCode,Date scanTime){
        EasyFreezeSiteDto dto = easyFreezeSiteManager.selectOneBysiteCode(siteCode);
        if(dto == null){
            return false;
        }
        //配置的提示开始时间
        Date remindStartTime = dto.getRemindStartTime();
        //配置的提示结束时间
        Date remindEndTime = dto.getRemindEndTime();
        if(DateHelper.compare(scanTime,remindStartTime)>=0 && DateHelper.compare(remindEndTime,scanTime) >=0){
            return true;
        }
        return false;

    }
}
