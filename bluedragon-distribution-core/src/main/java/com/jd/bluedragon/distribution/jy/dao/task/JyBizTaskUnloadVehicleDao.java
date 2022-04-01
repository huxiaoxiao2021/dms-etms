package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;

/**
 * 卸车业务任务表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:34
 */
public class JyBizTaskUnloadVehicleDao extends BaseDao<JyBizTaskUnloadVehicleEntity> {

    final static String NAMESPACE = JyBizTaskUnloadVehicleDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskUnloadVehicleEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
