package com.jd.bluedragon.distribution.worker.inventory;

import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.departure.BatchSendCarTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class InventoryExceptionTask extends DBSingleScheduler {

    private static final Log logger = LogFactory.getLog(InventoryExceptionTask.class);

    @Autowired
    private InventoryExceptionService inventoryExceptionService;

    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        logger.info("时间：" + new Date().toString() + "，执行盘点少货异常最新状态同步开始");
        //同步盘点异常少货状态
        int count = inventoryExceptionService.syncInventoryExceptionWaybillTrace();
        logger.info("时间：" + new Date().toString() + "，执行盘点少货异常最新状态同步结束，同步条数：" + count);
        return true;
    }

    @Override
    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
        logger.info("时间：" + new Date().toString() + "，执行盘点少货异常最新状态同步开始");
        //同步盘点异常少货状态
        int count = inventoryExceptionService.syncInventoryExceptionWaybillTrace();
        logger.info("时间：" + new Date().toString() + "，执行盘点少货异常最新状态同步结束，同步条数：" + count);
        List<Task> tasks = new ArrayList<>();
        return tasks;
    }
}
