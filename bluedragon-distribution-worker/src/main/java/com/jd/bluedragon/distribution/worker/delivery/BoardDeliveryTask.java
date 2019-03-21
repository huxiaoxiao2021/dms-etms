package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @ClassName: BoardDeliveryTask
 * @Description: 组板发货
 * @author: wuyoude
 * @date: 2018年3月29日 下午3:31:31
 *
 */
public class BoardDeliveryTask extends DBSingleScheduler {

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public boolean executeSingleTask(Task task, String ownSign)
            throws Exception {
        //调用组板发货处理逻辑
        return deliveryService.doBoardDelivery(task);
    }

}
