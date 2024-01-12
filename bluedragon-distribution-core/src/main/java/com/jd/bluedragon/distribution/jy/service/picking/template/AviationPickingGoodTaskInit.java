package com.jd.bluedragon.distribution.jy.service.picking.template;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskExtendInitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;
import com.jd.bluedragon.distribution.jy.service.picking.JyBizTaskPickingGoodService;
import com.jd.bluedragon.distribution.jy.service.picking.JyBizTaskPickingGoodTransactionManager;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingTaskAggsCacheService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 航空提货任务服务
 * @Author zhengchengfa
 * @Date 2023/12/7 21:35
 * @Description
 */
@Service
public class AviationPickingGoodTaskInit extends PickingGoodTaskInit {
    private static final Logger log = LoggerFactory.getLogger(AviationPickingGoodTaskInit.class);

    @Autowired
    private JyPickingTaskAggsCacheService jyPickingTaskAggsCacheService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JyBizTaskPickingGoodService jyBizTaskPickingGoodService;
    @Autowired
    private JyBizTaskPickingGoodTransactionManager transactionManager;
    @Autowired
    private VosManager vosManager;
    @Autowired
    @Qualifier(value = "jyPickingGoodDetailInitProducer")
    private DefaultJMQProducer jyPickingGoodDetailInitProducer;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;



    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }

    @Override
    protected boolean generatePickingGoodTask(PickingGoodTaskInitDto initDto) {
        JyBizTaskPickingGoodEntity pickingGoodEntity = jyBizTaskPickingGoodService.findLatestTaskByBusinessNumber(initDto.getBusinessNumber());
        if(!Objects.isNull(pickingGoodEntity)) {
            if(!Objects.isNull(pickingGoodEntity.getNodeRealArriveTime())) {
                logWarn("空铁提货计划消费生成提货任务，根据流水号%s查找提货任务已经实际落地，落地时间为【%s|bizId=%s】，消息不在消费，msg={}",
                        initDto.getBusinessNumber(), pickingGoodEntity.getNodeRealArriveTime(), pickingGoodEntity.getBizId(), JsonHelper.toJson(initDto));
                return true;
            }else {
                //历史数据删除
                transactionManager.deleteByBusinessNumber(initDto);
            }
        }
        //
        List<PickingGoodTaskExtendInitDto> extendInitDtoList = initDto.getExtendInitDtoList();
        //按批次流向分组,每个流向组装一条待提任务
        Map<String, List<PickingGoodTaskExtendInitDto>> extendListMap =  extendInitDtoList.stream().collect(Collectors.groupingBy(PickingGoodTaskExtendInitDto::getEndSiteCode));
        //待提场地存在性强校验
        Map<String, BaseStaffSiteOrgDto> endSiteMap = new HashMap<>();
        extendListMap.keySet().forEach(dmsCode -> {
            BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteByDmsCode(dmsCode);
            if(Objects.isNull(siteInfo) || Objects.isNull(siteInfo.getSiteCode())) {
                log.error("根据目的场地编码{}从基础资料查询提货场地信息为空,参数={}, 基础资料返回={}", dmsCode, JsonHelper.toJson(initDto), JsonHelper.toJson(siteInfo));
                throw new JyBizException(String.format("根据目的场地编码%s从基础资料查询提货场地信息为空,空铁业务主键为%s", dmsCode, initDto.getBusinessNumber()));
            }
            endSiteMap.put(dmsCode, siteInfo);
        });
        //组装任务数据：K-提货任务，V-任务附属信息list
        Map<JyBizTaskPickingGoodEntity,  List<JyBizTaskPickingGoodSubsidiaryEntity>> pickingTaskMap = new HashMap<>();
        extendListMap.forEach((dmsCode, extendDtoList) -> {
            BaseStaffSiteOrgDto endSite = endSiteMap.get(dmsCode);
            JyBizTaskPickingGoodEntity taskEntity = this.convertJyBizTaskPickingGoodEntity(initDto, endSite);
            List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList = this.convertTaskSubsidiaryEntityList(taskEntity, extendDtoList);
            pickingTaskMap.put(taskEntity, subsidiaryEntityList);
        });
        //批次插入
        List<JyBizTaskPickingGoodEntity> taskEntityList = pickingTaskMap.keySet().stream().collect(Collectors.toList());
        List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList = pickingTaskMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        transactionManager.batchInsertPickingGoodTask(taskEntityList, subsidiaryEntityList);
        super.setTaskMap(pickingTaskMap);
        return true;
    }



    /**
     * 通过封车编码获取封车信息
     * @param sealCarCode
     * @return
     */
    private SealCarDto findSealCarInfoBySealCarCodeOfTms(String sealCarCode){
        logInfo("AviationPickingGoodTaskInit获取封车信息开始 {}",sealCarCode);
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        logInfo("AviationPickingGoodTaskInit获取封车信息返回数据 {},{}",sealCarCode,JsonHelper.toJson(sealCarDtoCommonDto));
        if(sealCarDtoCommonDto == null || Constants.RESULT_SUCCESS != sealCarDtoCommonDto.getCode()){
            return null;
        }
        return sealCarDtoCommonDto.getData();
    }




    //构建任务
    private JyBizTaskPickingGoodEntity convertJyBizTaskPickingGoodEntity(PickingGoodTaskInitDto initDto, BaseStaffSiteOrgDto endSite) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        String bizId = jyBizTaskPickingGoodService.genPickingGoodTaskBizId(false);
        entity.setBizId(bizId);
        entity.setNextSiteId(endSite.getSiteCode().longValue());
        entity.setBusinessNumber(initDto.getBusinessNumber());
        entity.setServiceNumber(initDto.getServiceNumber());
        entity.setBeginNodeCode(initDto.getBeginNodeCode());
        entity.setBeginNodeName(initDto.getBeginNodeName());
        entity.setEndNodeCode(initDto.getEndNodeCode());
        entity.setEndNodeName(initDto.getEndNodeName());
        entity.setNodePlanStartTime(initDto.getNodePlanStartTime());
        entity.setNodePlanArriveTime(initDto.getNodePlanArriveTime());
        entity.setNodeRealStartTime(initDto.getNodeRealStartTime());
        entity.setNodeRealArriveTime(initDto.getNodeRealArriveTime());
        entity.setOperateNodeType(initDto.getOperateNodeType());
        entity.setCargoNumber(initDto.getCargoNumber());
        entity.setCargoWeight(initDto.getCargoWeight());
        entity.setStatus(PickingGoodStatusEnum.TO_PICKING.getCode());
        entity.setTaskType(initDto.getTaskType());
        entity.setManualCreatedFlag(Constants.NUMBER_ZERO);
        entity.setCreateUserErp(initDto.getCreateUserErp());
        entity.setCreateUserName(initDto.getCreateUserName());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(entity.getCreateTime());
        entity.setYn(Constants.NUMBER_ONE);
        entity.setIntercept(Constants.NUMBER_ZERO);
        return entity;
    }
    //构建任务附属信息
    private List<JyBizTaskPickingGoodSubsidiaryEntity> convertTaskSubsidiaryEntityList(JyBizTaskPickingGoodEntity taskEntity, List<PickingGoodTaskExtendInitDto> subsidiaryEntityList) {
        List<JyBizTaskPickingGoodSubsidiaryEntity> entityList = new ArrayList<>();
        subsidiaryEntityList.forEach(subsidiaryEntity -> {
            JyBizTaskPickingGoodSubsidiaryEntity entity = new JyBizTaskPickingGoodSubsidiaryEntity();
            entity.setBizId(taskEntity.getBizId());
            entity.setBusinessNumber(taskEntity.getBusinessNumber());
            entity.setSendCode(subsidiaryEntity.getBatchCode());
            entity.setSealCarCode(subsidiaryEntity.getSealCarCode());
            BaseStaffSiteOrgDto siteInfo = null;
            try{
                siteInfo = baseMajorManager.getBaseSiteByDmsCode(subsidiaryEntity.getStartSiteCode());
            }catch (Exception e) {
                log.warn("【附属字段不做关注】提货计划根据编码{}取上游场地信息异常，bizId={},提货流水号={}，批次号={}",
                        subsidiaryEntity.getStartSiteCode(), taskEntity.getBizId(), taskEntity.getBusinessNumber(), subsidiaryEntity.getBatchCode());
            }finally {
                if(Objects.isNull(siteInfo) || Objects.isNull(siteInfo.getSiteCode())) {
                    log.warn("【附属字段不做关注】提货计划根据编码{}取上游场地信息为空，bizId={},提货流水号={}，批次号={}",
                            subsidiaryEntity.getStartSiteCode(), taskEntity.getBizId(), taskEntity.getBusinessNumber(), subsidiaryEntity.getBatchCode());
                }
            }
            if(!Objects.isNull(siteInfo) && !Objects.isNull(siteInfo.getSiteCode())) {
                entity.setStartSiteId(siteInfo.getSiteCode().longValue());
            }
            entity.setStartSiteCode(subsidiaryEntity.getStartSiteCode());
            entity.setStartSiteName(subsidiaryEntity.getStartSiteName());
            entity.setEndSiteId(taskEntity.getNextSiteId());
            entity.setEndSiteCode(subsidiaryEntity.getEndSiteCode());
            entity.setEndSiteName(subsidiaryEntity.getEndSiteName());
            entity.setTransportCode(subsidiaryEntity.getTransportCode());
            entity.setCreateUserErp(taskEntity.getCreateUserErp());
            entity.setCreateUserName(taskEntity.getCreateUserName());
            entity.setCreateTime(new Date());
            entity.setUpdateTime(entity.getCreateTime());
            entity.setYn(Constants.NUMBER_ONE);
            entityList.add(entity);
        });
        return entityList;
    }



    @Override
    protected boolean pickingGoodDetailInit(PickingGoodTaskInitDto initDto) {
        Map<JyBizTaskPickingGoodEntity, List<JyBizTaskPickingGoodSubsidiaryEntity>> taskMap = super.getTaskMap();
        //K-sealCarCode, V-siteType
        Map<String, BaseStaffSiteOrgDto> scLastSiteType = new HashMap<>();
        taskMap.forEach((taskEntity, subsidiaryEntityList) -> {
            subsidiaryEntityList.forEach(subsidiaryEntity -> {
                if(!Objects.isNull(scLastSiteType.get(subsidiaryEntity.getSealCarCode()))) {
                    SealCarDto sealCarDto = this.findSealCarInfoBySealCarCodeOfTms(subsidiaryEntity.getSealCarCode());
                    if(!Objects.isNull(sealCarDto) && !Objects.isNull(sealCarDto.getStartSiteId())) {
                        BaseStaffSiteOrgDto siteDto = baseMajorManager.getBaseSiteBySiteId(sealCarDto.getStartSiteId());
                        if(!Objects.isNull(siteDto)) {
                            scLastSiteType.put(subsidiaryEntity.getSealCarCode(), siteDto);
                        }
                        if(!Objects.isNull(siteDto) && !Objects.isNull(siteDto.getSiteType())) {
                            logWarn("空铁提货计划【{}】待提明细初始化，根据封车编码{}查询上游封车场地{}类型为空，siteInfo={}", initDto.getBusinessNumber(), subsidiaryEntity.getSealCarCode(), sealCarDto.getStartSiteId(), JsonHelper.toJson(siteDto));
                        }
                    }else {
                        logWarn("空铁提货计划【{}】待提明细初始化，根据封车编码{}查询封车封车场地为空，sealInfo={}", initDto.getBusinessNumber(), subsidiaryEntity.getSealCarCode(), JsonHelper.toJson(sealCarDto));
                    }

                }
            });
        });

        Map<String, PickingGoodTaskDetailInitDto> otherToDmsInitMap = new HashMap<>();
        Map<String, PickingGoodTaskDetailInitDto> dmsToDmsInitMap = new HashMap<>();

        taskMap.forEach((taskEntity, subsidiaryEntityList) -> {
            subsidiaryEntityList.forEach(subsidiaryEntity -> {
                Integer siteType = scLastSiteType.get(subsidiaryEntity.getSealCarCode()).getSiteType();
                if(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getSource().equals(siteType)) {
                    if(Objects.isNull(dmsToDmsInitMap.get(subsidiaryEntity.getSendCode()))) {
                        PickingGoodTaskDetailInitDto detailInitDto = new PickingGoodTaskDetailInitDto();
                        detailInitDto.setStartSiteId(scLastSiteType.get(subsidiaryEntity.getSealCarCode()).getSiteCode().longValue());
                        detailInitDto.setStartSiteType(siteType);
                        detailInitDto.setTaskType(initDto.getTaskType());
                        detailInitDto.setBusinessNumber(initDto.getBusinessNumber());
                        detailInitDto.setServiceNumber(initDto.getServiceNumber());
                        detailInitDto.setBizId(taskEntity.getBizId());
                        detailInitDto.setPickingSiteId(taskEntity.getNextSiteId());
                        detailInitDto.setPickingNodeCode(taskEntity.getEndNodeCode());
                        //上游分拣中心发货按批次走send_d做明细初始化
                        detailInitDto.setBatchCode(subsidiaryEntity.getSendCode());
                        detailInitDto.setSealCarCode(subsidiaryEntity.getSealCarCode());
                        dmsToDmsInitMap.put(subsidiaryEntity.getSendCode(), detailInitDto);
                    }
                }else {
                    if(Objects.isNull(otherToDmsInitMap.get(subsidiaryEntity.getSealCarCode()))) {
                        PickingGoodTaskDetailInitDto detailInitDto = new PickingGoodTaskDetailInitDto();
                        detailInitDto.setTaskType(initDto.getTaskType());
                        detailInitDto.setBusinessNumber(initDto.getBusinessNumber());
                        detailInitDto.setServiceNumber(initDto.getServiceNumber());
                        detailInitDto.setBizId(taskEntity.getBizId());
                        detailInitDto.setPickingSiteId(taskEntity.getNextSiteId());
                        detailInitDto.setPickingNodeCode(taskEntity.getEndNodeCode());
                        //上游非分拣中心发货按sealCarCode走运输封车数据做明细初始化
                        detailInitDto.setSealCarCode(subsidiaryEntity.getSealCarCode());
                        detailInitDto.setBatchCode(subsidiaryEntity.getSendCode());

                        otherToDmsInitMap.put(subsidiaryEntity.getSealCarCode(), detailInitDto);
                    }
                }
            });
        });

        if(!CollectionUtils.isEmpty(otherToDmsInitMap.values())) {
//            PickingGoodDetailInitService detailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getTargetCode());
            this.sendPickingGoodDetailInitMq(otherToDmsInitMap.values(), false);
        }

        if(!CollectionUtils.isEmpty(dmsToDmsInitMap.values())) {
//            PickingGoodDetailInitService detailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING.getTargetCode());
            this.sendPickingGoodDetailInitMq(dmsToDmsInitMap.values(), true);
        }
        return true;
    }

    public boolean sendPickingGoodDetailInitMq(Collection<PickingGoodTaskDetailInitDto> values, boolean dmsToDmsFlag) {
        if(Objects.isNull(values)) {
            logWarn("sendPickingGoodDetailInitMq：参数为空不处理");
            return true;
        }
        List<Message> messageList = new ArrayList<>();
        values.forEach(detailInit -> {
            String msgText = JsonUtils.toJSONString(detailInit);
            String businessId = detailInit.getBatchCode();
            logInfo("待提明细初始化，businessId={},param={}", businessId, JsonHelper.toJson(detailInit));
            messageList.add(new Message(jyPickingGoodDetailInitProducer.getTopic(), msgText, businessId));

        });
        jyPickingGoodDetailInitProducer.batchSendOnFailPersistent(messageList);
        return false;
    }

    @Override
    protected boolean initDetailSwitch(PickingGoodTaskInitDto pickingGoodTaskInitDto) {
        if(Objects.isNull(pickingGoodTaskInitDto.getNodeRealArriveTime())) {
            logInfo("空铁提货计划【{}】没有落地时间，不做待提明细初始化,下发时间={}", pickingGoodTaskInitDto.getBusinessNumber(), pickingGoodTaskInitDto.getOperateTime());
            return false;
        }
        return true;
    }

}
