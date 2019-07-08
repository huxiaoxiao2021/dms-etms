package com.jd.bluedragon.distribution.middleend.sorting;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

public class DmsSortingServiceImpl  implements DmsSortingService{

    @Autowired
    private SortingService sortingService;

    @Autowired
    private SortingFactory sortingFactory;

    public boolean doSorting(Task sortingTask){
        if(sortingService.useNewSorting(sortingTask.getCreateSiteCode())){
            SortingVO sortingVO = new SortingVO(sortingTask);
            return sortingFactory.bulid(sortingVO).execute(sortingVO);
        }

        return sortingService.processTaskData(sortingTask);
    }

    public void cancelSorting(Sorting sorting){

    }
}
