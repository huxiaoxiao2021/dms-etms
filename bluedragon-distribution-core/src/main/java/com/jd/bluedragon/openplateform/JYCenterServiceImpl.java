package com.jd.bluedragon.openplateform;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.open.JYCenterService;
import com.jd.bluedragon.distribution.open.entity.*;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.openplateform
 * @ClassName: JYCenterServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 17:53
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service("jyCenterService")
public class JYCenterServiceImpl implements JYCenterService {

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private TaskService taskService;

    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.createSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<String> createSendCode(JYSendCodeRequest jySendCodeRequest) {

        InvokeResult<String> invokeResult = new InvokeResult<>();

        if (jySendCodeRequest == null) {
            invokeResult.parameterError();
            return invokeResult;
        }

        if (StringUtils.isEmpty(jySendCodeRequest.getDepartSiteCode())) {
            invokeResult.parameterError("批次始发地为空");
            return invokeResult;
        }

        if (StringUtils.isEmpty(jySendCodeRequest.getArriveSiteCode())) {
            invokeResult.parameterError("批次目的地为空");
            return invokeResult;
        }

        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumStringMap = new HashMap<>();

        //德邦融合的始发转化逻辑
        if (FromSourceEnum.DPRH.name().equals(jySendCodeRequest.getRequestProfile().getSysSource())) {
            BaseStaffSiteOrgDto siteOrgDto = baseService.queryDmsBaseSiteByCode(jySendCodeRequest.getDepartSiteCode());
            if (siteOrgDto == null) {
                invokeResult.parameterError("批次始发站点未在京东维护");
                return invokeResult;
            }

            attributeKeyEnumStringMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(siteOrgDto.getSiteCode()));
            attributeKeyEnumStringMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, jySendCodeRequest.getArriveSiteCode());
        }

        BusinessCodeFromSourceEnum fromSource = jySendCodeRequest.getRequestProfile() == null? BusinessCodeFromSourceEnum.UNKNOWN_SYS
                : BusinessCodeFromSourceEnum.getFromName(jySendCodeRequest.getRequestProfile().getSysSource());

        String operator = jySendCodeRequest.getOperatorInfo() == null? ""
                : StringUtils.isEmpty(jySendCodeRequest.getOperatorInfo().getOperateUserErp()) || "-1".equals(jySendCodeRequest.getOperatorInfo().getOperateUserErp())?
                jySendCodeRequest.getOperatorInfo().getOperateUserName() : jySendCodeRequest.getOperatorInfo().getOperateUserErp();

        String sendCode = sendCodeService.createSendCode(attributeKeyEnumStringMap, fromSource, operator);

        return new InvokeResult<>(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE, sendCode);
    }

    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.batchInspectionWithPage", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> batchInspectionWithPage(BatchInspectionPageRequest batchInspectionPageRequest) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (null == batchInspectionPageRequest || CollectionUtils.isEmpty(batchInspectionPageRequest.getUnloadDetailCargoList())) {
            result.parameterError();
            return result;
        }

        List<List<CargoOperateInfo>> lists = Lists.partition(batchInspectionPageRequest.getUnloadDetailCargoList(), 10);

        BaseStaffSiteOrgDto siteOrgDto = baseService.queryDmsBaseSiteByCode(batchInspectionPageRequest.getOperateSiteCode());
        if (siteOrgDto == null) {
            result.confirmMessage("操作站点未在京东维护");
            return result;
        }

        for (List<CargoOperateInfo> list : lists) {
            List<Task> tasks = new ArrayList<>();
            for (CargoOperateInfo cargoOperateInfo : list) {
                OperatorInfo operatorInfo = new OperatorInfo();
                operatorInfo.setOperateTime(cargoOperateInfo.getOperateTime());
                operatorInfo.setOperateSiteId(siteOrgDto.getSiteCode());
                operatorInfo.setOperateSiteCode(siteOrgDto.getDmsSiteCode());
                operatorInfo.setOperateSiteName(siteOrgDto.getSiteName());
                operatorInfo.setOperateUserId(-1);
                operatorInfo.setOperateUserErp(cargoOperateInfo.getOperateUserErp());
                operatorInfo.setOperateUserName(cargoOperateInfo.getOperateUserName());

                JYCargoOperateEntity entity = new JYCargoOperateEntity();
                entity.setBarcode(cargoOperateInfo.getBarcode());
                if (WaybillUtil.isPackageCode(cargoOperateInfo.getBarcode())) {
                    entity.setPackageCode(cargoOperateInfo.getBarcode());
                }
                if (BusinessHelper.isSendCode(batchInspectionPageRequest.getBatchCode())) {
                    entity.setSendCode(batchInspectionPageRequest.getBatchCode());
                }
                entity.setCreateSiteId(siteOrgDto.getSiteCode());
                entity.setCreateSiteCode(siteOrgDto.getDmsSiteCode());
                entity.setCreateSiteName(siteOrgDto.getSiteName());
                entity.setOperatorInfo(operatorInfo);
                entity.setRequestProfile(batchInspectionPageRequest.getRequestProfile());

                Task task = new Task();
                task.setBody(JsonHelper.toJson(entity));
                task.setType(Task.TASK_TYPE_JY_CARGO_OPERATE_INSPECTION);
                task.setTableName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE);
                task.setSequenceName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE_SEQ);
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setKeyword1(entity.getBarcode());
                task.setKeyword2(operatorInfo.getOperateSiteCode());
                task.setFingerprint(Md5Helper.encode(JsonHelper.toJson(entity)));

                tasks.add(task);
            }

            taskService.addBatch(tasks);
        }
        result.success();

        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.batchSortingWithPage", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> batchSortingWithPage(BatchSortingPageRequest batchSortingPageRequest) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (null == batchSortingPageRequest || CollectionUtils.isEmpty(batchSortingPageRequest.getCargoNoList()) || StringUtils.isEmpty(batchSortingPageRequest.getBoxCode())) {
            result.parameterError();
            return result;
        }

        if (StringUtils.isEmpty(batchSortingPageRequest.getBoxRouteCode()) || StringUtils.isEmpty(batchSortingPageRequest.getBoxRouteName())) {
            result.parameterError("缺少货物流向信息");
            return result;
        }


        BaseStaffSiteOrgDto siteOrgDto = baseService.queryDmsBaseSiteByCode(batchSortingPageRequest.getOperateSiteCode());
        if (siteOrgDto == null) {
            result.confirmMessage("操作站点未在京东维护");
            return result;
        }

        String[] boxRoutes = batchSortingPageRequest.getBoxRouteCode().split(Constants.SEPARATOR_HYPHEN);
        if (boxRoutes.length != 2) {
            result.parameterError("货物流向信息解析失败");
            return result;
        }

        BaseStaffSiteOrgDto siteOrgDto1 = baseService.queryDmsBaseSiteByCode(boxRoutes[0]);
        if (siteOrgDto1 == null) {
            result.confirmMessage("货物始发站点未在京东维护");
            return result;
        }

        BaseStaffSiteOrgDto siteOrgDto2 = baseService.queryDmsBaseSiteByCode(boxRoutes[1]);
        if (siteOrgDto2 == null) {
            result.confirmMessage("货物目的站点未在京东维护");
            return result;
        }

        List<List<CargoOperateInfo>> lists = Lists.partition(batchSortingPageRequest.getCargoNoList(), 10);

        for (List<CargoOperateInfo> list : lists) {
            List<Task> tasks = new ArrayList<>();
            for (CargoOperateInfo cargoOperateInfo : list) {
                OperatorInfo operatorInfo = new OperatorInfo();
                operatorInfo.setOperateTime(cargoOperateInfo.getOperateTime());
                operatorInfo.setOperateSiteId(siteOrgDto.getSiteCode());
                operatorInfo.setOperateSiteCode(siteOrgDto.getDmsSiteCode());
                operatorInfo.setOperateSiteName(siteOrgDto.getSiteName());
                operatorInfo.setOperateUserId(-1);
                operatorInfo.setOperateUserErp(cargoOperateInfo.getOperateUserErp());
                operatorInfo.setOperateUserName(cargoOperateInfo.getOperateUserName());

                JYCargoOperateEntity entity = new JYCargoOperateEntity();
                entity.setBarcode(cargoOperateInfo.getBarcode());
                if (WaybillUtil.isPackageCode(cargoOperateInfo.getBarcode())) {
                    entity.setPackageCode(cargoOperateInfo.getBarcode());
                }
                entity.setBoxCode(batchSortingPageRequest.getBoxCode());
                entity.setDataOperateType(batchSortingPageRequest.getOperateType());
                entity.setCreateSiteId(siteOrgDto1.getSiteCode());
                entity.setCreateSiteCode(siteOrgDto1.getDmsSiteCode());
                entity.setCreateSiteName(siteOrgDto1.getSiteName());
                entity.setReceiveSiteId(siteOrgDto2.getSiteCode());
                entity.setReceiveSiteCode(siteOrgDto2.getDmsSiteCode());
                entity.setReceiveSiteName(siteOrgDto2.getSiteName());
                entity.setOperatorInfo(operatorInfo);
                entity.setRequestProfile(batchSortingPageRequest.getRequestProfile());

                Task task = new Task();
                task.setBody(JsonHelper.toJson(entity));
                task.setType(Task.TASK_TYPE_JY_CARGO_OPERATE_SORTING);
                task.setTableName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE);
                task.setSequenceName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE_SEQ);
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setKeyword1(entity.getBarcode());
                task.setKeyword2(operatorInfo.getOperateSiteCode());
                task.setFingerprint(Md5Helper.encode(JsonHelper.toJson(entity)));

                tasks.add(task);
            }

            taskService.addBatch(tasks);
        }
        result.success();

        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.batchSendWithPage", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> batchSendWithPage(BatchSendPageRequest batchSendPageRequest) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (null == batchSendPageRequest || CollectionUtils.isEmpty(batchSendPageRequest.getLoadTaskDetail())) {
            result.parameterError("缺少货物明细信息");
            return result;
        }

        if (StringUtils.isEmpty(batchSendPageRequest.getBatchCode()) || !BusinessHelper.isSendCode(batchSendPageRequest.getBatchCode())) {
            result.parameterError("缺少货物流向批次信息");
            return result;
        }

        Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(batchSendPageRequest.getBatchCode());
        Integer receiveSiteCode = BusinessUtil.getReceiveSiteCodeFromSendCode(batchSendPageRequest.getBatchCode());

        BaseStaffSiteOrgDto siteOrgDto = baseService.queryDmsBaseSiteByCode(batchSendPageRequest.getOperateSiteCode());
        if (siteOrgDto == null) {
            result.confirmMessage("操作站点未在京东维护");
            return result;
        }

        BaseStaffSiteOrgDto siteOrgDto1 = baseService.queryDmsBaseSiteByCode(String.valueOf(createSiteCode));
        if (siteOrgDto1 == null) {
            result.confirmMessage("货物始发站点未在京东维护");
            return result;
        }

        BaseStaffSiteOrgDto siteOrgDto2 = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSiteCode));
        if (siteOrgDto2 == null) {
            result.confirmMessage("货物目的站点未在京东维护");
            return result;
        }

        List<List<CargoOperateInfo>> lists = Lists.partition(batchSendPageRequest.getLoadTaskDetail(), 10);

        for (List<CargoOperateInfo> list : lists) {
            List<Task> tasks = new ArrayList<>();
            for (CargoOperateInfo cargoOperateInfo : list) {
                OperatorInfo operatorInfo = new OperatorInfo();
                operatorInfo.setOperateTime(cargoOperateInfo.getOperateTime());
                operatorInfo.setOperateSiteId(siteOrgDto.getSiteCode());
                operatorInfo.setOperateSiteCode(siteOrgDto.getDmsSiteCode());
                operatorInfo.setOperateSiteName(siteOrgDto.getSiteName());
                operatorInfo.setOperateUserId(-1);
                operatorInfo.setOperateUserErp(cargoOperateInfo.getOperateUserErp());
                operatorInfo.setOperateUserName(cargoOperateInfo.getOperateUserName());

                JYCargoOperateEntity entity = new JYCargoOperateEntity();
                entity.setBarcode(cargoOperateInfo.getBarcode());
                if (WaybillUtil.isPackageCode(cargoOperateInfo.getBarcode())) {
                    entity.setPackageCode(cargoOperateInfo.getBarcode());
                }
                if (BusinessHelper.isBoxcode(cargoOperateInfo.getBarcode())) {
                    entity.setBoxCode(cargoOperateInfo.getBarcode());
                }
                if (BusinessHelper.isSendCode(batchSendPageRequest.getBatchCode())) {
                    entity.setSendCode(batchSendPageRequest.getBatchCode());
                }
                entity.setCreateSiteId(siteOrgDto1.getSiteCode());
                entity.setCreateSiteCode(siteOrgDto1.getDmsSiteCode());
                entity.setCreateSiteName(siteOrgDto1.getSiteName());
                entity.setReceiveSiteId(siteOrgDto2.getSiteCode());
                entity.setReceiveSiteCode(siteOrgDto2.getDmsSiteCode());
                entity.setReceiveSiteName(siteOrgDto2.getSiteName());
                entity.setOperatorInfo(operatorInfo);
                entity.setRequestProfile(batchSendPageRequest.getRequestProfile());

                Task task = new Task();
                task.setBody(JsonHelper.toJson(entity));
                task.setType(Task.TASK_TYPE_JY_CARGO_OPERATE_SEND);
                task.setTableName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE);
                task.setSequenceName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE_SEQ);
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setKeyword1(entity.getBarcode());
                task.setKeyword2(operatorInfo.getOperateSiteCode());
                task.setFingerprint(Md5Helper.encode(JsonHelper.toJson(entity)));

                tasks.add(task);
            }

            taskService.addBatch(tasks);
        }
        result.success();

        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.batchWeightVolume", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> batchWeightVolume(BatchWeightVolumeRequest batchWeightVolumeRequest) {

        if (null == batchWeightVolumeRequest || batchWeightVolumeRequest.getRequestProfile() == null
                || CollectionUtils.isEmpty(batchWeightVolumeRequest.getDetailList())) {
            return new InvokeResult<>(InvokeResult.RESULT_PARAMETER_ERROR_CODE, InvokeResult.PARAM_ERROR, Boolean.FALSE);
        }

        InvokeResult<Boolean> result = new InvokeResult<>();
        int failCount = 0, successCount = 0;

        FromSourceEnum fromSource = FromSourceEnum.valueOf(
                batchWeightVolumeRequest.getRequestProfile().getSysSource());
        for (WeightVolumeOperateInfo weightVolumeOperateInfo : batchWeightVolumeRequest.getDetailList()) {
            WeightVolumeEntity entity = new WeightVolumeEntity();
            entity.setBarCode(weightVolumeOperateInfo.getBarcode());
            if (FromSourceEnum.DPRH.equals(fromSource)) {
                entity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE);
            }
            entity.setSourceCode(fromSource);
            if (weightVolumeOperateInfo.getWeight() != null) {
                entity.setWeight(weightVolumeOperateInfo.getWeight().doubleValue());
            }
            if (weightVolumeOperateInfo.getVolume() != null) {
                entity.setVolume(weightVolumeOperateInfo.getVolume().doubleValue());
            }
            if (weightVolumeOperateInfo.getLength() != null) {
                entity.setLength(weightVolumeOperateInfo.getLength().doubleValue());
            }
            if (weightVolumeOperateInfo.getWidth() != null) {
                entity.setWidth(weightVolumeOperateInfo.getWidth().doubleValue());
            }
            if (weightVolumeOperateInfo.getHeight() != null) {
                entity.setHeight(weightVolumeOperateInfo.getHeight().doubleValue());
            }
            if (weightVolumeOperateInfo.getOperatorInfo() != null) {
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.queryDmsBaseSiteByCode(weightVolumeOperateInfo.getOperatorInfo().getOperateSiteCode());
                if (baseStaffSiteOrgDto != null) {
                    entity.setOperateSiteCode(baseStaffSiteOrgDto.getSiteCode());
                    entity.setOperateSiteName(baseStaffSiteOrgDto.getSiteName());
                }
                entity.setOperatorId(-1);
                entity.setOperatorCode(weightVolumeOperateInfo.getOperatorInfo().getOperateUserErp());
                entity.setOperatorName(weightVolumeOperateInfo.getOperatorInfo().getOperateUserName());
                entity.setOperateTime(new Date(weightVolumeOperateInfo.getOperatorInfo().getOperateTime()));
            }
            InvokeResult<Boolean> iterm = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
            if (iterm != null && iterm.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
                successCount ++;
            } else {
                failCount ++;
            }
        }

        if (successCount > 0 && failCount == 0) {
            result.success();
        } else if (successCount > 0 && failCount > 0) {
            result.confirmMessage("部分失败");
        } else if (successCount == 0) {
            result.error("全部失败");
        }

        return result;
    }
}
