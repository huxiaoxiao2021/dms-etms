package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xumei3 on 2018/8/8.
 */
public class BoardDeliveryCancelTask extends DBSingleScheduler {
    @Autowired
    private DeliveryService deliveryService;

    public boolean executeSingleTask(Task task, String ownSign)
            throws Exception {
        //调用组板发货处理逻辑
        return deliveryService.doBoardDeliveryCancel(task);
    }
}
