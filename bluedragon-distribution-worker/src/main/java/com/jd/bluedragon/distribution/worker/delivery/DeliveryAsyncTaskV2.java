package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.inspection.InspectionSplitTask;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wyh
 * @className DeliveryAsyncTask
 * @description
 * @date 2021/8/11 14:15
 **/
public class DeliveryAsyncTaskV2 extends DBSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(DeliveryAsyncTaskV2.class);

    @Autowired
    private IDeliveryOperationService deliveryOperationService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            deliveryOperationService.dealDeliveryTaskV2(task);
        }
        catch (Exception ex) {
            log.error("DeliveryAsyncTask-error. task:{}", JsonHelper.toJson(task), ex);
            return false;
        }

        return true;
    }
}
