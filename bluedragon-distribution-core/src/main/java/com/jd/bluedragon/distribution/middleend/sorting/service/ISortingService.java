package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.dto.CancelSortingOffsiteDto;
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

    /**
     * 异场地取消分拣-操作场地和集包场地不同
     * @param cancelSortingOffsiteDto 取消集包对象
     * @return 取消排序后的响应
     */
    SortingResponse cancelSortingOffsite(CancelSortingOffsiteDto cancelSortingOffsiteDto);

}
