package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.distribution.areadest.domain.AreaDestPlanDetail;

import java.util.Date;

/**
 * 龙门架发货关系方案操作流水
 * <p>
 * Created by lixin39 on 2017/3/17.
 */
public interface AreaDestPlanDetailService {

    /**
     * 新增操作流水记录
     *
     * @param detail
     * @return
     */
    boolean add(AreaDestPlanDetail detail);


    /**
     * 根据龙门架扫描时间获取方案启动记录
     *
     * @param machineId
     * @param operateSiteCode
     * @param scannerTime
     * @return
     */
    AreaDestPlanDetail getByScannerTime(Integer machineId, Integer operateSiteCode, Date scannerTime);

}
