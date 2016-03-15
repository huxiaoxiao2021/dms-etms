package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
/**
 * 龙门架验货消费接口
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameInspectionConsume")
public class ScannerFrameInspectionConsume implements ScannerFrameConsume {

    private static final Log logger= LogFactory.getLog(ScannerFrameInspectionConsume.class);

    @Autowired
    private TaskService taskService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {

        InspectionRequest inspection=new InspectionRequest();
        inspection.setUserCode(config.getOperateUserId());
        inspection.setUserName(config.getOperateUserName());
        inspection.setSiteCode(config.getCreateSiteCode());
        inspection.setSiteName(config.getCreateSiteName());
        inspection.setOperateTime(DateHelper.formatDateTime(uploadData.getScannerTime()));
        inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        inspection.setPackageBarOrWaybillCode(uploadData.getBarCode());

        TaskRequest request=new TaskRequest();
        request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
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
                + JsonHelper.toJson(JsonHelper.toJson(inspection))
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task=this.taskService.toTask(request, eachJson);
        if(logger.isDebugEnabled()){
            logger.debug(MessageFormat.format("验货任务插入{0}", JsonHelper.toJson(task)));
        }
        int result= this.taskService.add(task, true);
        return result>0;
    }
}
