package com.jd.bluedragon.distribution.half.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.dao.PackageHalfRedeliveryDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

/**
 *
 * @ClassName: PackageHalfRedeliveryDaoImpl
 * @Description: 包裹半收协商再投业务表--Dao接口实现
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
@Repository("packageHalfRedeliveryDao")
public class PackageHalfRedeliveryDaoImpl extends BaseDao<PackageHalfRedelivery> implements PackageHalfRedeliveryDao {

    @Override
    public int updateDealStateByWaybillCode(PackageHalfRedelivery packageHalfRedelivery) {
        return sqlSession.update(this.nameSpace+".updateDealStateByWaybillCode", packageHalfRedelivery);
    }
}
