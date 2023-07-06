package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;

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



    public int updateByBizProduct(JySendPredictAggsPO entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizProduct", entity);
    }

    public int insert(JySendPredictAggsPO entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }



}
