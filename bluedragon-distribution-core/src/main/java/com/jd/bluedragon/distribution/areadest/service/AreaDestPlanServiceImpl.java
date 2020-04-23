package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestPlanDao;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestPlanDetailDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;
import com.jd.bluedragon.utils.UsingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 龙门架发货关系方案
 * <p>
 * Created by lixin39 on 2017/3/17.
 */
@Service("areaDestPlanService")
public class AreaDestPlanServiceImpl implements AreaDestPlanService {

    private final Logger log = LoggerFactory.getLogger(AreaDestPlanServiceImpl.class);

    @Autowired
    private AreaDestPlanDao areaDestPlanDao;

    @Autowired
    private AreaDestPlanDetailDao areaDestPlanDetailDao;

    @Override
    public boolean add(AreaDestPlan areaDestPlan) {
        try {
            if (areaDestPlanDao.add(areaDestPlan) == 1) {
                return true;
            }
        } catch (Exception e) {
            log.error("龙门架发货关系方案新增失败", e);
        }
        return false;
    }

    @Override
    public boolean delete(Integer id, String updateUser, Integer updateUserCode) {
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("planId", id);
            parameters.put("updateUser", updateUser);
            parameters.put("updateUserCode", updateUserCode);
            areaDestPlanDao.disableById(parameters);
            return true;
        } catch (Exception e) {
            log.error("龙门架发货关系方案移除失败", e);
        }
        return false;
    }

    @Override
    public AreaDestPlan get(Integer planId) {
        try {
            if (planId != null) {
                return areaDestPlanDao.get(planId);
            }
        } catch (Exception e) {
            log.error("根据方案编号获取龙门架发货关系方案信息发生异常", e);
        }
        return null;
    }

    @Override
    public List<AreaDestPlan> getList(Integer operateSiteCode, Integer machineId) {
        List<AreaDestPlan> list = null;
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (operateSiteCode != null) {
                parameter.put("operateSiteCode", operateSiteCode);
            }
            if (machineId != null) {
                parameter.put("machineId", machineId);
            }
            list = areaDestPlanDao.getList(parameter);
        } catch (Exception e) {
            log.error("龙门架发货关系方案列表获取失败", e);
        }
        return list;
    }

    @Override
    public List<AreaDestPlan> getList(Integer operateSiteCode, Integer machineId, Pager pager) {
        List<AreaDestPlan> list = null;
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (operateSiteCode != null) {
                parameter.put("operateSiteCode", operateSiteCode);
            }
            if (machineId != null) {
                parameter.put("machineId", machineId);
            }
            Integer count = areaDestPlanDao.getCount(parameter);

            if (pager == null) {
                pager = new Pager();
            }

            if (count != null && count > 0) {
                pager.setTotalSize(count);
                pager.init();
                parameter.put("startIndex", pager.getStartIndex());
                parameter.put("pageSize", pager.getPageSize());
                list = areaDestPlanDao.getList(parameter);
            }
        } catch (Exception e) {
            log.error("龙门架发货关系方案列表获取失败", e);
        }
        return list;
    }

    @Override
    public Boolean isRepeatName(Integer operateSiteCode, String planName) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("operateSiteCode", operateSiteCode);
        parameter.put("planName", planName);
        if (areaDestPlanDao.getCount(parameter) > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean updateUsingState(Integer planId, UsingState state) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("planId", planId);
        if (state != null){
            parameter.put("state", state.getState());
        }
        if (areaDestPlanDao.updateUsingState(parameter) > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean isUsing(Integer planId) {
        AreaDestPlan plan = this.get(planId);
        if (plan != null){
            if (UsingState.USING.getState() == plan.getState()){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public List<AreaDestPlan> getMyPlan(Integer siteCode, Integer machineId) {
        List<AreaDestPlan> plan = new ArrayList<AreaDestPlan>();
        if (null != machineId) {
            if (this.log.isInfoEnabled()) {
                this.log.info("分拣中心：{},龙门架设备ID：{},请求获取当前分配方案",siteCode,machineId);
            }
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("siteCode", siteCode);
            param.put("machineId", machineId);
            plan = areaDestPlanDao.getMyPlans(param);
        }
        return plan;
    }

    @Override
    public Boolean modifyGantryPlan(Integer machineId, Long planId, Integer userCode, Integer siteCode) {
        Boolean bool = Boolean.FALSE;
        if (this.log.isInfoEnabled()) {
            this.log.info("修改龙门架方案操作--machineId：{},planId:{},userCode:{},siteCode:{}",machineId,planId,userCode, siteCode);
        }
        if (null != machineId && null != planId && null != siteCode && null != userCode) {
            if (isExist(planId, siteCode, machineId)) {
                /** 切换方案只要插入流水表就行 **/
                if (this.log.isInfoEnabled()) {
                    this.log.info("存在此方案列表,进行插入流水操作");
                }
                AreaDestPlanDetail detail = new AreaDestPlanDetail();
                detail.setMachineId(machineId);
                detail.setOperateSiteCode(siteCode);
                detail.setPlanId((int) (long) planId);
                detail.setOperateUserCode(userCode);
                detail.setStartTime(new Date());
                int i = areaDestPlanDetailDao.add(detail);
                if (i >= 1) {
                    bool = Boolean.TRUE;
                }
            }
        }
        return bool;
    }

    /**
     * 根据方案ID查看方案是不是存在
     */
    private Boolean isExist(Long planId, Integer siteCode, Integer machineId) {
        Boolean bool = Boolean.FALSE;
        if (null != planId) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("planId", planId);
            params.put("machineId", machineId);
            params.put("siteCode", siteCode);
            bool = areaDestPlanDao.isExist(params);
        }
        return bool;
    }

}
