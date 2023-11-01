package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.enums.JySendTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/4
 * @Description:
 * 消费 tms_seal_car_status
 * sealCarCode 维度
 * status  10 封车，20解封车，30出围栏，40进围栏
 * {"sealCarCode":"SC22033019316055","status":20,"operateUserCode":"kongtuantuan","operateUserName":"孔团团","operateTime":"2022-03-30 15:37:56","sealCarType":30,"batchCodes":null,"transBookCode":"TB22033029658674","volume":null,"weight":null,"transWay":null,"vehicleNumber":"京AAG6153","operateSiteId":271733,"operateSiteCode":"010Y316","operateSiteName":"北京槐柏树营业部","warehouseCode":null,"largeCargoDetails":null,"pieceCount":null,"source":1,"sealCarInArea":null}
{"sealCarCode":"SC22040419643118","status":10,"operateUserCode":"chenyifei6","operateUserName":"陈毅飞","operateTime":"2022-04-04 19:10:54","sealCarType":30,"batchCodes":["R202204041106221342","R1510931730171867136","R202204041145461342"],"transBookCode":"TB22040430009175","volume":null,"weight":null,"transWay":2,"vehicleNumber":"京AAJ7385","operateSiteId":1342,"operateSiteCode":"010Y059","operateSiteName":"北京上庄营业部","warehouseCode":null,"largeCargoDetails":null,"pieceCount":null,"source":2,"sealCarInArea":null}
 */
