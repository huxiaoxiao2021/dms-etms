package com.jd.bluedragon.distribution.middleend.manager;

import com.jd.ql.shared.services.sorting.api.ApiResult;
import com.jd.ql.shared.services.sorting.api.dto.SortingCancelObjectType;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;
import com.jd.ql.shared.services.sorting.api.dto.UserEnv;

import java.util.Date;

public interface MiddleEndSortingManager {
    /**
     * 有容器分拣--箱号
     * @param containerCode 容器编码
     * @param sortingObject 分拣对象
     * @param operator  操作人
     * @param operateTime 操作时间
     * @return
     */
    ApiResult<Void> sort(String containerCode, SortingObject sortingObject, UserEnv operator, Date operateTime);

    /**
     * 无容器分拣--原包/运单
     * @param sortingObject 分拣对象
     * @param operator 操作人
     * @param operateTime 操作时间
     * @return
     */
    ApiResult<Void> sortWithoutContainer(SortingObject sortingObject,UserEnv operator,Date operateTime);

    /**
     * 取消分拣
     * @param barCode 扫描码：箱号/运单号/包裹号
     * @param type 取消类型
     * @param operateSiteId 操作分拣ID
     * @param operator 操作人
     * @param operateTime 操作时间
     * @return
     */
    ApiResult<Void> cancelSorting(String barCode,SortingCancelObjectType type,Integer operateSiteId,UserEnv operator,Date operateTime);
}
