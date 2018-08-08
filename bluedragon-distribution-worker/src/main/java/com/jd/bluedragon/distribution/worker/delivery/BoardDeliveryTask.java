package com.jd.bluedragon.distribution.worker.delivery;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.SendDBSingleScheduler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 
 * @ClassName: BoardDeliveryTask
 * @Description: 组板发货
 * @author: wuyoude
 * @date: 2018年3月29日 下午3:31:31
 *
 */
public class BoardDeliveryTask extends SendDBSingleScheduler{
    @Autowired
    private DeliveryService deliveryService;
    
    public boolean executeSingleTask(Task task, String ownSign)
			throws Exception {
    	//调用组板发货处理逻辑
		return deliveryService.doBoardDelivery(task);
	}
}
