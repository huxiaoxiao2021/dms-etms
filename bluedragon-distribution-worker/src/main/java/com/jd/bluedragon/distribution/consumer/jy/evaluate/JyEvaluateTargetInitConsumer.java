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
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        // 设置本次评价明细
        targetResultDto.setDimensionList(targetInitDto.getDimensionList());
        // 设置当前汇总数据
        setSummaryData(targetInitDto, targetResultDto);
        // 发送报表消息
        String businessId = targetResultDto.getSourceBizId() + Constants.UNDER_LINE + EVALUATE_RESULT_BUSINESS_KEY
                + Constants.UNDER_LINE + targetResultDto.getOperateTime();
        evaluateTargetResultProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(targetResultDto));
    }

    private EvaluateTargetResultDto createTargetInfo(EvaluateTargetInitDto targetInitDto) {
        // 查询发货调度任务ID
        String targetTaskId = jyEvaluateCommonService.getJyScheduleTaskId(targetInitDto.getTargetBizId(), JyScheduleTaskTypeEnum.SEND.getCode());
        // 查询卸车调度任务ID
        String sourceTaskId = jyEvaluateCommonService.getJyScheduleTaskId(targetInitDto.getSourceBizId(), JyScheduleTaskTypeEnum.UNLOAD.getCode());
        // 查询发货任务协助人
        List<JyTaskGroupMemberEntity> taskGroupMembers = jyEvaluateCommonService.queryMemberListByTaskId(targetTaskId);
        // 根据发货任务操作场地查询区域信息
        BaseStaffSiteOrgDto targetSiteOrgDto = jyEvaluateCommonService.getSiteInfo(targetInitDto.getTargetSiteCode());
        // 根据卸车任务操作场地查询区域信息
        BaseStaffSiteOrgDto sourceSiteOrgDto = jyEvaluateCommonService.getSiteInfo(targetInitDto.getSourceSiteCode());

        return createEvaluateTargetInfo(targetInitDto, sourceTaskId, targetTaskId, taskGroupMembers, targetSiteOrgDto, sourceSiteOrgDto);
    }

    private EvaluateTargetResultDto createEvaluateTargetInfo(EvaluateTargetInitDto targetInitDto,
                                                                String sourceTaskId, String targetTaskId,
                                                                List<JyTaskGroupMemberEntity> taskGroupMembers,
                                                                BaseStaffSiteOrgDto targetSiteOrgDto,
                                                                BaseStaffSiteOrgDto sourceSiteOrgDto) {
        EvaluateTargetResultDto targetInfo = new EvaluateTargetResultDto();

        targetInfo.setTargetAreaCode(targetSiteOrgDto.getOrgId());
        targetInfo.setTargetAreaName(targetSiteOrgDto.getOrgName());
        targetInfo.setTargetSiteCode(targetInitDto.getTargetSiteCode());
        targetInfo.setTargetSiteName(targetInitDto.getTargetSiteName());
        targetInfo.setTargetTaskId(targetTaskId);
        targetInfo.setTargetStartTime(targetInitDto.getTargetStartTime());
        targetInfo.setTargetFinishTime(targetInitDto.getTargetFinishTime());
        targetInfo.setTransWorkItemCode(targetInitDto.getTransWorkItemCode());
        targetInfo.setVehicleNumber(targetInitDto.getVehicleNumber());
        targetInfo.setSealTime(targetInitDto.getSealTime());
        targetInfo.setHelperErp(getUserCodesStr(taskGroupMembers));

        targetInfo.setSourceAreaCode(sourceSiteOrgDto.getOrgId());
        targetInfo.setSourceAreaName(sourceSiteOrgDto.getOrgName());
        targetInfo.setSourceSiteCode(targetInitDto.getSourceSiteCode());
        targetInfo.setSourceSiteName(targetInitDto.getSourceSiteName());
        targetInfo.setSourceTaskId(sourceTaskId);
        targetInfo.setUnsealTime(targetInitDto.getUnsealTime());

        return targetInfo;
    }

    private String getUserCodesStr(List<JyTaskGroupMemberEntity> taskGroupMembers) {
        String userCodesStr = "";
        for (JyTaskGroupMemberEntity taskGroupMember : taskGroupMembers) {
            userCodesStr = Constants.SEPARATOR_COMMA + taskGroupMember.getUserCode();
        }
        return userCodesStr.substring(1);
    }

    private void setSummaryData(EvaluateTargetInitDto targetInitDto, EvaluateTargetResultDto targetResultDto) {
        List<JyEvaluateRecordEntity> recordList = jyEvaluateRecordDao.findRecordsBySourceBizId(targetInitDto.getSourceBizId());
        if (CollectionUtils.isEmpty(recordList)) {
            return;
        }
        List<String> dimensionCodeList = new ArrayList<>();
        List<String> erpList = new ArrayList<>();
        String remarkStr = "";
        int imgCount = 0;
        for (JyEvaluateRecordEntity evaluateRecord : recordList) {
            String dimensionCode = String.valueOf(evaluateRecord.getDimensionCode());
            String imgUrls = evaluateRecord.getImgUrl();
            String remark = evaluateRecord.getRemark();
            String evaluateUserErp = evaluateRecord.getCreateUserErp();
            if (!dimensionCodeList.contains(dimensionCode)) {
                dimensionCodeList.add(dimensionCode);
            }
            if (StringUtils.isNotBlank(imgUrls)) {
                imgCount = imgCount + imgUrls.split(Constants.SEPARATOR_COMMA).length;
            }
            if (!erpList.contains(evaluateUserErp)) {
                erpList.add(evaluateUserErp);
            }
            if (StringUtils.isNotBlank(remark)) {
                remarkStr = remarkStr + Constants.LINE_NEXT_CHAR + remark;
            }
        }
        targetResultDto.setStatus(EVALUATE_STATUS_DISSATISFIED);
        targetResultDto.setDimensionCode(String.join(Constants.SEPARATOR_COMMA, dimensionCodeList));
        targetResultDto.setEvaluateUserErp(String.join(Constants.SEPARATOR_COMMA, erpList));
        targetResultDto.setImgCount(imgCount);
        if (StringUtils.isNotBlank(remarkStr)) {
            targetResultDto.setRemark(remarkStr);
        }
    }

}


