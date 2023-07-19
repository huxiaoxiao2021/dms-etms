package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;
import com.jd.bluedragon.distribution.jy.send.JySendPredictProductType;

import java.util.List;

/**
 * 发货数据统计表
 * 
 * @author chenyaguo
 * @email
 * @date 2022-11-02 15:26:08
 */
public class JySendPredictAggsDao extends BaseDao<JySendPredictAggsPO>  {

    private final static String NAMESPACE = JySendPredictAggsDao.class.getName();


    public Long getunScanSumByCondition(JySendPredictAggsRequest request) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getunScanSumByCondition", request);
    }



    public List<JySendPredictProductType> getSendPredictProductTypeList(JySendPredictAggsRequest query){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendPredictProductTypeList", query);
    }

    public int updateByBizProduct(JySendPredictAggsPO entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizProduct", entity);
    }

    public int insert(JySendPredictAggsPO entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }



}
