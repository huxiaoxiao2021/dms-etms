package com.jd.bluedragon.distribution.jy.service.inspection;

import com.github.houbb.opencc4j.util.StringUtil;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2024/2/29 21:39
 * @Description
 */
@Service
public class JyTrustHandoverAutoInspectionServiceImpl implements JyTrustHandoverAutoInspectionService {

    private Logger logger = LoggerFactory.getLogger(JyTrustHandoverAutoInspectionServiceImpl.class);

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    private DmsConfigManager dmsConfigManager;
    @Autowired
    @Qualifier(value = "jyPackageArriveRetryAutoInspectionProducer")
    private DefaultJMQProducer jyPackageArriveRetryAutoInspectionProducer;
    @Autowired
    private InspectionService inspectionService;


    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, objects);
        }
    }

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyTrustHandoverAutoInspectionServiceImpl.packageArriveAndAutoInspection",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void packageArriveAndAutoInspection(PackageArriveAutoInspectionDto paramDto) {
        JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity = jyBizTaskUnloadVehicleService.findByTransWorkItemCode(paramDto.getTransWorkItemCode());
        if(Objects.isNull(taskUnloadVehicleEntity)) {
            if(this.retryIsExpire(paramDto.getFirstConsumerTime())) {
                logWarn("围栏到车包裹未找到卸车任务, 重试过期不再重试等待,数据丢弃，paramDto={}", JsonHelper.toJson(paramDto));
            }else {
                logWarn("围栏到车包裹未找到卸车任务，无法确认是否非逐单卸做自动验货，重试等待，paramDto={}", JsonHelper.toJson(paramDto));
                this.sendRetryPackageArriveAutoInspectionMQ(paramDto);
            }
            return;
        }
        if(!Objects.isNull(taskUnloadVehicleEntity.getUnloadStartTime())) {
            logInfo("围栏到车包裹自动验货，该任务【bizId:{}】已经开始人工卸车【startTime:{}】,不触发自动验货，到车包裹信息={}",
                    taskUnloadVehicleEntity.getBizId(), taskUnloadVehicleEntity.getUnloadStartTime(), JsonHelper.toJson(paramDto));
            return;
        }
        if(StringUtils.isBlank(taskUnloadVehicleEntity.getTagsSign())) {
            if(!this.retryIsExpire(paramDto.getFirstConsumerTime())) {
                logWarn("围栏到车包裹非逐单卸未打标，无法确认是否自动验货，重试等待，paramDto={}", JsonHelper.toJson(paramDto));
                this.sendRetryPackageArriveAutoInspectionMQ(paramDto);
                return ;
            }
            logWarn("围栏到车包裹非逐单卸未打标, 重试已超期，不再等待是否逐单卸标识，直接触发自动验货，paramDto={}", JsonHelper.toJson(paramDto));
        }else if (BusinessUtil.isSignY(taskUnloadVehicleEntity.getTagsSign(), JyUnloadTaskSignConstants.POSITION_2)) {
            logInfo("围栏到车包裹，该任务【bizId:{}】为逐单卸包裹，不做自动验货，paramDto={}", taskUnloadVehicleEntity.getBizId(), JsonHelper.toJson(paramDto));
            return;
        }

        //PDA实操互斥打标
        this.markTaskAutoInspection(taskUnloadVehicleEntity);

        //自动验货执行
        InspectionVO vo = this.convertArriveCarAutoInspectionVO(paramDto);
        this.addInspectionTask(vo, InspectionBizSourceEnum.ELECTRONIC_FENCE);
    }

    //围栏到车自动验货task对象组装
    private InspectionVO convertArriveCarAutoInspectionVO(PackageArriveAutoInspectionDto paramDto) {
        InspectionVO inspectionVO = new InspectionVO();
        inspectionVO.setBarCodes(Collections.singletonList(paramDto.getPackageCode()));
        inspectionVO.setSiteCode(paramDto.getArriveSiteId());
        inspectionVO.setSiteName(paramDto.getArriveSiteName());
        inspectionVO.setUserCode(-1);
        inspectionVO.setUserName(StringUtil.EMPTY);
        inspectionVO.setOperateTime(DateHelper.formatDateTime(paramDto.getOperateTime()));
        inspectionVO.setOperatorData(this.getOperatorData(OperatorTypeEnum.VEHICLE.getCode(), paramDto.getTransWorkItemCode()));
        return inspectionVO;
    }

    //验货类型组装
    private OperatorData getOperatorData(Integer operatorTypeCode, String operatorId) {
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(operatorId);
        operatorData.setOperatorTypeCode(operatorTypeCode);
        return operatorData;
    }

    //到车自动验货重试是否过期
    private boolean retryIsExpire(Long firstConsumerTime) {
        if(Objects.isNull(firstConsumerTime)) {
            return false;
        }
        //
        Long expireTime = firstConsumerTime + 60l * 1000l * dmsConfigManager.getUccPropertyConfiguration().getPackageArriveAutoInspectionRetryMinutes();
        return NumberHelper.gt(System.currentTimeMillis(), expireTime);
    }

    //发送围栏到车包裹重试等待自动验货mq
    private void sendRetryPackageArriveAutoInspectionMQ(PackageArriveAutoInspectionDto paramDto) {
        logInfo("围栏到车包裹自动验货前置条件不符合，进入重试队列，发送重试消息{}", JsonHelper.toJson(paramDto));
        jyPackageArriveRetryAutoInspectionProducer.sendOnFailPersistent(paramDto.getPackageCode(), JsonHelper.toJson(paramDto));
    }

    //卸车任务打标：自动验货
    private void markTaskAutoInspection(JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity) {
        if(Constants.NUMBER_ONE.equals(taskUnloadVehicleEntity.getAutoInspectionFlag())) {
            return;
        }
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setId(taskUnloadVehicleEntity.getId());
        entity.setAutoInspectionFlag(Constants.NUMBER_ONE);
        Date currentTime = new Date();
        entity.setAutoInspectionStartTime(currentTime);
        entity.setUpdateTime(currentTime);
        jyBizTaskUnloadVehicleService.updateOfBusinessInfoById(entity);
    }

    //验货
    private void addInspectionTask(InspectionVO inspectionVO, InspectionBizSourceEnum inspectionBizSourceEnum) {
        InvokeResult<Boolean> taskResult = inspectionService.addInspection(inspectionVO, inspectionBizSourceEnum);
        if (!taskResult.codeSuccess()) {
            Profiler.businessAlarm("dms.worker.JyTrustHandoverAutoInspectionServiceImpl.addInspectionTask", "卸车扫描插入验货任务失败，将重试");
            logger.warn("卸车扫描插入验货任务失败，将重试. {}，{}", JsonHelper.toJson(inspectionVO), JsonHelper.toJson(taskResult));
            // 再次尝试插入任务
            try {
                Thread.sleep(100);
                inspectionService.addInspection(inspectionVO, inspectionBizSourceEnum);
            } catch (InterruptedException e) {
                logger.error("再次插入卸车验货任务异常. {}", JsonHelper.toJson(inspectionVO), e);
            }
        }
    }
}
