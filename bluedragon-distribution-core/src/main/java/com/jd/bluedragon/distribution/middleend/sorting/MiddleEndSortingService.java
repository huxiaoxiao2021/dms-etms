package com.jd.bluedragon.distribution.middleend.sorting;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;

public interface MiddleEndSortingService {
    void doSorting(Sorting sorting);
    void cancelSorting(Sorting sorting);
}
