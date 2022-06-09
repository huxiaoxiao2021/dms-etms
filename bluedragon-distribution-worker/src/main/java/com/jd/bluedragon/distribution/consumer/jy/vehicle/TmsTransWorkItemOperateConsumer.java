package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.TransWorkItemDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.TmsLineTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
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
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private JyBizTaskSendVehicleService taskSendVehicleService;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

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

        TransWorkItemDto workItemDto = JsonHelper.fromJson(message.getText(), TransWorkItemDto.class);
        // 过滤丢弃的数据
        if (filterDiscardData(workItemDto)) {
            return;
        }

        String transWorkCode = workItemDto.getTransWorkCode();
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(transWorkCode);
        if (transWorkBillDto == null) {
            logger.warn("根据派车任务查询派车单为空. {}", JsonHelper.toJson(workItemDto));
            return;
        }
        BaseStaffSiteOrgDto startSiteInfo = baseMajorManager.getBaseSiteByDmsCode(workItemDto.getBeginNodeCode());
        if (startSiteInfo == null || !NumberHelper.gt0(startSiteInfo.getSiteCode())) {
            logger.warn("派车单明细始发场地不存在. {}", JsonHelper.toJson(workItemDto));
            return;
        }

        BaseStaffSiteOrgDto endSiteInfo = baseMajorManager.getBaseSiteByDmsCode(workItemDto.getEndNodeCode());
        if (endSiteInfo == null || !NumberHelper.gt0(endSiteInfo.getSiteCode())) {
            logger.warn("派车单明细目的场地不存在. {}", JsonHelper.toJson(workItemDto));
            return;
        }

        String mutexKey = getTransWorkMutexKey(transWorkCode);
        if (!redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), TRANS_WORK_CACHE_EXPIRE, TimeUnit.MINUTES, false)) {
            String warnMsg = MessageFormat.format("派车单{}-{}正在处理中!", workItemDto.getTransWorkItemCode(), transWorkCode);
            logger.warn(warnMsg, JsonHelper.toJson(workItemDto));
            throw new JyBizException(warnMsg);
        }

        JyBizTaskSendVehicleEntity sendTaskQ = new JyBizTaskSendVehicleEntity(transWorkBillDto.getTransWorkCode(), startSiteInfo.getSiteCode().longValue());
        JyBizTaskSendVehicleEntity existSendTaskMain = taskSendVehicleService.findByTransWorkAndStartSite(sendTaskQ);

        String sendVehicleBiz = getSendVehicleBiz(existSendTaskMain);
        try {
            // 初始化发货任务
            if (existSendTaskMain == null) {

                initSendVehicleTask(workItemDto, transWorkBillDto, startSiteInfo.getSiteCode(), sendVehicleBiz);
            }

            // 发货任务追加流向
            if (OPERATE_TYPE_CREATED == workItemDto.getOperateType()) {

                addSendTaskDetail(workItemDto, startSiteInfo, endSiteInfo, sendVehicleBiz);
            }
            // 取消发货任务流向
            else if (OPERATE_TYPE_CANCEL == workItemDto.getOperateType()) {

                cancelSendTaskDetail(workItemDto);
            }

            // 更新lastPlanDepartTime最晚发车时间
            updateSendVehicleLastPlanDepartTime(startSiteInfo.getSiteCode(), endSiteInfo, sendVehicleBiz);
        }
        catch (Exception e) {
            logger.warn("消费运输派车单明细失败! {}", JsonHelper.toJson(workItemDto));
            throw new JyBizException("消费运输派车单明细失败! ");
        }
        finally {
            redisClientOfJy.del(mutexKey);
        }
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
    private void cancelSendTaskDetail(TransWorkItemDto workItemDto) {
        JyBizTaskSendVehicleDetailEntity cancelQ = new JyBizTaskSendVehicleDetailEntity();
        cancelQ.setBizId(workItemDto.getTransWorkItemCode());
        cancelQ.setUpdateTime(new Date());
        if (taskSendVehicleDetailService.cancelDetail(cancelQ) <= 0) {
            logger.warn("取消派车单明细失败! {}", JsonHelper.toJson(workItemDto));
            throw new JyBizException("取消派车单明细失败!");
        }

        logInfo("取消派车单明细.{}", JsonHelper.toJson(workItemDto));
    }

    /**
     * 发货任务追加流向
     * @param workItemDto
     * @param startSiteInfo
     * @param endSiteInfo
     * @param sendVehicleBiz
     */
    private void addSendTaskDetail(TransWorkItemDto workItemDto, BaseStaffSiteOrgDto startSiteInfo, BaseStaffSiteOrgDto endSiteInfo, String sendVehicleBiz) {
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
        taskSendVehicleDetailEntity.setCreateUserErp("sys.dms");
        taskSendVehicleDetailEntity.setCreateUserName("sys.dms");

        if (taskSendVehicleDetailService.saveTaskSendDetail(taskSendVehicleDetailEntity) <= 0) {
            logger.warn("初始化派车单明细失败! {}", JsonHelper.toJson(taskSendVehicleDetailEntity));
            throw new JyBizException("初始化派车单明细失败!");
        }

        logInfo("初始化派车单明细.{}", JsonHelper.toJson(workItemDto));
    }

    /**
     * 初始化发货任务
     * @param workItemDto
     * @param transWorkBillDto
     * @param startSiteId
     * @param sendVehicleBiz
     */
    private void initSendVehicleTask(TransWorkItemDto workItemDto, TransWorkBillDto transWorkBillDto, Integer startSiteId, String sendVehicleBiz) {
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
        sendVehicleEntity.setCreateUserErp("sys.dms");
        sendVehicleEntity.setCreateUserName("sys.dms");

        if (taskSendVehicleService.initTaskSendVehicle(sendVehicleEntity) <= 0) {
            logger.warn("初始化派车单失败！{}", JsonHelper.toJson(sendVehicleEntity));
            throw new JyBizException("初始化派车单失败！");
        }

        logInfo("初始化派车单.{}", JsonHelper.toJson(workItemDto));

        // 创建发货调度任务
        createSendScheduleTask(sendVehicleEntity);
    }

    /**
     * 创建发货调度任务
     * @param sendVehicleEntity
     * @return
     */
    private JyScheduleTaskResp createSendScheduleTask(JyBizTaskSendVehicleEntity sendVehicleEntity){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(sendVehicleEntity.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        req.setOpeUser(sendVehicleEntity.getCreateUserErp());
        req.setOpeUserName(sendVehicleEntity.getCreateUserName());
        req.setOpeTime(new Date());
        return jyScheduleTaskManager.createScheduleTask(req);
    }

    /**
     * 更新发货任务最晚发车时间
     * @param startSiteId
     * @param endSiteInfo
     * @param sendVehicleBiz
     */
    private void updateSendVehicleLastPlanDepartTime(Integer startSiteId, BaseStaffSiteOrgDto endSiteInfo, String sendVehicleBiz) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId.longValue(), Long.valueOf(endSiteInfo.getSiteCode()), sendVehicleBiz);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            return;
        }
        Date lastPlanDepartTime = vehicleDetailList.get(0).getPlanDepartTime();
        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
            if (lastPlanDepartTime.before(detailEntity.getPlanDepartTime())) {
                lastPlanDepartTime = detailEntity.getPlanDepartTime();
            }
        }

        JyBizTaskSendVehicleEntity updateSendTaskReq = new JyBizTaskSendVehicleEntity();
        updateSendTaskReq.setBizId(sendVehicleBiz);
        updateSendTaskReq.setLastPlanDepartTime(lastPlanDepartTime);
        int rows = taskSendVehicleService.updateLastPlanDepartTime(updateSendTaskReq);

        logInfo("更新派车单最晚发车时间. {}-{}", updateSendTaskReq.getBizId(), rows);
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
                || null == mqDto.getPlanDepartTime()
                || null == mqDto.getOperateType()) {
            logger.warn("TmsTransWorkItemOperateConsumer consume -->关键数据为空，内容为【{}】", JsonHelper.toJson(mqDto));
            return true;
        }

        String startSiteCode = mqDto.getBeginNodeCode();
        // 检查始发地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteByDmsCode(startSiteCode);
        if (siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())) {
            //丢弃数据
            logger.warn("TmsTransWorkItemOperateConsumer不需要关心的数据丢弃, 目的站点:{}, 目的站点类型:{}, 消息:{}",
                    startSiteCode, siteInfo == null ? null : siteInfo.getSiteType(), JsonHelper.toJson(mqDto));
            return true;
        }

        return false;
    }

}
