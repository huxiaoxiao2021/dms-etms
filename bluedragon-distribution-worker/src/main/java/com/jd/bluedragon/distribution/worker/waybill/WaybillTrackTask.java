package com.jd.bluedragon.distribution.worker.waybill;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;

/**
 * 同步全程跟踪
 * 
 * @author lihuihui
 */
public class WaybillTrackTask extends DBSingleScheduler {
    @Autowired
    private WaybillService waybillService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return waybillService.doWaybillTraceTask(task);
    }
    
    public boolean executeSingleTask(Task task) throws Exception {
        return this.executeSingleTask(task, null);
    }
}
