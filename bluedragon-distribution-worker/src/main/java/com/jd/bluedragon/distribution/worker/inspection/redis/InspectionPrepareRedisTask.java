package com.jd.bluedragon.distribution.worker.inspection.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dudong on 2014/10/18.
 */
public class InspectionPrepareRedisTask extends RedisSingleScheduler {
    private static final Logger log = LoggerFactory.getLogger(InspectionPrepareRedisTask.class);

    @Autowired
    private InspectionService inspectionService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            log.info("自动分拣写入交接表Redis任务, task id is {}" , task.getId());
            result = inspectionService.dealHandoverPackages(task);
        } catch (Exception e) {
            log.error("自动分拣写入交接表Redis任务执行失败，task id is {}" , task.getId(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
