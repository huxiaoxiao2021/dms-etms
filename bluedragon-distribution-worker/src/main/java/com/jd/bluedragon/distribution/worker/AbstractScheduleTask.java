package com.jd.bluedragon.distribution.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;

public abstract class AbstractScheduleTask extends AbstractScheduler<Task>{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    protected TaskService taskService;

	/**
     * This select will be called by each task manager
     */
	public List<Task> selectTasks(String arg0, int queueNum,
			List<String> queryCondition, int fetchNum) throws Exception {
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }

        List<Task> tasks = new ArrayList<Task>();
        try {
            
			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

            List<Task> Tasks = taskService.findLimitedTasks(this.type, fetchNum, this.ownSign);
            for (Task task : Tasks) {
                if (!isMyTask(queueNum, task.getId(), queryCondition)) {
                    continue;
                }
                tasks.add(task);
            }
        } catch (Exception e) {
            this.logger.error("出现异常， 异常信息为：" + e.getMessage(), e);
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
