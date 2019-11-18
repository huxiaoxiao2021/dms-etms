package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface ISortingService {
    /**
     * 分拣
     * @param sortingTask
     * @return
     */
    boolean doSorting(Task sortingTask);

    /**
     * 取消分拣
     * @param sorting
     */
    SortingResponse cancelSorting(Sorting sorting);
}
