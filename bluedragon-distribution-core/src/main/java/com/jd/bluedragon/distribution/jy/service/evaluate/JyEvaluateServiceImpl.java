package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.google.common.base.Joiner;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateTargetReq;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.DimensionOption;
import com.jd.bluedragon.common.dto.operation.workbench.evaluate.response.EvaluateDimensionDto;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateDimensionDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateTargetInfoDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

    /**
     * 评价状态：0-不满意 1-满意
     */
    private static final Integer EVALUATE_STATUS_DISSATISFIED = 0;

    private static final Integer EVALUATE_STATUS_SATISFIED = 1;

    @Autowired
    private JyEvaluateDimensionDao jyEvaluateDimensionDao;
    @Autowired
    private JyEvaluateTargetInfoDao jyEvaluateTargetInfoDao;
    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;
    @Autowired
    private JyEvaluateCommonService jyEvaluateCommonService;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.dimensionOptions", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<DimensionOption> dimensionOptions() {
        List<JyEvaluateDimensionEntity> list = jyEvaluateDimensionDao.findAllDimension();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<DimensionOption> options = new ArrayList<>();
        for (JyEvaluateDimensionEntity dimension : list) {
            DimensionOption dimensionOption = new DimensionOption();
            dimensionOption.setCode(dimension.getCode());
            dimensionOption.setName(dimension.getName());
            dimensionOption.setStatus(dimension.getStatus());
            dimensionOption.setHasTextBox(dimension.getHasTextBox());
            options.add(dimensionOption);
        }
        return options;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.checkIsEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean checkIsEvaluate(EvaluateTargetReq request) {
        JyEvaluateTargetInfoEntity evaluateTargetInfo = jyEvaluateTargetInfoDao.findBySourceBizId(request.getSourceBizId());
        if (evaluateTargetInfo == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    map 内存组装
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.findTargetEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<EvaluateDimensionDto> findTargetEvaluateInfo(EvaluateTargetReq request) {
        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(request.getSourceBizId());
        if (CollectionUtils.isEmpty(recordList)) {
            return new ArrayList<>();
        }
        Map<Integer, JyEvaluateDimensionEntity> dimensionEnumMap = jyEvaluateDimensionDao.findAllDimensionMap();
        if (dimensionEnumMap.isEmpty()) {
            throw new JyBizException("查询评价维度枚举列表为空");
        }
        Map<Integer, EvaluateDimensionDto> map = new HashMap<>();
        for (JyEvaluateRecordEntity record : recordList) {
            transformDataToMap(map, dimensionEnumMap, record);
        }
        return new ArrayList<>(map.values());
    }

    @Override加锁
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.saveTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void saveTargetEvaluate(EvaluateTargetReq request) {
        JyEvaluateTargetInfoEntity targetInfo = jyEvaluateTargetInfoDao.findBySourceBizId(request.getSourceBizId());
        if (targetInfo != null) {
            throw new JyBizException("请勿重复提交");
        }
        // 评价目标基础信息实体
        JyEvaluateTargetInfoEntity evaluateTargetInfo = createTargetInfo(request);
        List<JyEvaluateRecordEntity> recordList;
        // 评价明细列表
        if (EVALUATE_STATUS_SATISFIED.equals(request.getStatus())) {
            // 构造满意的记录
            recordList = createSatisfiedEvaluateRecord(request, evaluateTargetInfo);
        } else {
            // 构造不满意的记录
            recordList = createEvaluateRecord(request, evaluateTargetInfo);
        }
        // 保存(前面都是对象组装，事务都加在这一层)
        jyEvaluateCommonService.saveEvaluateInfo(evaluateTargetInfo, recordList);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.updateTargetEvaluate", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void updateTargetEvaluate(EvaluateTargetReq request) {
        JyEvaluateTargetInfoEntity evaluateTargetInfo = jyEvaluateTargetInfoDao.findBySourceBizId(request.getSourceBizId());
        if (evaluateTargetInfo == null) {
            LOGGER.warn("updateTargetEvaluate|修改评价详情时未找到评价基础信息:request={}", JsonHelper.toJson(request));
            throw new JyBizException("修改评价详情时未找到评价基础信息");
        }
        // 评价明细列表
        List<JyEvaluateRecordEntity> recordList = createEvaluateRecord(request, evaluateTargetInfo);
        // 修改(前面都是对象组装，事务都加在这一层)
        jyEvaluateCommonService.updateEvaluateInfo(evaluateTargetInfo, recordList);
    }

报表字段单独加工
    private JyEvaluateTargetInfoEntity createTargetInfo(EvaluateTargetReq request) {
        // 查询运输任务封车详情
        SealCarDto sealCarDto = jyEvaluateCommonService.findSealCarInfoBySealCarCodeOfTms(request.getSourceBizId());

        String transWorkItemCode = sealCarDto.getTransWorkItemCode();
        Integer targetSiteCode = sealCarDto.getStartSiteId();
        Integer sourceSiteCode = sealCarDto.getEndSiteId();

        // 根据派车单号查询发货任务
        JyBizTaskSendVehicleDetailEntity sendVehicleDetail = jyEvaluateCommonService.findByByTransWorkItemCode(transWorkItemCode);
        // 查询发货调度任务ID
        String targetTaskId = jyEvaluateCommonService.getJyScheduleTaskId(sendVehicleDetail.getSendVehicleBizId(), JyScheduleTaskTypeEnum.SEND.getCode());
        // 查询卸车调度任务ID
        String sourceTaskId = jyEvaluateCommonService.getJyScheduleTaskId(request.getSourceBizId(), JyScheduleTaskTypeEnum.UNLOAD.getCode());
        // 查询发货任务协助人
        List<JyTaskGroupMemberEntity> taskGroupMembers = jyEvaluateCommonService.queryMemberListByTaskId(targetTaskId);
        // 根据发货任务操作场地查询区域信息
        BaseStaffSiteOrgDto targetSiteOrgDto = jyEvaluateCommonService.getSiteInfo(targetSiteCode);
        // 根据卸车任务操作场地查询区域信息
        BaseStaffSiteOrgDto sourceSiteOrgDto = jyEvaluateCommonService.getSiteInfo(sourceSiteCode);

        return createEvaluateTargetInfo(sealCarDto, sendVehicleDetail, sourceTaskId,
                targetTaskId, taskGroupMembers, targetSiteOrgDto, sourceSiteOrgDto, request);
    }


    private String getUserCodesStr(List<JyTaskGroupMemberEntity> taskGroupMembers) {
        String userCodesStr = "";
        for (JyTaskGroupMemberEntity taskGroupMember : taskGroupMembers) {
            userCodesStr = Constants.SEPARATOR_COMMA + taskGroupMember.getUserCode();
        }
        return userCodesStr.substring(1);
    }


    private JyEvaluateTargetInfoEntity createEvaluateTargetInfo(SealCarDto sealCarDto, JyBizTaskSendVehicleDetailEntity sendDetail,
                                                                String sourceTaskId, String targetTaskId,
                                                                List<JyTaskGroupMemberEntity> taskGroupMembers,
                                                                BaseStaffSiteOrgDto targetSiteOrgDto,
                                                                BaseStaffSiteOrgDto sourceSiteOrgDto,
                                                                EvaluateTargetReq request) {
        JyEvaluateTargetInfoEntity targetInfo = new JyEvaluateTargetInfoEntity();
        targetInfo.setTargetAreaCode(targetSiteOrgDto.getOrgId());
        targetInfo.setTargetAreaName(targetSiteOrgDto.getOrgName());
        targetInfo.setTargetSiteCode(sealCarDto.getStartSiteId());
        targetInfo.setTargetSiteName(sealCarDto.getStartSiteName());
        targetInfo.setTargetTaskId(targetTaskId);
        targetInfo.setTargetBizId(sendDetail.getSendVehicleBizId());
        targetInfo.setTargetStartTime(sendDetail.getCreateTime());
        targetInfo.setTargetFinishTime(sendDetail.getUpdateTime());
        targetInfo.setTransWorkItemCode(sealCarDto.getTransWorkItemCode());
        targetInfo.setVehicleNumber(sealCarDto.getVehicleNumber());
        targetInfo.setSealTime(sealCarDto.getSealCarTime());
        targetInfo.setHelperErp(getUserCodesStr(taskGroupMembers));

        targetInfo.setSourceAreaCode(sourceSiteOrgDto.getOrgId());
        targetInfo.setSourceAreaName(sourceSiteOrgDto.getOrgName());
        targetInfo.setSourceSiteCode(sealCarDto.getEndSiteId());
        targetInfo.setSourceSiteName(sealCarDto.getEndSiteName());
        targetInfo.setSourceTaskId(sourceTaskId);
        targetInfo.setSourceBizId(request.getSourceBizId());
        targetInfo.setUnsealTime(sealCarDto.getDesealCarTime());
        targetInfo.setEvaluateType(EVALUATE_TYPE_LOAD);
        targetInfo.setStatus(request.getStatus());
        targetInfo.setEvaluateUserErp(request.getUser().getUserErp());

        targetInfo.setCreateUserErp(request.getUser().getUserErp());
        targetInfo.setCreateUserName(request.getUser().getUserName());
        targetInfo.setUpdateUserErp(request.getUser().getUserErp());
        targetInfo.setUpdateUserName(request.getUser().getUserName());
        targetInfo.setCreateTime(new Date());
        targetInfo.setUpdateTime(new Date());
        targetInfo.setYn(Constants.YN_YES);
        return targetInfo;
    }


    private List<JyEvaluateRecordEntity> createEvaluateRecord(EvaluateTargetReq request, JyEvaluateTargetInfoEntity evaluateTargetInfo) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        StringBuilder dimensionCode = new StringBuilder();
        int imgCount = 0;
        StringBuilder remark = new StringBuilder();
        List<String> dimensionList = null;
        if (StringUtils.isNotBlank(evaluateTargetInfo.getDimensionCode())) {
            dimensionList = new ArrayList<>(Arrays.asList(evaluateTargetInfo.getDimensionCode().split(Constants.SEPARATOR_COMMA)));
        }
        for (EvaluateDimensionReq dimensionReq : request.getDimensionList()) {
            JyEvaluateRecordEntity record = new JyEvaluateRecordEntity();
            record.setEvaluateType(EVALUATE_TYPE_LOAD);
            record.setTargetBizId(evaluateTargetInfo.getTargetBizId());
            record.setSourceBizId(evaluateTargetInfo.getSourceBizId());
            record.setStatus(request.getStatus());
            record.setDimensionCode(dimensionReq.getDimensionCode());
            if (CollectionUtils.isNotEmpty(dimensionList)) {
                if (!dimensionList.contains(String.valueOf(dimensionReq.getDimensionCode()))) {
                    dimensionCode.append(Constants.SEPARATOR_COMMA).append(dimensionReq.getDimensionCode());
                }
            } else {
                dimensionCode.append(Constants.SEPARATOR_COMMA).append(dimensionReq.getDimensionCode());
            }
            if (CollectionUtils.isNotEmpty(dimensionReq.getImgUrlList())) {
                int count = dimensionReq.getImgUrlList().size();
                imgCount = imgCount + count;
                record.setImgCount(count); 报表字段走异步
                record.setImgUrl(Joiner.on(Constants.SEPARATOR_COMMA).join(dimensionReq.getImgUrlList()));
            }
            if (StringUtils.isNotBlank(dimensionReq.getRemark())) {
                remark.append(Constants.SEPARATOR_VERTICAL_LINE).append(dimensionReq.getRemark());
                record.setRemark(dimensionReq.getRemark());
            }
            record.setCreateUserErp(request.getUser().getUserErp());
            record.setCreateUserName(request.getUser().getUserName());
            record.setUpdateUserErp(request.getUser().getUserErp());
            record.setUpdateUserName(request.getUser().getUserName());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setYn(Constants.YN_YES);
            recordList.add(record);
        }
        setExtendTargetInfo(request, evaluateTargetInfo, remark, dimensionCode, imgCount);
        return recordList;
    }

    private List<JyEvaluateRecordEntity> createSatisfiedEvaluateRecord(EvaluateTargetReq request, JyEvaluateTargetInfoEntity evaluateTargetInfo) {
        List<JyEvaluateRecordEntity> recordList = new ArrayList<>();
        JyEvaluateRecordEntity record = new JyEvaluateRecordEntity();
        record.setEvaluateType(1);
        record.setTargetBizId(evaluateTargetInfo.getTargetBizId());
        record.setSourceBizId(evaluateTargetInfo.getSourceBizId());
        record.setStatus(request.getStatus());
        record.setCreateUserErp(request.getUser().getUserErp());
        record.setCreateUserName(request.getUser().getUserName());
        record.setUpdateUserErp(request.getUser().getUserErp());
        record.setUpdateUserName(request.getUser().getUserName());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setYn(Constants.YN_YES);
        recordList.add(record);

        evaluateTargetInfo.setEvaluateUserErp(request.getUser().getUserErp());
        return recordList;
    }

    private void setExtendTargetInfo(EvaluateTargetReq request, JyEvaluateTargetInfoEntity evaluateTargetInfo,
                                     StringBuilder remarkStr, StringBuilder dimensionStr, int imgCount) {
        if (evaluateTargetInfo.getId() == null) {  报表加工另外处理
            if (dimensionStr.length() > 0) {
                evaluateTargetInfo.setDimensionCode(dimensionStr.substring(1));
            }
            evaluateTargetInfo.setImgCount(imgCount);
            if (remarkStr.length() > 0) {
                evaluateTargetInfo.setRemark(remarkStr.substring(1));
            }
        } else {
            evaluateTargetInfo.setImgCount(evaluateTargetInfo.getImgCount() + imgCount);
            if (remarkStr.length() > 0) {
                if (StringUtils.isNotBlank(evaluateTargetInfo.getRemark())) {
                    evaluateTargetInfo.setRemark(evaluateTargetInfo.getRemark() + Constants.SEPARATOR_VERTICAL_LINE + remarkStr.substring(1));
                } else {
                    evaluateTargetInfo.setRemark(remarkStr.substring(1));
                }
            }
            List<String> oldUserErpList = new ArrayList<>(Arrays.asList(evaluateTargetInfo.getEvaluateUserErp().split(Constants.SEPARATOR_COMMA)));
            if (!oldUserErpList.contains(request.getUser().getUserErp())) {
                evaluateTargetInfo.setEvaluateUserErp(evaluateTargetInfo.getEvaluateUserErp() + Constants.SEPARATOR_COMMA + request.getUser().getUserErp());
            }
            if (dimensionStr.length() > 0) {
                if (StringUtils.isNotBlank(evaluateTargetInfo.getDimensionCode())) {
                    evaluateTargetInfo.setDimensionCode(evaluateTargetInfo.getDimensionCode() + Constants.SEPARATOR_COMMA + dimensionStr.substring(1));
                } else {
                    evaluateTargetInfo.setDimensionCode(dimensionStr.substring(1));
                }
            }
        }
    }


    private void transformDataToMap(Map<Integer, EvaluateDimensionDto> map, Map<Integer,
            JyEvaluateDimensionEntity> dimensionEnumMap, JyEvaluateRecordEntity record) {
        // 评价维度编码
        Integer dimensionCode = record.getDimensionCode();
        // 评价维度对应图片url
        String imgUrl = record.getImgUrl();
        // 评价维度文本框内容
        String remark = record.getRemark();

        EvaluateDimensionDto evaluateDimensionDto = map.get(dimensionCode);
        if (evaluateDimensionDto == null) {
            evaluateDimensionDto = new EvaluateDimensionDto();
            evaluateDimensionDto.setDimensionCode(dimensionCode);
            // 评价维度详情
            JyEvaluateDimensionEntity dimensionEnum = dimensionEnumMap.get(dimensionCode);
            // 设置维度名称
            evaluateDimensionDto.setDimensionName(dimensionEnum.getName());
            // 设置维度图片列表
            if (StringUtils.isNotBlank(imgUrl)) {
                evaluateDimensionDto.setImgUrlList(new ArrayList<>(Arrays.asList(imgUrl.split(Constants.SEPARATOR_COMMA))));
            }
            // 设置维度是否重点关注
            evaluateDimensionDto.setStatus(dimensionEnum.getStatus());
            // 设置维度是否携带文本框
            evaluateDimensionDto.setHasTextBox(dimensionEnum.getHasTextBox());
            // 设置维度文本框内容
            evaluateDimensionDto.setRemark(remark);
            map.put(dimensionCode, evaluateDimensionDto);
        } else {
            // 同一评价维度图片放在一起
            if (StringUtils.isNotBlank(imgUrl)) {
                List<String> currentImgUrlList = new ArrayList<>(Arrays.asList(imgUrl.split(Constants.SEPARATOR_COMMA)));
                if (CollectionUtils.isEmpty(evaluateDimensionDto.getImgUrlList())) {
                    evaluateDimensionDto.setImgUrlList(currentImgUrlList);
                } else {
                    evaluateDimensionDto.getImgUrlList().addAll(currentImgUrlList);
                }
            }
            // 文本框内容显示在一起
            if (StringUtils.isNotBlank(remark)) {
                if (StringUtils.isBlank(evaluateDimensionDto.getRemark())) {
                    evaluateDimensionDto.setRemark(remark);
                } else {
                    evaluateDimensionDto.setRemark(evaluateDimensionDto.getRemark() + "\n" + remark);
                }
            }
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<JyEvaluateTargetInfoEntity>> queryPageList(JyEvaluateTargetInfoQuery query) {
        Result<List<JyEvaluateTargetInfoEntity>> result = Result.success();
        this.checkAndFillQuery(query);
        result.setData(jyEvaluateTargetInfoDao.queryPageList(query));
        return result;
    }

    private void checkAndFillQuery(JyEvaluateTargetInfoQuery query) {
        if (query.getPageSize() == null || query.getPageSize() <= 0) {
            query.setPageSize(10);
        }
        if (query.getPageNumber() == null || query.getPageNumber() < 1) {
            query.setPageNumber(1);
        }
        query.setLimit(query.getPageSize());
        int offset = (query.getPageNumber() - 1) * query.getPageSize();
        query.setOffset(offset);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Long> queryCount(JyEvaluateTargetInfoQuery query) {
        Result<Long> result = Result.success();
        this.checkAndFillQuery(query);
        result.setData(jyEvaluateTargetInfoDao.queryCount(query));
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryInfoByTargetBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<JyEvaluateTargetInfoEntity> queryInfoByTargetBizId(String businessId) {
        Result<JyEvaluateTargetInfoEntity> result = Result.success();
        result.setData(jyEvaluateTargetInfoDao.queryInfoByTargetBizId(businessId));
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateServiceImpl.queryRecordByTargetBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<List<JyEvaluateRecordEntity>> queryRecordByTargetBizId(String businessId) {
        Result<List<JyEvaluateRecordEntity>> result = Result.success();
        result.setData(jyEvaluateRecordDao.queryRecordByTargetBizId(businessId));
        return result;
    }
}
