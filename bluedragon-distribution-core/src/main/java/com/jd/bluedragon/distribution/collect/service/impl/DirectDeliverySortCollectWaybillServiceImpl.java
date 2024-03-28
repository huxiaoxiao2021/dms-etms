package com.jd.bluedragon.distribution.collect.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedReason;
import com.jd.bluedragon.common.dto.operation.workbench.collect.CollectTerminatedRequest;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.MerchCollectManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.collect.domain.DirectDeliverySortCollectRequest;
import com.jd.bluedragon.distribution.collect.service.DirectDeliverySortCollectWaybillService;
import com.jd.bluedragon.distribution.packageWeighting.PackageWeightingService;
import com.jd.bluedragon.distribution.packageWeighting.domain.BusinessTypeEnum;
import com.jd.bluedragon.distribution.packageWeighting.domain.PackageWeighting;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.express.collect.api.CommonDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskcollectwaybillterminate.NoTaskCollectWaybillTerminateCommand;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskcollectwaybillterminate.NoTaskCollectWaybillTerminateDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectFailureWaybillInfoDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillCommand;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillDTO;
import com.jdl.express.collect.api.merchcollectwaybill.commands.notaskfinishcollectwaybill.NoTaskFinishCollectWaybillInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 揽收服务
 *
 * @author hujiping
 * @date 2024/2/29 10:29 AM
 */
@Slf4j
@Service("directDeliverySortCollectWaybillService")
public class DirectDeliverySortCollectWaybillServiceImpl implements DirectDeliverySortCollectWaybillService {

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private MerchCollectManager merchCollectManager;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private PackageWeightingService packageWeightingService;

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "CollectWaybillService.queryTerminatedReasons",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public List<CollectTerminatedReason> queryTerminatedReasons() {
        SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_COLLECT_TERMINATE_REASON);
        if(sysConfig == null || StringUtils.isEmpty(sysConfig.getConfigContent())){
            return Lists.newArrayList();
        }
        return JsonHelper.jsonToList(sysConfig.getConfigContent(), CollectTerminatedReason.class);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "CollectWaybillService.terminateCollect",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> terminateCollect(CollectTerminatedRequest request) {
        InvokeResult<Void> result = terminatedParamsCheck(request);
        if(!result.codeSuccess()){
            return result;
        }
        CommonDTO<NoTaskCollectWaybillTerminateDTO> commonDTO = merchCollectManager.terminateNoTaskCollectWaybill(convert2TerminateCommand(request));
        if(commonDTO == null || !Objects.equals(CommonDTO.CODE_SUCCESS, commonDTO.getCode())){
            log.error(String.format("单号:%s揽收异常,异常原因:%s", request.getWaybillCode(), commonDTO == null ? InvokeResult.SERVER_ERROR_MESSAGE : commonDTO.getMessage()));
            result.error(String.format("单号:%s揽收异常,请联系分拣小秘!", request.getWaybillCode()));
            return result;
        }
        if(!commonDTO.getData().isTerminateSuccess()){
            result.error(String.format("单号:%s揽收失败!失败原因:%s", request.getWaybillCode(), commonDTO.getData().getFailureWaybill().getFailureMsg()));
            return result;
        }
        result.success();
        result.setMessage(String.format("单号:%s揽收终止成功!", request.getWaybillCode()));
        return result;
    }

    @Override
    public InvokeResult<Void> directDeliverySortCollectCheck(DirectDeliverySortCollectRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.success();

        // params check
        if(!paramsCheck(request, result)){
            return result;
        }

        if(!checkIsCanCollect(request, result)){
            return result;
        }

        // 揽收校验
        if(!collectCheck(request, result)){
            return result;
        }
        return result;
    }

