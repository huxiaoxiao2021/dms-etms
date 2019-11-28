package com.jd.bluedragon.distribution.framework;

import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class SendDBSingleScheduler extends DBSingleScheduler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected String keyType;

    protected String ownSigns;

	/**
     * This select will be called by each task manager
     */
	public List<Task> selectTasks(String arg0, int queueNum,
			List<String> queryCondition, int fetchNum) throws Exception {
        if (queryCondition.size() == 0) {
            return Collections.emptyList();
        }
        List<String> ownSignList = new ArrayList<>();
        if(StringUtils.isNotBlank(ownSigns)){
            ownSignList = Arrays.asList(ownSigns.split(","));
        }
        List<Task> tasks = new ArrayList<Task>();
        try {
            
			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

            List<Task> Tasks = taskService.findSendTasks(this.type, fetchNum, this.keyType,queryCondition, ownSign, ownSignList);
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

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

    public void setOwnSigns(String ownSigns) {
        this.ownSigns = ownSigns;
    }
}
