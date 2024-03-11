package com.jd.bluedragon.distribution.jy.service.inspection;

import com.github.houbb.opencc4j.util.StringUtil;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.distribution.cyclebox.service.BoxMaterialRelationService;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionBoxDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.RecycleMaterialAutoInspectionPackageDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Value("${packageArriveCarAutoInspectionMqRetryIntervalMinutes:2}")
    private Integer packageArriveCarAutoInspectionMqRetryIntervalMinutes;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private BoxMaterialRelationService boxMaterialRelationService;
    @Autowired
    private BoxService boxService;
    @Autowired
    @Qualifier(value = "jyRecycleMaterialAutoInspectionBoxProducer")
    private DefaultJMQProducer jyRecycleMaterialAutoInspectionBoxProducer;
    @Autowired
    @Qualifier(value = "jyRecycleMaterialAutoInspectionPackageProducer")
    private DefaultJMQProducer jyRecycleMaterialAutoInspectionPackageProducer;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private JyTrustHandoverAutoInspectionCacheService cacheService;


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
    @JProfiler(jKey = "DMSWORKER.jy.JyTrustHandoverAutoInspectionServiceImpl.packageArriveCarAutoInspection",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void packageArriveCarAutoInspection(PackageArriveAutoInspectionDto paramDto) {
        if(!cacheService.lockPackageArriveCarAutoInspection(paramDto.getArriveSiteId(), paramDto.getPackageCode(), paramDto.getOperateTime().getTime())) {
            logWarn("围栏到车包裹自动验货重复消息正在处理，当前消息丢弃, paramDto={}", JsonHelper.toJson(paramDto));
            return;
        }else if(cacheService.existCachePackageArriveCarAutoInspection(paramDto.getArriveSiteId(), paramDto.getPackageCode(), paramDto.getOperateTime().getTime())) {
            logWarn("围栏到车包裹自动验货重复消息，当前消息丢弃, paramDto={}", JsonHelper.toJson(paramDto));
            return;
        }
        try {
            if (!this.isExecutable(paramDto.getFirstConsumerTime(), paramDto.getRetryIntervalMinutes())) {
                logInfo("围栏到车包裹自动验货重试，未到执行时间，继续放入重试队列，paramDto={}", JsonHelper.toJson(paramDto));
                this.sendRetryPackageArriveAutoInspectionMQ(paramDto, false);
                return;
            }
            JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity = jyBizTaskUnloadVehicleService.findByTransWorkItemCode(paramDto.getTransWorkItemCode());
            if (Objects.isNull(taskUnloadVehicleEntity)) {
                if (this.retryIsExpire(paramDto.getFirstConsumerTime(), 1)) {
                    logWarn("围栏到车包裹未找到卸车任务, 重试过期不再重试等待,数据丢弃，paramDto={}", JsonHelper.toJson(paramDto));
                } else {
                    logWarn("围栏到车包裹未找到卸车任务，无法确认是否非逐单卸做自动验货，重试等待，paramDto={}", JsonHelper.toJson(paramDto));
                    this.sendRetryPackageArriveAutoInspectionMQ(paramDto, true);
                }
                return;
            }
            if (!Objects.isNull(taskUnloadVehicleEntity.getUnloadStartTime())) {
                logInfo("围栏到车包裹自动验货，该任务【bizId:{}】已经开始人工卸车【startTime:{}】,不触发自动验货，到车包裹信息={}",
                        taskUnloadVehicleEntity.getBizId(), taskUnloadVehicleEntity.getUnloadStartTime(), JsonHelper.toJson(paramDto));
                return;
            }
            if (StringUtils.isBlank(taskUnloadVehicleEntity.getTagsSign())) {
                if (!this.retryIsExpire(paramDto.getFirstConsumerTime(), 0)) {
                    logWarn("围栏到车包裹非逐单卸未打标，无法确认是否自动验货，重试等待，paramDto={}", JsonHelper.toJson(paramDto));
                    this.sendRetryPackageArriveAutoInspectionMQ(paramDto, true);
                    return;
                }
                logWarn("围栏到车包裹非逐单卸未打标, 重试已超期，不再等待是否逐单卸标识，直接触发自动验货，paramDto={}", JsonHelper.toJson(paramDto));
            } else if (BusinessUtil.isSignY(taskUnloadVehicleEntity.getTagsSign(), JyUnloadTaskSignConstants.POSITION_2)) {
                logInfo("围栏到车包裹，该任务【bizId:{}】为逐单卸包裹，不做自动验货，paramDto={}", taskUnloadVehicleEntity.getBizId(), JsonHelper.toJson(paramDto));
                return;
            }

            //PDA实操互斥打标
            this.markTaskAutoInspection(taskUnloadVehicleEntity);

            //自动验货执行
            InspectionVO vo = this.convertArriveCarAutoInspectionVO(paramDto);
            logInfo("packageArriveCarAutoInspection:围栏到车包裹自动验货addTask={}", JsonHelper.toJson(paramDto));
            this.addInspectionTask(vo, InspectionBizSourceEnum.ELECTRONIC_FENCE);

            //save success cache
            cacheService.saveCachePackageArriveCarAutoInspection(paramDto.getArriveSiteId(), paramDto.getPackageCode(), paramDto.getOperateTime().getTime());
        }catch (Exception ex) {
            logger.error("围栏到车包裹自动验货异常，errMsg={}, 消息体={}", ex.getMessage(), JsonHelper.toJson(paramDto));
            throw new JyBizException("围栏到车包裹自动验货异常" + paramDto.getPackageCode());
        }finally {
            cacheService.unlockPackageArriveCarAutoInspection(paramDto.getArriveSiteId(), paramDto.getPackageCode(), paramDto.getOperateTime().getTime());
        }
    }

    //是否可执行  true可执行
    private boolean isExecutable(Long firstConsumerTime, Integer retryIntervalMinutes) {
        if(Objects.isNull(firstConsumerTime) || Objects.isNull(retryIntervalMinutes)) {
            return true;
        }
        Long executableTime = firstConsumerTime + retryIntervalMinutes * 60l * 1000l;
        return NumberHelper.gt(System.currentTimeMillis(), executableTime);
    }

    //围栏到车自动验货task对象组装
    private InspectionVO convertArriveCarAutoInspectionVO(PackageArriveAutoInspectionDto paramDto) {
        InspectionVO inspectionVO = new InspectionVO();
        inspectionVO.setBarCodes(Collections.singletonList(paramDto.getPackageCode()));
        inspectionVO.setSiteCode(paramDto.getArriveSiteId());
        inspectionVO.setSiteName(paramDto.getArriveSiteName());
        inspectionVO.setUserCode(-1);
        inspectionVO.setUserName("-1");
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
    private boolean retryIsExpire(Long firstConsumerTime, Integer node) {
        if(Objects.isNull(firstConsumerTime)) {
            return false;
        }
        //
        Long expireTime = 0l;
        if(Constants.NUMBER_ONE.equals(node)) {
            expireTime = firstConsumerTime + 60l * 1000l * dmsConfigManager.getUccPropertyConfiguration().getPackageArriveAutoInspectionNullTaskRetryMinutes();
        }else {
            expireTime = firstConsumerTime + 60l * 1000l * dmsConfigManager.getUccPropertyConfiguration().getPackageArriveAutoInspectionRetryMinutes();
        }
        return NumberHelper.gt(System.currentTimeMillis(), expireTime);
    }

    //发送围栏到车包裹重试等待自动验货mq
    private void sendRetryPackageArriveAutoInspectionMQ(PackageArriveAutoInspectionDto paramDto, Boolean setRetryIntervalTimeFlag) {
        if(Boolean.TRUE.equals(setRetryIntervalTimeFlag)) {
            this.setRetryIntervalTime(paramDto);
        }
        paramDto.setRetryFlag(true);
        logInfo("围栏到车包裹自动验货前置条件不符合，进入重试队列，发送重试消息{}", JsonHelper.toJson(paramDto));
        jyPackageArriveRetryAutoInspectionProducer.sendOnFailPersistent(paramDto.getPackageCode(), JsonHelper.toJson(paramDto));
    }

    private void setRetryIntervalTime(PackageArriveAutoInspectionDto paramDto) {
        if(Objects.isNull(paramDto)) {
            return;
        }
        Integer intervalMinutes = packageArriveCarAutoInspectionMqRetryIntervalMinutes;
        if(NumberHelper.gt(1, intervalMinutes)) {
            intervalMinutes = 1;
        }
        paramDto.setRetryIntervalMinutes(NumberHelper.gt0(paramDto.getRetryIntervalMinutes())
                ? paramDto.getRetryIntervalMinutes() + intervalMinutes
                : intervalMinutes);
        return;
    }

    //到车自动验货时给卸车任务打标：自动验货
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

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyTrustHandoverAutoInspectionServiceImpl.recycleMaterialEnterSiteAutoInspection",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void recycleMaterialEnterSiteAutoInspection(RecycleMaterialAutoInspectionDto param) {
        if(!cacheService.lockRecycleMaterialEnterSite(param.getOperateSiteId(), param.getMaterialCode(), param.getOperateTime())) {
            logWarn("循环物资进场存在相同消息正在处理，当前消息丢弃, paramDto={}", JsonHelper.toJson(param));
            return;
        }else if(cacheService.existCacheRecycleMaterialEnterSite(param.getOperateSiteId(), param.getMaterialCode(), param.getOperateTime())) {
            logWarn("循环物资进场重复消息，当前消息丢弃, paramDto={}", JsonHelper.toJson(param));
            return;
        }
        try {
            String boxCode = this.getBoxCodeByMaterialCode(param.getMaterialCode());
            if(StringUtils.isBlank(boxCode)) {
                logWarn("循环物资进场自动验货，根据物资编码查询箱号为空，不处理：param={}", JsonHelper.toJson(param));
                return;
            }
            Set<String> boxCodeSet = this.getGenealogyAllBoxCode(boxCode);

            //需求：监听循环物资供应商的信息获取物资编码，再找到循环物资关联的箱号，针对箱内每一个包裹记录验货节点，验货类型为电子网关验货，操作人id传-1，场地传网关对应的场地
            //todo 注意：此处场景要求按箱内包裹做自动验货，并非做自动收箱逻辑
            logInfo("物资编码{}内涉及箱号集合为{}", param.getMaterialCode(), JsonHelper.toJson(boxCodeSet));
            this.sendRecycleMaterialPackageAutoInspectionMq(param, boxCodeSet);

            //save success cache
            cacheService.saveCacheRecycleMaterialEnterSite(param.getOperateSiteId(), param.getMaterialCode(), param.getOperateTime());
        }catch (Exception ex) {
            logger.error("循环物资进场消息处理异常，errMsg={}, 消息体={}", ex.getMessage(), JsonHelper.toJson(param));
            throw new JyBizException("循环物资进场消息处理异常" + param.getMaterialCode());
        }finally {
            cacheService.unlockRecycleMaterialEnterSite(param.getOperateSiteId(), param.getMaterialCode(), param.getOperateTime());
        }
    }

    //循环物资进场触发箱维度自动验货
    private void sendRecycleMaterialPackageAutoInspectionMq(RecycleMaterialAutoInspectionDto inspectionDto, Set<String> boxCodeSet) {
        List<Message> messageList = new ArrayList<>();
        boxCodeSet.forEach(boxCode -> {
            RecycleMaterialAutoInspectionBoxDto dto = new RecycleMaterialAutoInspectionBoxDto();
            dto.setBoxCode(boxCode);
            dto.setMaterialCode(inspectionDto.getMaterialCode());
            dto.setOperateSiteId(inspectionDto.getOperateSiteId());
            dto.setOperateSiteName(inspectionDto.getOperateSiteName());
            dto.setOperateTime(inspectionDto.getOperateTime());
            dto.setSendTime(System.currentTimeMillis());
            String msgText = JsonHelper.toJson(dto);
            logInfo("循环物资进场地自动验货：箱维度消息发布, msg={}", msgText);
            messageList.add(new Message(jyRecycleMaterialAutoInspectionBoxProducer.getTopic(),msgText, boxCode));
        });
        jyRecycleMaterialAutoInspectionBoxProducer.batchSendOnFailPersistent(messageList);
    }

    //获取箱号及所有后代箱号
    private Set<String> getGenealogyAllBoxCode(String boxCode) {
        Set<String> boxCodeSet = new HashSet<>();
        boxCodeSet.add(boxCode);

        Box box = new Box();
        box.setCode(boxCode);
        List<Box> boxList = boxService.listAllDescendantsByParentBox(box);
        boxList.forEach(boxTemp -> {
            if(StringUtils.isNotBlank(boxTemp.getCode())) {
                boxCodeSet.add(boxTemp.getCode());
            }
        });
        return boxCodeSet;
    }

    //获取物资编码关联的箱号
    private String getBoxCodeByMaterialCode(String materialCode) {
        BoxMaterialRelation boxMaterialRelation = boxMaterialRelationService.getDataByMaterialCode(materialCode);
        if(!Objects.isNull(boxMaterialRelation) && StringUtils.isNotBlank(boxMaterialRelation.getBoxCode())) {
            return boxMaterialRelation.getBoxCode();
        }
        return null;
    }


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyTrustHandoverAutoInspectionServiceImpl.recycleMaterialBoxAutoInspection",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void recycleMaterialBoxAutoInspection(RecycleMaterialAutoInspectionBoxDto param) {
        if(!cacheService.lockRecycleMaterialEnterSiteBoxInspection(param.getOperateSiteId(), param.getBoxCode(), param.getOperateTime())) {
            logWarn("循环物资进场关联箱号验货存在相同消息正在处理，当前消息丢弃, paramDto={}", JsonHelper.toJson(param));
            return;
        }else if(cacheService.existCacheRecycleMaterialEnterSiteBoxInspection(param.getOperateSiteId(), param.getBoxCode(), param.getOperateTime())) {
            logWarn("循环物资进场关联箱号验货重复消息，当前消息丢弃, paramDto={}", JsonHelper.toJson(param));
            return;
        }
        try {
            List<SendDetail> sendDetailList = deliveryService.getCancelSendByBox(param.getBoxCode());
            if(CollectionUtils.isEmpty(sendDetailList)) {
                logWarn("箱内包裹自动验货，根据箱号查询箱内包裹为空，param={}", JsonHelper.toJson(param));
                return;
            }
            logInfo("recycleMaterialBoxAutoInspection:箱{}内包裹数量{}个", param.getBoxCode(), sendDetailList.size());
            List<Message> messageList = new ArrayList<>();
            sendDetailList.forEach(sendDetail -> {
                if(StringUtils.isNotBlank(sendDetail.getPackageBarcode())) {
                    RecycleMaterialAutoInspectionPackageDto packageDto = new RecycleMaterialAutoInspectionPackageDto();
                    packageDto.setPackageCode(sendDetail.getPackageBarcode());
                    packageDto.setBoxCode(param.getBoxCode());
                    packageDto.setMaterialCode(param.getMaterialCode());
                    packageDto.setOperateTime(param.getOperateTime());
                    packageDto.setOperateSiteId(param.getOperateSiteId());
                    packageDto.setOperateSiteName(param.getOperateSiteName());
                    packageDto.setSendTime(System.currentTimeMillis());
                    String msgText = JsonHelper.toJson(packageDto);
                    logInfo("循环物资进场地自动验货：包裹维度消息发布, msg={}", msgText);
                    messageList.add(new Message(jyRecycleMaterialAutoInspectionPackageProducer.getTopic(), msgText, sendDetail.getPackageBarcode()));
                }
            });
            jyRecycleMaterialAutoInspectionPackageProducer.batchSendOnFailPersistent(messageList);
            //save success cache
            cacheService.saveCacheRecycleMaterialEnterSiteBoxInspection(param.getOperateSiteId(), param.getBoxCode(), param.getOperateTime());
        }catch (Exception ex) {
            logger.error("循环物资进场关联箱号验货消息处理异常，errMsg={}, 消息体={}", ex.getMessage(), JsonHelper.toJson(param));
            throw new JyBizException("循环物资进场关联箱号验货消息处理异常" + param.getBoxCode());
        }finally {
            cacheService.unlockRecycleMaterialEnterSiteBoxInspection(param.getOperateSiteId(), param.getBoxCode(), param.getOperateTime());
        }
    }


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyTrustHandoverAutoInspectionServiceImpl.recycleMaterialPackageAutoInspection",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void recycleMaterialPackageAutoInspection(RecycleMaterialAutoInspectionPackageDto param) {
        if(!cacheService.lockRecycleMaterialEnterSitePackageInspection(param.getOperateSiteId(), param.getPackageCode(), param.getOperateTime())) {
            logWarn("循环物资进场关联箱内包裹验货存在相同消息正在处理，当前消息丢弃, paramDto={}", JsonHelper.toJson(param));
            return;
        }else if(cacheService.existCacheRecycleMaterialEnterSitePackageInspection(param.getOperateSiteId(), param.getPackageCode(), param.getOperateTime())) {
            logWarn("循环物资进场关联箱内包裹验货重复消息，当前消息丢弃, paramDto={}", JsonHelper.toJson(param));
            return;
        }
        try {
            InspectionVO inspectionVO = new InspectionVO();
            inspectionVO.setBarCodes(Collections.singletonList(param.getPackageCode()));
            inspectionVO.setSiteCode(param.getOperateSiteId());
            inspectionVO.setSiteName(param.getOperateSiteName());
            inspectionVO.setUserCode(-1);
            inspectionVO.setUserName("-1");
            inspectionVO.setOperateTime(DateHelper.formatDateTime(new Date(param.getOperateTime())));
            inspectionVO.setOperatorData(this.getOperatorData(OperatorTypeEnum.MATERIAL.getCode(), param.getMaterialCode()));
            logInfo("物资自动扫描包裹自动验货addTask={}", JsonHelper.toJson(inspectionVO));
            this.addInspectionTask(inspectionVO, InspectionBizSourceEnum.ELECTRONIC_GATEWAY);
            //save success cache
            cacheService.saveCacheRecycleMaterialEnterSitePackageInspection(param.getOperateSiteId(), param.getPackageCode(), param.getOperateTime());
        }catch (Exception ex) {
            logger.error("循环物资进场关联箱内包裹验货消息处理异常，errMsg={}, 消息体={}", ex.getMessage(), JsonHelper.toJson(param));
            throw new JyBizException("循环物资进场关联箱内包裹验货消息处理异常" + param.getBoxCode());
        }finally {
            cacheService.unlockRecycleMaterialEnterSitePackageInspection(param.getOperateSiteId(), param.getPackageCode(), param.getOperateTime());
        }
    }
}
