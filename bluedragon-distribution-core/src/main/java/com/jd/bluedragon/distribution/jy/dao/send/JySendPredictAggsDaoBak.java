package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.*;

import java.util.List;

/**
 * 发货数据统计表
 * 
 * @author chenyaguo
 * @email
 * @date 2022-11-02 15:26:08
 */
class JySendPredictAggsDaoBak extends BaseDao<JySendPredictAggsPO>  implements JySendPredictAggsDaoStrategy{

    private final static String NAMESPACE = JySendPredictAggsDaoBak.class.getName();

    @Override
    public Long getunScanCountByCondition(JySendPredictAggsRequest request) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getunScanCountByCondition", request);
    }

    @Override
    public List<JySendPredictAggsPO> getListByCondition(JySendPredictAggsRequest request) {
        return this.getSqlSession().selectList(NAMESPACE + ".getListByCondition", request);
    }

    public int updateByBizProduct(JySendPredictAggsPO entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizProduct", entity);
    }

    public int insert(JySendPredictAggsPO entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }



}
