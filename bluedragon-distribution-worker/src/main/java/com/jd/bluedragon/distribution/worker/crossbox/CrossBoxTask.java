package com.jd.bluedragon.distribution.worker.crossbox;

import com.jd.bluedragon.distribution.crossbox.service.CrossBoxService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 跨箱号中转维护中导入功能中，数据的定时激活和失效处理
 * 
 * @author xumei1
 *
 */
public class CrossBoxTask extends DBSingleScheduler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TaskService taskService;

	@Autowired
	private CrossBoxService crossBoxService;

	public List<Task> selectTasks(String arg0, int queueNum, List<String> queryCondition, int fetchNum) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("任务执行fetchNum is {}" , fetchNum);
		}
		if (queryCondition.size() == 0) {
			return Collections.emptyList();
		}

		List<Task> tasks = new ArrayList<Task>();
		try {

			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

			List<Task> Tasks = taskService.findTaskTypeByStatus(this.type, fetchNum,queryCondition);
			for (Task task : Tasks) {
				Long id = Long.parseLong(task.getCreateSiteCode()+""+task.getReceiveSiteCode());
				if (!isMyTask(queueNum, id, queryCondition)) {
					continue;
				}
				tasks.add(task);
			}
		} catch (Exception e) {
			this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
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
		log.info("{}抓取到[{}]条任务待处理",getWorkerDescPrefix(),tasks.size());

		int dealDataFail = 0;
		for (Task task : tasks) {
			boolean result = executeSingleTask(task, ownSign);
			if (!result) {
				dealDataFail++;
			}
		}
		if (dealDataFail > 0) {
			log.warn("{}抓取到[{}]条任务,{}条数据执行失败！",getWorkerDescPrefix(),tasks.size(),dealDataFail );
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
