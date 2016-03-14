package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 龙门架验货消费接口
 * Created by wangtingwei on 2016/3/14.
 */
public class ScannerFrameInspectionConsume implements ScannerFrameConsume {
    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {

        Task task=new Task();
        task.setOperateType(Task.TASK_TYPE_INSPECTION);
        task.setKeyword1();
        return false;
    }
}
