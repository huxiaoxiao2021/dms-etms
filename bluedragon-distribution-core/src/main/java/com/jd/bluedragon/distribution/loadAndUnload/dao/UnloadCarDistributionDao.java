package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/6/23 11:27
 */
public class UnloadCarDistributionDao extends BaseDao<UnloadCar> {

    public static final String namespace = UnloadCarDistributionDao.class.getName();

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCar detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }

}
