package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskSendAggsEntity;

import java.util.List;

public class JyPickingTaskSendAggsDao extends BaseDao<JyPickingTaskSendAggsEntity> {
    private final static String NAMESPACE = JyPickingTaskSendAggsDao.class.getName();

    public List<JyPickingTaskSendAggsEntity> findByBizIdList(List<String> bizIdList, Long siteId, Long sendNextSiteId) {

        JyPickingTaskSendAggsCondition condition = new JyPickingTaskSendAggsCondition();
        condition.setBizIdList(bizIdList);
        condition.setPickingSiteId(siteId);
        condition.setNextSiteId(sendNextSiteId);
        return this.getSqlSession().selectList(NAMESPACE + ".findByBizIdsAndCondition", condition);
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