package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;

import java.util.List;

/**
 * 龙门架发货关系方案
 * <p>
 * Created by lixin39 on 2017/3/17.
 */
public interface AreaDestPlanService {

    /**
     * 新增关系方案
     *
     * @param areaDestPlan
     * @return
     */
    boolean add(AreaDestPlan areaDestPlan);

    /**
     * 删除
     *
     * @param id
     * @param updateUser
     * @param updateUserCode
     * @return
     */
    boolean delete(Integer id, String updateUser, Integer updateUserCode);

    /**
     * 根据方案编号获取方案信息
     *
     * @param planId
     * @return
     */
    AreaDestPlan get(Integer planId);

    /**
     * 获取方案列表
     *
     * @param operateSiteCode
     * @param machineId
     * @return
     */
    List<AreaDestPlan> getList(Integer operateSiteCode, Integer machineId);

    /**
     * 分页获取方案列表
     *
     * @param operateSiteCode
     * @param machineId
     * @param pager
     * @return
     */
    List<AreaDestPlan> getList(Integer operateSiteCode, Integer machineId, Pager pager);

    /**
     * 获取当前分拣中心的当前龙门架设备的所有发货方案
     * @param siteCode
     * @param machineId
     * @return
     */
    List<AreaDestPlan> getMyPlan(Integer siteCode,Integer machineId);

    /**
     * 龙门架方案切换
     * @param machineId
     * @param planId
     * @param userCode
     * @param siteCode
     * @return
     */
    Boolean ModifyGantryPlan(Integer machineId,Long planId,Integer userCode,Integer siteCode);

}
