package com.jd.bluedragon.distribution.middleend.manager;

import com.jd.bluedragon.Constants;
import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.SortingService;
import com.jd.ql.shared.services.sorting.api.dto.SortingCancelObjectType;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;
import com.jd.ql.shared.services.sorting.api.dto.UserEnv;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("middleEndSortingManager")
public class MiddleEndSortingManagerImpl implements MiddleEndSortingManager {

    @Autowired
    @Qualifier("sharedSortingService")
    private SortingService sharedSortingService;

    /**
     * 无容器分拣--原包/运单
     * @param containerCode
     * @param sortingObject
     * @param operator
     * @param operateTime
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.WORKER.MiddleEndSortingManagerImpl.sort",mState = {JProEnum.TP,JProEnum.FunctionError})
    public ApiResult<Void> sort(String containerCode, SortingObject sortingObject, UserEnv operator, Date operateTime){
        return sharedSortingService.sort(containerCode,sortingObject,operator,operateTime);
    }

    /**
     * 有容器分拣--有箱号
     * @param sortingObject
     * @param operator
     * @param operateTime
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.WORKER.MiddleEndSortingManagerImpl.sortWithoutContainer",mState = {JProEnum.TP,JProEnum.FunctionError})
    public ApiResult<Void> sortWithoutContainer(SortingObject sortingObject,UserEnv operator,Date operateTime){
        return sharedSortingService.sortWithoutContainer(sortingObject,operator,operateTime);
    }

    /**
     * 取消分拣
     * @param barCode
     * @param type
     * @param operateSiteId
     * @param operator
     * @param operateTime
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.WORKER.MiddleEndSortingManagerImpl.cancelSorting",mState = {JProEnum.TP,JProEnum.FunctionError})
    public ApiResult<Void> cancelSorting(String barCode, SortingCancelObjectType type, Integer operateSiteId, UserEnv operator, Date operateTime){
        return sharedSortingService.cancelSorting(barCode,type,operator,operateTime);
    }
}
