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
class JySendProductAggsDaoBak extends BaseDao<JySendProductAggsEntity>  implements JySendProductAggsDaoStrategy{

    private final static String NAMESPACE = JySendProductAggsDaoBak.class.getName();

    public List<JySendVehicleProductType> getSendVehicleProductTypeList(JySendProductAggsEntityQuery query){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendVehicleProductTypeList", query);
    }

    public Long getToScanCountSum(JySendProductAggsEntityQuery query){
        return this.getSqlSession().selectOne(NAMESPACE + ".getToScanCountSum", query);
    }

    @Override
    public List<JySendProductAggsEntity> getToScanNumByEndSiteList(JySendProductAggsEntityQuery query) {
        return this.getSqlSession().selectList(NAMESPACE + ".getToScanNumByEndSiteList", query);
    }

    public int updateByBizProduct(JySendProductAggsEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizProduct", entity);
    }

    public int insert(JySendProductAggsEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public List<JySendProductAggsEntity> getSendProductAggBakData(JySendProductAggsEntity entity){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendProductAggBakData", entity);
    }

}
