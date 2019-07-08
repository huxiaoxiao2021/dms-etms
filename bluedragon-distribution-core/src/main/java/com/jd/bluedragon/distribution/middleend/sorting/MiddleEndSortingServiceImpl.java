package com.jd.bluedragon.distribution.middleend.sorting;

import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.middleend.manager.SortingMiddlePlateManager;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ql.shared.services.sorting.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class MiddleEndSortingServiceImpl extends AbstractSortingService{
    @Autowired
    private SortingMiddlePlateManager manager;

    public void coreSorting(SortingObjectExtend sorting){
        Sorting dmsSorting = sorting.getDmsSorting();

        //判断是否是有容器的
        boolean isHaveContainer = false;
        if(BusinessUtil.isBoxcode(dmsSorting.getBoxCode())){
            isHaveContainer = true;
        }
        UserEnv operator = new UserEnv();
        if(isHaveContainer){
            manager.sortWithoutContainer(sorting,operator,new Date());
        }else{
            manager.sort(dmsSorting.getBoxCode(),sorting,operator,new Date());
        }
    }
}
