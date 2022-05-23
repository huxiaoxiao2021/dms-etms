package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

/**
 * 发车业务任务表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleDao extends BaseDao<JyBizTaskSendVehicleEntity> {

    private final static String NAMESPACE = JyBizTaskSendVehicleDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    /**
     * 根据bizID查询任务详情
     * @param bizId
     * @return
     */
    public JyBizTaskSendVehicleEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }
    public int updateById(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".update", entity);
    }
    public int updateByBizId(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }
}
