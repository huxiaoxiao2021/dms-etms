package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskAviationAirTypeStatistics;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskAviationStatusStatistics;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanQueryCondition;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:08
 * @Description
 */
public interface JyBizTaskSendAviationPlanService {

    int initTaskSendVehicle(JyBizTaskSendAviationPlanEntity entity);

    JyBizTaskSendAviationPlanEntity findByBizId(String bizId);

    List<JyBizTaskSendAviationPlanEntity> findByBizIdList(List<String> bizIdList);

    int updateByBizId(JyBizTaskSendAviationPlanEntity updateAviationPlan);

    /**
     * 航空计划始发中途拦截取消 true: 需要拦截
     * @param bizId
     * @return
     */
    boolean aviationPlanIntercept(String bizId);

    List<AviationNextSiteStatisticsDto> queryNextSitesByStartSite(JyBizTaskSendAviationPlanQueryCondition param);

    List<JyBizTaskAviationStatusStatistics> toSendAndSendingStatusStatistics(JyBizTaskSendAviationPlanQueryCondition condition);

    List<JyBizTaskAviationStatusStatistics> statusStatistics(JyBizTaskSendAviationPlanQueryCondition condition);

    List<JyBizTaskSendAviationPlanEntity> pageFetchAviationTaskByNextSite(JyBizTaskSendAviationPlanQueryCondition condition);

    List<JyBizTaskAviationAirTypeStatistics> airTypeStatistics(Integer siteCode);

    List<JyBizTaskSendAviationPlanEntity> pageFindAirportInfoByCurrentSite(JyBizTaskSendAviationPlanQueryCondition condition);

    List<JyBizTaskSendAviationPlanEntity> pageQueryAviationPlanByCondition(JyBizTaskSendAviationPlanQueryCondition condition);

    int updateStatus(JyBizTaskSendAviationPlanEntity entity);

    List<JyBizTaskSendAviationPlanEntity> findNoSealTaskByBizIds(List<String> bizIds);
    
    List<JyBizTaskSendAviationPlanEntity> pageQueryRecommendTaskByNextSiteId(JyBizTaskSendAviationPlanQueryCondition condition);
}