@Service("sealSyncJySendTaskStatusConsumer")
public class SealSyncJySendTaskStatusConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(SealSyncJySendTaskStatusConsumer.class);

    /**
     * 封车状态
     */
    private static final Integer TMS_STATUS_SEAL = 10;
    private static final String DEFAULT_USER = "sys";

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;
    @Autowired
    JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    JyVehicleSendRelationService jyVehicleSendRelationService;



    @Override
    @JProfiler(jKey = "DMSWORKER.jy.SealSyncJySendTaskStatusConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("SealSyncJySendTaskStatusConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("SealSyncJySendTaskStatusConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsSealCarStatusMQBody mqBody = JsonHelper.fromJson(message.getText(),TmsSealCarStatusMQBody.class);
        if(mqBody == null){
            logger.error("SealSyncJySendTaskStatusConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        //
        if(!TMS_STATUS_SEAL.equals(mqBody.getStatus())){
            return ;
        }
        if(StringUtils.isBlank(mqBody.getSingleBatchCode())) {
            return ;
        }
        if(logger.isInfoEnabled()){
            logger.info("消费处理 sealSyncJySendTaskStatus 开始，内容{}",message.getText());
        }
        if(!deal(mqBody)){
            //处理失败 重试
            logger.error("消费处理 sealSyncJySendTaskStatus 失败，内容{}",message.getText());
            throw new JyBizException("消费处理tms_seal_car_status失败");
        }else{
            if(logger.isInfoEnabled()) {
                logger.info("消费处理 sealSyncJySendTaskStatus 成功，内容{}", message.getText());
            }
        }

    }

    /**
     * 处理逻辑
     * @param tmsSealCarStatus
     * @return
     */
    private boolean deal(TmsSealCarStatusMQBody tmsSealCarStatus){
        //无批次过滤
        List<String> batchCodes = Arrays.asList(tmsSealCarStatus.getSingleBatchCode());
        List<JySendCodeEntity> sendCodeEntityList =jyVehicleSendRelationService.querySendDetailBizIdBySendCode(batchCodes);
        if (CollectionUtils.isEmpty(sendCodeEntityList)){
            return true;
        }
        for(JySendCodeEntity jySendCodeEntity : sendCodeEntityList) {
            JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(jySendCodeEntity.getSendVehicleBizId());
            JyBizTaskSendVehicleDetailEntity taskSendDetail = jyBizTaskSendVehicleDetailService.findByBizId(jySendCodeEntity.getSendDetailBizId());
            //封车状态同步
            if(!this.syncSendTaskSealStatusHandler(taskSendDetail, taskSend, tmsSealCarStatus)) {
                logger.error("封车状态同步发货任务状态变更逻辑处理异常，msg={}", JsonHelper.toJson(tmsSealCarStatus));
                throw new JyBizException("封车状态同步发货任务状态变更逻辑处理异常");
            }

            //封车自建任务处理
            if(!this.sealManualCreatedSendTaskHandler(taskSendDetail, taskSend, tmsSealCarStatus)) {
                logger.error("封车状态同步发货任务状态自建任务逻辑处理异常，msg={}", JsonHelper.toJson(tmsSealCarStatus));
                throw new JyBizException("封车状态同步发货任务状态自建任务逻辑处理异常");
            }


        }


        return true;
    }

    //状态回退：仅处理运输任务。不处理自建任务
    private boolean syncSendTaskSealStatusHandler(JyBizTaskSendVehicleDetailEntity taskSendDetail, JyBizTaskSendVehicleEntity taskSend, TmsSealCarStatusMQBody myBody) {
        if(Constants.NUMBER_ONE.equals(taskSend.getManualCreatedFlag())) {
            return true;
        }
        //无效状态过滤，仅处理未封车状态
        if(!JyBizTaskSendDetailStatusEnum.TO_SEND.getCode().equals(taskSendDetail.getVehicleStatus()) && !JyBizTaskSendDetailStatusEnum.SENDING.getCode().equals(taskSendDetail.getVehicleStatus()) && !JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode().equals(taskSendDetail.getVehicleStatus())) {
            return true;
        }
        //发货主表
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(DEFAULT_USER);
        taskSend.setUpdateUserName(DEFAULT_USER);
        //发货明细表
        taskSendDetail.setSealCarTime(DateHelper.parseDate(myBody.getOperateTime(), Constants.DATE_TIME_FORMAT));
        taskSendDetail.setUpdateTime(taskSend.getUpdateTime());
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        //发货航空表（特殊场景）
        JyBizTaskSendAviationPlanEntity aviationPlanEntity = null;
        if(JySendTaskTypeEnum.AVIATION.getCode().equals(taskSendDetail.getTaskType())) {
            JyBizTaskSendAviationPlanEntity sendAviationPlanEntity = jyBizTaskSendAviationPlanService.findByBizId(taskSend.getBizId());
            //只处理未发货
            if(!Objects.isNull(sendAviationPlanEntity) && (JyBizTaskSendDetailStatusEnum.TO_SEND.getCode().equals(taskSendDetail.getVehicleStatus()) || JyBizTaskSendDetailStatusEnum.SENDING.getCode().equals(taskSendDetail.getVehicleStatus()) || JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode().equals(taskSendDetail.getVehicleStatus()))){
                aviationPlanEntity = new JyBizTaskSendAviationPlanEntity();
                aviationPlanEntity.setTaskStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());//航空任务不存在主子状态不一致场景，不需要计算主任务取最小子任务状态，所有表状态相同，直接置为封车状态
                aviationPlanEntity.setUpdateTime(taskSend.getUpdateTime());
                aviationPlanEntity.setUpdateUserName(taskSend.getUpdateUserName());
                aviationPlanEntity.setUpdateUserErp(taskSend.getUpdateUserErp());
                aviationPlanEntity.setBizId(taskSend.getBizId());
            }
        }

        //封车统计数据：保证上下文数据模型数据一致性，封车统计表插入一条任务封车数据  todo 暂不考虑
//        JySealStatisticsSummaryEntity summaryEntity = new JySealStatisticsSummaryEntity(taskSend.getBizId(),
//                BusinessKeyTypeEnum.JY_SEND_TASK.getCode(),
//                taskSend.getStartSiteId().intValue(),
//                SummarySourceEnum.SEAL.getCode());
//        summaryEntity.setCreateTime(new Date());
//        summaryEntity.setUpdateTime(summaryEntity.getUpdateTime());
//        summaryEntity.setCreateUserErp(taskSend.getUpdateUserErp());
//        summaryEntity.setCreateUserName(taskSend.getUpdateUserName());
//        summaryEntity.setUpdateUserErp(taskSend.getUpdateUserErp());
//        summaryEntity.setUpdateUserName(taskSend.getUpdateUserName());

        return sendVehicleTransactionManager.syncSendTaskSealHandler(taskSend, taskSendDetail, aviationPlanEntity, null);
    }

    //封车自建任务删除
    private boolean sealManualCreatedSendTaskHandler(JyBizTaskSendVehicleDetailEntity taskSendDetail, JyBizTaskSendVehicleEntity taskSend, TmsSealCarStatusMQBody myBody) {
        if(!Constants.NUMBER_ONE.equals(taskSend.getManualCreatedFlag())) {
            return true;
        }
        if(logger.isInfoEnabled()) {
            logger.info("消费封车消息删除jy自建发货任务, bizId={},detailBizId={},myBody={}", taskSend.getBizId(), taskSendDetail.getBizId(), JsonHelper.toJson(myBody));
        }
        //删除主任务
        Date now = new Date();
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(taskSend.getBizId());
        entity.setYn(Constants.YN_NO);
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(DEFAULT_USER);
        entity.setUpdateUserName(DEFAULT_USER);
        jyBizTaskSendVehicleService.updateSendVehicleTask(entity);
        //删除子任务
        JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setSendVehicleBizId(taskSendDetail.getBizId());
        detailEntity.setYn(Constants.YN_NO);
        detailEntity.setUpdateTime(now);
        detailEntity.setUpdateUserErp(DEFAULT_USER);
        detailEntity.setUpdateUserName(DEFAULT_USER);
        jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(detailEntity);
        return true;
    }


    /**
     * 消息实体
     */
    private class TmsSealCarStatusMQBody implements Serializable {

        static final long serialVersionUID = 1L;
        /**
         * 封车编码
         */
        private String sealCarCode;
        /**
         * 10 封车，20解封车，30进围栏，40出围栏
         */
        private Integer status;
        /**
         * 操作人ERP
         */
        private String operateUserCode;
        /**
         * 操作人名字
         */
        private String operateUserName;
        /**
         * 操作时间
         */
        private String operateTime;
        /**
         * 批次 JSON格式
         */
        private List<String> batchCodes;
        /**
         * 运输封车批次集合拆分
         * batchCodes 中的某一个
         */
        private String singleBatchCode;


        public String getSealCarCode() {
            return sealCarCode;
        }

        public void setSealCarCode(String sealCarCode) {
            this.sealCarCode = sealCarCode;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getOperateUserCode() {
            return operateUserCode;
        }

        public void setOperateUserCode(String operateUserCode) {
            this.operateUserCode = operateUserCode;
        }

        public String getOperateUserName() {
            return operateUserName;
        }

        public void setOperateUserName(String operateUserName) {
            this.operateUserName = operateUserName;
        }

        public String getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(String operateTime) {
            this.operateTime = operateTime;
        }

        public List<String> getBatchCodes() {
            return batchCodes;
        }

        public void setBatchCodes(List<String> batchCodes) {
            this.batchCodes = batchCodes;
        }

        public String getSingleBatchCode() {
            return singleBatchCode;
        }

        public void setSingleBatchCode(String singleBatchCode) {
            this.singleBatchCode = singleBatchCode;
        }
    }
}
