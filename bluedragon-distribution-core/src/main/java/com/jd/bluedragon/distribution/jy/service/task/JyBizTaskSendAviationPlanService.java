package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:08
 * @Description
 */
public interface JyBizTaskSendAviationPlanService {

    int initTaskSendVehicle(JyBizTaskSendAviationPlanEntity entity);

    JyBizTaskSendAviationPlanEntity findByBizId(String bizId);

    int updateByBizId(JyBizTaskSendAviationPlanEntity updateAviationPlan);

    /**
     * 航空计划始发中途拦截取消
     * @param bizId
     * @return
     */
    Boolean aviationPlanIntercept(String bizId);
}
