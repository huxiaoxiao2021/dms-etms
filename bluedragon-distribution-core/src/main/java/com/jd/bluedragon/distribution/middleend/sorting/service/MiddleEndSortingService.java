package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;

public interface MiddleEndSortingService {
    /**
     * 分拣核心操作：1写sorting表；2发分拣的全称跟踪；3逆向的发送退货一百分和快退MQ
     * @param sorting
     * @return
     */
    boolean coreSorting(SortingObjectExtend sorting);

    /**
     * 取消分拣
     * @param sorting
     */
    SortingResponse cancelSorting(Sorting sorting);
}
