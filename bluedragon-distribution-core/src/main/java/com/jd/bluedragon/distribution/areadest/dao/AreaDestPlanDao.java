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
     * 根据方案编号获取方案信息
     *
     * @param planId
     * @return
     */
    public AreaDestPlan get(Integer planId) {
        return this.getSqlSession().selectOne(AreaDestPlanDao.namespace + ".get", planId);
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
    public Integer getCount(Map<String, Object> parameters) {
        return this.getSqlSession().selectOne(AreaDestPlanDao.namespace + ".getCount", parameters);
    }

    /**
     * 更新龙门架使用状态
     *
     * @param parameters
     * @return
     */
    public Integer updateUsingState(Map<String, Object> parameters) {
        return this.getSqlSession().update(AreaDestPlanDao.namespace + ".update", parameters);
    }

    /**
     * 获取当前龙门架设备的所有方案
     * @param params
     * @return
     */
    public List<AreaDestPlan> getMyPlans (Map<String,Object> params){
        return this.getSqlSession().selectOne(AreaDestPlanDao.namespace + ".getMyPlans",params);
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

    /**
     * 根据ID查看方案是否存在
     */
    public Boolean isExist(Map<String ,Object> params){
        AreaDestPlan result = new AreaDestPlan();
        result = this.getSqlSession().selectOne(AreaDestPlanDao.namespace + ".isExist",params);
        return  result != null;
    }

}
