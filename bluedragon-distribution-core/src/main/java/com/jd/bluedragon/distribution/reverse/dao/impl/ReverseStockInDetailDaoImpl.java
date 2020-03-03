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
}
