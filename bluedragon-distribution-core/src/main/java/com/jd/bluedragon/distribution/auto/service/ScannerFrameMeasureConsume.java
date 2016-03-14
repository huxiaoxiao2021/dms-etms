package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 龙门架测量体积消费
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameMeasureConsume")
public class ScannerFrameMeasureConsume implements ScannerFrameConsume {

    private static final Log logger= LogFactory.getLog(ScannerFrameMeasureConsume.class);

    @Autowired
    private TaskService taskService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
        if(!SerialRuleUtil.isMatchAllPackageNo(uploadData.getBarCode()))
            return true;
        OpeEntity opeEntity = new OpeEntity();
        opeEntity.setOpeType(1);//分拣中心称重
        opeEntity.setWaybillCode(SerialRuleUtil.getWaybillCode(uploadData.getBarCode()));
        opeEntity.setOpeDetails(new ArrayList<OpeObject>());

        OpeObject obj = new OpeObject();
        obj.setOpeSiteId(config.getSiteCode());
        obj.setOpeSiteName(config.getSiteName());
        obj.setpWidth(uploadData.getWidth());
        obj.setpLength(uploadData.getLength());
        obj.setpHigh(uploadData.getHeight());
        obj.setPackageCode(uploadData.getBarCode());
        obj.setOpeUserId(config.getOperteUserId());
        obj.setOpeUserName(config.getUpdateUserName());
        obj.setOpeTime(DateHelper.formatDateTime(uploadData.getScannerTime()));

        opeEntity.getOpeDetails().add(obj);
        String body = "[" + JsonHelper.toJson(opeEntity) + "]";
        Task task = new Task();
        task.setBody(body);
        task.setType(Task.TASK_TYPE_WEIGHT);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
        task.setCreateSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
        task.setKeyword1(String.valueOf(opeEntity.getOpeDetails().get(0).getOpeSiteId()));
        task.setKeyword2("上传长宽高");
        task.setBody(body);
        task.setBoxCode("");
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setReceiveSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
        task.setOwnSign(BusinessHelper.getOwnSign());
        int result=taskService.add(task);
        return result>0;
    }
}
