package com.jd.bluedragon.distribution.worker.globaltrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class LoadBillTask extends DBSingleScheduler {

	private final Log logger = LogFactory.getLog(this.getClass());
	
    @Autowired
	private LoadBillService loadBillService;
	
	@Override
	protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
		boolean result = false;
		try {
			this.logger.info("task id is " + task.getId());
			//result = this.loadBillService.add(task);
		} catch (Exception e) {
			this.logger.error("task id is" + task.getId());
			this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
			return Boolean.FALSE;
		}
		return result;
	}

	@Override
	public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
		if (queryCondition.size() == 0) {
			return Collections.emptyList();
		}

		List<Task> tasks = new ArrayList<Task>();
		try {

			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

			List<Task> Tasks = taskService.findSpecifiedTasks(this.type, fetchNum, this.ownSign);
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

}
