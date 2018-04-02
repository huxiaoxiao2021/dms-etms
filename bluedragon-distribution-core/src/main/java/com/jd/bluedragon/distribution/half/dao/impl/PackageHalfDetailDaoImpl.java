package com.jd.bluedragon.distribution.half.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.dao.PackageHalfDetailDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: PackageHalfDetailDaoImpl
 * @Description: 包裹半收操作明细表--Dao接口实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
public class PackageHalfDetailDaoImpl extends BaseDao<PackageHalfDetail> implements PackageHalfDetailDao {


    @Override
    public List<PackageHalfDetail> getPackageHalfDetailByWaybillCode(String waybillCode) {
        return sqlSession.selectList(this.nameSpace+".getPackageHalfDetailByWaybillCode", waybillCode);
    }

    @Override
    public void deleteOfSaveFail(String waybillCode) {
        sqlSession.delete(this.nameSpace+".deleteOfSaveFail", waybillCode);
    }
}
