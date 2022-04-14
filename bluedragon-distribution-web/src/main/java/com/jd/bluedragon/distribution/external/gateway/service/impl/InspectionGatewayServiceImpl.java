package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.inspection.request.InspectionRequest;
import com.jd.bluedragon.common.dto.inspection.response.ConsumableRecordResponseDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckWaybillTypeRequest;
import com.jd.bluedragon.common.dto.inspection.response.InspectionResultDto;
import com.jd.bluedragon.common.dto.waybill.request.ThirdWaybillReq;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.HintCheckRequest;
import com.jd.bluedragon.distribution.api.request.ThirdWaybillRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionCheckResult;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.rest.allianceBusi.AllianceBusiResouse;
import com.jd.bluedragon.distribution.rest.inspection.InspectionResource;
import com.jd.bluedragon.distribution.rest.storage.StorageResource;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.InspectionGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
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
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

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
    private InspectionService inspectionService;

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
        JdResponse<InspectionResult> response = inspectionResource.getStorageCode(packageBarOrWaybillCode, siteCode);
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

        // 提示语校验
        HintCheckRequest hintCheckRequest = new HintCheckRequest();
        hintCheckRequest.setPackageCode(barCode);
        hintCheckRequest.setCreateSiteCode(request.getCreateSiteCode());

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
}
