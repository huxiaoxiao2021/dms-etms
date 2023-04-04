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
class JySendProductAggsDao extends BaseDao<JySendProductAggsEntity>  implements JySendProductAggsDaoStrategy{

    private final static String NAMESPACE = JySendProductAggsDao.class.getName();

    public List<JySendVehicleProductType> getSendVehicleProductTypeList(JySendProductAggsEntityQuery query){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendVehicleProductTypeList", query);
    }

    public Long getToScanCountSum(JySendProductAggsEntityQuery query){
        return this.getSqlSession().selectOne(NAMESPACE + ".getToScanCountSum", query);
    }


    public List<JySendProductAggsEntity> getSendAggsListByCondition(JySendProductAggsEntityQuery query) {
        return this.getSqlSession().selectList(NAMESPACE + ".getSendAggsListByCondition", query);
    }


}
