package com.jd.bluedragon.distribution.half.service;

import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: PackageHalfRedeliveryService
 * @Description: 包裹半收协商再投业务表--Service接口
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
public interface PackageHalfRedeliveryService extends Service<PackageHalfRedelivery> {

    /**
     * 按运单更新协商再投包裹状态（分拣操作半收时调用）
     * @param waybillCode
     * @param updateUserCode
     * @param updateUserErp
     * @param updateUserName
     * @return
     */
    public int  updateDealStateByWaybillCode(String waybillCode, Integer updateUserCode, String updateUserErp, String  updateUserName);

    /**
     * 根据运单号和站点ID查询运单是否已经存在
     * @param waybillCode
     * @param siteCode
     * @return
     */
    public String queryExistsByWaybillCodeAndSiteCode(String waybillCode, Integer siteCode);
}
