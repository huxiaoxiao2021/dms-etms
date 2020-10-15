package com.jd.bluedragon.distribution.loadAndUnload.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务协助人
 * @author: wuming
 * @create: 2020-10-15 20:21
 */
public class LoadCarHelperDao extends BaseDao<LoadCarHelper> {

    public static final String namespace = LoadCarHelperDao.class.getName();

    public int add(LoadCarHelper detail) {
        return this.getSqlSession().insert(namespace + ".add", detail);
    }


}
