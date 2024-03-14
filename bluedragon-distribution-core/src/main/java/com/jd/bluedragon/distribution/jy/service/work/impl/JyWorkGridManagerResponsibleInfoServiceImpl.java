package com.jd.bluedragon.distribution.jy.service.work.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.distribution.jy.dao.work.JyWorkGridManagerResponsibleInfoDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerResponsibleInfo;
import com.jd.bluedragon.distribution.jy.dto.work.ViolentSortingResponsibleInfoDTO;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerResponsibleInfoService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskTypeEnum;
import com.jd.bluedragon.distribution.work.constant.ViolentSortingResponsibleStatusEnum;
import com.jd.jmq.common.exception.JMQException;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.common.utils.JsonHelper;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.common.dto.work.ResponsibleWorkTypeEnum.*;

@Slf4j
@Service("jyWorkGridManagerResponsibleInfoService")
public class JyWorkGridManagerResponsibleInfoServiceImpl implements JyWorkGridManagerResponsibleInfoService {
    @Autowired
    @Qualifier("jyWorkGridManagerResponsibleInfoDao")
    private JyWorkGridManagerResponsibleInfoDao jyWorkGridManagerResponsibleInfoDao;
    @Autowired
    private WorkGridManager workGridManager;

    @Autowired
    @Qualifier("violentSortingResponsibleInfoProducer")
    private DefaultJMQProducer violentSortingResponsibleInfoProducer;

    @Autowired
    @Qualifier("jyBizTaskWorkGridManagerService")
    private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;

    @Autowired
    @Qualifier("jyUserManager")
    private JyUserManager jyUserManager;


    @Override
    public int add(JyWorkGridManagerResponsibleInfo responsibleInfo) {
        return jyWorkGridManagerResponsibleInfoDao.add(responsibleInfo);

    }
    @Override
    public JyWorkGridManagerResponsibleInfo queryByBizId(String bizId) {
        return jyWorkGridManagerResponsibleInfoDao.queryByBizId(bizId);
    }
    
    @Override
    public ResponsibleInfo queryResponsibleInfoByBizId(String bizId){
        JyWorkGridManagerResponsibleInfo entity = queryByBizId(bizId);
        if(entity == null){
            return null;
        }
        ResponsibleInfo dto = new ResponsibleInfo();
        dto.setWorkType(entity.getWorkType());
        ResponsibleWorkTypeEnum workTypeEnum = ResponsibleWorkTypeEnum.getByCode(entity.getWorkType());
        switch (workTypeEnum){
            case FORMAL_WORKER:
                dto.setErp(entity.getErp());
                dto.setName(entity.getName());
                break;
            case OUTWORKER:
                ResponsibleSupplier supplier = new ResponsibleSupplier();
                supplier.setSupplierId(entity.getSupplierId());
                supplier.setSupplierName(entity.getSupplierName());
                dto.setSupplier(supplier);
                dto.setIdCard(entity.getIdCard());
                dto.setName(entity.getName());
                break;
            case TEMPORARY_WORKERS:
                dto.setName(entity.getName());
                dto.setIdCard(entity.getIdCard());
                JyWorkGridOwnerDto gridOwner = new JyWorkGridOwnerDto();
                gridOwner.setErp(entity.getGridOwnerErp());
                gridOwner.setName(entity.getGridOwnerErp());
                dto.setGridOwner(gridOwner);
        }
        return dto;
        
    }
    
    @Override
    public void saveTaskResponsibleInfo(JyWorkGridManagerData taskData, ResponsibleInfo responsibleInfo){
        String bizId = taskData.getBizId();
        //暴力分拣任务
        if(WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(taskData.getTaskType())){
            Integer siteCode = taskData.getSiteCode();
            String processInstanceId = taskData.getViolenceSortInfoData().getProcessInstanceId();
            violentSortingTaskSaveResponsibleInfo(bizId, responsibleInfo,siteCode, processInstanceId);
            return;
        }
        
        //其他任务
        notViolentSortingTaskSaveResponsibleInfo(bizId, taskData.getTaskRefGridKey());
    }
    
