package com.jd.bluedragon.distribution.reverse.dao.impl;

import com.jd.bluedragon.distribution.reverse.dao.ReverseStockInDetailDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetail;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库明细
 * @author: liuduo8
 * @create: 2019-12-18 21:31
 **/

public class ReverseStockInDetailDaoImpl extends BaseDao<ReverseStockInDetail> implements ReverseStockInDetailDao {

    @Override
    public List<ReverseStockInDetail> findByWaybillCodeAndType(ReverseStockInDetail reverseStockInDetail) {
        return getSqlSession().selectList(this.nameSpace+".findByWaybillCodeAndType",reverseStockInDetail);
    }

    @Override
    public boolean updateStatus(ReverseStockInDetail reverseStockInDetail) {
        return getSqlSession().update(this.nameSpace+".updateStatus",reverseStockInDetail)>0;
    }

    /**
     * 根据固定参数获取返回数据 默认只返回有效数据
     * 防止全表返回 默认查询条件带运单号
     * @param reverseStockInDetail
     * @return
     */
    @Override
    public List<ReverseStockInDetail> findByParam(ReverseStockInDetail reverseStockInDetail) {

        return getSqlSession().selectList(this.nameSpace + ".findByParam",reverseStockInDetail);
    }
}
