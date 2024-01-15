package com.jd.bluedragon.distribution.jy.service.seal.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dto.task.SealSyncOpenCloseSendTaskDto;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.seal.SealSyncOpenCloseSendTaskService;
import com.jd.bluedragon.distribution.jy.service.send.JyVehicleSendRelationService;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.enums.JySendTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
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
        if(logger.isInfoEnabled()){
            logger.info("封车关闭新版发货任务状态开始，param={}", JsonHelper.toJson(param));
        }
        List<String> batchCodes = Arrays.asList(param.getSingleBatchCode());
        List<JySendCodeEntity> sendCodeEntityList =jyVehicleSendRelationService.querySendDetailBizIdBySendCode(batchCodes);
        if (CollectionUtils.isEmpty(sendCodeEntityList)){
            if(logger.isInfoEnabled()) {
                logger.info("封车同步新版发货任务数据，根据批次号{}未查到任务信息，不做处理", param.getSingleBatchCode());
            }
            return true;
        }
        sendCodeEntityList.forEach(entity -> {
            this.sealSyncTaskExecute(entity, param);
        });
        Profiler.registerInfoEnd(info);
        return true;
    }

    private boolean sealSyncTaskExecute(JySendCodeEntity jySendCodeEntity, SealSyncOpenCloseSendTaskDto param) {
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
        return true;
    }

}