    private void violentSortingTaskSaveResponsibleInfo(String bizId, ResponsibleInfo responsibleInfo,
                                                       Integer siteCode, String processInstanceId){
        JyWorkGridManagerResponsibleInfo responsibleEntity = new JyWorkGridManagerResponsibleInfo();
        responsibleEntity.setBizId(bizId);
        responsibleEntity.setName(responsibleInfo.getName());
        responsibleEntity.setWorkType(responsibleInfo.getWorkType());
        responsibleEntity.setErp(responsibleInfo.getErp());
        responsibleEntity.setCreateTime(new Date());
        ResponsibleSupplier supplier = null;
        if((supplier = responsibleInfo.getSupplier()) != null
                && StringUtils.isNotBlank(supplier.getSupplierId())
                && StringUtils.isNotBlank(supplier.getSupplierName())){
            responsibleEntity.setSupplierId(supplier.getSupplierId());
            responsibleEntity.setSupplierName(supplier.getSupplierName());
        }

        JyWorkGridOwnerDto gridOwnerDto = responsibleInfo.getGridOwner();
        if(gridOwnerDto != null){
            responsibleEntity.setGridOwnerErp(gridOwnerDto.getErp());
        }
        responsibleEntity.setSiteCode(siteCode);
        responsibleEntity.setProcessInstanceId(processInstanceId);
        jyWorkGridManagerResponsibleInfoDao.add(responsibleEntity);
        log.info("暴力分拣任务责任人信息保存成功，bizId:{}", bizId);
    }
    
    private void notViolentSortingTaskSaveResponsibleInfo(String bizId, String refGridKey){
        JyWorkGridManagerResponsibleInfo responsibleEntity = new JyWorkGridManagerResponsibleInfo();
        responsibleEntity.setBizId(bizId);
        responsibleEntity.setWorkType(FORMAL_WORKER.getCode());
        responsibleEntity.setCreateTime(new Date());
        Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(refGridKey);
        if(workGridResult != null && workGridResult.getData() != null){
            responsibleEntity.setGridOwnerErp(workGridResult.getData().getOwnerUserErp());
            jyWorkGridManagerResponsibleInfoDao.add(responsibleEntity);
            log.info("非暴力分拣任务责任人信息保存成功，bizId:{}", bizId);
            return;
        }
        
        log.info("非暴力分拣任务责任人信息保存失败，任务未查到网格信息，bizId:{}", bizId);
    }

    /**
     * 任务超时未处理时保存任务责任信息
     * @param bizIds
     * @return
     */
    @Override
    public List<JyWorkGridManagerData> workGridManagerExpiredSaveResponsibleInfo(List<String> bizIds){
        List<JyWorkGridManagerData> taskDatas = new ArrayList<>();
        for(String bizId : bizIds){
            JyWorkGridManagerData jyWorkGridManagerData =  jyBizTaskWorkGridManagerService.queryTaskDataByBizId(bizId);
            if(jyWorkGridManagerData == null){
                log.warn("分拣任务超时保存责任信息,任务不存在或已删除，bizId:{}", bizId);
                continue;
            }
            if(!WorkTaskStatusEnum.OVER_TIME.getCode().equals(jyWorkGridManagerData.getStatus())){
                log.info("分拣任务超时保存责任信息,非超时任务，bizId:{}", bizId);
                continue;
            }
            //非暴力分拣任务 保存网格组长
            if(!WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(jyWorkGridManagerData.getTaskType())){
                log.info("暴力分拣任务超时上传判责系统,非暴力分拣任务不处理，bizId:{}", bizId);
                notViolentSortingTaskSaveResponsibleInfo(bizId, jyWorkGridManagerData.getTaskRefGridKey());
                continue;
            }
            
            /** 暴力分拣任务 **/
            //暴力分拣信息
            ViolenceSortInfoData violenceSortInfoData = jyWorkGridManagerData.getViolenceSortInfoData();
            ResponsibleInfo responsibleInfo = null;
            //查询任务网格
            Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(jyWorkGridManagerData.getTaskRefGridKey());
            WorkGrid workGrid;
            String ownerUserErp = null;
            //默认网格组长为责任人
            if(workGridResult != null && (workGrid = workGridResult.getData()) != null 
                    && StringUtils.isNotBlank(ownerUserErp = workGrid.getOwnerUserErp())){
                log.info("暴力分拣任务超时定责，使用网格组长为责任人，bizId:{}, TaskRefGridKey:{},ownerUserErp:{}",
                        bizId, jyWorkGridManagerData.getTaskRefGridKey(), ownerUserErp);
                responsibleInfo = new ResponsibleInfo();
                JyWorkGridOwnerDto jyWorkGridOwnerDto = new JyWorkGridOwnerDto();
                jyWorkGridOwnerDto.setErp(ownerUserErp);
                responsibleInfo.setGridOwner(jyWorkGridOwnerDto);
                
            }
            
            //网格组长为空，将使用场地负责人为责任人
            if(StringUtils.isBlank(ownerUserErp)){
                JyUserDto jyUserDto = jyUserManager.querySiteLeader(jyWorkGridManagerData.getSiteCode());
                if(jyUserDto != null){
                    ownerUserErp = jyUserDto.getUserErp();
                    log.info("暴力分拣任务超时定责，未查到网格组长，使用场地负责人为责任人，bizId:{}, TaskRefGridKey:{},ownerUserErp:{}",
                            bizId, jyWorkGridManagerData.getTaskRefGridKey(), ownerUserErp);
                    responsibleInfo = new ResponsibleInfo();
                    responsibleInfo.setErp(ownerUserErp);
                    responsibleInfo.setName(jyUserDto.getUserName());
                }else {
                    log.info("暴力分拣任务超时定责，未查到网格组长，未查到场地负责人，bizId:{}, TaskRefGridKey:{},ownerUserErp:{}",
                            bizId, jyWorkGridManagerData.getTaskRefGridKey(), ownerUserErp);
                }
            }
            
            if(responsibleInfo != null){
                jyWorkGridManagerData.setResponsibleInfo(responsibleInfo);
                responsibleInfo.setWorkType(FORMAL_WORKER.getCode());
                violentSortingTaskSaveResponsibleInfo(bizId, responsibleInfo, jyWorkGridManagerData.getSiteCode(), 
                        violenceSortInfoData.getProcessInstanceId());
            }
            taskDatas.add(jyWorkGridManagerData);
        }
        return taskDatas;
    }

