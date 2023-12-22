package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingTaskAggsEntity;

import java.util.List;

public class JyPickingTaskAggsDao extends BaseDao<JyPickingTaskAggsEntity> {
    private final static String NAMESPACE = JyPickingTaskAggsDao.class.getName();


    public List<JyPickingTaskAggsEntity> findByBizIdList(List<String> bizIdList, Long siteId) {

        JyPickingTaskAggsCondition condition = new JyPickingTaskAggsCondition();
        condition.setBizIdList(bizIdList);
        condition.setPickingSiteId(siteId);
        return this.getSqlSession().selectList(NAMESPACE + ".findByBizIdsAndCondition", condition);
    }
//
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyPickingTaskAggs record);
//
//    int insertSelective(JyPickingTaskAggs record);
//
//    JyPickingTaskAggs selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyPickingTaskAggs record);
//
//    int updateByPrimaryKey(JyPickingTaskAggs record);
}