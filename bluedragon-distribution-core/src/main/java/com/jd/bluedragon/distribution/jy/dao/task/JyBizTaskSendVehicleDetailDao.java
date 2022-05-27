package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;

/**
 * 发车任务明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleDetailDao extends BaseDao<JyBizTaskSendVehicleDetailEntity> {

    private final static String NAMESPACE = JyBizTaskSendVehicleDetailDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public JyBizTaskSendVehicleDetailEntity findByBizId(String bizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".select", bizId);
    }
}
