package com.jd.bluedragon.distribution.worker.batchForward;

import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author hujiping
 */
public class BatchForwardTask extends SendDBSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchForwardTask.class);

    @Autowired
    private BatchForwardService batchForwardService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {

        BatchForwardTask.log.info("处理整批转发Task={}" , task.getId());

        return batchForwardService.dealBatchForwardTask(task);
    }
}
