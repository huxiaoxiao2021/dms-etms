package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.distribution.areadest.dao.AreaDestPlanDetailDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixin39 on 2017/3/17.
 */
@Service("areaDestPlanDetailService")
public class AreaDestPlanDetailServiceImpl implements AreaDestPlanDetailService {

    private final Logger log = LoggerFactory.getLogger(AreaDestPlanDetailServiceImpl.class);

    @Autowired
    private AreaDestPlanDetailDao areaDestPlanDetailDao;

    @Override
    public boolean add(AreaDestPlanDetail detail) {
        try {
            if (areaDestPlanDetailDao.add(detail) == 1) {
                return true;
            }
        } catch (Exception e) {
            log.error("龙门架发货关系方案操作流水新增失败", e);
        }
        return false;
    }

    @Override
    public AreaDestPlanDetail getByScannerTime(Integer machineId, Integer operateSiteCode, Date scannerTime) {
        try {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("machineId", machineId);
            parameters.put("operateSiteCode", operateSiteCode);
            parameters.put("scannerTime", scannerTime);
            return areaDestPlanDetailDao.getByScannerTime(parameters);
        } catch (Exception e) {
            log.error("根据龙门架扫描时间获取方案启动记录失败", e);
        }
        return null;
    }

}
