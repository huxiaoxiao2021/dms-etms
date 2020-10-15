package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务
 * @author: wuming
 * @create: 2020-10-15 20:21
 */
public class LoadCarDao extends BaseDao<LoadCar> {

    public static final String namespace = LoadCarDao.class.getName();

    public int add(LoadCar detail) {
        return this.getSqlSession().insert(namespace + ".add", detail);
    }


}
