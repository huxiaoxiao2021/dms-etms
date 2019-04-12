package com.jd.bluedragon.distribution.worker.sorting;

import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SortingSplitTask extends DBSingleScheduler {
    
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SortingFactory sortingFactory;

	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        SortingVO sortingVO = new SortingVO(task);
        sortingFactory.bulid(sortingVO).execute(sortingVO);
        return true;
	}
}
