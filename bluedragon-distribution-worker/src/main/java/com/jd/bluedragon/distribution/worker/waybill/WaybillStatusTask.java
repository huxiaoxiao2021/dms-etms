package com.jd.bluedragon.distribution.worker.waybill;

import java.util.ArrayList;
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
 * 同步运单状态到青龙运单系统Worker
 * 
 * @author Zhipeng Wong
 */
public class WaybillStatusTask extends AbstractScheduler<Task> {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private WaybillStatusService waybillStatusService;
    
    public boolean execute(Object[] taskArray, String ownSign) throws Exception {
        List<Task> tasksMix = this.asList(taskArray);
        this.lock(tasksMix);
        List<Task> tasks=new ArrayList<Task>();
        List<Task> taskFinend = new ArrayList<Task>();

        for (Task task : tasksMix) {
            logger.debug("6667妥投" + task.getType());
            if (task.getType() != null && task.getType().equals(Task.TASK_TYPE_WAYBILL_FINISHED)) {
                logger.debug("6667妥投任务" + task.getType());
                taskFinend.add(task);
            } else {
                tasks.add(task);
            }
        }
        try {
            this.waybillStatusService.sendModifyWaybillStatusNotify(tasks);
        } catch (Exception e) {
            this.logger.warn("调用运单服务出现异常， 异常信息为：", e);
            this.repeat(tasks);
        }

        for (Task task:taskFinend){
            try{
                logger.debug("置运单妥投状态开始");
                this.waybillStatusService.sendModifyWaybillStatusFinished(task);
            }catch (Exception e){
                this.logger.warn("调用运单服务置运单妥投出现异常， 异常信息为：", e);
                List<Task> temp=new ArrayList<Task>();
                temp.add(task);
                this.repeat(temp);
            }
        }

        
        return Boolean.TRUE;
    }
    
    public List<Task> selectTasks(String ownSign, int queueNum, List<String> subQueues, int fetchNum)
            throws Exception {
        Integer aFetchNum = this.fecthNum(queueNum, fetchNum, subQueues);
        List<Task> unhandles = this.taskService.findLimitedTasks(aFetchNum);
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
            if (!(task.getType() != null && task.getType().equals(Task.TASK_TYPE_WAYBILL_FINISHED)))
                task.setType(Task.TASK_TYPE_WAYBILL);

            this.taskService.doLock(task);
        }
    }
    
    private void repeat(List<Task> tasks) {
        for (Task task : tasks) {
            task.setType(Task.TASK_TYPE_WAYBILL);
            this.taskService.doRepeat(task);
        }
    }
}
