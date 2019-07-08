package com.jd.bluedragon.distribution.middleend.manager;

import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.dto.SortingCancelObjectType;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;
import com.jd.ql.shared.services.sorting.api.dto.UserEnv;

import java.util.Date;

public interface SortingMiddlePlateManager {
    ApiResult<Void> sort(String containerCode, SortingObject sortingObject, UserEnv operator, Date operateTime);

    ApiResult<Void> sortWithoutContainer(SortingObject sortingObject,UserEnv operator,Date operateTime);

    ApiResult<Void> cancelSorting(String barCode,SortingCancelObjectType type,Integer operateSiteId,UserEnv operator,Date operateTime);
}