    /**
     * 给判责系统推送暴力分拣定责信息
     * @param oldData
     * @param responsibleInfo
     */
    @Override
    public void sendViolentSortingResponsibleInfo(JyWorkGridManagerData oldData, ResponsibleInfo responsibleInfo){
        //非暴力分拣任务不处理 
        if(!WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(oldData.getTaskType())){
            return;
        }
        ViolentSortingResponsibleInfoDTO info = new ViolentSortingResponsibleInfoDTO();
        info.setResponsibleType(responsibleInfo.getWorkType());
        ResponsibleWorkTypeEnum workTypeEnum = ResponsibleWorkTypeEnum.getByCode(responsibleInfo.getWorkType());
        switch (workTypeEnum){
            case FORMAL_WORKER:
                info.setResponsibleCode(responsibleInfo.getErp());
                break;
            case OUTWORKER:
                info.setResponsibleCode(responsibleInfo.getSupplier().getSupplierId());
                break;
            case TEMPORARY_WORKERS:
                info.setResponsibleCode(responsibleInfo.getErp());

        }
        info.setSiteCode(oldData.getSiteCode());
        ViolenceSortInfoData violenceSortInfoData = oldData.getViolenceSortInfoData();
        info.setId(violenceSortInfoData.getId());
        info.setStatus(ViolentSortingResponsibleStatusEnum.DETERMINED.getCode());
        info.setAdvanceErp(oldData.getHandlerErp());
        info.setProcessInstanceId(violenceSortInfoData.getProcessInstanceId());
        String key = String.valueOf(violenceSortInfoData.getId());
        try {
            violentSortingResponsibleInfoProducer.send(key, JsonHelper.toJSONString(info));
        } catch (JMQException e) {
            log.error("暴力分拣责任信息上报判责系统，发送jmq异常,bizId:{}", oldData.getBizId(),e);
            throw new RuntimeException(e);
        }
    } 
    @Override
    public JdCResponse<Boolean> checkResponsibleInfo(JyWorkGridManagerData taskData, JdCResponse<Boolean> result){
        String bizId = taskData.getBizId();
        //非暴力分拣任务类型 不用校验
        if(!WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(taskData.getTaskType())){
            return result;
        }
        ResponsibleInfo responsibleInfo = taskData.getResponsibleInfo();
        if(responsibleInfo == null){
            result.toFail("暴力分拣任务，责任人信息不能为空");
            return result;
        }
        Integer workType = null;
        if((workType = responsibleInfo.getWorkType()) == null){
            result.toFail("责任人工种类型不能为空");
            return result;
        }
        if(ResponsibleWorkTypeEnum.getByCode(workType) == null){
            result.toFail("责任人工种类型值非法");
            log.info("责任人工种类型值非法,bizId:{},workType:{}", bizId, responsibleInfo.getWorkType());
            return result;
        }
        //正式工
        if(FORMAL_WORKER.getCode().equals(workType) 
                && (StringUtils.isBlank(responsibleInfo.getErp()) || StringUtils.isBlank(responsibleInfo.getName()))){
            result.toFail("请选择自有员工责任人");
            log.info("责任人工种为自有，erp或名称为空,bizId:{},erp:{},name:{}", bizId, responsibleInfo.getErp(),
                    responsibleInfo.getName());
            return result;
        }
        //外包工 身份证或名称为空
        if(OUTWORKER.getCode().equals(workType)
                && (StringUtils.isBlank(responsibleInfo.getIdCard()) || StringUtils.isBlank(responsibleInfo.getName()))){
            result.toFail("请选择外包工责任人");
            log.info("责任人工种为外包，idCard或名称为空,bizId:{},idCard:{},name:{}", bizId, responsibleInfo.getIdCard(),
                    responsibleInfo.getName());
            return result;
        }
        //外包工 外包商信息为空
        ResponsibleSupplier supplier = null;
        if(OUTWORKER.getCode().equals(workType)
                && ((supplier = responsibleInfo.getSupplier()) == null 
                || StringUtils.isBlank(supplier.getSupplierId()) 
                || StringUtils.isBlank(supplier.getSupplierName()))){
            result.toFail("请选择外包工对应的外包商");
            log.info("责任人工种为外包，外包商信息为空，,bizId:{},supplier:{}", bizId, JsonHelper.toJSONString(supplier));
            return result;
        }
        
        //临时工 身份信息
        if(TEMPORARY_WORKERS.getCode().equals(workType) 
                && (StringUtils.isBlank(responsibleInfo.getIdCard()))){
            result.toFail("请选择临时工责任人");
            log.info("责任人工种为临时工，临时工身份证或姓名为空,bizId:{},idCard:{},name:{}", bizId, responsibleInfo.getIdCard(), 
                    responsibleInfo.getName());
            return result;
        }
        //临时工 对应的网格组长
        if(TEMPORARY_WORKERS.getCode().equals(workType)
                && (responsibleInfo.getGridOwner() == null || StringUtils.isBlank(responsibleInfo.getGridOwner().getErp()))){
            result.toFail("请选择临时工时网格组长不能为空");
            log.info("责任人工种为临时工，临时工身份证或姓名为空,bizId:{},idCard:{},name:{}", bizId, responsibleInfo.getIdCard(),
                    responsibleInfo.getName());
            return result;
        }
        return result;
    }

