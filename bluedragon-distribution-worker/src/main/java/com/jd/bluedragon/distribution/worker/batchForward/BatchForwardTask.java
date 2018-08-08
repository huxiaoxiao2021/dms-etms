package com.jd.bluedragon.distribution.worker.batchForward;

import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author hujiping
 */
public class BatchForwardTask extends SendDBSingleScheduler {

    private static final Log logger = LogFactory.getLog(BatchForwardTask.class);

    @Autowired
    private BatchForwardService batchForwardService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {

        BatchForwardTask.logger.info("处理整批转发Task" + task.getId());

        return batchForwardService.dealBatchForwardTask(task);
    }
}
