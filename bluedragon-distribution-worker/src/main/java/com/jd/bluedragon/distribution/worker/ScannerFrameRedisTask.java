package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.auto.service.ScannerFrameDispatchService;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangtingwei on 2016/3/10.
 */
public class ScannerFrameRedisTask extends RedisSingleScheduler {
    @Autowired
    private ScannerFrameDispatchService scannerFrameDispatchService;
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return scannerFrameDispatchService.dispatch(JsonHelper.fromJson(task.getBody(), UploadData.class));
    }
}
