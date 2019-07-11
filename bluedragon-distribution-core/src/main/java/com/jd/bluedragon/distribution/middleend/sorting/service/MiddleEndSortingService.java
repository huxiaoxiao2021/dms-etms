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
     * 分拣补验货
     * 1.补验货差异表inspection_ec
     * @param sorting
     */
    void sortingAddInspection(SortingObjectExtend sorting);

    /**
     * 分拣补发货
     * 1.补send_d表
     * 2.补发货全称跟踪
     * @param sorting
     */
    void sortingAddSend(SortingObjectExtend sorting);

    /**
     * 分拣写操作日志
     * cassandra日志
     * @param sorting
     */
    void sortingAddOperationLog(SortingObjectExtend sorting);

    /**
     * 取件单处理
     * @param sorting
     */
    void fillSortingIfPickup(SortingObjectExtend sorting);


    /**
     * 取消分拣
     * @param sorting
     */
    SortingResponse cancelSorting(Sorting sorting);
}
