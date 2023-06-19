package com.jd.bluedragon.openplateform;

import com.alibaba.fastjson.JSON;
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
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 批量验货每页最多条数
     */
    private static final int PAGE_SIZE = 200;

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
        if(log.isInfoEnabled()){
            log.info("JYCenterServiceImpl.createSendCode param {}", JsonHelper.toJson(jySendCodeRequest.getRequestProfile()));
        }
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

            BaseStaffSiteOrgDto siteOrgDto1 = baseService.queryDmsBaseSiteByCode(jySendCodeRequest.getArriveSiteCode());
            if (siteOrgDto1 == null) {
                invokeResult.parameterError("批次目的站点未在京东维护");
                return invokeResult;
            }

            attributeKeyEnumStringMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(siteOrgDto.getSiteCode()));
            attributeKeyEnumStringMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(siteOrgDto1.getSiteCode()));
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
        if(log.isInfoEnabled()){
            log.info("JYCenterServiceImpl.batchInspectionWithPage param {}", JsonHelper.toJson(batchInspectionPageRequest.getRequestProfile()));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (null == batchInspectionPageRequest || CollectionUtils.isEmpty(batchInspectionPageRequest.getUnloadDetailCargoList())) {
            result.parameterError();
            return result;
        }

        if (batchInspectionPageRequest.getUnloadDetailCargoList().size() > PAGE_SIZE) {
            result.parameterError("禁止超过每页200条的限制");
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
                operatorInfo.setOperateUserId(batchInspectionPageRequest.getOperateUserCode());
                operatorInfo.setOperateUserErp(batchInspectionPageRequest.getOperateUserErp());
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
        if(log.isInfoEnabled()){
            log.info("JYCenterServiceImpl.batchSortingWithPage param {}", JsonHelper.toJson(batchSortingPageRequest.getRequestProfile()));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (null == batchSortingPageRequest || CollectionUtils.isEmpty(batchSortingPageRequest.getCargoNoList()) || StringUtils.isEmpty(batchSortingPageRequest.getBoxCode())) {
            result.parameterError();
            return result;
        }

        if (StringUtils.isEmpty(batchSortingPageRequest.getBoxRouteCode()) || StringUtils.isEmpty(batchSortingPageRequest.getBoxRouteName())) {
            result.parameterError("缺少货物流向信息");
            return result;
        }

        if (batchSortingPageRequest.getCargoNoList().size() > PAGE_SIZE) {
            result.parameterError(String.format("一次最多传%s条", PAGE_SIZE));
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
        if(log.isInfoEnabled()){
            log.info("JYCenterServiceImpl.batchSendWithPage param {}", JsonHelper.toJson(batchSendPageRequest.getRequestProfile()));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();

        if (null == batchSendPageRequest || CollectionUtils.isEmpty(batchSendPageRequest.getCargoNoList())) {
            result.parameterError("缺少货物明细信息");
            return result;
        }

        if (StringUtils.isEmpty(batchSendPageRequest.getBatchCode()) || !BusinessHelper.isSendCode(batchSendPageRequest.getBatchCode())) {
            result.parameterError("缺少货物流向批次信息");
            return result;
        }

        if (batchSendPageRequest.getCargoNoList().size() > PAGE_SIZE) {
            result.parameterError(String.format("一次最多传%s条", PAGE_SIZE));
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

        List<List<CargoOperateInfo>> lists = Lists.partition(batchSendPageRequest.getCargoNoList(), 10);

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
                if (BusinessHelper.isBoxcode(cargoOperateInfo.getBarcode())) {
                    entity.setBoxCode(cargoOperateInfo.getBarcode());
                } else {
                    if (WaybillUtil.isPackageCode(cargoOperateInfo.getBarcode())) {
                        entity.setPackageCode(cargoOperateInfo.getBarcode());
                    }
                }
                if (BusinessHelper.isSendCode(batchSendPageRequest.getBatchCode())) {
                    entity.setSendCode(batchSendPageRequest.getBatchCode());
                }
                // 增加任务开始扫描时间及结束时间
                entity.setTaskScanBeginTime(batchSendPageRequest.getScanBeginTime());
                entity.setTaskScanEndTime(batchSendPageRequest.getScanEndTime());

                entity.setDataOperateType(batchSendPageRequest.getOperateType());
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

    /**
     * 接收装车发货完成接口
     *
     * @param sendVehicleFinishRequest 发货完成请求
     * @return 返回是否成功
     */
    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.sendVehicleFinish", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> sendVehicleFinish(SendVehicleFinishRequest sendVehicleFinishRequest) {
        if(log.isInfoEnabled()){
            log.info("JYCenterServiceImpl.sendVehicleFinish param {}", JsonHelper.toJson(sendVehicleFinishRequest));
        }
        InvokeResult<Boolean> result = new InvokeResult<>();

        // check param
        final Result<Void> checkResult = this.checkParam4sendVehicleFinish(sendVehicleFinishRequest);
        if (!checkResult.isSuccess()) {
            log.info("JYCenterServiceImpl.sendVehicleFinish check fail {} {}", JSON.toJSONString(sendVehicleFinishRequest), JSON.toJSONString(checkResult));
            result.parameterError(checkResult.getMessage());
            return result;
        }

        try {
            OperatorInfo operatorInfo = sendVehicleFinishRequest.getOperatorInfo();

            BaseStaffSiteOrgDto operateSiteInfo = baseService.queryDmsBaseSiteByCode(operatorInfo.getOperateSiteCode());
            if (operateSiteInfo == null) {
                result.confirmMessage(String.format("操作站点%s未在京东维护", operatorInfo.getOperateSiteCode()));
                return result;
            }

            final List<String> batchCodes = sendVehicleFinishRequest.getBatchCodes();

            // 根据批次获取去重的始发地、目的地
            Map<String, Integer> batchCodeStartSiteCodeMap = new HashMap<>();
            Map<String, Integer> batchCodeReceiveSiteCodeMap = new HashMap<>();
            Map<Integer, BaseStaffSiteOrgDto> siteMap = new HashMap<>();
            siteMap.put(operateSiteInfo.getSiteCode(), operateSiteInfo);

            for (String batchCode : batchCodes) {
                Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(batchCode);
                Integer receiveSiteCode = BusinessUtil.getReceiveSiteCodeFromSendCode(batchCode);

                batchCodeStartSiteCodeMap.put(batchCode, createSiteCode);
                batchCodeReceiveSiteCodeMap.put(batchCode, receiveSiteCode);

                if (!siteMap.containsKey(createSiteCode)){
                    BaseStaffSiteOrgDto startSite = baseService.queryDmsBaseSiteByCode(String.valueOf(createSiteCode));
                    if (startSite == null) {
                        result.confirmMessage(String.format("批次号%s表示的货物始发站点%s未在京东维护", batchCode, createSiteCode));
                        return result;
                    }
                    siteMap.put(createSiteCode, startSite);
                }

                if (!siteMap.containsKey(receiveSiteCode)) {
                    BaseStaffSiteOrgDto receiveSite = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSiteCode));
                    if (receiveSite == null) {
                        result.confirmMessage(String.format("批次号%s表示的货物目的站点%s未在京东维护", batchCode, receiveSite));
                        return result;
                    }
                    siteMap.put(receiveSiteCode, receiveSite);
                }
            }

            List<Task> tasks = new ArrayList<>();
            for (String batchCode : batchCodes) {
                final Integer startSiteCode = batchCodeStartSiteCodeMap.get(batchCode);
                final Integer receiveSiteCode = batchCodeReceiveSiteCodeMap.get(batchCode);
                final BaseStaffSiteOrgDto startSite = siteMap.get(startSiteCode);
                final BaseStaffSiteOrgDto receiveSite = siteMap.get(receiveSiteCode);

                operatorInfo.setOperateSiteId(operateSiteInfo.getSiteCode());
                operatorInfo.setOperateSiteCode(operateSiteInfo.getDmsSiteCode());
                operatorInfo.setOperateSiteName(operateSiteInfo.getSiteName());
                operatorInfo.setOperateUserId(-1);

                JYCargoOperateEntity entity = new JYCargoOperateEntity();
                entity.setSendCode(batchCode);
                // 增加任务开始扫描时间及结束时间
                entity.setTaskScanBeginTime(sendVehicleFinishRequest.getScanBeginTime());
                entity.setTaskScanEndTime(sendVehicleFinishRequest.getScanEndTime());

                entity.setCreateSiteId(startSite.getSiteCode());
                entity.setCreateSiteCode(startSite.getDmsSiteCode());
                entity.setCreateSiteName(startSite.getSiteName());
                entity.setReceiveSiteId(receiveSite.getSiteCode());
                entity.setReceiveSiteCode(receiveSite.getDmsSiteCode());
                entity.setReceiveSiteName(receiveSite.getSiteName());
                entity.setOperatorInfo(operatorInfo);
                entity.setRequestProfile(sendVehicleFinishRequest.getRequestProfile());

                Task task = new Task();
                task.setBody(JsonHelper.toJson(entity));
                task.setType(Task.TASK_TYPE_JY_CARGO_OPERATE_SEND_FINISH);
                task.setTableName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE);
                task.setSequenceName(Task.TABLE_NAME_JY_OPEN_CARGO_OPERATE_SEQ);
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setKeyword1(entity.getSendCode());
                task.setKeyword2(operatorInfo.getOperateSiteCode());
                task.setCreateSiteCode(startSite.getSiteCode());
                task.setReceiveSiteCode(receiveSite.getSiteCode());
                task.setFingerprint(Md5Helper.encode(JsonHelper.toJson(entity)));
                tasks.add(task);
            }
            taskService.addBatch(tasks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private Result<Void> checkParam4OperatorInfo(OperatorInfo operatorInfo){
        Result<Void> result = Result.success();
        if (operatorInfo == null) {
            return result.toFail("参数错误，operatorInfo不能为空");
        }
        if (StringUtils.isEmpty(operatorInfo.getOperateUserErp())) {
            return result.toFail("参数错误，operatorInfo.operateUserErp不能为空");
        }
        if (StringUtils.isEmpty(operatorInfo.getOperateUserName())) {
            return result.toFail("参数错误，operatorInfo.operateUserName不能为空");
        }
        if (StringUtils.isEmpty(operatorInfo.getOperateSiteCode())) {
            return result.toFail("参数错误，operatorInfo.operateSiteCode不能为空");
        }
        if (StringUtils.isEmpty(operatorInfo.getOperateSiteName())) {
            return result.toFail("参数错误，operatorInfo.operateSiteName不能为空");
        }
        if (operatorInfo.getOperateTime() == null) {
            return result.toFail("参数错误，operatorInfo.operateTime不能为空");
        }
        return result;
    }

    private Result<Void> checkParam4RequestProfile(RequestProfile requestProfile){
        Result<Void> result = Result.success();
        if (requestProfile == null) {
            return result.toFail("参数错误，requestProfile不能为空");
        }
        if (StringUtils.isEmpty(requestProfile.getSysSource())) {
            return result.toFail("参数错误，requestProfile.sysSource不能为空");
        }
        if (requestProfile.getTimestamp() == null) {
            return result.toFail("参数错误，requestProfile.timestamp不能为空");
        }
        if (requestProfile.getTimestamp() <= 0) {
            return result.toFail("参数错误，requestProfile.timestamp值不合法，必须大于0");
        }

        return result;
    }

    private Result<Void> checkParam4sendVehicleFinish(SendVehicleFinishRequest sendVehicleFinishRequest) {
        Result<Void> result = Result.success();

        if (sendVehicleFinishRequest == null) {
            return result.toFail("参数错误，参数不能为空");
        }
        final Result<Void> checkParam4OperatorInfoResult = this.checkParam4OperatorInfo(sendVehicleFinishRequest.getOperatorInfo());
        if (!checkParam4OperatorInfoResult.isSuccess()) {
            return result.toFail(checkParam4OperatorInfoResult.getMessage(), checkParam4OperatorInfoResult.getCode());
        }
        final Result<Void> checkParam4RequestProfileResult = this.checkParam4RequestProfile(sendVehicleFinishRequest.getRequestProfile());
        if (!checkParam4RequestProfileResult.isSuccess()) {
            return result.toFail(checkParam4RequestProfileResult.getMessage(), checkParam4RequestProfileResult.getCode());
        }
        if (sendVehicleFinishRequest.getScanBeginTime() == null) {
            return result.toFail("参数错误，scanBeginTime不能为空");
        }
        if (sendVehicleFinishRequest.getScanBeginTime() <= 0) {
            return result.toFail("参数错误，scanBeginTime不合法");
        }
        if (sendVehicleFinishRequest.getScanEndTime() == null) {
            return result.toFail("参数错误，scanEndTime不能为空");
        }
        if (sendVehicleFinishRequest.getScanEndTime() <= 0) {
            return result.toFail("参数错误，scanEndTime不合法");
        }
        if (CollectionUtils.isEmpty(sendVehicleFinishRequest.getBatchCodes())) {
            return result.toFail("参数错误，batchCodes不能为空");
        }
        if (sendVehicleFinishRequest.getBatchCodes().size() > PAGE_SIZE) {
            return result.toFail(String.format("参数错误，batchCodes一次最多传%s条", PAGE_SIZE));
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMS.JSF.JYCenterService.batchWeightVolume", jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> batchWeightVolume(BatchWeightVolumeRequest batchWeightVolumeRequest) {
        if(log.isInfoEnabled()){
            log.info("JYCenterServiceImpl.batchWeightVolume param {}", JsonHelper.toJson(batchWeightVolumeRequest.getRequestProfile()));
        }

        if (null == batchWeightVolumeRequest || batchWeightVolumeRequest.getRequestProfile() == null
                || CollectionUtils.isEmpty(batchWeightVolumeRequest.getDetailList())) {
            return new InvokeResult<>(InvokeResult.RESULT_PARAMETER_ERROR_CODE, InvokeResult.PARAM_ERROR, Boolean.FALSE);
        }

        InvokeResult<Boolean> result = new InvokeResult<>();
        int failCount = 0, successCount = 0;
        StringBuilder failMsg = new StringBuilder(), successMsg = new StringBuilder("OK");

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
            if (weightVolumeOperateInfo.getHigh() != null) {
                entity.setHeight(weightVolumeOperateInfo.getHigh().doubleValue());
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

                if (weightVolumeOperateInfo.getOperatorInfo().getOperateTime() == null || weightVolumeOperateInfo.getOperatorInfo().getOperateTime() <= 0) {
                    failCount++;
                    failMsg.append("操作时间不正确;");
                    continue;
                }
                entity.setOperateTime(new Date(weightVolumeOperateInfo.getOperatorInfo().getOperateTime()));
            }
            InvokeResult<Boolean> iterm = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
            if (iterm != null && iterm.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
                successCount ++;
            } else {
                failCount ++;
                if (iterm != null) {
                    failMsg.append(iterm.getMessage());
                }
            }
        }

        if (successCount > 0 && failCount == 0) {
            result.success();
        } else if (successCount > 0 && failCount > 0) {
            result.confirmMessage("部分失败:" + failMsg.toString());
        } else if (successCount == 0) {
            result.error("全部失败:" + failMsg.toString());
        }
        return result;
    }
}
