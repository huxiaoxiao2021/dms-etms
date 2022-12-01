package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;

import java.util.List;

/**
 * 发货数据统计表
 * 
 * @author chenyaguo
 * @email
 * @date 2022-11-02 15:26:08
 */
public class JySendProductAggsDao extends BaseDao<JySendProductAggsEntity> {

    private final static String NAMESPACE = JySendProductAggsDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendProductAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public JySendProductAggsEntity getVehicleSendStatistics(String sendVehicleBizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".getVehicleSendStatistics", sendVehicleBizId);
    }

    public List<JySendProductAggsEntity> findBySendVehicleBizId(String sendVehicleBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".findBySendVehicleBizId", sendVehicleBizId);
    }

    public List<JySendVehicleProductType> getSendVehicleProductTypeList(String sendVehicleBizId){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendVehicleProductTypeList", sendVehicleBizId);
    }

    public Long getToScanCountSum(String sendVehicleBizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".getToScanCountSum", sendVehicleBizId);
    }
}
