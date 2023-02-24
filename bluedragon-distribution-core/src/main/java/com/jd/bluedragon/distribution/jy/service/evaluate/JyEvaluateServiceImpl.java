package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.google.common.base.Joiner;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateDimensionDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateTargetInfoDao;
import com.jd.bluedragon.distribution.jy.dao.group.JyTaskGroupMemberDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("jyEvaluateService")
public class JyEvaluateServiceImpl implements JyEvaluateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateServiceImpl.class);

    /**
     * 评价类型：1-装车 2-卸车
     */
    private static final Integer EVALUATE_TYPE_LOAD = 1;

    private static final Integer EVALUATE_TYPE_UNLOAD = 2;

    @Autowired
    private JyEvaluateDimensionDao jyEvaluateDimensionDao;
    @Autowired
    private JyEvaluateTargetInfoDao jyEvaluateTargetInfoDao;
    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;
    @Autowired
    private VosManager vosManager;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;
    @Autowired
    private JyTaskGroupMemberDao jyTaskGroupMemberDao;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JyEvaluateCommonService jyEvaluateCommonService;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.dimensionOptions", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> dimensionOptions() {
        JdCResponse<List<SelectOption>> result = new JdCResponse<>();
        result.toSucceed();
        try {
            List<JyEvaluateDimensionEntity> list = jyEvaluateDimensionDao.findAllDimension();
            if (CollectionUtils.isEmpty(list)) {
                result.setData(new ArrayList<>());
                return result;
            }
            List<SelectOption> options = new ArrayList<>();
            for (JyEvaluateDimensionEntity dimension : list) {
                options.add(new SelectOption(dimension.getCode(), dimension.getName(), dimension.getStatus()));
            }
            result.setData(options);
        } catch (Exception e) {
            LOGGER.error("dimensionOptions|获取评价维度枚举列表接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.checkIsEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> checkIsEvaluate(EvaluateTargetReq request) {
        JdCResponse<Boolean> result = new JdCResponse<>();
        result.toSucceed();
        try {
            JyEvaluateTargetInfoEntity evaluateTargetInfo = jyEvaluateTargetInfoDao.findBySourceBizId(request.getSourceBizId());
            if (evaluateTargetInfo == null) {
                result.setData(Boolean.FALSE);
                return result;
            }
            result.setData(Boolean.TRUE);
        } catch (Exception e) {
            LOGGER.error("checkIsEvaluate|查询目标评价与否接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.findTargetEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<EvaluateDimensionDto>> findTargetEvaluateInfo(EvaluateTargetReq request) {
        JdCResponse<List<EvaluateDimensionDto>> result = new JdCResponse<>();
        result.toSucceed();
        try {
            List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(request.getSourceBizId());
            if (CollectionUtils.isEmpty(recordList)) {
                result.setData(new ArrayList<>());
                return result;
            }
            Map<Integer, JyEvaluateDimensionEntity> dimensionEnumMap = jyEvaluateDimensionDao.findAllDimensionMap();
            if (dimensionEnumMap.isEmpty()) {
                result.toError("查询评价维度枚举列表为空");
                return result;
            }
            Map<Integer, EvaluateDimensionDto> map = new HashMap<>();
            for (JyEvaluateRecordEntity record : recordList) {
                transformDataToMap(map, dimensionEnumMap, record);
            }
            result.setData(new ArrayList<>(map.values()));
        } catch (Exception e) {
            LOGGER.error("findTargetEvaluateInfo|查询目标评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.saveTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> saveTargetEvaluate(EvaluateTargetReq request) {
        JdCResponse<Void> result = new JdCResponse<>();
        result.toSucceed();
        try {
            JdCResponse<JyEvaluateTargetInfoEntity> targetResult = createTargetInfo(request);
            if (!targetResult.isSucceed()) {
                LOGGER.warn("createTargetInfo|创建评价目标基础信息出错,request={},msg={}", JsonHelper.toJson(request), targetResult.getMessage());
                result.toError(targetResult.getMessage());
                return result;
            }
            // 评价目标基础信息实体
            JyEvaluateTargetInfoEntity evaluateTargetInfo = targetResult.getData();
            // 评价明细列表
            List<JyEvaluateRecordEntity> recordList = createEvaluateRecord(request, evaluateTargetInfo);
            // 保存
            jyEvaluateCommonService.saveEvaluateInfo(evaluateTargetInfo, recordList);
        } catch (Exception e) {
            LOGGER.error("findTargetEvaluateInfo|查询目标评价详情接口出现异常", e);
            result.toError("服务器异常");
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.updateTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> updateTargetEvaluate(EvaluateTargetReq request) {
        return null;
    }


    private JdCResponse<JyEvaluateTargetInfoEntity> createTargetInfo(EvaluateTargetReq request) {
        JdCResponse<JyEvaluateTargetInfoEntity> result = new JdCResponse<>();
        SealCarDto sealCarDto = findSealCarInfoBySealCarCodeOfTms(request.getSourceBizId());
        if (sealCarDto == null) {
            LOGGER.warn("saveTargetEvaluate|根据封车编码查询运输封车详情返回空,request={}", JsonHelper.toJson(request));
            result.toError("根据封车编码查询运输封车详情返回空");
            return result;
        }
        String transWorkItemCode = sealCarDto.getTransWorkItemCode();
        Integer targetSiteCode = sealCarDto.getEndSiteId();
        Integer sourceSiteCode = sealCarDto.getDesealSiteId();
        JyBizTaskSendVehicleEntity sendVehicleEntity = jyBizTaskSendVehicleService.findByTransWorkAndStartSite(new JyBizTaskSendVehicleEntity(transWorkItemCode, Long.valueOf(targetSiteCode)));
        if (sendVehicleEntity == null) {
            LOGGER.warn("saveTargetEvaluate|查询发货任务返回空,request={},transWorkItemCode={},targetSiteCode={}", JsonHelper.toJson(request), transWorkItemCode, targetSiteCode);
            result.toError("查询发货任务返回空");
            return result;
        }
        // 发货调度任务ID
        String targetTaskId = getJyScheduleTaskId(sendVehicleEntity.getBizId(), JyScheduleTaskTypeEnum.SEND.getCode());
        if (StringUtils.isBlank(targetTaskId)) {
            LOGGER.warn("saveTargetEvaluate|查询发货调度任务返回空,request={},targetBizId={}", JsonHelper.toJson(request), sendVehicleEntity.getBizId());
            result.toError("查询发货调度任务返回空");
            return result;
        }
        // 卸车调度任务ID
        String sourceTaskId = getJyScheduleTaskId(request.getSourceBizId(), JyScheduleTaskTypeEnum.UNLOAD.getCode());
        if (StringUtils.isBlank(sourceTaskId)) {
            LOGGER.warn("saveTargetEvaluate|查询卸车调度任务返回空,request={},sourceBizId={}", JsonHelper.toJson(request), request.getSourceBizId());
            result.toError("查询卸车调度任务返回空");
            return result;
        }
        // 查询发货任务协助人
        JyTaskGroupMemberQuery query = new JyTaskGroupMemberQuery();
        query.setRefTaskId(targetTaskId);
        List<JyTaskGroupMemberEntity> taskGroupMembers = jyTaskGroupMemberDao.queryMemberListByTaskId(query);
        if (CollectionUtils.isEmpty(taskGroupMembers)) {
            LOGGER.warn("saveTargetEvaluate|查询发货任务协助人返回空,request={},targetTaskId={}", JsonHelper.toJson(request), targetTaskId);
            result.toError("查询发货任务协助人返回空");
            return result;
        }
        JyEvaluateTargetInfoEntity targetInfo = new JyEvaluateTargetInfoEntity();
        BaseStaffSiteOrgDto targetSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(targetSiteCode);
        if (targetSiteOrgDto == null) {
            LOGGER.warn("saveTargetEvaluate|查询发货任务所属区域返回空,request={},targetSiteCode={}", JsonHelper.toJson(request), targetSiteCode);
            result.toError("查询发货任务所属区域返回空");
            return result;
        }
        BaseStaffSiteOrgDto sourceSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(sourceSiteCode);
        if (sourceSiteOrgDto == null) {
            LOGGER.warn("saveTargetEvaluate|查询卸车任务所属区域返回空,request={},sourceSiteCode={}", JsonHelper.toJson(request), sourceSiteCode);
            result.toError("查询卸车任务所属区域返回空");
            return result;
        }
        targetInfo.setTargetAreaCode((int)targetSiteOrgDto.getAreaId());
        targetInfo.setTargetAreaName(targetSiteOrgDto.getAreaName());
        targetInfo.setTargetSiteCode(targetSiteCode);
        targetInfo.setTargetSiteName(sealCarDto.getEndSiteName());
        targetInfo.setTargetTaskId(targetTaskId);
        targetInfo.setTargetBizId(sendVehicleEntity.getBizId());
        targetInfo.setTargetStartTime(sendVehicleEntity.getCreateTime());
        targetInfo.setTargetFinishTime(sendVehicleEntity.getUpdateTime());
        targetInfo.setTransWorkItemCode(transWorkItemCode);
        targetInfo.setVehicleNumber(sealCarDto.getVehicleNumber());
        targetInfo.setSealTime(sealCarDto.getSealCarTime());
        targetInfo.setHelperErp(getUserCodesStr(taskGroupMembers));

        targetInfo.setSourceAreaCode((int)sourceSiteOrgDto.getAreaId());
        targetInfo.setSourceAreaName(sourceSiteOrgDto.getAreaName());
        targetInfo.setSourceSiteCode(sourceSiteCode);
        targetInfo.setSourceSiteName(sealCarDto.getDesealSiteName());
        targetInfo.setSourceTaskId(sourceTaskId);
        targetInfo.setSourceBizId(request.getSourceBizId());
        targetInfo.setUnsealTime(sealCarDto.getDesealCarTime());
        targetInfo.setEvaluateType(EVALUATE_TYPE_LOAD);
        targetInfo.setStatus(request.getStatus());
        targetInfo.setEvaluateUserErp(request.getUser().getUserErp());

        result.setData(targetInfo);
        return result;
    }


    private String getUserCodesStr(List<JyTaskGroupMemberEntity> taskGroupMembers) {
        String userCodesStr = "";
        for (JyTaskGroupMemberEntity taskGroupMember : taskGroupMembers) {
            userCodesStr = Constants.SEPARATOR_COMMA + taskGroupMember.getUserCode();
        }
        return userCodesStr.substring(1);
    }

    /**
     * 查询调度任务ID
     */
    private String getJyScheduleTaskId(String bizId, String taskType) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(taskType);
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return null != scheduleTask ? scheduleTask.getTaskId() : StringUtils.EMPTY;
    }

    /**
     * 通过封车编码获取封车信息
     */
    private SealCarDto findSealCarInfoBySealCarCodeOfTms(String sealCarCode){
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        LOGGER.info("TmsSealCarStatusConsumer获取封车信息返回数据sealCarCode={},result={}",sealCarCode, JsonHelper.toJson(sealCarDtoCommonDto));
        if (sealCarDtoCommonDto == null || Constants.RESULT_SUCCESS != sealCarDtoCommonDto.getCode()) {
            return null;
        }
        return sealCarDtoCommonDto.getData();
    }

    private List<JyEvaluateRecordEntity> createEvaluateRecord(EvaluateTargetReq request, JyEvaluateTargetInfoEntity evaluateTargetInfo) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        String dimensionCodeStr = "";
        int imgCount = 0;
        for (EvaluateDimensionReq dimensionReq : request.getDimensionList()) {
            dimensionCodeStr = Constants.SEPARATOR_COMMA + dimensionReq.getDimensionCode();
            JyEvaluateRecordEntity record = new JyEvaluateRecordEntity();
            record.setEvaluateType(1);
            record.setTargetBizId(evaluateTargetInfo.getTargetBizId());
            record.setSourceBizId(evaluateTargetInfo.getSourceBizId());
            record.setStatus(request.getStatus());
            record.setDimensionCode(dimensionReq.getDimensionCode());
            if (CollectionUtils.isNotEmpty(dimensionReq.getImgUrlList())) {
                int count = dimensionReq.getImgUrlList().size();
                imgCount = imgCount + count;
                record.setImgCount(count);
                record.setImgUrl(Joiner.on(Constants.SEPARATOR_COMMA).join(dimensionReq.getImgUrlList()));
            }
            record.setRemark(dimensionReq.getRemark());
            record.setCreateUserErp(request.getUser().getUserErp());
            record.setCreateUserName(request.getUser().getUserName());
            record.setUpdateUserErp(request.getUser().getUserErp());
            record.setUpdateUserName(request.getUser().getUserName());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setYn(Constants.YN_YES);
            recordList.add(record);
        }
        evaluateTargetInfo.setDimensionCode(dimensionCodeStr.substring(1));
        evaluateTargetInfo.setImgCount(imgCount);
        return recordList;
    }

    private void transformDataToMap(Map<Integer, EvaluateDimensionDto> map, Map<Integer,
            JyEvaluateDimensionEntity> dimensionEnumMap, JyEvaluateRecordEntity record) {
        EvaluateDimensionDto evaluateDimensionDto = map.get(record.getDimensionCode());
        if (evaluateDimensionDto == null) {
            evaluateDimensionDto = new EvaluateDimensionDto();
            evaluateDimensionDto.setDimensionCode(record.getDimensionCode());
            JyEvaluateDimensionEntity dimensionEnum = dimensionEnumMap.get(record.getDimensionCode());
            evaluateDimensionDto.setDimensionName(dimensionEnum.getName());
            if (StringUtils.isNotBlank(record.getImgUrl())) {
                evaluateDimensionDto.setImgUrlList(Arrays.asList(record.getImgUrl().split(Constants.SEPARATOR_COMMA)));
            }
            if (Constants.NUMBER_ONE.equals(dimensionEnum.getStatus())) {
                evaluateDimensionDto.setStatus(dimensionEnum.getStatus());
            }
            evaluateDimensionDto.setRemark(record.getRemark());
        } else {
            if (StringUtils.isNotBlank(record.getImgUrl())) {
                List<String> currentImgUrlList = Arrays.asList(record.getImgUrl().split(Constants.SEPARATOR_COMMA));
                if (CollectionUtils.isEmpty(evaluateDimensionDto.getImgUrlList())) {
                    evaluateDimensionDto.setImgUrlList(currentImgUrlList);
                } else {
                    evaluateDimensionDto.getImgUrlList().addAll(currentImgUrlList);
                }
            }
            if (StringUtils.isNotBlank(record.getRemark())) {
                if (StringUtils.isNotBlank(evaluateDimensionDto.getRemark())) {
                    evaluateDimensionDto.setRemark(evaluateDimensionDto.getRemark() + "\n" + record.getRemark());
                } else {
                    evaluateDimensionDto.setRemark(record.getRemark());
                }
            }
        }
    }

}
