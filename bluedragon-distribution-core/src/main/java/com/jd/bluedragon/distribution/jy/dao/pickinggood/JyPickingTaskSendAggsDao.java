package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsEntity;

import java.util.Date;
import java.util.List;

public class JyPickingTaskSendAggsDao extends BaseDao<JyPickingTaskSendAggsEntity> {
    private final static String NAMESPACE = JyPickingTaskSendAggsDao.class.getName();

    public List<JyPickingTaskSendAggsEntity> findByBizIdList(List<String> bizIdList, Long siteId, Long sendNextSiteId) {

        JyPickingTaskSendAggsCondition condition = new JyPickingTaskSendAggsCondition();
        condition.setBizIdList(bizIdList);
        condition.setPickingSiteId(siteId);
        condition.setNextSiteId(sendNextSiteId);
        return this.getSqlSession().selectList(NAMESPACE + ".findByBizIdList", condition);
    }

    public int insertSelective(JyPickingTaskSendAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyPickingTaskSendAggsEntity findByBizIdAndNextSite(Long siteId, Long nextSiteId, String bizId) {
        JyPickingTaskSendAggsEntity queryEntity = new JyPickingTaskSendAggsEntity(siteId, nextSiteId, bizId);
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizIdAndNextSite", queryEntity);
    }


    public int updatePickingAggWaitScanItemNum(Long siteId, Long nextSiteId, String bizId, Integer waitScanTotalItemNum) {
        JyPickingTaskSendAggsEntity updateEntity = new JyPickingTaskSendAggsEntity(siteId, nextSiteId, bizId);
        updateEntity.setWaitScanTotalCount(waitScanTotalItemNum);
        updateEntity.setUpdateTime(new Date());
        return this.getSqlSession().update(NAMESPACE + ".updatePickingSendAggWaitScanItemNum", updateEntity);
    }
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyPickingTaskSendAggs record);
//
//    int insertSelective(JyPickingTaskSendAggs record);
//
//    JyPickingTaskSendAggs selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyPickingTaskSendAggs record);
//
//    int updateByPrimaryKey(JyPickingTaskSendAggs record);
}