package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;

import java.util.List;

/**
 * 发货数据统计表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendAggsDaoBak extends BaseDao<JySendAggsEntity> implements JySendAggsDaoStrategy {

    private final static String NAMESPACE = JySendAggsDaoBak.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".getVehicleSendStatistics", sendVehicleBizId);
    }

    public List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".findBySendVehicleBiz", sendVehicleBizId);
    }

    public int updateByBizId(JySendAggsEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }

    public int insertBySendAggEntity(JySendAggsEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertBySendAggEntity", entity);
    }

    public List<JySendAggsEntity> getSendAggBakData(JySendAggsEntity query){
        return this.getSqlSession().selectList(NAMESPACE + ".getSendAggMainData", query);
    }
}
