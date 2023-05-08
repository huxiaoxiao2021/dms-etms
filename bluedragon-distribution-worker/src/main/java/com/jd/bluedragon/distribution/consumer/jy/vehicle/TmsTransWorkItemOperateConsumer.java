package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleData;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.SendVehicleDetailTaskDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransWorkItemDto;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.enums.SendStatusEnum;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.PsStoreInfo;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkDto;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TmsTransWorkItemOperateConsumer
 * @Description tms_trans_work_item_operate <href>https://cf.jd.com/pages/viewpage.action?pageId=868186705</href>
 * @Author wyh
 * @Date 2022/5/25 20:03
 **/
@Service("tmsTransWorkItemOperateConsumer")
public class TmsTransWorkItemOperateConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TmsTransWorkItemOperateConsumer.class);

    private static final int TRANS_WORK_CACHE_EXPIRE = 15;

    /**
     * 派车单明细操作类型
     */
    private static final int OPERATE_TYPE_CREATED = 10; // 创建
    private static final int OPERATE_TYPE_CANCEL = 20; // 作废

    /**
     * 派车单状态
     */
    private static final int WORK_STATUS_INIT = 10; // 初始化
    private static final int WORK_STATUS_START = 20; // 已开始
    private static final int WORK_STATUS_END = 30; // 已结束
    private static final Integer WORK_ITEM_STATUS_CANCEL = 200; // 作废

    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;

    @Autowired
    private JyBizTaskSendVehicleService taskSendVehicleService;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

    @Autowired
    private SendVehicleTransactionManager transactionManager;

    @Autowired
    private JdiTransWorkWSManager transWorkWSManager;

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;

    @Autowired
    private JyVehicleSendRelationService jyVehicleSendRelationService;

    @Autowired
    @Qualifier(value = "sendVehicleDetailTaskProducer")
    private DefaultJMQProducer sendVehicleDetailTaskProducer;

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsTransWorkItemOperateConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsTransWorkItemOperateConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        logInfo("消费运输派车明细消息. {}", message.getText());

        TransWorkItemDto workItemDto = JsonHelper.fromJson(message.getText(), TransWorkItemDto.class);
        // 过滤丢弃的数据
        if (filterDiscardData(workItemDto)) {
            return;
        }
        String transWorkCode = workItemDto.getTransWorkCode();
        //锁定派车单执行 防止并发问题
        String mutexKey = getTransWorkMutexKey(transWorkCode);
        if (!redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), TRANS_WORK_CACHE_EXPIRE, TimeUnit.MINUTES, false)) {
            String warnMsg = String.format("派车单%s-%s正在处理中!", workItemDto.getTransWorkItemCode(), transWorkCode);
            logger.warn(warnMsg, JsonHelper.toJson(workItemDto));
            throw new JyBizException(warnMsg);
        }
        TransWorkBillDto transWorkBillDto = getTransWorkBillDto(transWorkCode);
        if (transWorkBillDto == null) {
            logger.warn("根据派车任务查询派车单为空. {}", JsonHelper.toJson(workItemDto));
            return;
        }
        JyLineTypeEnum lineType = TmsLineTypeEnum.getLineType(transWorkBillDto.getTransType());
        if (!JyLineTypeEnum.TRUNK_LINE.equals(lineType) && !JyLineTypeEnum.BRANCH_LINE.equals(lineType)) {
            logger.warn("派车单类型非干、支类型. {},tmsTransWorkBill:{}", JsonHelper.toJson(workItemDto),JsonHelper.toJson(transWorkBillDto));
            //调整仅记录日志不阻碍运行防止后增加派车明细将派车单线路类型回刷
            //return;
        }

        BaseStaffSiteOrgDto startSiteInfo = baseMajorManager.getBaseSiteByDmsCode(workItemDto.getBeginNodeCode());
        if (startSiteInfo == null || !NumberHelper.gt0(startSiteInfo.getSiteCode())) {
            logger.warn("派车单明细始发场地不存在. {}", JsonHelper.toJson(workItemDto));
            return;
        }

        BaseStaffSiteOrgDto endSiteInfo = baseMajorManager.getBaseSiteByDmsCode(workItemDto.getEndNodeCode());
        if (endSiteInfo == null || !NumberHelper.gt0(endSiteInfo.getSiteCode())) {
            //兼容目的目的地是库房类型
            PsStoreInfo psStoreInfo = baseMajorManager.getStoreByCode(workItemDto.getEndNodeCode());
            if(psStoreInfo != null ){
                endSiteInfo  = new BaseStaffSiteOrgDto();
                endSiteInfo.setSiteCode(psStoreInfo.getDmsSiteId());
                endSiteInfo.setSiteName(psStoreInfo.getDmsStoreName());
            }else{
                logger.warn("派车单明细目的场地不存在. {}", JsonHelper.toJson(workItemDto));
                return;
            }
        }

        JyBizTaskSendVehicleEntity sendTaskQ = new JyBizTaskSendVehicleEntity(transWorkBillDto.getTransWorkCode(), startSiteInfo.getSiteCode().longValue());
        JyBizTaskSendVehicleEntity existSendTaskMain = taskSendVehicleService.findByTransWorkAndStartSite(sendTaskQ);

        String sendVehicleBiz = getSendVehicleBiz(existSendTaskMain);
        try {
            // 初始化发货任务
            JyBizTaskSendVehicleEntity sendVehicleEntity = null;
            if (existSendTaskMain == null) {
                sendVehicleEntity = createSendTaskDomain(transWorkBillDto, startSiteInfo.getSiteCode(), sendVehicleBiz);
            }

            // 发货任务追加流向
            if (OPERATE_TYPE_CREATED == workItemDto.getOperateType()) {
                JyBizTaskSendVehicleDetailEntity taskSendVehicleDetailEntity = addSendTaskDetail(workItemDto, startSiteInfo, endSiteInfo, sendVehicleBiz);

                if (taskSendVehicleDetailEntity != null) {
                    JyLineTypeEnum detailLineType = TmsLineTypeEnum.getLineType(workItemDto.getTransType());
                    taskSendVehicleDetailEntity.setLineType(detailLineType.getCode());
                    taskSendVehicleDetailEntity.setLineTypeName(detailLineType.getName());
                    checkIfNeedSaveTaskSimpleCode(taskSendVehicleDetailEntity);
                    transactionManager.saveTaskSendAndDetail(sendVehicleEntity, taskSendVehicleDetailEntity);
                    // 发送任务明细mq
                    sendVehicleDetailTaskMQ(taskSendVehicleDetailEntity, workItemDto.getOperateType());
                }
            }
            // 取消发货任务流向
            else if (OPERATE_TYPE_CANCEL == workItemDto.getOperateType()) {
                JyBizTaskSendVehicleDetailEntity vehicleDetail =taskSendVehicleDetailService.findByBizId(workItemDto.getTransWorkItemCode());
                if (ObjectHelper.isNotNull(vehicleDetail)){
                    if (JyBizTaskSendStatusEnum.TO_SEND.getCode().equals(vehicleDetail.getVehicleStatus())){
                        cancelSendTaskDetail(workItemDto,vehicleDetail);
                        // 发送任务明细mq
                        sendVehicleDetailTaskMQ(vehicleDetail, workItemDto.getOperateType());
                    }
                    else {
                        labelSendTaskDetailCancel(vehicleDetail);
                    }
                }
                else {
                    logger.error("取消JyBizTaskSendVehicleDetail异常，不存在该流向任务或者已经被取消",JsonHelper.toJson(workItemDto));
                }
            }

            // 更新lastPlanDepartTime最晚发车时间
            // 更新线路类型，多个派车明细创建后可能会引起派车任务对应主发货任务的线路类型调整
            updateSendVehicleLastPlanDepartTimeAndLineType(startSiteInfo.getSiteCode(), sendVehicleBiz,lineType);

            //刷新状态
            if(existSendTaskMain != null){
                reloadTaskStatus(sendVehicleBiz);
            }
        }
        catch (Exception e) {
            logger.error("消费运输派车单明细失败! {}", JsonHelper.toJson(workItemDto), e);
            throw new JyBizException("消费运输派车单明细失败! ");
        }
        finally {
            redisClientOfJy.del(mutexKey);
        }
    }

    private void checkIfNeedSaveTaskSimpleCode(JyBizTaskSendVehicleDetailEntity detailEntity) {
        try {
            if (ObjectHelper.isNotNull(detailEntity)){
                String key = String.format(Constants.JY_TMS_SIMPLE_TASK_CODE_PREFIX, detailEntity.getTransWorkItemCode());
                String taskSimpleCode =redisClientOfJy.get(key);
                if (ObjectHelper.isNotNull(taskSimpleCode)){
                    detailEntity.setTaskSimpleCode(taskSimpleCode);
                }
            }
        } catch (Exception e) {
            logger.error("获取任务简码缓存信息异常",e);
        }
    }

    private void sendVehicleDetailTaskMQ(JyBizTaskSendVehicleDetailEntity entity, Integer operateType) {
        SendVehicleDetailTaskDto detailTaskDto = new SendVehicleDetailTaskDto();
        detailTaskDto.setBizId(entity.getBizId());
        detailTaskDto.setStartSiteId(entity.getStartSiteId());
        detailTaskDto.setStartSiteName(entity.getStartSiteName());
        detailTaskDto.setEndSiteId(entity.getEndSiteId());
        detailTaskDto.setEndSiteName(entity.getEndSiteName());
        List<String> sendCodes = jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(entity.getBizId());
        detailTaskDto.setSendCodes(sendCodes);
        detailTaskDto.setOperateType(operateType);
        detailTaskDto.setOperateTime(new Date());
        String businessId = entity.getBizId() + Constants.UNDERLINE_FILL + operateType;
        sendVehicleDetailTaskProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(detailTaskDto));
    }


    /**
     * 刷新任务状态
     */
    private void reloadTaskStatus(String sendVehicleBiz) {
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(sendVehicleBiz);
        taskSend.setUpdateUserErp(Constants.SYS_NAME);
        taskSend.setUpdateUserName(Constants.SYS_NAME);
        if(!sendVehicleTransactionManager.reloadStatusWithoutCompare(taskSend)){
            logger.warn("sendVehicleBiz:{} reloadTaskStatus fail!",sendVehicleBiz);
        }

    }

    private void labelSendTaskDetailCancel(JyBizTaskSendVehicleDetailEntity vehicleDetail) {
        JyBizTaskSendVehicleDetailEntity entity =new JyBizTaskSendVehicleDetailEntity();
        entity.setBizId(vehicleDetail.getBizId());
        entity.setExcepLabel(SendTaskExcepLabelEnum.CANCEL.getCode());
        entity.setUpdateTime(new Date());
        taskSendVehicleDetailService.updateByBiz(entity);
    }

    /**
     * 根据派车单号获取派车单信息
     * 派车单线路类型特殊处理， 已派车明细中所有的线路类型 干支传摆 从大到小处理
     * @param transWorkCode
     */
    private TransWorkBillDto getTransWorkBillDto(String transWorkCode){
        BigTransWorkDto bigTransWorkDto = jdiQueryWSManager.queryTransWorkAndAllItem( transWorkCode);
        if(bigTransWorkDto != null && bigTransWorkDto.getTransWorkBillDto() != null ){
            TransWorkBillDto transWorkBillDto = bigTransWorkDto.getTransWorkBillDto();
            if(!CollectionUtils.isEmpty(bigTransWorkDto.getTransWorkItemDtoList())){
                Integer lineTypeCode = transWorkBillDto.getTransType();
                JyLineTypeEnum lineType = TmsLineTypeEnum.getLineType(transWorkBillDto.getTransType());
                logInfo("TmsTransWorkItemOperateConsumer getTransWorkBillDto  transWorkCode:{} lineType:{} jy:{} ",transWorkCode,transWorkBillDto.getTransType(),lineType.getName());
                //派车明细中的线路类型 干支传摆 从大到小处理 （ 以 JyLineTypeEnum order 排名顺序）
                for(com.jd.tms.jdi.dto.TransWorkItemDto item : bigTransWorkDto.getTransWorkItemDtoList()){
                    JyLineTypeEnum itemLineType = TmsLineTypeEnum.getLineType(item.getTransType());
                    if(itemLineType.getOrder() < lineType.getOrder()){
                        lineType = itemLineType;
                        lineTypeCode = item.getTransType();
                        logInfo("TmsTransWorkItemOperateConsumer getTransWorkBillDto  transWorkCode:{} itemLineType:{} jy:{} ",transWorkCode,item.getTransType(),itemLineType.getName());
                    }
                }
                //替换线路类型
                transWorkBillDto.setTransType(lineTypeCode);
            }
            logInfo("TmsTransWorkItemOperateConsumer getTransWorkBillDto end transWorkCode:{} lineType:{} ",transWorkCode,transWorkBillDto.getTransType());
            return transWorkBillDto;
        }
        return null;
    }

    private String getSendVehicleBiz(JyBizTaskSendVehicleEntity existSendTaskMain) {
        String sendVehicleBiz;
        if (existSendTaskMain != null) {
            // 发货任务增加流向
            sendVehicleBiz = existSendTaskMain.getBizId();
        }
        else {
            sendVehicleBiz = genMainTaskBizId();

        }
        return sendVehicleBiz;
    }

    /**
     * 取消发货流向
     * @param workItemDto
     */
    private void cancelSendTaskDetail(TransWorkItemDto workItemDto,JyBizTaskSendVehicleDetailEntity detailEntity) {
        JyBizTaskSendVehicleDetailEntity cancelQ = new JyBizTaskSendVehicleDetailEntity();
        cancelQ.setBizId(workItemDto.getTransWorkItemCode());
        cancelQ.setUpdateTime(new Date());

        int rows = taskSendVehicleDetailService.cancelDetail(cancelQ);
        if (rows <= 0) {
            logger.warn("取消派车单明细失败! {}", JsonHelper.toJson(workItemDto));
        }
        else {
            Integer noCancelCount =taskSendVehicleDetailService.countNoCancelSendDetail(detailEntity);
            if (noCancelCount==null || noCancelCount<=0){
                JyBizTaskSendVehicleEntity entity =new JyBizTaskSendVehicleEntity();
                entity.setBizId(detailEntity.getSendVehicleBizId());
                entity.setVehicleStatus(JyBizTaskSendStatusEnum.CANCEL.getCode());
                entity.setYn(Constants.YN_NO);
                entity.setUpdateTime(new Date());
                taskSendVehicleService.updateSendVehicleTask(entity);
            }
        }
        logInfo("取消派车单明细.{}", JsonHelper.toJson(workItemDto));
    }

    /**
     * 发货任务追加流向
     * @param workItemDto
     * @param startSiteInfo
     * @param endSiteInfo
     * @param sendVehicleBiz
     * @return
     */
    private JyBizTaskSendVehicleDetailEntity addSendTaskDetail(TransWorkItemDto workItemDto, BaseStaffSiteOrgDto startSiteInfo, BaseStaffSiteOrgDto endSiteInfo, String sendVehicleBiz) {

        // 判断是作废不保存
        if (judgeTransWorkItemCancel(workItemDto)) {
            logger.warn("派车明细创建时已作废. {}", JsonHelper.toJson(workItemDto));
            return null;
        }

        return createSendDetailDomain(workItemDto, startSiteInfo, endSiteInfo, sendVehicleBiz);
    }

    /**
     * 校验派车明细是否作废
     * @return
     */
    private boolean judgeTransWorkItemCancel(TransWorkItemDto workItemDto) {
        BigQueryOption queryOption = new BigQueryOption();
        queryOption.setQueryTransWorkItemDto(Boolean.TRUE);
        BigTransWorkItemDto bigTransWorkItemDto = transWorkWSManager.queryTransWorkItemByOptionWithRead(workItemDto.getTransWorkItemCode(), queryOption);
        if (bigTransWorkItemDto != null) {
            com.jd.tms.jdi.dto.TransWorkItemDto transWorkItemDto = bigTransWorkItemDto.getTransWorkItemDto();
            if (transWorkItemDto != null && Objects.equals(WORK_ITEM_STATUS_CANCEL, transWorkItemDto.getStatus())) {
                return true;
            }
        }

        return false;
    }

    private JyBizTaskSendVehicleDetailEntity createSendDetailDomain(TransWorkItemDto workItemDto, BaseStaffSiteOrgDto startSiteInfo, BaseStaffSiteOrgDto endSiteInfo, String sendVehicleBiz) {
        JyBizTaskSendVehicleDetailEntity taskSendVehicleDetailEntity = new JyBizTaskSendVehicleDetailEntity();
        taskSendVehicleDetailEntity.setSendVehicleBizId(sendVehicleBiz);
        taskSendVehicleDetailEntity.setBizId(workItemDto.getTransWorkItemCode());
        taskSendVehicleDetailEntity.setTransWorkItemCode(workItemDto.getTransWorkItemCode());
        taskSendVehicleDetailEntity.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode());
        taskSendVehicleDetailEntity.setStartSiteId(startSiteInfo.getSiteCode().longValue());
        taskSendVehicleDetailEntity.setStartSiteName(startSiteInfo.getSiteName());
        taskSendVehicleDetailEntity.setEndSiteId(endSiteInfo.getSiteCode().longValue());
        taskSendVehicleDetailEntity.setEndSiteName(endSiteInfo.getSiteName());
        taskSendVehicleDetailEntity.setPlanDepartTime(workItemDto.getPlanDepartTime());
        taskSendVehicleDetailEntity.setCreateUserErp(Constants.SYS_NAME);
        taskSendVehicleDetailEntity.setCreateUserName(Constants.SYS_NAME);
        return taskSendVehicleDetailEntity;
    }

    private JyBizTaskSendVehicleEntity createSendTaskDomain(TransWorkBillDto transWorkBillDto, Integer startSiteId, String sendVehicleBiz) {
        JyBizTaskSendVehicleEntity sendVehicleEntity = new JyBizTaskSendVehicleEntity();
        sendVehicleEntity.setBizId(sendVehicleBiz);
        sendVehicleEntity.setTransWorkCode(transWorkBillDto.getTransWorkCode());
        sendVehicleEntity.setStartSiteId(startSiteId.longValue());
        sendVehicleEntity.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
        sendVehicleEntity.setVehicleNumber(StringUtils.EMPTY);
        sendVehicleEntity.setTransWay(transWorkBillDto.getTransWay());
        sendVehicleEntity.setTransWayName(transWorkBillDto.getTransWayName());

        if (transWorkBillDto.getVehicleType() != null) {
            sendVehicleEntity.setVehicleType(transWorkBillDto.getVehicleType());
            BasicVehicleTypeDto basicVehicleTypeDto = basicQueryWSManager.getVehicleTypeByVehicleType(sendVehicleEntity.getVehicleType());
            if (basicVehicleTypeDto != null) {
                sendVehicleEntity.setVehicleTypeName(basicVehicleTypeDto.getVehicleTypeName());
            }
        }
        JyLineTypeEnum lineType = TmsLineTypeEnum.getLineType(transWorkBillDto.getTransType());
        sendVehicleEntity.setLineType(lineType.getCode());
        sendVehicleEntity.setLineTypeName(lineType.getName());
        sendVehicleEntity.setCreateUserErp(Constants.SYS_NAME);
        sendVehicleEntity.setCreateUserName(Constants.SYS_NAME);
        return sendVehicleEntity;
    }

    /**
     * 更新发货任务最晚发车时间
     * @param startSiteId
     * @param sendVehicleBiz
     */
    private void updateSendVehicleLastPlanDepartTimeAndLineType(Integer startSiteId, String sendVehicleBiz,JyLineTypeEnum lineType) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId.longValue(), sendVehicleBiz);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            return;
        }
        Date lastPlanDepartTime = vehicleDetailList.get(0).getPlanDepartTime();
        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
            if (lastPlanDepartTime != null) {
                if (detailEntity.getPlanDepartTime() != null) {
                    if (lastPlanDepartTime.before(detailEntity.getPlanDepartTime())) {
                        lastPlanDepartTime = detailEntity.getPlanDepartTime();
                    }
                }
            }
            else {
                if (detailEntity.getPlanDepartTime() != null) {
                    lastPlanDepartTime = detailEntity.getPlanDepartTime();
                }
            }
        }

        JyBizTaskSendVehicleEntity updateSendTaskReq = new JyBizTaskSendVehicleEntity();
        updateSendTaskReq.setBizId(sendVehicleBiz);
        Boolean needUpdate = Boolean.FALSE;
        if (lastPlanDepartTime != null) {
            updateSendTaskReq.setLastPlanDepartTime(lastPlanDepartTime);
            needUpdate = Boolean.TRUE;
        }
        if(lineType != null){
            updateSendTaskReq.setLineType(lineType.getCode());
            updateSendTaskReq.setLineTypeName(lineType.getName());
            needUpdate = Boolean.TRUE;
        }
        if(needUpdate){
            updateSendTaskReq.setUpdateTime(new Date());
            int rows = taskSendVehicleService.updateLastPlanDepartTimeAndLineType(updateSendTaskReq);
            logInfo("更新派车单最晚发车时间和线路类型.{}-{} , req:{}", updateSendTaskReq.getBizId(), rows,JsonHelper.toJson(updateSendTaskReq));
        }
    }

    private void logInfo(String message, Object ...objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }

    private String getTransWorkMutexKey(String transWorkCode) {
        return String.format(CacheKeyConstants.JY_SEND_TRANS_WORK_KEY, transWorkCode);
    }

    /**
     * 生成send_vehicle业务主键
     * @return
     */
    private String genMainTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    /**
     * 需要丢弃的数据
     * @param mqDto
     * @return true：丢弃
     */
    private boolean filterDiscardData(TransWorkItemDto mqDto) {
        if (mqDto == null) {
            logger.warn("TmsTransWorkItemOperateConsumer consume -->JSON转换后为空");
            return true;
        }

        if (StringUtils.isBlank(mqDto.getTransWorkCode())
                || StringUtils.isEmpty(mqDto.getTransWorkItemCode())
                || StringUtils.isBlank(mqDto.getBeginNodeCode())
                || StringUtils.isBlank(mqDto.getEndNodeCode())
                || null == mqDto.getOperateType()) {
            logger.warn("TmsTransWorkItemOperateConsumer consume -->关键数据为空，内容为【{}】", JsonHelper.toJson(mqDto));
            return true;
        }

        String startSiteCode = mqDto.getBeginNodeCode();
        // 检查始发地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteByDmsCode(startSiteCode);
        if (siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())) {
            //丢弃数据
            logger.warn("TmsTransWorkItemOperateConsumer不需要关心的数据丢弃, 始发站点:{}, 始发站点类型:{}, 消息:{}",
                    startSiteCode, siteInfo == null ? null : siteInfo.getSiteType(), JsonHelper.toJson(mqDto));
            return true;
        }

        return false;
    }

}
