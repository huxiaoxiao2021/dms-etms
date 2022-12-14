package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.JySendAggsDaoInterface;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;

import java.util.List;

/**
 * 发货数据统计表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-30 15:26:08
 */
public class JySendAggsDao2 extends BaseDao<JySendAggsEntity> implements JySendAggsDaoInterface {

    private final static String NAMESPACE = JySendAggsDao2.class.getName();

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

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insertOrUpdate(JySendAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertOrUpdate", entity);
    }
}
