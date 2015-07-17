package com.jd.bluedragon.distribution.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.distribution.task.domain.Task;

public abstract class SendDBSingleScheduler extends DBSingleScheduler {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	protected String keyType;

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

            List<Task> Tasks = taskService.findSendTasks(this.type, fetchNum, this.keyType);
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

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
}
