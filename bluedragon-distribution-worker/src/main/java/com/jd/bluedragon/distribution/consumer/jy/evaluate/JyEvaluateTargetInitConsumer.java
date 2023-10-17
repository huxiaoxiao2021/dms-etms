package com.jd.bluedragon.distribution.consumer.jy.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dto.evaluate.EvaluateTargetInitDto;
import com.jd.bluedragon.distribution.jy.dto.evaluate.EvaluateTargetResultDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.service.evaluate.JyEvaluateCommonService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("jyEvaluateTargetInitConsumer")
public class JyEvaluateTargetInitConsumer extends MessageBaseConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateTargetInitConsumer.class);

    /**
     * 评价类型：1-装车 2-卸车
     */
    private static final Integer EVALUATE_TYPE_LOAD = 1;

    /**
     * 评价状态：0-不满意 1-满意
     */
    private static final Integer EVALUATE_STATUS_DISSATISFIED = 0;

    private static final Integer EVALUATE_STATUS_SATISFIED = 1;

    /**
     * 装车评价结果消息业务key
     */
    private static final String EVALUATE_RESULT_BUSINESS_KEY = "RESULT";


    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;
    @Autowired
    private JyEvaluateCommonService jyEvaluateCommonService;
    @Autowired
    @Qualifier("evaluateTargetResultProducer")
    private DefaultJMQProducer evaluateTargetResultProducer;




    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            LOGGER.warn("JyEvaluateTargetInitConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            LOGGER.warn("JyEvaluateTargetInitConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        LOGGER.info("消费装车评价报表初始化消息:msg={}", message.getText());

        EvaluateTargetInitDto targetInitDto = JsonHelper.fromJson(message.getText(), EvaluateTargetInitDto.class);
        if (targetInitDto == null) {
            LOGGER.warn("JyEvaluateTargetInitConsumer|targetInitDto反序列化为空:msg={}", message.getText());
            return;
        }
        // 报表最终结果
        EvaluateTargetResultDto targetResultDto;
        // 如果是首次评价
        if (targetInitDto.isFirstEvaluate()) {
            // 设置评价基础信息
            targetResultDto = createTargetInfo(targetInitDto);
        } else {
            targetResultDto = new EvaluateTargetResultDto();
        }
        targetResultDto.setTargetBizId(targetInitDto.getTargetBizId());
        targetResultDto.setSourceBizId(targetInitDto.getSourceBizId());
        targetResultDto.setEvaluateType(EVALUATE_TYPE_LOAD);
        targetResultDto.setOperateUserErp(targetInitDto.getOperateUserErp());
        targetResultDto.setOperateUserName(targetInitDto.getOperateUserName());
        targetResultDto.setOperateTime(targetInitDto.getOperateTime());
        targetResultDto.setStatus(EVALUATE_STATUS_SATISFIED);
        targetResultDto.setFirstEvaluate(targetInitDto.isFirstEvaluate());
        // 设置本次评价明细
        targetResultDto.setDimensionList(targetInitDto.getDimensionList());
        // 设置当前汇总数据
        setSummaryData(targetInitDto, targetResultDto);
        // 发送报表消息
        String businessId = targetResultDto.getSourceBizId() + Constants.UNDER_LINE + EVALUATE_RESULT_BUSINESS_KEY
                + Constants.UNDER_LINE + targetResultDto.getOperateTime();
        LOGGER.info("JyEvaluateTargetInitConsumer|开始发送:init={},result={}", message.getText(), JsonHelper.toJson(targetResultDto));
        evaluateTargetResultProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(targetResultDto));
    }

    private EvaluateTargetResultDto createTargetInfo(EvaluateTargetInitDto targetInitDto) {
        
        // 查询发货调度任务
        JyScheduleTaskResp targetScheduleTask = null;
        if (StringUtils.isNotBlank(targetInitDto.getTargetBizId())) {
            targetScheduleTask = jyEvaluateCommonService.getJyScheduleTask(targetInitDto.getTargetBizId(), JyScheduleTaskTypeEnum.SEND.getCode());
        }
        String targetTaskId = Constants.EMPTY_FILL;
        // 发货任务协助人
        List<JyTaskGroupMemberEntity> taskGroupMembers = null;
        if (targetScheduleTask != null) {
            targetTaskId = targetScheduleTask.getTaskId();
            taskGroupMembers = jyEvaluateCommonService.queryMemberListByTaskId(targetTaskId);
        }
        // 查询卸车调度任务
        JyScheduleTaskResp sourceScheduleTask = jyEvaluateCommonService.getJyScheduleTask(targetInitDto.getSourceBizId(), JyScheduleTaskTypeEnum.UNLOAD.getCode());
        String sourceTaskId = Constants.EMPTY_FILL;
        if (sourceScheduleTask != null) {
            sourceTaskId = sourceScheduleTask.getTaskId();
        }

        if (targetInitDto.getUnsealTime() == null || targetInitDto.getSealTime() == null) {
            SealCarDto sealCarDto = jyEvaluateCommonService.findSealCarInfoBySealCarCodeOfTms(targetInitDto.getSourceBizId());
            if (sealCarDto != null) {
                targetInitDto.setUnsealTime(sealCarDto.getDesealCarTime());
                targetInitDto.setSealTime(sealCarDto.getSealCarTime());
                if (StringUtils.isBlank(targetInitDto.getTargetSiteName())) {
                    targetInitDto.setTargetSiteCode(sealCarDto.getStartSiteId());
                    targetInitDto.setTargetSiteName(sealCarDto.getStartSiteName());
                }
            }
        }

        // 根据发货任务操作场地查询区域信息
        BaseStaffSiteOrgDto targetSiteOrgDto = jyEvaluateCommonService.getSiteInfo(targetInitDto.getTargetSiteCode());
        // 根据卸车任务操作场地查询区域信息
        BaseStaffSiteOrgDto sourceSiteOrgDto = jyEvaluateCommonService.getSiteInfo(targetInitDto.getSourceSiteCode());

        EvaluateTargetResultDto targetResultDto = createEvaluateTargetInfo(targetInitDto, taskGroupMembers, targetSiteOrgDto, sourceSiteOrgDto);
        targetResultDto.setTargetTaskId(targetTaskId);
        targetResultDto.setSourceTaskId(sourceTaskId);
        if (targetScheduleTask != null) {
            targetResultDto.setTargetStartTime(targetScheduleTask.getTaskStartTime());
            targetResultDto.setTargetFinishTime(targetScheduleTask.getTaskEndTime());
        }
        return targetResultDto;
    }

    private EvaluateTargetResultDto createEvaluateTargetInfo(EvaluateTargetInitDto targetInitDto,
                                                                List<JyTaskGroupMemberEntity> taskGroupMembers,
                                                                BaseStaffSiteOrgDto targetSiteOrgDto,
                                                                BaseStaffSiteOrgDto sourceSiteOrgDto) {
        EvaluateTargetResultDto targetInfo = new EvaluateTargetResultDto();

        if (targetSiteOrgDto != null) {
            targetInfo.setTargetAreaCode(targetSiteOrgDto.getOrgId());
            targetInfo.setTargetAreaName(targetSiteOrgDto.getOrgName());
        }
        targetInfo.setTargetSiteCode(targetInitDto.getTargetSiteCode());
        targetInfo.setTargetSiteName(targetInitDto.getTargetSiteName());

        targetInfo.setTransWorkItemCode(targetInitDto.getTransWorkItemCode());
        targetInfo.setVehicleNumber(targetInitDto.getVehicleNumber());
        targetInfo.setSealTime(targetInitDto.getSealTime());
        if (CollectionUtils.isNotEmpty(taskGroupMembers)) {
            targetInfo.setHelperErp(getUserCodesStr(taskGroupMembers));
        }
        if (sourceSiteOrgDto != null) {
            targetInfo.setSourceAreaCode(sourceSiteOrgDto.getOrgId());
            targetInfo.setSourceAreaName(sourceSiteOrgDto.getOrgName());
        }
        targetInfo.setSourceSiteCode(targetInitDto.getSourceSiteCode());
        targetInfo.setSourceSiteName(targetInitDto.getSourceSiteName());
        targetInfo.setUnsealTime(targetInitDto.getUnsealTime());

        return targetInfo;
    }

    private String getUserCodesStr(List<JyTaskGroupMemberEntity> taskGroupMembers) {
        Set<String> set = new HashSet<>();
        for (JyTaskGroupMemberEntity taskGroupMember : taskGroupMembers) {
            set.add(taskGroupMember.getUserCode());
        }
        return String.join(Constants.SEPARATOR_COMMA, set);
    }

    private void setSummaryData(EvaluateTargetInitDto targetInitDto, EvaluateTargetResultDto targetResultDto) {
        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(targetInitDto.getSourceBizId());
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        // 评价维度编码集合
        List<String> dimensionCodeList = new ArrayList<>();
        // 评价人erp集合
        List<String> erpList = new ArrayList<>();
        // 备注汇总
        StringBuilder remarkStr = new StringBuilder();
        // 图片汇总
        int imgCount = 0;
        for (JyEvaluateRecordEntity evaluateRecord : recordList) {
            // 评价维度编码
            String dimensionCode = String.valueOf(evaluateRecord.getDimensionCode());
            // 图片url集合
            String imgUrls = evaluateRecord.getImgUrl();
            // 备注
            String remark = evaluateRecord.getRemark();
            // 评价人erp
            String evaluateUserErp = evaluateRecord.getCreateUserErp();
            // 评价状态
            Integer status = evaluateRecord.getEvaluateStatus();
            if (!erpList.contains(evaluateUserErp)) {
                erpList.add(evaluateUserErp);
            }
            // 只有不满意的记录才需要统计以下三个指标
            if (EVALUATE_STATUS_DISSATISFIED.equals(status)) {
                if (!dimensionCodeList.contains(dimensionCode)) {
                    dimensionCodeList.add(dimensionCode);
                }
                if (StringUtils.isNotBlank(imgUrls)) {
                    imgCount = imgCount + imgUrls.split(Constants.SEPARATOR_COMMA).length;
                }
                if (StringUtils.isNotBlank(remark)) {
                    remarkStr.append(Constants.LINE_NEXT_CHAR).append(remark);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(dimensionCodeList)) {
            targetResultDto.setStatus(EVALUATE_STATUS_DISSATISFIED);
            targetResultDto.setDimensionCode(String.join(Constants.SEPARATOR_COMMA, dimensionCodeList));
        }
        targetResultDto.setEvaluateUserErp(String.join(Constants.SEPARATOR_COMMA, erpList));
        targetResultDto.setImgCount(imgCount);
        if (StringUtils.isNotBlank(remarkStr.toString())) {
            targetResultDto.setRemark(remarkStr.substring(2));
        }
    }

}


