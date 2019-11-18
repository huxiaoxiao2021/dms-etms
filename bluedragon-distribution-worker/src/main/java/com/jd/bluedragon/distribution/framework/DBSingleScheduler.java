package com.jd.bluedragon.distribution.framework;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.worker.AbstractScheduleTask;

public abstract class DBSingleScheduler extends AbstractScheduleTask {

	private final Log logger = LogFactory.getLog(this.getClass());

	private String desc;

	private TaskHanlder taskHanlder = new DBTaskHanlder();

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
			boolean result = handleSingleTask(task, ownSign);
			if (!result) {
				dealDataFail++;
			}
		}
		if (dealDataFail > 0) {
			logger.error(getWorkerDescPrefix() + "抓取" + tasks.size() + "条任务，"
					+ dealDataFail + "条数据执行失败！");
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
			logger.error("处理任务TaskId:"+task.getId()+"失败!消息体："+ JsonHelper.toJson(task), e);
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
