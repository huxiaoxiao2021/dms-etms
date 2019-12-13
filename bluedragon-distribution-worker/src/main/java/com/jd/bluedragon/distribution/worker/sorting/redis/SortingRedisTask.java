package com.jd.bluedragon.distribution.worker.sorting.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SortingRedisTask extends RedisSingleScheduler {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private SortingService sortingService;
    @Autowired
    private SortingFactory sortingFactory;

	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
		boolean result = false;
		try {
            this.log.info("task id is {}" , task.getId());
            if(sortingService.useNewSorting(task.getCreateSiteCode())){
                SortingVO sortingVO = new SortingVO(task);
                result = sortingFactory.bulid(sortingVO).execute(sortingVO);
            }else{
                result = this.sortingService.doSorting(task);
            }
        } catch (Exception e) {
            this.log.error("处理分拣任务发生异常,task id is {}" , task.getId(), e);
            return Boolean.FALSE;
        }
		return result;
	}
    
}
