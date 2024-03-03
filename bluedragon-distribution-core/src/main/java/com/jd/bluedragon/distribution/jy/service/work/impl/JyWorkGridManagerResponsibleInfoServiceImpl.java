package com.jd.bluedragon.distribution.jy.service.work.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.*;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.distribution.jy.dao.work.JyWorkGridManagerResponsibleInfoDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerResponsibleInfo;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerResponsibleInfoService;
import com.jd.bluedragon.distribution.jy.work.enums.ViolenceSortTaskCaseEnum;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskTypeEnum;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.common.utils.JsonHelper;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    @Override
    public int add(JyWorkGridManagerResponsibleInfo responsibleInfo) {
        return jyWorkGridManagerResponsibleInfoDao.add(responsibleInfo);

    }
    @Override
    public JyWorkGridManagerResponsibleInfo queryByBizId(String bizId) {
        return jyWorkGridManagerResponsibleInfoDao.queryByBizId(bizId);
    }
    
    @Override
    public void saveTaskResponsibleInfo(JyWorkGridManagerData taskData){
        List<JyWorkGridManagerCaseData> caseList = taskData.getCaseList();
        JyWorkGridManagerResponsibleInfo responsibleEntity = new JyWorkGridManagerResponsibleInfo();
        String bizId = taskData.getBizId();
        //暴力分拣任务
        if(WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(taskData.getTaskType())){
            //用户确认为暴力分拣
            if(CollectionUtils.isNotEmpty(caseList) 
                    && ViolenceSortTaskCaseEnum.YES.getCaseCode().equals(caseList.get(0).getCaseCode())){
                ResponsibleInfo responsibleInfo = taskData.getResponsibleInfo();
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
                jyWorkGridManagerResponsibleInfoDao.add(responsibleEntity);
                log.info("暴力分拣任务责任人信息保存成功，bizId:{}", bizId);
                return;
            }
            log.info("暴力分拣任务-现场确认为非暴力分拣无需保存责任人，bizId:{}", bizId);
            return;
            
        }
        
        //其他任务
        responsibleEntity.setBizId(bizId);
        responsibleEntity.setWorkType(FORMAL_WORKER.getCode());
        responsibleEntity.setCreateTime(new Date());
        Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(taskData.getTaskRefGridKey());
        if(workGridResult != null && workGridResult.getData() != null){
            responsibleEntity.setGridOwnerErp(workGridResult.getData().getOwnerUserErp());
            jyWorkGridManagerResponsibleInfoDao.add(responsibleEntity);
            log.info("非暴力分拣任务责任人信息保存成功，bizId:{}", bizId);
            return;
        }
        log.info("非暴力分拣任务责任人信息保存失败，任务未查到网格信息，bizId:{}", bizId);
    }
    @Override
    public JdCResponse<Boolean> checkResponsibleInfo(JyWorkGridManagerData taskData, JdCResponse<Boolean> result){
        String bizId = taskData.getBizId();
        //非暴力分拣任务类型 不用校验
        if(!WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(taskData.getTaskType())){
            return result;
        }
        List<JyWorkGridManagerCaseData> caseList = taskData.getCaseList();
        //现场确认为非暴力分拣任务
        if(CollectionUtils.isEmpty(caseList) 
                || !ViolenceSortTaskCaseEnum.YES.getCaseCode().equals(caseList.get(0).getTaskCode())){
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
                && (StringUtils.isBlank(responsibleInfo.getIdCard()) || StringUtils.isBlank(responsibleInfo.getName()))){
            result.toFail("请选择临时工责任人");
            log.info("责任人工种为临时工，临时工身份证或姓名为空,bizId:{},idCard:{},name:{}", bizId, responsibleInfo.getIdCard(), 
                    responsibleInfo.getName());
            return result;
        }
        //临时工 对应的网格组长
        if(TEMPORARY_WORKERS.getCode().equals(workType)
                && (responsibleInfo.getGridOwner() == null || StringUtils.isBlank(responsibleInfo.getGridOwner() .getErp()))){
            result.toFail("请选择临时工责任人");
            log.info("责任人工种为临时工，临时工身份证或姓名为空,bizId:{},idCard:{},name:{}", bizId, responsibleInfo.getIdCard(),
                    responsibleInfo.getName());
            return result;
        }
        return result;
    }
    
}
