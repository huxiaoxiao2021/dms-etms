package com.jd.bluedragon.distribution.worker.inspection.redis;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dudong on 2014/10/18.
 */
public class InspectionPrepareRedisTask extends RedisSingleScheduler {
    private static final Log logger= LogFactory.getLog(InspectionPrepareRedisTask.class);

    @Autowired
    private InspectionService inspectionService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            logger.info("自动分拣写入交接表Redis任务, task id is " + task.getId());
            result = inspectionService.dealHandoverPackages(task);
        } catch (Exception e) {
            logger.error("自动分拣写入交接表Redis任务执行失败，task id is" + task.getId());
            logger.error("自动分拣写入交接表Redis任务执行失败，异常信息为：" + e.getMessage(), e);

            return Boolean.FALSE;
        }
        return result;
    }
}
