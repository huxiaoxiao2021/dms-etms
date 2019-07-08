package com.jd.bluedragon.distribution.middleend.sorting;

import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;

public class FailOverSortingServiceImpl extends AbstractSortingService{
    private SortingService sortingService;
    private MiddleEndSortingService middleEndSortingService;

    public void cancelSorting(Sorting sorting){

    }

    private void coreSorting(SortingObjectExtend sorting){
        //调中台的service
        if(middleEndSortingService.coreSorting(sorting)){

            //拆成包裹维度
            sortingService.saveOrUpdate(sorting.getDmsSorting());
        }else{
            //找到按运单处理的方法

            sortingService.saveOrUpdate(sorting.getDmsSorting());
            sortingService.addSortingAdditionalTask(sorting.getDmsSorting());
            sortingService.notifyBlocker(sorting.getDmsSorting());
            sortingService.backwardSendMQ(sorting.getDmsSorting());
            //生成任务
        }
    }
}
