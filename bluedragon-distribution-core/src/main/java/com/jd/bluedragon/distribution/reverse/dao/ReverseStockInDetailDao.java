package com.jd.bluedragon.distribution.reverse.dao;

import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetail;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库明细
 * @author: liuduo8
 * @create: 2019-12-18 21:31
 **/
public interface ReverseStockInDetailDao extends Dao<ReverseStockInDetail> {


    List<ReverseStockInDetail> findByWaybillCodeAndType(ReverseStockInDetail reverseStockInDetail);

    boolean updateStatus(ReverseStockInDetail reverseStockInDetail);
}
