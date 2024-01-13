package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingTaskAggQueryDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public class JyPickingTaskAggsDao extends BaseDao<JyPickingTaskAggsEntity> {
    private final static String NAMESPACE = JyPickingTaskAggsDao.class.getName();


    public List<JyPickingTaskAggsEntity> findByBizIdList(List<String> bizIdList, Long siteId) {
        JyPickingTaskAggsCondition condition = new JyPickingTaskAggsCondition();
        condition.setBizIdList(bizIdList);
        condition.setPickingSiteId(siteId);
        return this.getSqlSession().selectList(NAMESPACE + ".findByBizIdsAndCondition", condition);
    }

    public List<String> pageRecentWaitScanEqZero(JyPickingTaskAggQueryDto queryDto) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageRecentWaitScanEqZero", queryDto);
    }
    public int insertSelective(JyPickingTaskAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyPickingTaskAggsEntity findByBizId(String bizId, Long siteId) {
        JyPickingTaskAggsEntity queryEntity = new JyPickingTaskAggsEntity(siteId, bizId);
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", queryEntity);
    }

    public int updatePickingAggWaitScanItemNum(String bizId, Long siteId, Integer waitScanTotalItemNum) {
        JyPickingTaskAggsEntity updateEntity = new JyPickingTaskAggsEntity(siteId, bizId);
        updateEntity.setWaitScanTotalCount(waitScanTotalItemNum);
        updateEntity.setUpdateTime(new Date());
        return this.getSqlSession().update(NAMESPACE + ".updatePickingAggWaitScanItemNum", updateEntity);
    }

    public int updateScanStatistics(JyPickingTaskAggsEntity updateEntity) {
        return this.getSqlSession().update(NAMESPACE + ".updateScanStatistics", updateEntity);
    }
}