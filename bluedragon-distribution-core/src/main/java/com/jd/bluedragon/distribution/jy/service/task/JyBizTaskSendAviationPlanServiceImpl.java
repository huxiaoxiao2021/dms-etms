package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendAviationPlanDao;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.task.*;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:08
 * @Description  空铁库表查询服务
 */
@Service
public class JyBizTaskSendAviationPlanServiceImpl implements JyBizTaskSendAviationPlanService {

    public static final Integer nextSiteListQueryPageLimitMax = 200;
    public static final Integer nextSiteListQueryPageLimitDefault = 50;

    @Value("${jyAviationSendSealListNextSiteQueryLimit:50}")
    private int aviationSendSealListNextSiteQueryLimit;
    @Value("${jyAviationSendSealToSendQueryTakeOffTimeStartHour:24}")
    private int toSendQueryTakeOffTimeStartHour;

    @Autowired
    private JyBizTaskSendAviationPlanDao jyBizTaskSendAviationPlanDao;
    @Autowired
    private JyBizTaskSendAviationPlanCacheService aviationPlanCacheService;
    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


    @Override
    public int initTaskSendVehicle(JyBizTaskSendAviationPlanEntity entity) {
        if(!Objects.isNull(this.findByBizId(entity.getBizId()))) {
            return 0;
        }
        return jyBizTaskSendAviationPlanDao.insertSelective(entity);
    }

    @Override
    public JyBizTaskSendAviationPlanEntity findByBizId(String bizId) {
        return jyBizTaskSendAviationPlanDao.findByBizId(bizId);
    }

    @Override
    public List<JyBizTaskSendAviationPlanEntity> findByBizIdList(List<String> bizIdList) {
        if(CollectionUtils.isEmpty(bizIdList)) {
            return null;
        }
        return jyBizTaskSendAviationPlanDao.findByBizIdList(bizIdList);
    }

    @Override
    public int updateByBizId(JyBizTaskSendAviationPlanEntity entity) {
        if(Objects.isNull(entity)) {
            return 0;
        }
        return jyBizTaskSendAviationPlanDao.updateByBizId(entity);
    }

    @Override
    public boolean aviationPlanIntercept(String bizId) {
        if(StringUtils.isBlank(bizId)) {
            return false;
        }
        if(aviationPlanCacheService.existCacheAviationPlanCancel(bizId)) {
            return true;
        }
        JyBizTaskSendAviationPlanEntity entity = this.findByBizId(bizId);
        if(!Objects.isNull(entity) && !Objects.isNull(entity.getIntercept()) && entity.getIntercept().equals(1)) {
            aviationPlanCacheService.saveCacheAviationPlanCancel(bizId);
            return true;
        }
        return false;
    }

