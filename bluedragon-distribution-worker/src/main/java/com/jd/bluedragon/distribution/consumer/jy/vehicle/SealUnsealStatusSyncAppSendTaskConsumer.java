package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dao.send.JySendTransferLogDao;
import com.jd.bluedragon.distribution.jy.dto.task.SealUnsealStatusSyncAppSendTaskMQDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述： 封车/取消封车时，按批次维度处理，反查新版发货任务数据，同步任务状态
 *
 * 解决场景：
 *  （1） 新版app发货任务未监听非新版入口封车节点，导致出现状态不一致的问题，比如老版传摆封车后，新版app发货任务还在待发货或者发货中列表
 *  （2） 新版封车时支持扫描批次号封车，封车仅修改当前任务状态，未关注扫描批次号关联任务状态，比如自建任务没有走绑定封车流程，被扫描批次封车到另外的运输任务中，实际批次已经封车，但是该自建任务还在发货中
 *
 *  该消息为分拣worker自产自销
 *  producer入口存在两个：
 *      （1）运输封车消息：tms_seal_car_status
 *      （2）运输取消封车消息： tms_cancel_seal_car_batch
 *
 * 其他：
 *  ucc控制该功能是否启用，默认隐式开启，如果需要关闭可ucc平台配置
 *      key: uccPropertyConfiguration.sealUnsealStatusSyncAppSendTaskSwitch
 *      value: uccPropertyConfiguration.sealUnsealStatusSyncAppSendTaskSwitch
 *
 *
 *  备注：
 *      （1）封车时，除乐任务状态已封车，还会关闭小组任务和调度任务
 *      撤销封车时仅对状态做了回退
 *
 */
