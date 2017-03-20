package com.jd.bluedragon.distribution.areadest.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;

import java.util.List;
import java.util.Map;

/**
 * 龙门架发货关系方案
 * <p>
 * Created by lixin39 on 2017/3/16.
 */
public class AreaDestPlanDao extends BaseDao<AreaDestPlan> {

    private final static String namespace = AreaDestPlanDao.class.getName();

    /**
     * 新增关系方案
     *
     * @param areaDestPlan
     * @return
     */
    public int add(AreaDestPlan areaDestPlan) {
        return this.getSqlSession().insert(AreaDestPlanDao.namespace + ".add", areaDestPlan);
    }

    /**
     * 获取龙门架发货关系方案列表
     *
     * @param parameters
     * @return
     */
    public List<AreaDestPlan> getList(Map<String, Object> parameters) {
        return this.getSqlSession().selectList(AreaDestPlanDao.namespace + ".getList", parameters);
    }

    /**
     * 获取龙门架发货关系方案数量
     *
     * @param parameters
     * @return
     */
    public int getCount(Map<String, Object> parameters) {
        return this.getSqlSession().selectOne(AreaDestPlanDao.namespace + ".getCount", parameters);
    }

    /**
     * 根据主键id设置为无效
     *
     * @param parameters
     */
    public int disableById(Map<String, Object> parameters) {
        return this.getSqlSession().update(AreaDestPlanDao.namespace + ".disableById", parameters);
    }

}
