package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.TaskStatusStatistics;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendAviationPlanDao;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanQueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:08
 * @Description  空铁库表查询服务
 */
@Service
public class JyBizTaskSendAviationPlanServiceImpl implements JyBizTaskSendAviationPlanService {

    @Autowired
    private JyBizTaskSendAviationPlanDao jyBizTaskSendAviationPlanDao;
    @Autowired
    private JyBizTaskSendAviationPlanCacheService aviationPlanCacheService;

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
    public int updateByBizId(JyBizTaskSendAviationPlanEntity entity) {
        if(Objects.isNull(entity)) {
            return 0;
        }
        return jyBizTaskSendAviationPlanDao.updateByBizId(entity);
    }

    @Override
    public Boolean aviationPlanIntercept(String bizId) {
        if(StringUtils.isBlank(bizId)) {
            return null;
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
    public List<AviationNextSiteStatisticsDto> queryNextSitesByStartSite(JyBizTaskSendAviationPlanQueryCondition param) {
        return null;
    }

    @Override
    public List<TaskStatusStatistics> statusStatistics(JyBizTaskSendAviationPlanQueryCondition condition) {
        return null;
    }




}
