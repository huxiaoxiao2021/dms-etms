package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyBizUnloadVehicleBatchEntity;

/**
 * 封车批次关系表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyBizUnloadVehicleBatchDao extends BaseDao<JyBizUnloadVehicleBatchEntity> {

    final static String NAMESPACE = JyBizUnloadVehicleBatchDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizUnloadVehicleBatchEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
}
