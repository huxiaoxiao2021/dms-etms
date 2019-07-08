package com.jd.bluedragon.distribution.middleend;

import com.jd.bluedragon.distribution.middleend.sorting.AbstractSortingService;
import com.jd.bluedragon.distribution.middleend.sorting.DmsSortingService;
import com.jd.bluedragon.distribution.middleend.sorting.FailOverSortingService;
import com.jd.bluedragon.distribution.middleend.sorting.MiddleEndSortingService;

public class ServiceFactory {
    private int type;
    private DmsSortingService dmsSortingService;
    private MiddleEndSortingService middleEndSortingService;
    private FailOverSortingService failOverSortingService;
    public AbstractSortingService getSortingService(){
        if(type == 1){
            return dmsSortingService;
        }else if(type == 2){
            return middleEndSortingService;
        }else if(type == 3){
            return failOverSortingService;
        }

        return failOverSortingService;
    }
}
