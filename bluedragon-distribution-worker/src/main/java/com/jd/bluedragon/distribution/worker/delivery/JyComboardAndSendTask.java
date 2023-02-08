package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JyComboardAndSendTask extends DBSingleScheduler {
    private static final Logger log = LoggerFactory.getLogger(JyComboardAndSendTask.class);


    @Autowired
    private IDeliveryOperationService deliveryOperationService;
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            log.info("JyComboardAndSendTask starting deal task:{}",JsonHelper.toJson(task));
            deliveryOperationService.dealComboardAndSendTask(task);
        }
        catch (Exception ex) {
            log.error("JyComboardAndSendTask-error. task:{}", JsonHelper.toJson(task), ex);
            return false;
        }
        return true;
    }
}
