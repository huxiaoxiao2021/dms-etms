package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskExtendInitDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;
import com.jd.bluedragon.distribution.jy.service.picking.JyBizTaskPickingGoodService;
import com.jd.bluedragon.distribution.jy.service.picking.JyBizTaskPickingGoodTransactionManager;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingTaskAggsCacheService;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public void setPickingGoodDetailInitService(PickingGoodDetailInitService pickingGoodDetailInitService) {
        super.setPickingGoodDetailInitService(pickingGoodDetailInitService);
    }

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
        //调度任务初始化
        //todo zcf
        createUnSealScheduleTask(null);
        return true;
    }

    //todo zcf 看一下调度任务失败的逻辑， 和提货任务生成不在一个事务，考虑是否继续异步拆分，或者保证幂等
    private boolean createUnSealScheduleTask(Object dto){
//        JyScheduleTaskReq req = new JyScheduleTaskReq();
//        req.setBizId(dto.getBizId());
//        req.setTaskType(JyScheduleTaskTypeEnum.UNSEAL.getCode());
//        req.setOpeUser(dto.getOperateUserErp());
//        req.setOpeUserName(dto.getOperateUserName());
//        req.setOpeTime(dto.getOperateTime());
//        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.createScheduleTask(req);
//        return jyScheduleTaskResp != null;
        return true;
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
        entity.setUpdateTime(entity.getUpdateTime());
        entity.setYn(Constants.NUMBER_ONE);

        return entity;
    }
    //构建任务附属信息
    private List<JyBizTaskPickingGoodSubsidiaryEntity> convertTaskSubsidiaryEntityList(JyBizTaskPickingGoodEntity taskEntity, List<PickingGoodTaskExtendInitDto> extendDtoList) {
        List<JyBizTaskPickingGoodSubsidiaryEntity> entityList = new ArrayList<>();
        extendDtoList.forEach(subsidiaryEntity -> {
            JyBizTaskPickingGoodSubsidiaryEntity entity = new JyBizTaskPickingGoodSubsidiaryEntity();
            entity.setBizId(taskEntity.getBizId());
            entity.setBusinessNumber(taskEntity.getBusinessNumber());
            entity.setSendCode(subsidiaryEntity.getBatchCode());
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
        //根据场地类型获取待提明细初始化服务实例
        Integer startSiteType = 64; //上游场地类型
        PickingGoodTaskDetailInitServiceEnum detailInitServiceEnum = PickingGoodTaskDetailInitServiceEnum.getEnumBySource(startSiteType);
        if(!Objects.isNull(detailInitServiceEnum)) {
            PickingGoodDetailInitService pickingGoodDetailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(detailInitServiceEnum.getTargetCode());
            if(!Objects.isNull(pickingGoodDetailInitService)) {
                this.setPickingGoodDetailInitService(pickingGoodDetailInitService);
            }else {
                logWarn("航空提货计划消费，未查到待提明细初始化服务，initDto={}, 上游场地类型为{}", JsonHelper.toJson(initDto), startSiteType);
            }
        }

        pickingGoodDetailInitService.pickingGoodDetailInit(null);
        return true;
    }

    @Override
    protected boolean initDetailSwitch(PickingGoodTaskInitDto pickingGoodTaskInitDto) {
        //todo zcf  根据到达时间做初始化
        return super.initDetailSwitch(pickingGoodTaskInitDto);
    }
}
