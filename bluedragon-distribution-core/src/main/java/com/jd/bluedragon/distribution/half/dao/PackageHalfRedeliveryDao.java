package com.jd.bluedragon.distribution.half.dao;

import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedeliveryCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;

/**
 *
 * @ClassName: PackageHalfRedeliveryDao
 * @Description: 包裹半收协商再投业务表--Dao接口
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
public interface PackageHalfRedeliveryDao extends Dao<PackageHalfRedelivery> {

    /**
     * 按运单更新协商再投包裹状态（分拣操作半收时调用）
     * @param packageHalfRedelivery
     * @return
     */
    public int  updateDealStateByWaybillCode(PackageHalfRedelivery packageHalfRedelivery);

    /**
     * 根据条件查询运单是否已经存在
     * @param condition
     * @return
     */
    public String queryExistsByPagerCondition(PackageHalfRedeliveryCondition condition);
}