    private InvokeResult<Void> terminatedParamsCheck(CollectTerminatedRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        if(!WaybillUtil.isPackageCode(request.getWaybillCode()) && !WaybillUtil.isWaybillCode(request.getWaybillCode())){
            result.parameterError("请录入有效的运单号|包裹号!");
            return result;
        }
        request.setWaybillCode(WaybillUtil.getWaybillCode(request.getWaybillCode()));
        if(request.getOperateSiteCode() == null || request.getOperateUserId() == null){
            result.parameterError("缺少操作场地|人信息!");
            return result;
        }
        if(request.getTerminatedReasonCode() == null || StringUtils.isEmpty(request.getTerminatedReasonName())){
            result.parameterError("请选择有效的揽收终止原因!");
            return result;
        }
        if(StringUtils.isEmpty(request.getOperateSiteName())){
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getOperateSiteCode());
            if(baseSite == null){
                result.parameterError(String.format("当前场地:%s不存在!", request.getOperateSiteCode()));
                return result;
            }
            request.setOperateSiteName(baseSite.getSiteName());
        }
        if(StringUtils.isEmpty(request.getOperateUserName())){
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(request.getOperateUserId());
            if(baseStaff == null){
                result.parameterError(String.format("当前操作人:%s不存在!", request.getOperateUserId()));
                return result;
            }
            request.setOperateUserName(baseStaff.getStaffName());
        }
        return result;
    }

    private NoTaskCollectWaybillTerminateCommand convert2TerminateCommand(CollectTerminatedRequest request) {
        NoTaskCollectWaybillTerminateCommand command = new NoTaskCollectWaybillTerminateCommand();
        command.setWaybillCode(request.getWaybillCode());
        command.setSiteId(request.getOperateSiteCode());
        command.setSiteName(request.getOperateSiteName());
        command.setOperatorId(request.getOperateUserId());
        command.setOperatorName(request.getOperateUserName());
        command.setEndReason(Integer.valueOf(request.getTerminatedReasonCode()));
        command.setReasonMemo(request.getTerminatedReasonName());
        return command;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "CollectWaybillService.directDeliverySortCollect",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<Void> directDeliverySortCollect(DirectDeliverySortCollectRequest request) {
        InvokeResult<Void> result = directDeliverySortCollectCheck(request);
        if(!result.codeSuccess()){
            return result;
        }
        // 调用终端无任务揽收接口
        CommonDTO<NoTaskFinishCollectWaybillDTO> collectResult = merchCollectManager.noTaskFinishCollectWaybill(convert2NoTaskCollectReq(request));
        if(collectResult == null){
            log.error("直送分拣揽收包裹:{}服务异常!", request.getPackOrWaybillCode());
            result.error("终端揽收接口异常,请联系分拣小秘!");
            return result;
        }
        if(!Objects.equals(CommonDTO.CODE_SUCCESS, collectResult.getCode())){
            log.warn("直送分拣揽收包裹:{}失败,失败原因:{}", request.getPackOrWaybillCode(), collectResult.getMessage());
            result.error(String.format("揽收失败,失败原因：%s", collectResult.getMessage()));
            return result;
        }
        if(collectResult.getData() != null && CollectionUtils.isNotEmpty(collectResult.getData().getOrderIds())){
            // 因为是包裹维度的揽收，所以只用取第一个
            NoTaskFinishCollectFailureWaybillInfoDTO collectFailWaybillDto = collectResult.getData().getOrderIds().get(0);
            log.warn("直送分拣揽收包裹:{}失败,失败原因:{}", request.getPackOrWaybillCode(), collectFailWaybillDto.getResultMsg());
            result.error(String.format("揽收失败,失败原因：%s", collectFailWaybillDto.getResultMsg()));
            return result;
        }
        return result;
    }

    private NoTaskFinishCollectWaybillCommand convert2NoTaskCollectReq(DirectDeliverySortCollectRequest request) {
        NoTaskFinishCollectWaybillCommand command = new NoTaskFinishCollectWaybillCommand();
        command.setSystemSource(23); // 23表示来源分拣，且无需拦截校验
        command.setSiteId(request.getOperateSiteCode());
        command.setSiteName(request.getOperateSiteName());
        command.setOperatorId(request.getOperateUserId());
        command.setOperatorName(request.getOperateUserName());
        command.setOperateUserCode(request.getOperateUserErp());
        command.setItems(Collections.singletonList(new NoTaskFinishCollectWaybillInfoDTO()));
        // 揽收包裹明细
        BigWaybillDto bigWaybillDto = request.getBigWaybillDto();
        NoTaskFinishCollectWaybillInfoDTO collectDto = command.getItems().get(0);
        collectDto.setRefId(request.getPackOrWaybillCode());
        collectDto.setWeight(request.getWeight());
        collectDto.setLength(request.getLength());
        collectDto.setHeight(request.getHigh());
        collectDto.setWidth(request.getWidth());
        collectDto.setVolume(request.getVolume());
        collectDto.setPackCount(bigWaybillDto.getWaybill().getGoodNumber());
        collectDto.setGoods(bigWaybillDto.getWaybill().getWaybillExt() == null ? null : bigWaybillDto.getWaybill().getWaybillExt().getConsignWare());
        return command;
    }

    private boolean paramsCheck(DirectDeliverySortCollectRequest request, InvokeResult<Void> result) {
        if(!WaybillUtil.isPackageCode(request.getPackOrWaybillCode())){
            result.parameterError("直送分拣揽收目前只支持按包裹维度揽收!");
            return false;
        }
        if(request.getOperateSiteCode() == null || request.getOperateUserId() == null){
            result.parameterError("缺少操作场地|人等参数!");
            return false;
        }
        // fill operateInfo
        if(StringUtils.isEmpty(request.getOperateSiteName())){
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getOperateSiteCode());
            if(baseSite == null){
                result.parameterError(String.format("当前场地:%s不存在!", request.getOperateSiteCode()));
                return false;
            }
            request.setOperateSiteName(baseSite.getSiteName());
        }
        if(StringUtils.isEmpty(request.getOperateUserErp()) || StringUtils.isEmpty(request.getOperateUserName())){
            BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByStaffId(request.getOperateUserId());
            if(baseStaff == null){
                result.parameterError(String.format("当前操作人:%s不存在!", request.getOperateUserId()));
                return false;
            }
            request.setOperateUserErp(baseStaff.getAccountNumber());
            request.setOperateUserName(baseStaff.getStaffName());
        }
        return true;
    }

    private boolean checkIsCanCollect(DirectDeliverySortCollectRequest request, InvokeResult<Void> result) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getPackOrWaybillCode());

        if(request.getBigWaybillDto() == null){
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(Boolean.TRUE);
            wChoice.setQueryWaybillE(Boolean.TRUE);
            wChoice.setQueryWaybillP(Boolean.TRUE);
            wChoice.setQueryWaybillExtend(Boolean.TRUE);
            BaseEntity<BigWaybillDto> waybillEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if(waybillEntity == null || waybillEntity.getData() == null || waybillEntity.getData().getWaybill() == null){
                result.error(String.format("运单:%s不存在!", waybillCode));
                return false;
            }
            // fill waybill
            request.setBigWaybillDto(waybillEntity.getData());
        }

        BigWaybillDto bigWaybillDto = request.getBigWaybillDto();

        // 是否直送分拣
        if(!BusinessUtil.isDirectDeliverySort(bigWaybillDto.getWaybill().getWaybillSign())){
            return false;
        }

        // 揽收场地
        Integer pickupSiteId = bigWaybillDto.getWaybillPickup() == null ? null : bigWaybillDto.getWaybillPickup().getPickupSiteId();

        if(pickupSiteId == null){
            String errorMsg = String.format("运单:%s的揽收场地不存在!", waybillCode);
            log.warn(errorMsg);
            result.error(errorMsg);
            return false;
        }

        BaseStaffSiteOrgDto packupSite = baseMajorManager.getBaseSiteBySiteId(pickupSiteId);
        
        return packupSite != null 
                && Objects.equals(packupSite.getDmsId() == null ? pickupSiteId : packupSite.getDmsId(), request.getOperateSiteCode());
    }

    private boolean collectCheck(DirectDeliverySortCollectRequest request, InvokeResult<Void> result) {
        Waybill waybill = request.getBigWaybillDto().getWaybill();
        if(!NumberHelper.gt0(waybill.getOldSiteId())){
            result.error(String.format("运单:%s运单预分拣不存在!", waybill.getWaybillCode()));
            return false;
        }

        // fill weightVolume
        fillWeightVolume(request);
        
        // weight check
        if(!NumberHelper.gt0(request.getWeight()) 
                || !NumberHelper.gt0(request.getVolume())){
            result.error(String.format("包裹:%s揽收校验失败：未检测到称重数据，请按照包裹维度操作称重!", request.getPackOrWaybillCode()));
            return false;
        }
        
        return true;
    }

    private void fillWeightVolume(DirectDeliverySortCollectRequest request) {
        if(BusinessHelper.isTrust(request.getBigWaybillDto().getWaybill().getWaybillSign())){
            // 信任商家揽收重量体积不做处理，和终端研发约定传1
            BigDecimal defaultVal = BigDecimal.valueOf(1);
            request.setWeight(defaultVal);
            request.setLength(defaultVal);
            request.setWidth(defaultVal);
            request.setHigh(defaultVal);
        } else if(NumberHelper.gt0(request.getWeight())){
            // 有重量，则表示自动化设备传入的
            request.setWeight(request.getWeight());
            request.setLength(request.getLength());
            request.setWidth(request.getWidth());
            request.setHigh(request.getHigh());
        } else {
            // 需查询称重流水
            String packageCode = request.getPackOrWaybillCode();
            PackageWeighting recentPackWeightFlow = packageWeightingService.queryPackWeightFlowRecentDetail(WaybillUtil.getWaybillCode(packageCode), 
                    packageCode, Lists.newArrayList(BusinessTypeEnum.DMS.getCode(), BusinessTypeEnum.DMS_SORT.getCode()));
            if(recentPackWeightFlow != null){
                request.setWeight(recentPackWeightFlow.getWeight() == null ? BigDecimal.ZERO : BigDecimal.valueOf(recentPackWeightFlow.getWeight()));
                request.setLength(recentPackWeightFlow.getLength() == null ? BigDecimal.ZERO : BigDecimal.valueOf(recentPackWeightFlow.getLength()));
                request.setWidth(recentPackWeightFlow.getWidth() == null ? BigDecimal.ZERO : BigDecimal.valueOf(recentPackWeightFlow.getWidth()));
                request.setHigh(recentPackWeightFlow.getHeight() == null ? BigDecimal.ZERO : BigDecimal.valueOf(recentPackWeightFlow.getHeight()));
            }
        }
        request.setVolume(
                request.getLength() == null ? BigDecimal.ZERO : request.getLength()
                        .multiply(request.getWidth() == null ? BigDecimal.ZERO : request.getWidth())
                        .multiply(request.getHigh() == null ? BigDecimal.ZERO : request.getHigh(), new MathContext(3, RoundingMode.HALF_UP))
        );
    }
}
