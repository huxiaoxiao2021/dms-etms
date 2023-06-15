package com.jd.bluedragon.distribution.worker.work;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.framework.NoneTaskHanlder;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 任务线上化-扫描任务
 * 定时扫描task_work_grid_manager_site_scan数据记录，生成网格巡检任务，并分配给相应的岗位
 * @author wuyoude
 *
 */
public class WorkGridManagerSiteScanTask extends DBSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
	@Qualifier("jyWorkGridManagerBusinessService")
    private JyWorkGridManagerBusinessService jyWorkGridManagerBusinessService;
    
    public WorkGridManagerSiteScanTask(){
    	super(new NoneTaskHanlder());
    }
    @Override
    public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return jyWorkGridManagerBusinessService.executeWorkGridManagerSiteScanTask(task);
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
        	task0.setId(0L);
            if (isMyTask(queueNum, task0.getId(), queryCondition)) {
            	tasks.add(task0);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
        }
        return tasks;
    }
    /**
     * 用于判断由id指定的任务是否由本机上的队列处理
     * @param queueNum
     *            抓取到的队列总数
     * @param id 任务id或其它
     * @param subQueues
     *            当前实例分配到的队列,队列编号(1,2,3,4.....)
     * @return
     */
    public boolean isMyTask(long queueNum, long id, List<?> subQueues) {
        if (queueNum == subQueues.size()) {//说明只有一台机器
            return true;
        }
        
        long m = id % queueNum;//计算此task的模值,用于确定分配到哪个队列，对应PAMIRS_SCHEDULE_QUEUE表中的queue_id
        for (Object o : subQueues) {//遍历本机器上的分配的任务队列，如果计算得出的队列在本机器上，返回true
            if (m == Long.parseLong(o.toString())) {
                return true;
            }
        }
        return false;
    }
}
