package com.jd.bluedragon.distribution.worker.sorting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;

public class SortingTask extends DBSingleScheduler {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private SortingService sortingService;
    
	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		boolean result = false;
		try {
            this.logger.info("task id is " + task.getId());
            result = this.sortingService.doSorting(task);
        } catch (Exception e) {
            this.logger.error("task id is" + task.getId());
            this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
		return result;
	}
    
}
