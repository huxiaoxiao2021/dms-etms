package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.distribution.areadest.dao.AreaDestPlanDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 龙门架发货关系方案
 * <p>
 * Created by lixin39 on 2017/3/17.
 */
@Service("areaDestPlanService")
public class AreaDestPlanServiceImpl implements AreaDestPlanService {

    private final Logger logger = Logger.getLogger(AreaDestServiceImpl.class);

    @Autowired
    private AreaDestPlanDao areaDestPlanDao;

    @Override
    public boolean add(AreaDestPlan areaDestPlan) {
        try {
            if (areaDestPlanDao.add(areaDestPlan) == 1) {
                return true;
            }
        } catch (Exception e) {
            logger.error("龙门架发货关系方案新增失败", e);
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
            logger.error("龙门架发货关系方案移除失败", e);
        }
        return false;
    }

    @Override
    public List<AreaDestPlan> getList(Integer operateSiteCode, Integer machineId) {
        try{
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("operateSiteCode", operateSiteCode);
            parameters.put("machineId", machineId);
            return areaDestPlanDao.getList(parameters);
        }catch (Exception e){
            logger.error("龙门架发货关系方案列表获取失败", e);
        }
        return null;
    }

    @Override
    public Boolean ModifyGantryPlan(Integer machineId, Long planId, Long userCode, Integer siteCode) {
        return null;
    }
}
