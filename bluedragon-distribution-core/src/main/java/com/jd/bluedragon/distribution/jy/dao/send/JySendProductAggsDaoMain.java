package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
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
class JySendProductAggsDaoMain extends BaseDao<JySendProductAggsEntity>  implements JySendProductAggsDaoStrategy{

    private final static String NAMESPACE = JySendProductAggsDaoMain.class.getName();

    public List<JySendVehicleProductType> getSendVehicleProductTypeList(JySendProductAggsEntityQuery query){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendVehicleProductTypeList", query);
    }

    public Long getToScanCountSum(String sendVehicleBizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".getToScanCountSum", sendVehicleBizId);
    }


    public int updateByBizProduct(JySendProductAggsEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizProduct", entity);
    }

    public int insert(JySendProductAggsEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public List<JySendProductAggsEntity> getSendProductAggMainData(JySendProductAggsEntity entity){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendProductAggMainData", entity);
    }
}
