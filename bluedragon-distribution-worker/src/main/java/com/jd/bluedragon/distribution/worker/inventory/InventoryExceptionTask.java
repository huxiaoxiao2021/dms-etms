package com.jd.bluedragon.distribution.worker.inventory;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryExceptionTask extends DBSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(InventoryExceptionTask.class);

    @Autowired
    private InventoryExceptionService inventoryExceptionService;

    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        log.debug("时间：{}，执行盘点少货异常最新状态同步开始",new Date().toString());
        //同步盘点异常少货状态
        int count = inventoryExceptionService.syncInventoryExceptionWaybillTrace();
        log.debug("时间：{}，执行盘点少货异常最新状态同步结束，同步条数：{}" ,new Date().toString(), count);
        return true;
    }

    @Override
    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
        log.debug("时间：{}，执行盘点少货异常最新状态同步开始",new Date().toString());
        //同步盘点异常少货状态
        int count = inventoryExceptionService.syncInventoryExceptionWaybillTrace();
        log.debug("时间：{}，执行盘点少货异常最新状态同步结束，同步条数：" ,new Date().toString(), count);
        List<Task> tasks = new ArrayList<>();
        return tasks;
    }
}