@Service("sealUnsealStatusSyncAppSendTaskConsumer")
public class SealUnsealStatusSyncAppSendTaskConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(SealUnsealStatusSyncAppSendTaskConsumer.class);

    private static final String DEFAULT_USER = "sys";
    //开关
    private static final String DEFAULT_SWITCH = "1,1,1";
    //开关默认开启值
    private static final String DEFAULT_SWITCH_OPEN = "1";



    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    private JySendTransferLogDao jySendTransferLogDao;
    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.SealUnsealStatusSyncAppSendTaskConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("SealUnsealStatusSyncAppSendTaskConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("SealUnsealStatusSyncAppSendTaskConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        SealUnsealStatusSyncAppSendTaskMQDto mqBody = JsonHelper.fromJson(message.getText(),SealUnsealStatusSyncAppSendTaskMQDto.class);
        if(mqBody == null){
            logger.error("SealUnsealStatusSyncAppSendTaskConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(StringUtils.isBlank(mqBody.getSingleBatchCode())) {
            return;
        }
        if(!SealUnsealStatusSyncAppSendTaskMQDto.STATUS_SEAL.equals(mqBody.getStatus()) && !SealUnsealStatusSyncAppSendTaskMQDto.STATUS_UNSEAL.equals(mqBody.getStatus())) {
            return;
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
    //
    private boolean deal(SealUnsealStatusSyncAppSendTaskMQDto mqBody){
        String switchStr = uccPropertyConfiguration.getSealUnsealStatusSyncAppSendTaskSwitch();
        if(StringUtils.isBlank(switchStr) || switchStr.split(Constants.SEPARATOR_COMMA).length < 3) {
            switchStr = DEFAULT_SWITCH;
        }
        String[] arr = switchStr.split(Constants.SEPARATOR_COMMA);
        if(!DEFAULT_SWITCH_OPEN.equals(arr[0].trim())) {
            logger.warn("封车解封车同步jy发货任务状态开关关闭，不做处理，批次号={}，ucc同步开关关闭（ucc:sealUnsealStatusSyncAppSendTaskSwitch）", mqBody.getSingleBatchCode());
            return true;
        }

        if(SealUnsealStatusSyncAppSendTaskMQDto.STATUS_SEAL.equals(mqBody.getStatus())) {
            if(!DEFAULT_SWITCH_OPEN.equals(arr[1].trim())) {
                logger.warn("封车同步jy发货任务状态开关关闭，不做处理，批次号={}，ucc同步开关关闭（ucc:sealUnsealStatusSyncAppSendTaskSwitch）", mqBody.getSingleBatchCode());
                return true;
            }
            return dealSeal(mqBody);

        }else if(SealUnsealStatusSyncAppSendTaskMQDto.STATUS_UNSEAL.equals(mqBody.getStatus())) {
            if(!DEFAULT_SWITCH_OPEN.equals(arr[2].trim())) {
                logger.warn("取消封车同步jy发货任务状态开关关闭，不做处理，批次号={}，ucc同步开关关闭（ucc:sealUnsealStatusSyncAppSendTaskSwitch）", mqBody.getSingleBatchCode());
                return true;
            }
            return dealUnSeal(mqBody);
        }
        return true;
    }

    /**
     * 处理逻辑
     * @param mqBody
     * @return
     */
    private boolean dealSeal(SealUnsealStatusSyncAppSendTaskMQDto mqBody){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.SealUnsealStatusSyncAppSendTaskConsumer.dealSeal", Constants.UMP_APP_NAME_DMSWEB,false, true);

        List<String> batchCodes = Arrays.asList(mqBody.getSingleBatchCode());
        List<JySendCodeEntity> sendCodeEntityList =jyVehicleSendRelationService.querySendDetailBizIdBySendCode(batchCodes);
        if (CollectionUtils.isEmpty(sendCodeEntityList)){
            if(logger.isInfoEnabled()) {
                logger.info("封车同步新版发货任务数据，根据批次号{}未查到任务信息，不做处理", mqBody.getSingleBatchCode());
            }
            return true;
        }
        for(JySendCodeEntity jySendCodeEntity : sendCodeEntityList) {
            JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(jySendCodeEntity.getSendVehicleBizId());
            if(Objects.isNull(taskSend) || Constants.NUMBER_ZERO.equals(taskSend.getYn())) {
                if(logger.isInfoEnabled()) {
                    logger.info("封车同步新版发货任务数据，根据批次号{}查到任务为空或yn=0，不做处理", mqBody.getSingleBatchCode(), jySendCodeEntity.getSendVehicleBizId());
                }
                return true;
            }
            if(Constants.NUMBER_ONE.equals(taskSend.getManualCreatedFlag())) {
                //封车批次关联的bizId是自建任务：yn=0
                sendVehicleTransactionManager.deleteSendTask(jySendCodeEntity.getSendDetailBizId());
            }else {
                //非自建任务，
                if(!sendVehicleTransactionManager.syncSendTaskSealStatusHandler(jySendCodeEntity.getSendDetailBizId(), taskSend, mqBody)) {
                    logger.error("封车状态同步发货任务状态变更逻辑处理异常，msg={}", JsonHelper.toJson(mqBody));
                    return false;
                }
            }
        }

        Profiler.registerInfoEnd(info);
        return true;
    }


    private boolean dealUnSeal(SealUnsealStatusSyncAppSendTaskMQDto mqBody) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.SealUnsealStatusSyncAppSendTaskConsumer.dealUnSeal", Constants.UMP_APP_NAME_DMSWEB,false, true);

        //无批次过滤
        List<String> batchCodes = Arrays.asList(mqBody.getSingleBatchCode());
        List<JySendCodeEntity> sendCodeEntityList =jyVehicleSendRelationService.querySendDetailBizIdBySendCode(batchCodes);
        if (CollectionUtils.isEmpty(sendCodeEntityList)){
            if(logger.isInfoEnabled()) {
                logger.info("取消封车同步新版发货任务数据，根据批次号{}未查到任务信息，不做处理", mqBody.getSingleBatchCode());
            }
            return true;
        }
        for(JySendCodeEntity jySendCodeEntity : sendCodeEntityList) {
            JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(jySendCodeEntity.getSendVehicleBizId());
            if(Objects.isNull(taskSend)) {
                if(logger.isInfoEnabled()) {
                    logger.info("取消封车同步新版发货任务数据，根据批次号{}查到任务{}为空，不做处理", mqBody.getSingleBatchCode(), jySendCodeEntity.getSendVehicleBizId());
                }
                return true;
            }
            if(Constants.NUMBER_ONE.equals(taskSend.getManualCreatedFlag()) && Constants.YN_NO.equals(taskSend.getYn())) {
                //特殊场景：自建任务yn=0入口：（1）绑定迁移（2）当前消息按批次封车自建任务同步删除， 第二种方式取消封车时yn=0回退到yn=1
                sendVehicleTransactionManager.resetSendTaskYnYes(jySendCodeEntity.getSendDetailBizId(), taskSend.getBizId());
            }else {
                if(Constants.YN_NO.equals(taskSend.getYn())) {
                    if(logger.isInfoEnabled()) {
                        logger.info("取消封车同步新版发货任务数据，根据批次号{}查到运输任务{}无效yn=0，不做处理", mqBody.getSingleBatchCode(), jySendCodeEntity.getSendVehicleBizId());
                    }
                    return true;
                }
                //运输任务已封车回退待封车
                sendVehicleTransactionManager.syncSendTaskToSealHandler(jySendCodeEntity.getSendDetailBizId(), taskSend, mqBody);
            }
        }

        Profiler.registerInfoEnd(info);
        return true;
    }


}
