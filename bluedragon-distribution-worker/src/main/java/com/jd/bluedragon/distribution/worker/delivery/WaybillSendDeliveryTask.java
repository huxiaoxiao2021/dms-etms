package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 按运单发货
 */
public class WaybillSendDeliveryTask extends DBSingleScheduler {

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        //调用按运单发货处理逻辑
        return deliveryService.doWaybillSendDelivery(task);
    }

}
