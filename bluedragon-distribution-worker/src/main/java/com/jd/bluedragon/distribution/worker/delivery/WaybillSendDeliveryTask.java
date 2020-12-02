package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 按运单发货
 * 基类中 按 tye- keyType查询 task
 */
public class WaybillSendDeliveryTask extends SendDBSingleScheduler {

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        //调用按运单发货处理逻辑
        return deliveryService.doWaybillSendDelivery(task);
    }

}
