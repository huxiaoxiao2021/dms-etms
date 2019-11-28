package com.jd.bluedragon.distribution.worker.globaltrade;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadBillTask extends DBSingleScheduler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
	private LoadBillService loadBillService;
	
	@Override
	protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
		boolean result = false;
		try {
			this.log.info("task id is {}" , task.getId());
			//result = this.loadBillService.add(task);
		} catch (Exception e) {
			this.log.error("处理分拣任务发生异常,task id is {}" , task.getId(),e);
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

			List<Task> Tasks = taskService.findSpecifiedTasks(this.type, fetchNum, this.ownSign, queryCondition);
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

}
