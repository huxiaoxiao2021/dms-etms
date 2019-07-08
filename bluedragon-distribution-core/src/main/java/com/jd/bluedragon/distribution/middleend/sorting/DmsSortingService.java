package com.jd.bluedragon.distribution.middleend.sorting;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface DmsSortingService{
    boolean doSorting(Task sortingTask);
    void cancelSorting(Sorting sorting);
}
