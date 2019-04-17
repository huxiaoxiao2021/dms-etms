package com.jd.bluedragon.distribution.worker.sorting;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SortingTask extends DBSingleScheduler {
    
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SortingService sortingService;

    @Autowired
    private SortingFactory sortingFactory;


	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {

	    if(sortingService.useNewSorting(task.getCreateSiteCode())){
            SortingVO sortingVO = new SortingVO(task);
            return sortingFactory.bulid(sortingVO).execute(sortingVO);
        }

        return sortingService.processTaskData(task);
	}




}

