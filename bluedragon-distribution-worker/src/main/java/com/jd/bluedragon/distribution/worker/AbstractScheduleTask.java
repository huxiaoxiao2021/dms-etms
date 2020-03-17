package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractScheduleTask extends AbstractScheduler<Task>{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected TaskService taskService;

	/**
     * This select will be called by each task manager
     */
	public List<Task> selectTasks(String arg0, int queueNum,
			List<String> queryCondition, int fetchNum) throws Exception {
        if(!isActive){
        	log.warn("任务[{}-{}]未准备就绪或已停止，本次不抓取任务数据！", this.taskType, this.ownSign);
        	return Collections.emptyList();
        }
		if(log.isInfoEnabled()){
            log.info("任务执行fetchNum is {}",fetchNum);
        }
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<Task>();
        try {
            
			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

            List<Task> Tasks = taskService.findLimitedTasks(this.type, fetchNum, this.ownSign, queryCondition);
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
        }
        return tasks;
    }

    public boolean updateTask(Long id, int executeCount, int status, int type) {
        Task task = new Task();
        task.setId(id);
        task.setExecuteCount(executeCount);
        task.setStatus(status);
        task.setType(type);
        this.taskService.updateBySelective(task);
        return true;
    }

    public boolean updateTask(Long id, int status, int type) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        task.setType(type);
        this.taskService.updateBySelective(task);
        return true;
    }
    
	@Override
	public Comparator<Task> getComparator() {
		return new Comparator<Task>() {
			public int compare(Task o1, Task o2) {
				if (null != o1 && null != o2
						&& o1.getId()!=null&&o1.getId().equals(o2.getId())) {
					return 0;
				} else {
					return 1;
				}
			}

		};
	}
}
