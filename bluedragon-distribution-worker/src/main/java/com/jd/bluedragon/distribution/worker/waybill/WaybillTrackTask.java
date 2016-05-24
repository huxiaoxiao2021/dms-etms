package com.jd.bluedragon.distribution.worker.waybill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusService;
import com.jd.bluedragon.distribution.worker.AbstractScheduler;

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
}