    @Override
    public List<AviationNextSiteStatisticsDto> queryNextSitesByStartSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        JyBizTaskSendAviationPlanQueryCondition queryCondition = new JyBizTaskSendAviationPlanQueryCondition();
        BeanUtils.copyProperties(condition, queryCondition);
        if(!NumberHelper.gt0(aviationSendSealListNextSiteQueryLimit) || aviationSendSealListNextSiteQueryLimit > nextSiteListQueryPageLimitMax) {
            aviationSendSealListNextSiteQueryLimit = nextSiteListQueryPageLimitDefault;
        }
        queryCondition.setPageSize(aviationSendSealListNextSiteQueryLimit);
        return jyBizTaskSendAviationPlanDao.queryNextSitesByStartSite(queryCondition);
    }

    @Override
    public List<JyBizTaskAviationStatusStatistics> toSendAndSendingStatusStatistics(JyBizTaskSendAviationPlanQueryCondition condition) {
        //待发货统计
        JyBizTaskSendAviationPlanQueryCondition toSendQueryCondition = new JyBizTaskSendAviationPlanQueryCondition();
        BeanUtils.copyProperties(condition, toSendQueryCondition);
        if(!JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getSendTaskStatus().equals(condition.getTaskStatus())) {
            toSendQueryCondition.setTakeOffTimeStart(DateHelper.newTimeRangeHoursAgo(new Date(), toSendQueryTakeOffTimeStartHour));
        }
        List<JyBizTaskAviationStatusStatistics> toSendRes = jyBizTaskSendAviationPlanDao.statusStatistics(toSendQueryCondition);

        //发货中统计
        JyBizTaskSendAviationPlanQueryCondition sendingQueryCondition = new JyBizTaskSendAviationPlanQueryCondition();
        BeanUtils.copyProperties(condition, sendingQueryCondition);
        sendingQueryCondition.setTakeOffTimeStart(null);
        List<JyBizTaskAviationStatusStatistics> sendingRes = jyBizTaskSendAviationPlanDao.statusStatistics(sendingQueryCondition);

        if(CollectionUtils.isEmpty(toSendRes) && CollectionUtils.isEmpty(sendingRes)) {
            return null;
        }

        Map<Integer, JyBizTaskAviationStatusStatistics> toSendMap = toSendRes.stream().collect(Collectors.toMap(k->k.getTaskStatus(), v->v));
        Map<Integer, JyBizTaskAviationStatusStatistics> sendingMap = sendingRes.stream().collect(Collectors.toMap(k->k.getTaskStatus(), v->v));

        List<JyBizTaskAviationStatusStatistics> res = new ArrayList<>();
        if(null != toSendMap.get(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getSendTaskStatus())) {
            res.add(toSendMap.get(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getSendTaskStatus()));
        }
        if(null != sendingMap.get(JyAviationRailwaySendVehicleStatusEnum.SENDING.getSendTaskStatus())) {
            res.add(sendingMap.get(JyAviationRailwaySendVehicleStatusEnum.SENDING.getSendTaskStatus()));
        }
        return res;
    }


    @Override
    public List<JyBizTaskAviationStatusStatistics> statusStatistics(JyBizTaskSendAviationPlanQueryCondition condition) {
        JyBizTaskSendAviationPlanQueryCondition queryCondition = new JyBizTaskSendAviationPlanQueryCondition();
        BeanUtils.copyProperties(condition, queryCondition);
        return jyBizTaskSendAviationPlanDao.statusStatistics(queryCondition);
    }

    @Override
    public List<JyBizTaskSendAviationPlanEntity> pageFetchAviationTaskByNextSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return jyBizTaskSendAviationPlanDao.pageFetchAviationTaskByNextSite(condition);
    }

    @Override
    public List<JyBizTaskAviationAirTypeStatistics> airTypeStatistics(Integer siteId) {
        if(!NumberHelper.gt0(siteId)) {
            return null;
        }
        return jyBizTaskSendAviationPlanDao.airTypeStatistics(siteId);
    }

    @Override
    public List<JyBizTaskSendAviationPlanEntity> pageFindAirportInfoByCurrentSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return jyBizTaskSendAviationPlanDao.pageFindAirportInfoByCurrentSite(condition);
    }

    @Override
    public List<JyBizTaskSendAviationPlanEntity> pageQueryAviationPlanByCondition(JyBizTaskSendAviationPlanQueryCondition condition) {
        return jyBizTaskSendAviationPlanDao.pageQueryAviationPlanByCondition(condition);
    }

    @Override
    public int updateStatus(JyBizTaskSendAviationPlanEntity entity) {
        return jyBizTaskSendAviationPlanDao.updateStatus(entity);
    }

    @Override
    public List<JyBizTaskSendAviationPlanEntity> findNoSealTaskByBizIds(List<String> bizIds) {
        return jyBizTaskSendAviationPlanDao.findNoSealTaskByBizIds(bizIds);
    }

    @Override
    public List<JyBizTaskSendAviationPlanEntity> pageQueryRecommendTaskByNextSiteId(JyBizTaskSendAviationPlanQueryCondition condition) {
        return jyBizTaskSendAviationPlanDao.pageQueryRecommendTaskByNextSiteId(condition);
    }
}
