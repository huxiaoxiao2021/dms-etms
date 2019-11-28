package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.AbstractScheduleTask;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class DBSingleScheduler extends AbstractScheduleTask {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String desc;

	private TaskHanlder taskHanlder = new DBTaskHanlder();

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
			boolean result = handleSingleTask(task, ownSign);
			if (!result) {
				dealDataFail++;
			}
		}
		if (dealDataFail > 0) {
			log.warn("{}抓取到[{}]条任务,{}条数据执行失败！",getWorkerDescPrefix(),tasks.size(),dealDataFail);
			return false;
		} else {
			return true;
		}
	}

	private boolean handleSingleTask(Task task, String ownSign) throws Exception {
		if (task == null) {
			return false;
		}
		boolean result = false;
		try {
			result = taskHanlder.preHandle(task);
			if (result) {
				result = executeSingleTask(task, ownSign);
				if (result) {
					taskHanlder.handleSuccess(task);
				} else {
					taskHanlder.handleError(task);
				}
			}
		} catch (Throwable e) {
			taskHanlder.handleError(task);
			log.error("处理任务TaskId:{}失败!消息体：{}",task.getId(), JsonHelper.toJson(task), e);
		}
		return result;
	}

	protected abstract boolean executeSingleTask(Task task, String ownSign) throws Exception ;

	public void setDesc(String desc) {
		this.desc = desc;
	}

	protected String getWorkerDescPrefix() {
		if (desc == null) {
			return "[" + desc + "]worker";
		} else {
			return "[]worker";
		}
	}

}
