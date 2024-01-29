package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.domain.SortingVO;
import com.jd.bluedragon.distribution.sorting.dto.CancelSortingOffsiteDto;
import com.jd.bluedragon.distribution.sorting.service.SortingFactory;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service("dmsSortingService")
public class DmsSortingServiceImpl implements ISortingService {

    @Autowired
    @Qualifier("sortingService")
    protected SortingService dmsSortingService;

    @Autowired
    private SortingFactory sortingFactory;

    /**
     * 纯分拣中心操作分拣
     *
     * @param sortingTask
     * @return
     */
    @JProfiler(jKey = "DMSWORKER.DmsSortingServiceImpl.doSorting", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    @Override
    public boolean doSorting(Task sortingTask) {
        SortingVO sortingVO = new SortingVO(sortingTask);
        return sortingFactory.bulid(sortingVO).execute(sortingVO);
    }

    @Override
    public SortingResponse cancelSorting(Sorting sorting) {
        return dmsSortingService.doCancelSorting(sorting);
    }

    @Override
    public SortingResponse cancelSortingOffsite(CancelSortingOffsiteDto cancelSortingOffsiteDto) {
        return dmsSortingService.doCancelSortingOffsite(cancelSortingOffsiteDto);
    }
}