package com.jd.bluedragon.distribution.worker.crossbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;

/**
 * 跨箱号中转维护中导入功能中，数据的定时激活和失效处理
 * 
 * @author xumei1
 *
 */
public class CrossBoxTask extends DBSingleScheduler {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private TaskService taskService;

	@Autowired
	private CrossBoxService crossBoxService;

	public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("任务执行fetchNum is" + fetchNum);
		}
		if (queryCondition.size() == 0) {
			return Collections.emptyList();
		}

		List<Task> tasks = new ArrayList<Task>();
		try {

			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

			List<Task> Tasks = taskService.findTaskTypeByStatus(this.type, fetchNum);
			for (Task task : Tasks) {
				Long id = Long.parseLong(task.getCreateSiteCode()+""+task.getReceiveSiteCode());
				if (!isMyTask(queueNum, id, queryCondition)) {
					continue;
				}
				tasks.add(task);
			}
		} catch (Exception e) {
			this.logger.error("出现异常， 异常信息为：" + e.getMessage(), e);
		}
		return tasks;
	}

	@Override
	public boolean execute(Object[] taskArray, String ownSign) throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		for (Object task : taskArray) {
			if (task != null && task instanceof Task) {
				tasks.add((Task) task);
			}
		}
		logger.info(getWorkerDescPrefix() + "抓取到[" + tasks.size() + "]条任务待处理");

		int dealDataFail = 0;
		for (Task task : tasks) {
			boolean result = executeSingleTask(task, ownSign);
			if (!result) {
				dealDataFail++;
			}
		}
		if (dealDataFail > 0) {
			logger.error(getWorkerDescPrefix() + "抓取" + tasks.size() + "条任务，" + dealDataFail + "条数据执行失败！");
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
		int result = crossBoxService.activeCrossBox(task);
		if (result == 1) {
			taskService.updateTaskStatus(task);
		} else if(result == 3){
			return false;
		}
		return true;
	}
}
