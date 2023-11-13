package com.jd.bluedragon.distribution.jy.service.seal.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dto.task.SealSyncOpenCloseSendTaskDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.seal.SealSyncOpenCloseSendTaskService;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/11/13 11:23
 * @Description
 */
@Service("sealSyncOpenCloseSendTaskService")
public class SealSyncOpenCloseSendTaskServiceImpl implements SealSyncOpenCloseSendTaskService {
    private Logger logger = LoggerFactory.getLogger(SealSyncOpenCloseSendTaskServiceImpl.class);


    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private JyVehicleSendRelationService jyVehicleSendRelationService;




    @Override
    public boolean dealSeal(SealSyncOpenCloseSendTaskDto param) {

        CallerInfo info = Profiler.registerInfo("DMS.BASE.SealSyncOpenCloseSendTaskConsumer.dealSeal", Constants.UMP_APP_NAME_DMSWEB,false, true);

        List<String> batchCodes = Arrays.asList(param.getSingleBatchCode());
        List<JySendCodeEntity> sendCodeEntityList =jyVehicleSendRelationService.querySendDetailBizIdBySendCode(batchCodes);
        if (CollectionUtils.isEmpty(sendCodeEntityList)){
            if(logger.isInfoEnabled()) {
                logger.info("封车同步新版发货任务数据，根据批次号{}未查到任务信息，不做处理", param.getSingleBatchCode());
            }
            return true;
        }
        if(!Constants.NUMBER_ONE.equals(sendCodeEntityList.size())) {
            logger.error("正常场景一个批次号反查只会查到一个任务，查到多个不确定处理哪个直接丢弃，param={},res={}", JsonHelper.toJson(param), JsonHelper.toJson(sendCodeEntityList));
            return true;
        }
        JySendCodeEntity jySendCodeEntity = sendCodeEntityList.get(0);
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(jySendCodeEntity.getSendVehicleBizId());
        if(Objects.isNull(taskSend) || Constants.NUMBER_ZERO.equals(taskSend.getYn())) {
            if(logger.isInfoEnabled()) {
                logger.info("封车同步新版发货任务数据，根据批次号{}查到任务为空或yn=0，不做处理", param.getSingleBatchCode(), jySendCodeEntity.getSendVehicleBizId());
            }
            return true;
        }
        if(Constants.NUMBER_ONE.equals(taskSend.getManualCreatedFlag())) {
            //封车批次关联的bizId是自建任务：yn=0
            sendVehicleTransactionManager.deleteSendTask(jySendCodeEntity.getSendDetailBizId());
        }else {
            //非自建任务，
            if(!sendVehicleTransactionManager.syncSendTaskSealStatusHandler(jySendCodeEntity.getSendDetailBizId(), taskSend, param)) {
                logger.error("封车状态同步发货任务状态变更逻辑处理异常，msg={}", JsonHelper.toJson(param));
                return false;
            }
        }

        Profiler.registerInfoEnd(info);
        return true;
    }

    @Override
    public boolean dealCancelSeal(SealSyncOpenCloseSendTaskDto param) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.SealSyncOpenCloseSendTaskConsumer.dealUnSeal", Constants.UMP_APP_NAME_DMSWEB,false, true);

        //无批次过滤
        List<String> batchCodes = Arrays.asList(param.getSingleBatchCode());
        List<JySendCodeEntity> sendCodeEntityList =jyVehicleSendRelationService.querySendDetailBizIdBySendCode(batchCodes);
        if (CollectionUtils.isEmpty(sendCodeEntityList)){
            if(logger.isInfoEnabled()) {
                logger.info("取消封车同步新版发货任务数据，根据批次号{}未查到任务信息，不做处理", param.getSingleBatchCode());
            }
            return true;
        }
        if(!Constants.NUMBER_ONE.equals(sendCodeEntityList.size())) {
            logger.error("正常场景一个批次号反查只会查到一个任务，查到多个不确定处理哪个直接丢弃，param={},res={}", JsonHelper.toJson(param), JsonHelper.toJson(sendCodeEntityList));
            return true;
        }
        JySendCodeEntity jySendCodeEntity = sendCodeEntityList.get(0);

        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(jySendCodeEntity.getSendVehicleBizId());
        if(Objects.isNull(taskSend)) {
            if(logger.isInfoEnabled()) {
                logger.info("取消封车同步新版发货任务数据，根据批次号{}查到任务{}为空，不做处理", param.getSingleBatchCode(), jySendCodeEntity.getSendVehicleBizId());
            }
            return true;
        }
        if(Constants.NUMBER_ONE.equals(taskSend.getManualCreatedFlag()) && Constants.YN_NO.equals(taskSend.getYn())) {
            //特殊场景：自建任务yn=0入口：（1）绑定迁移（2）当前消息按批次封车自建任务同步删除， 第二种方式取消封车时yn=0回退到yn=1
            sendVehicleTransactionManager.resetSendTaskYnYes(jySendCodeEntity.getSendDetailBizId(), taskSend.getBizId());
        }else {
            if(Constants.YN_NO.equals(taskSend.getYn())) {
                if(logger.isInfoEnabled()) {
                    logger.info("取消封车同步新版发货任务数据，根据批次号{}查到运输任务{}无效yn=0，不做处理", param.getSingleBatchCode(), jySendCodeEntity.getSendVehicleBizId());
                }
                return true;
            }
            //运输任务已封车回退待封车
            sendVehicleTransactionManager.syncSendTaskToSealHandler(jySendCodeEntity.getSendDetailBizId(), taskSend, param);
        }

        Profiler.registerInfoEnd(info);
        return true;
    }

}
