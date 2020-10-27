package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
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
}
