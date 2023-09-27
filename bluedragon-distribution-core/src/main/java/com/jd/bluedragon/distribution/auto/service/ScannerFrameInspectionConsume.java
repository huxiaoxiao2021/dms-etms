package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 龙门架验货消费接口
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameInspectionConsume")
public class ScannerFrameInspectionConsume implements ScannerFrameConsume {

    private static final Logger log = LoggerFactory.getLogger(ScannerFrameInspectionConsume.class);

    @Autowired
    private TaskService taskService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo("dmsWork.ScannerFrameInspectionConsume.onMessage");
    	Profiler.registerInfoEnd(callerInfo);
        //默认验货为正向验货
        Integer businessType = Constants.BUSSINESS_TYPE_POSITIVE;
        if(config.getWaybillBusinessType() != null){
            businessType = config.getWaybillBusinessType();
        }
        InspectionRequest inspection=new InspectionRequest();
        inspection.setUserCode(config.getOperateUserId());
        inspection.setUserName(config.getOperateUserName());
        inspection.setSiteCode(config.getCreateSiteCode());
        inspection.setSiteName(config.getCreateSiteName());
        inspection.setOperateTime(DateHelper.formatDateTime(uploadData.getScannerTime()));
        inspection.setBusinessType(businessType);
        inspection.setPackageBarOrWaybillCode(uploadData.getBarCode());
        if (uploadData.getSource() != null && uploadData.getSource() == 2) {
            inspection.setBizSource(InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION.getCode());
        } else {
            inspection.setBizSource(InspectionBizSourceEnum.AUTOMATIC_GANTRY_INSPECTION.getCode());
        }
        if (StringUtils.isNotEmpty(uploadData.getRegisterNo())){
            inspection.setMachineCode(uploadData.getRegisterNo());
        }
        /**
         * 设置操作对象信息
         */
        setOperatorData(inspection,uploadData);
        
        TaskRequest request=new TaskRequest();
        request.setBusinessType(businessType);
        request.setKeyword1(String.valueOf(config.getCreateSiteCode()));
        request.setKeyword2(uploadData.getBarCode());
        request.setType(Task.TASK_TYPE_INSPECTION);
        request.setOperateTime(DateHelper.formatDateTime(uploadData.getScannerTime()));
        request.setSiteCode(config.getCreateSiteCode());
        request.setSiteName(config.getCreateSiteName());
        request.setUserCode(config.getOperateUserId());
        request.setUserName(config.getOperateUserName());
        //request.setBody();
        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task=this.taskService.toTask(request, eachJson);
        if(log.isDebugEnabled()){
            log.debug("验货任务插入{}", JsonHelper.toJson(task));
        }
        int result= this.taskService.add(task, true);
        return result>0;
    }
    private void setOperatorData(InspectionRequest inspection,UploadData uploadData) {
    	if(inspection == null || uploadData == null) {
    		return;
    	}
    	OperatorData operatorData = uploadData.getOperatorData();
    	if(operatorData == null) {
    		operatorData = new OperatorData();
    		operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
    		operatorData.setOperatorId(inspection.getMachineCode());
    	}
    	inspection.setOperatorTypeCode(operatorData.getOperatorTypeCode());
    	inspection.setOperatorId(operatorData.getMachineCode());
    	inspection.setOperatorData(operatorData);
    }
}
