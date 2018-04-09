package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.dao.PackageHalfRedeliveryDao;
import com.jd.bluedragon.distribution.half.service.PackageHalfRedeliveryService;

/**
 *
 * @ClassName: PackageHalfRedeliveryServiceImpl
 * @Description: 包裹半收协商再投业务表--Service接口实现
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
@Service("packageHalfRedeliveryService")
public class PackageHalfRedeliveryServiceImpl extends BaseService<PackageHalfRedelivery> implements PackageHalfRedeliveryService {

	@Autowired
	@Qualifier("packageHalfRedeliveryDao")
	private PackageHalfRedeliveryDao packageHalfRedeliveryDao;

	@Override
	public Dao<PackageHalfRedelivery> getDao() {
		return this.packageHalfRedeliveryDao;
	}

    /**
     * 按运单更新协商再投包裹状态（分拣操作半收时调用）
     * @param waybillCode
     * @param updateUserCode
     * @param updateUserErp
     * @param updateUserName
     * @return
     */
    @Override
    public int updateDealStateByWaybillCode(String waybillCode, Integer updateUserCode, String updateUserErp, String updateUserName) {
        PackageHalfRedelivery param = new PackageHalfRedelivery();
        param.setWaybillCode(waybillCode);
        param.setUpdateUser(updateUserErp);
        param.setUpdateUserCode(updateUserCode);
        param.setUpdateUserName(updateUserName);
        return packageHalfRedeliveryDao.updateDealStateByWaybillCode(param);
    }

    /**
     * 根据运单号和站点ID查询运单是否已经存在
     * @param waybillCode
     * @param siteCode
     * @return
     */
    @Override
    public String queryExistsByWaybillCodeAndSiteCode(String waybillCode, Integer siteCode) {
        PackageHalfRedelivery param = new PackageHalfRedelivery();
        param.setWaybillCode(waybillCode);
        param.setDmsSiteCode(siteCode);
        return packageHalfRedeliveryDao.queryExistsByPagerCondition(param);
    }

}
