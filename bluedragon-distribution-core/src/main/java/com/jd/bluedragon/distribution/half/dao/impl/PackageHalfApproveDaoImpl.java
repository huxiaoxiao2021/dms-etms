package com.jd.bluedragon.distribution.half.dao.impl;

import com.jd.bluedragon.distribution.half.domain.PackageHalfApproveCondition;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.half.domain.PackageHalfApprove;
import com.jd.bluedragon.distribution.half.dao.PackageHalfApproveDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: PackageHalfApproveDaoImpl
 * @Description: 包裹半收协商再投终端提交业务表--Dao接口实现
 * @author wuyoude
 * @date 2018年04月11日 18:45:19
 *
 */
@Repository("packageHalfApproveDao")
public class PackageHalfApproveDaoImpl extends BaseDao<PackageHalfApprove> implements PackageHalfApproveDao {

    @Override
    public PackageHalfApprove queryOneByWaybillCode(PackageHalfApproveCondition condition) {
        return sqlSession.selectOne(this.nameSpace+".queryOneByWaybillCode", condition);
    }

    @Override
    public List<PackageHalfApprove> queryListByWaybillCode(List<String> wayBillCodes) {
        return sqlSession.selectList(this.nameSpace+".queryListByWaybillCode", wayBillCodes);
    }
}