    /**
     * 超时任务设置责任人为网格组长
     * @param jyWorkGridManagerDatas
     */
    @Override
    public void workGridManagerExpiredSendMq(List<JyWorkGridManagerData> jyWorkGridManagerDatas) {
        
        for(JyWorkGridManagerData taskData : jyWorkGridManagerDatas){
            ViolenceSortInfoData violenceSortInfoData = taskData.getViolenceSortInfoData();
            ViolentSortingResponsibleInfoDTO info = new ViolentSortingResponsibleInfoDTO();
            info.setId(violenceSortInfoData.getId());
            info.setSiteCode(taskData.getSiteCode());
            info.setProcessInstanceId(violenceSortInfoData.getProcessInstanceId());
            //定责信息
            ResponsibleInfo responsibleInfo = taskData.getResponsibleInfo();
            String key = String.valueOf(violenceSortInfoData.getId());

            if(responsibleInfo == null){
                info.setStatus(ViolentSortingResponsibleStatusEnum.NOT_FOUND.getCode());
                log.info("暴力分拣超时任务，未查到网格信息或网格组长为空，bizId:{},workgridKey:{}", taskData.getBizId(), 
                        taskData.getTaskRefGridKey());
                try {
                    violentSortingResponsibleInfoProducer.send(key, JsonHelper.toJSONString(info));
                } catch (JMQException e) {
                    log.error("暴力分拣超时任务-无网格组长,发送消息异常", e);
                    throw new RuntimeException(e);
                }
                continue;
            }
            
            info.setResponsibleType(FORMAL_WORKER.getCode());
            String responseibleCode = null;
            if(responsibleInfo.getGridOwner() != null){
                responseibleCode = responsibleInfo.getGridOwner().getErp();
            }else {
                responseibleCode = responsibleInfo.getErp();
            }
            info.setResponsibleCode(responseibleCode);
            info.setAdvanceErp(taskData.getHandlerErp());
            info.setStatus(ViolentSortingResponsibleStatusEnum.DETERMINED.getCode());
            try {
                violentSortingResponsibleInfoProducer.send(key, JsonHelper.toJSONString(info));
            } catch (JMQException e) {
                log.error("暴力分拣超时任务-默认网格组长,发送消息异常", e);
                throw new RuntimeException(e);
            }
        }
    }
    

}
