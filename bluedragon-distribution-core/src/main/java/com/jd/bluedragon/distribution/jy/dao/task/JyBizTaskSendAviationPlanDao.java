package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskAviationAirTypeStatistics;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskAviationStatusStatistics;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanQueryCondition;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:05
 * @Description
 */
public class JyBizTaskSendAviationPlanDao extends BaseDao<JyBizTaskSendAviationPlanEntity> {

    private final static String NAMESPACE = JyBizTaskSendAviationPlanDao.class.getName();

    //    int deleteByPrimaryKey(Long id);
//
//    int insert(JyNizTaskSendAviationPlanEntity record);
//
    public int insertSelective(JyBizTaskSendAviationPlanEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyBizTaskSendAviationPlanEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public List<JyBizTaskSendAviationPlanEntity> findByBizIdList(List<String> bizIdList) {
        return this.getSqlSession().selectList(NAMESPACE + ".findByBizIdList", bizIdList);
    }


    public int updateByBizId(JyBizTaskSendAviationPlanEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }

    private List<Integer> defaultAllStatus() {
        return Arrays.asList(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode(),
                JyBizTaskSendDetailStatusEnum.SENDING.getCode(),
                JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode(),
                JyBizTaskSendDetailStatusEnum.SEALED.getCode()
                );
    }

    public List<JyBizTaskAviationStatusStatistics> statusStatistics(JyBizTaskSendAviationPlanQueryCondition condition) {
        if(CollectionUtils.isEmpty(condition.getTaskStatusList())) {
            condition.setTaskStatusList(this.defaultAllStatus());
        }
        return this.getSqlSession().selectList(NAMESPACE + ".statusStatistics", condition);
    }

    public List<AviationNextSiteStatisticsDto> queryNextSitesByStartSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryNextSitesByStartSite", condition);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageFetchAviationTaskByNextSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageFetchAviationTaskByNextSite", condition);
    }

    public List<JyBizTaskAviationAirTypeStatistics> airTypeStatistics(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".airTypeStatistics", condition);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageFindAirportInfoByCurrentSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageFindAirportInfoByCurrentSite", condition);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageQueryAviationPlanByCondition(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageQueryAviationPlanByCondition", condition);
    }

    public int updateStatus(JyBizTaskSendAviationPlanEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateStatus", entity);
    }



    public List<JyBizTaskSendAviationPlanEntity> findNoSealTaskByBizIds(List<String> bizIds) {
        if(CollectionUtils.isEmpty(bizIds)) {
            return null;
        }
        JyBizTaskSendAviationPlanQueryCondition entity = new JyBizTaskSendAviationPlanQueryCondition();
        entity.setBizIdList(bizIds);
        entity.setTaskStatus(JyBizTaskSendStatusEnum.SEALED.getCode());
        return this.getSqlSession().selectList(NAMESPACE + ".findNoSealTaskByBizIds", entity);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageQueryRecommendTaskByNextSiteId(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageQueryRecommendTaskByNextSiteId", condition);
    }

    public int batchUpdateShuttleSealFlag(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateShuttleSealFlag", condition);
    }

}
