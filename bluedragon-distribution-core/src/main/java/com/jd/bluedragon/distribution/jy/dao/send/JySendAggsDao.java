package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntityQuery;

import java.util.List;

/**
 * 发货数据统计表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
class JySendAggsDao extends BaseDao<JySendAggsEntity> implements JySendAggsDaoStrategy {

    private final static String NAMESPACE = JySendAggsDao.class.getName();

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

    public List<JySendAggsEntity> getSendAggsListByCondition(JySendAggsEntityQuery query) {
        return this.getSqlSession().selectList(NAMESPACE + ".getSendAggsListByCondition", query);
    }


    public JySendAggsEntity findSendAggExistAbnormal(String sendVehicleBizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".findSendAggExistAbnormal", sendVehicleBizId);
    }
}
