package com.jd.bluedragon.distribution.worker.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 无预分拣站点数据扫描任务
 * 定时扫描WaybillHasnoPresiteRecordScanTask数据记录，更新数据状态
 * @author wuyoude
 *
 */
public class WaybillHasnoPresiteRecordScanTask extends DBSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;

    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return waybillHasnoPresiteRecordService.doScan();
    }

    /**
     * @param arg0
     * @param queueNum
     * @param queryCondition
     * @param fetchNum
     */
    @Override
    public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
        List<Task> tasks = new ArrayList<Task>();
        try {
        	Task task0 = new Task();
        	task0.setId(1L);
            if (!isMyTask(queueNum, task0.getId(), queryCondition)) {
            	tasks.add(task0);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
        }
        return tasks;
    }
}
