package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SortingFactory {

    @Autowired
    private SortingWaybillServiceImpl sortingWaybillService;
    @Autowired
    private SortingPackServiceImpl sortingPackService;
    @Autowired
    private SortingWaybillSplitServiceImpl sortingWaybillTurnService;

    public SortingCommonSerivce bulid(SortingVO sortingVO){

        if(sortingVO.isWaybillSorting()){
            return sortingWaybillService;
        }else if(sortingVO.isPackSorting()){
            return sortingPackService;
        }else if(sortingVO.isWaybillSplitSorting()){
            return sortingWaybillTurnService;
        }
        return sortingPackService;
    }
}
