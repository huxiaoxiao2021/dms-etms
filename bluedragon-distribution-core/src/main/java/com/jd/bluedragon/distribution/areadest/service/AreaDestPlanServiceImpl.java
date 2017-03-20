package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestPlanDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlan;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.utils.ObjectMapHelper;
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

    private final Logger logger = Logger.getLogger(AreaDestPlanServiceImpl.class);

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
        List<AreaDestPlan> list = null;
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (operateSiteCode != null ){
                parameter.put("operateSiteCode", operateSiteCode);
            }
            if(machineId != null){
                parameter.put("machineId", machineId);
            }
            list = areaDestPlanDao.getList(parameter);
        } catch (Exception e) {
            logger.error("龙门架发货关系方案列表获取失败", e);
        }
        return list;
    }

    @Override
    public List<AreaDestPlan> getList(Integer operateSiteCode, Integer machineId, Pager pager) {
        List<AreaDestPlan> list = null;
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (operateSiteCode != null ){
                parameter.put("operateSiteCode", operateSiteCode);
            }
            if(machineId != null){
                parameter.put("machineId", machineId);
            }
            int count = areaDestPlanDao.getCount(parameter);

            if (pager == null) {
                pager = new Pager();
            }

            if (count > 0) {
                pager.setTotalSize(count);
                pager.init();
                parameter.put("startIndex", pager.getStartIndex());
                parameter.put("pageSize", pager.getPageSize());
                list = areaDestPlanDao.getList(parameter);
            }
        } catch (Exception e) {
            logger.error("龙门架发货关系方案列表获取失败", e);
        }
        return list;
    }

}
