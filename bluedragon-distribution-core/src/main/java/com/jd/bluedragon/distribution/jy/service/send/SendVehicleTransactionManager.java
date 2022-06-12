package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;

/**
 * @ClassName SendVehicleTransactionManager
 * @Description
 * @Author wyh
 * @Date 2022/6/12 20:41
 **/
@Component("sendVehicleTransactionManager")
public class SendVehicleTransactionManager {

    private static final Logger log = LoggerFactory.getLogger(SendVehicleTransactionManager.class);

    @Autowired
    private JyBizTaskSendVehicleService taskSendVehicleService;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private JyVehicleSendRelationService jySendCodeService;

    @Autowired
    private SendCodeService sendCodeService;

    /**
     * 保存发货任务和发货流向
     * @param sendVehicleEntity
     * @param detailEntity
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.saveTaskSendAndDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED)
    public boolean saveTaskSendAndDetail(JyBizTaskSendVehicleEntity sendVehicleEntity, JyBizTaskSendVehicleDetailEntity detailEntity) {
        if (sendVehicleEntity != null) {

            logInfo("初始化派车单.{}", JsonHelper.toJson(sendVehicleEntity));

            taskSendVehicleService.initTaskSendVehicle(sendVehicleEntity);

            // 创建发货调度任务
            createSendScheduleTask(sendVehicleEntity);
        }
        if (detailEntity != null) {

            logInfo("初始化派车单明细.{}", JsonHelper.toJson(detailEntity));

            taskSendVehicleDetailService.saveTaskSendDetail(detailEntity);

            // 首次创建发货流向时，同时生成批次
            saveSendCode(detailEntity);
        }

        return true;
    }

    /**
     * 创建发货调度任务
     * @param sendVehicleEntity
     * @return
     */
    public JyScheduleTaskResp createSendScheduleTask(JyBizTaskSendVehicleEntity sendVehicleEntity){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(sendVehicleEntity.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        req.setOpeUser(sendVehicleEntity.getCreateUserErp());
        req.setOpeUserName(sendVehicleEntity.getCreateUserName());
        req.setOpeTime(new Date());
        return jyScheduleTaskManager.createScheduleTask(req);
    }

    /**
     * 生成发货批次
     * @param sendDetailDomain
     */
    public int saveSendCode(JyBizTaskSendVehicleDetailEntity sendDetailDomain) {
        JySendCodeEntity sendCodeEntity = new JySendCodeEntity();
        sendCodeEntity.setSendVehicleBizId(sendDetailDomain.getSendVehicleBizId());
        sendCodeEntity.setSendDetailBizId(sendDetailDomain.getBizId());
        sendCodeEntity.setSendCode(generateSendCode(sendDetailDomain.getStartSiteId(), sendDetailDomain.getEndSiteId(), sendDetailDomain.getCreateUserErp()));
        sendCodeEntity.setCreateUserErp(sendDetailDomain.getCreateUserErp());
        sendCodeEntity.setCreateUserName(sendDetailDomain.getCreateUserName());
        sendCodeEntity.setUpdateUserErp(sendCodeEntity.getCreateUserErp());
        sendCodeEntity.setUpdateUserName(sendCodeEntity.getCreateUserName());
        Date now = new Date();
        sendCodeEntity.setCreateTime(now);
        sendCodeEntity.setUpdateTime(now);

        return jySendCodeService.add(sendCodeEntity);
    }

    private String generateSendCode(Long startSiteId, Long destSiteId, String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(startSiteId));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(destSiteId));
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, createUser);
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
}
