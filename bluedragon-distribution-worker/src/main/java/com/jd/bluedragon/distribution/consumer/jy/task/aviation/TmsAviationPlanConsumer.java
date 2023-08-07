package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.TmsAviationPlanDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.SendVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.enums.JyAviationPlanBookedStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/8/7 10:52
 * @Description 运输航空计划消费
 */
@Service("tmsAviationPlanConsumer")
public class TmsAviationPlanConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(TmsAviationPlanConsumer.class);

    public static final String CACHE_DEFAULT = "1";

    public static final String JY_LOCK_AVIATION_PLAN_SEND_KEY = "jy:lock:aviationPlan:send:%s";
    public static final int JY_LOCK_AVIATION_PLAN_SEND_KEY_TIMEOUT_SECONDS = 120;



    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;
    @Autowired
    private SendVehicleTransactionManager transactionManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.tmsAviationPlanConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("tmsAviationPlanConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("tmsAviationPlanConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsAviationPlanDto mqBody = JsonHelper.fromJson(message.getText(), TmsAviationPlanDto.class);
        if(mqBody == null){
            log.error("tmsAviationPlanConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费处理tmsAviationPlanConsumer开始，内容{}",message.getText());
        }

        mqBody.setBusinessId(message.getBusinessId());
        this.generateAviationSendPlanTask(mqBody);
        if (log.isInfoEnabled()) {
            log.info("tmsAviationPlanConsumer航空计划生成航空发货任务成功，businessId={}，订舱号={}", message.getBusinessId(), mqBody.getBookingCode());
        }
    }


    /**
     * 生成航空发货任务
     * @param mqBody
     * @return
     */
    private void generateAviationSendPlanTask(TmsAviationPlanDto mqBody) {
        //无效数据过滤
        if(invalidDataFilter(mqBody)) {
            log.warn("无效航空计划丢弃：{}", JsonHelper.toJson(mqBody));
            return;
        }
        //订舱号唯一：并发锁处理
        if(!lockGenerateSendAviationPlan(mqBody.getBookingCode())) {
            String warnMsg = String.format("航空计划发货任务生成-订舱号%s正在处理中!", mqBody.getBookingCode());
            log.warn(warnMsg, JsonHelper.toJson(mqBody));
            throw new JyBizException(warnMsg);
        }
        try{
            //新增航空计划
            if(JyAviationPlanBookedStatusEnum.ADD.getCode().equals(mqBody.getBookedStatus())) {
                this.addAviationPlanTask(mqBody);
            }
            //删除航空计划
            else if(JyAviationPlanBookedStatusEnum.DISCARD.getCode().equals(mqBody.getBookedStatus())) {
                this.discardAviationPlanTask(mqBody);
            }
            //修改航空计划
            else if(JyAviationPlanBookedStatusEnum.UPDATE.getCode().equals(mqBody.getBookedStatus())) {
                this.updateAviationPlanTask(mqBody);
            }
        }catch (Exception e) {
            //释放锁
            unlockGenerateSendAviationPlan(mqBody.getBookingCode());
        }

    }


    /**
     * 修改航空任务
     * @param mqBody
     */
    private void updateAviationPlanTask(TmsAviationPlanDto mqBody) {
        //        todo zcf  考虑时间顺序、是否废除等逻辑
    }

    /**
     * 废弃航空任务
     * @param mqBody
     */
    private void discardAviationPlanTask(TmsAviationPlanDto mqBody) {
//        todo zcf  不能直接删除， 任务进行中有废弃提示，走迁移流程
    }

    /**
     * 生成航空计划
     * @param mqBody
     */
    private void addAviationPlanTask(TmsAviationPlanDto mqBody) {
        JyBizTaskSendVehicleEntity existSendTaskMain = jyBizTaskSendVehicleService.findByBookingCode(mqBody.getBookingCode());
        if(!Objects.isNull(existSendTaskMain)) {
            if(log.isInfoEnabled()) {
                log.info("航空计划订舱号【{}】生成发货任务已存在，不做处理，mqBody={}", JsonHelper.toJsonMs(mqBody));
            }
            return;
        }
        //航空计划
        JyBizTaskSendAviationPlanEntity aviationPlanEntity = this.generateAviationPlanEntity(mqBody);
        JyBizTaskSendVehicleEntity sendVehicleEntity = this.aviationPlanConvertSendTask(aviationPlanEntity);
        JyBizTaskSendVehicleDetailEntity sendVehicleDetailEntity = this.aviationPlanConvertSendTaskDetail(aviationPlanEntity);
        transactionManager.saveAviationPlanAndTaskSendAndDetail(aviationPlanEntity, sendVehicleEntity, sendVehicleDetailEntity);

    }


    private JyBizTaskSendAviationPlanEntity generateAviationPlanEntity(TmsAviationPlanDto mqBody) {
        JyBizTaskSendAviationPlanEntity entity = new JyBizTaskSendAviationPlanEntity();
        entity.setBizId(jyBizTaskSendVehicleService.genMainTaskBizId());
        entity.setBookingCode(mqBody.getBookingCode());
        entity.setStartSiteCode(mqBody.getStartNodeCode());
        BaseStaffSiteOrgDto currSite = baseMajorManager.getBaseSiteByDmsCode(mqBody.getStartNodeCode());
        if (Objects.isNull(currSite)) {
            log.warn("航空计划发货任务创建：基础资料未查到始发分拣【{}】场地信息：mqBody={}" , mqBody.getStartNodeCode(), JsonHelper.toJson(mqBody));
            throw new JyBizException();
        }
        entity.setStartSiteId(currSite.getSiteCode());
        entity.setStartSiteName(mqBody.getStartNodeName());
        entity.setFlightNumber(mqBody.getFlightNumber());
        entity.setTakeOffTime(mqBody.getTakeOffTime());
        entity.setTouchDownTime(mqBody.getTouchDownTime());
        entity.setAirCompanyCode(mqBody.getAirCompanyCode());
        entity.setAirCompanyName(mqBody.getAirCompanyName());
        entity.setBeginNodeCode(mqBody.getBeginNodeCode());
        entity.setBeginNodeName(mqBody.getBeginNodeName());
        entity.setEndNodeCode(mqBody.getEndNodeCode());
        entity.setEndNodeName(mqBody.getEndNodeName());
        entity.setCarrierCode(mqBody.getCarrierCode());
        entity.setCarrierName(mqBody.getCarrierName());
        entity.setBookingWeight(mqBody.getBookingWeight());
        entity.setCargoType(mqBody.getCargoType());
        entity.setAirType(mqBody.getAirType());
        entity.setBookedStatus(mqBody.getBookedStatus());
        //todo zcf 根据航空计划获取路由系统中流向场地信息： 待确认
//        entity.setNextSiteCode();
//        entity.setNextSiteId();
//        entity.setNextSiteName();

        entity.setCreateUserErp(Constants.SYS_NAME);
        entity.setCreateUserName(Constants.SYS_NAME);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());
        entity.setYn(Constants.YN_YES);

        return entity;
    }


    private JyBizTaskSendVehicleEntity aviationPlanConvertSendTask(JyBizTaskSendAviationPlanEntity aviationPlanEntity) {
        JyBizTaskSendVehicleEntity sendVehicleEntity = new JyBizTaskSendVehicleEntity();
        sendVehicleEntity.setBizId(aviationPlanEntity.getBizId());
        sendVehicleEntity.setStartSiteId(aviationPlanEntity.getStartSiteId().longValue());
        sendVehicleEntity.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
        sendVehicleEntity.setCreateUserErp(aviationPlanEntity.getCreateUserErp());
        sendVehicleEntity.setCreateUserName(aviationPlanEntity.getCreateUserName());
        sendVehicleEntity.setCreateTime(aviationPlanEntity.getCreateTime());
        sendVehicleEntity.setUpdateTime(aviationPlanEntity.getUpdateTime());
        return sendVehicleEntity;
    }

    private JyBizTaskSendVehicleDetailEntity aviationPlanConvertSendTaskDetail(JyBizTaskSendAviationPlanEntity aviationPlanEntity) {
        JyBizTaskSendVehicleDetailEntity taskSendVehicleDetailEntity = new JyBizTaskSendVehicleDetailEntity();
        taskSendVehicleDetailEntity.setSendVehicleBizId(aviationPlanEntity.getBizId());
        taskSendVehicleDetailEntity.setBizId(aviationPlanEntity.getBookingCode());
        taskSendVehicleDetailEntity.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode());
        taskSendVehicleDetailEntity.setStartSiteId(aviationPlanEntity.getStartSiteId().longValue());
        taskSendVehicleDetailEntity.setStartSiteName(aviationPlanEntity.getStartSiteName());
        taskSendVehicleDetailEntity.setEndSiteId(aviationPlanEntity.getNextSiteId().longValue());
        taskSendVehicleDetailEntity.setEndSiteName(aviationPlanEntity.getNextSiteName());
        taskSendVehicleDetailEntity.setCreateUserErp(aviationPlanEntity.getCreateUserErp());
        taskSendVehicleDetailEntity.setCreateUserName(aviationPlanEntity.getCreateUserName());
        taskSendVehicleDetailEntity.setCreateTime(aviationPlanEntity.getCreateTime());
        taskSendVehicleDetailEntity.setUpdateTime(aviationPlanEntity.getUpdateTime());
        return taskSendVehicleDetailEntity;
    }


    /**
     * 过滤无效数据  返回true 无效
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsAviationPlanDto mqBody) {
        if(StringUtils.isBlank(mqBody.getBookingCode())) {
            log.error("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：订舱号为空，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }
        if(Objects.isNull(JyAviationPlanBookedStatusEnum.getBookedStatusEnumByCode(mqBody.getBookedStatus()))) {
            log.error("tmsAviationPlanConsumer.invalidDataFilter:无效数据丢弃：订舱状态无效，内容为【{}】", JsonHelper.toJson(mqBody));
            return true;
        }

        return false;
    }

    //订舱号并发锁获取
    private boolean lockGenerateSendAviationPlan(String bookingCode) {
        return redisClientOfJy.set(
                this.getLockKeyGenerateSendAviationPlan(bookingCode),
                TmsAviationPlanConsumer.CACHE_DEFAULT,
                TmsAviationPlanConsumer.JY_LOCK_AVIATION_PLAN_SEND_KEY_TIMEOUT_SECONDS,
                TimeUnit.SECONDS,
                false);
    }
    //订舱号并发锁释放
    private void unlockGenerateSendAviationPlan(String bookingCode) {
        redisClientOfJy.del(this.getLockKeyGenerateSendAviationPlan(bookingCode));
    }
    //订舱号并发锁key获取
    private String getLockKeyGenerateSendAviationPlan(String bookingCode) {
        return String.format(TmsAviationPlanConsumer.JY_LOCK_AVIATION_PLAN_SEND_KEY, bookingCode);
    }

}
