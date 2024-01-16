package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.AirRailTaskCountDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.*;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;

import java.util.List;

public class JyBizTaskPickingGoodDao extends BaseDao<JyBizTaskPickingGoodEntity> {

    private final static String NAMESPACE = JyBizTaskPickingGoodDao.class.getName();

    public JyBizTaskPickingGoodEntity findByBizIdWithYn(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizIdWithYn", entity);
    }

    public int insertSelective(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int updateTaskByBizIdWithCondition(JyBizTaskPickingGoodEntityCondition entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateTaskByBizIdWithCondition", entity);
    }

    public JyBizTaskPickingGoodEntity findLatestEffectiveManualCreateTask(Long siteId, Integer taskType) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setNextSiteId(siteId);
        entity.setTaskType(taskType);
        return this.getSqlSession().selectOne(NAMESPACE + ".findLatestEffectiveManualCreateTask", entity);
    }

    public JyBizTaskPickingGoodEntity findLatestTaskByBusinessNumber(String businessNumber, Integer taskType) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBusinessNumber(businessNumber);
        entity.setTaskType(taskType);
        return this.getSqlSession().selectOne(NAMESPACE + ".findLatestTaskByBusinessNumber", entity);
    }
    public List<JyBizTaskPickingGoodEntity> findAllTaskByBusinessNumber(String businessNumber, Integer taskType) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBusinessNumber(businessNumber);
        entity.setTaskType(taskType);
        return this.getSqlSession().selectList(NAMESPACE + ".findAllTaskByBusinessNumber", entity);
    }


    public int deleteByBusinessNumber(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByBusinessNumber", entity);
    }

    public List<JyBizTaskPickingGoodEntity> listTaskGroupByPickingNodeCode(JyPickingTaskGroupQueryDto queryDto) {
        return this.getSqlSession().selectList(NAMESPACE + ".listTaskGroupByPickingNodeCode", queryDto);
    }

    public List<JyBizTaskPickingGoodEntity> listTaskByPickingNodeCode(JyPickingTaskBatchQueryDto queryDto) {
        return this.getSqlSession().selectList(NAMESPACE + ".listTaskByPickingNodeCode", queryDto);
    }

    public List<AirRailTaskCountDto> countAllStatusByPickingSiteId(AirRailTaskCountQueryDto countQueryDto) {
        return this.getSqlSession().selectList(NAMESPACE + ".countAllStatusByPickingSiteId", countQueryDto);
    }

    public int batchFinishPickingTaskByBizId(JyPickingTaskBatchUpdateDto updateDto) {
        return this.getSqlSession().update(NAMESPACE + ".batchFinishPickingTaskByBizId", updateDto);
    }

    public List<String> pageRecentCreatedManualBizId(JyBizTaskPickingGoodQueryDto queryDto) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageRecentCreatedManualBizId", queryDto);
    }

    public List<JyBizTaskPickingGoodEntity> listTaskByPickingSiteId(JyPickingTaskBatchQueryDto queryDto) {
        return this.getSqlSession().selectList(NAMESPACE + ".listTaskByPickingSiteId", queryDto);
    }

    public int batchInsert(List<JyBizTaskPickingGoodEntity> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", entityList);
    }

    public List<String> findManualCreateTaskBizIds(List<String> bizIdList) {
        return this.getSqlSession().selectList(NAMESPACE + ".findManualCreateTaskBizIds", bizIdList);
    }
}