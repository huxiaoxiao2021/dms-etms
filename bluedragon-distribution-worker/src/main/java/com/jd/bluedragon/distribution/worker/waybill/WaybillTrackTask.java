package com.jd.bluedragon.distribution.worker.waybill;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusService;
import com.jd.bluedragon.distribution.worker.AbstractScheduler;

/**
 * 同步全程跟踪
 * 
 * @author lihuihui
 */
public class WaybillTrackTask extends AbstractScheduler<Task> {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private WaybillStatusService waybillStatusService;
    
    public boolean execute(Object[] taskArray, String ownSign) throws Exception {
        List<Task> tasks = this.asList(taskArray);
        this.lock(tasks);
        
        try {
            this.waybillStatusService.sendModifyWaybillTrackNotify(tasks);
        } catch (Exception e) {
            this.logger.warn("调用运单[回传全程跟踪]服务出现异常， 异常信息为：", e);
            this.repeat(tasks);
        }
        
        return Boolean.TRUE;
    }
    
    public List<Task> selectTasks(String ownSign, int queueNum, List<String> subQueues, int fetchNum)
            throws Exception {
        Integer aFetchNum = this.fecthNum(queueNum, fetchNum, subQueues);
        List<Task> unhandles =  taskService.findLimitedTasks(this.type, aFetchNum, this.ownSign);;
        return this.assign(queueNum, subQueues, unhandles, "getId");
    }
    
    public Comparator<Task> getComparator() {
        return new Comparator<Task>() {
            public int compare(Task task0, Task task1) {
                if (null != task0 && null != task1 && task0.getId() != null
                        && task0.getId().equals(task1.getId())) {
                    return 0;
                }
                return 1;
            }
        };
    }
    
    private void lock(List<Task> tasks) {
        for (Task task : tasks) {
            task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
            this.taskService.doLock(task);
        }
    }
    
    private void repeat(List<Task> tasks) {
        for (Task task : tasks) {
            task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
            this.taskService.doRepeat(task);
        }
    }
}
