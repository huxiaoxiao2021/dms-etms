package com.jd.bluedragon.distribution.areadest.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;

import java.util.List;
import java.util.Map;

/**
 * 龙门架发货关系方案
 *
 * Created by lixin39 on 2017/3/16.
 */
public class AreaDestPlanDao extends BaseDao<AreaDestPlan>{

    private final static String namespace = AreaDestPlanDao.class.getName();

    /**
     * 新增关系方案
     *
     * @param areaDestPlan
     * @return
     */
    public int add(AreaDestPlan areaDestPlan){
        return this.getSqlSession().insert(AreaDestPlanDao.namespace + ".add", areaDestPlan);
    }

    /**
     * 获取龙门架发货关系方案列表
     *
     * @param parameters
     * @return
     */
    public List<AreaDestPlan> getList(Map<String, Object> parameters){
        return this.getSqlSession().selectList(AreaDestPlanDao.namespace + ".getList", parameters);
    }

    /**
     * 根据主键id设置为无效
     *
     * @param parameters
     */
    public int disableById(Map<String, Object> parameters) {
        return this.getSqlSession().update(AreaDestPlanDao.namespace + ".disableById", parameters);
    }

    /**
     * 根据主键启用发货方案
     */
    public int enableById(Map<String,Object> params){
        return this.getSqlSession().update(AreaDestPlanDao.namespace + ".enableById",params);
    }

    /**
     * 根据龙门架关闭除了选中方案以外的其他方案
     */
    public int disableByMachineId(Map<String ,Object> params) {
        return this.getSqlSession().update(AreaDestPlanDao.namespace + ".disableByMachineId",params);
    }

}
