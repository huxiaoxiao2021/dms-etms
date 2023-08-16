package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskAviationAirTypeStatistics;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskAviationStatusStatistics;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanQueryCondition;

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

    public List<JyBizTaskAviationStatusStatistics> statusStatistics(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".statusStatistics", condition);
    }

    public List<AviationNextSiteStatisticsDto> queryNextSitesByStartSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryNextSitesByStartSite", condition);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageFetchAviationTaskByNextSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageFetchAviationTaskByNextSite", condition);
    }

    public List<JyBizTaskAviationAirTypeStatistics> airTypeStatistics(Integer siteId) {
        return this.getSqlSession().selectList(NAMESPACE + ".airTypeStatistics", siteId);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageFindAirportInfoByCurrentSite(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageFindAirportInfoByCurrentSite", condition);
    }

    public List<JyBizTaskSendAviationPlanEntity> pageQueryAviationPlanByCondition(JyBizTaskSendAviationPlanQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageQueryAviationPlanByCondition", condition);
    }

//
//    JyNizTaskSendAviationPlanEntity selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyNizTaskSendAviationPlanEntity record);
//
//    int updateByPrimaryKey(JyNizTaskSendAviationPlanEntity record);
}
