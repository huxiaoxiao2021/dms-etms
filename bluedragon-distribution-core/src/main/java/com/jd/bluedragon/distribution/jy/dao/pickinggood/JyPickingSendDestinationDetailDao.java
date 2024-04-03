package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;

import java.util.List;

public class JyPickingSendDestinationDetailDao extends BaseDao<JyPickingSendDestinationDetailEntity> {
    private final static String NAMESPACE = JyPickingSendDestinationDetailDao.class.getName();


    public String fetchLatestNoCompleteBatchCode(Long curSiteId, Long nextSiteId, Integer taskType) {
        JyPickingSendDestinationDetailEntity entity = new JyPickingSendDestinationDetailEntity();
        entity.setCreateSiteId(curSiteId);
        entity.setNextSiteId(nextSiteId);
        entity.setTaskType(taskType);
        return this.getSqlSession().selectOne(NAMESPACE + ".fetchLatestNoCompleteBatchCode", entity);
    }

    public int insertSelective(JyPickingSendDestinationDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int finishSendTask(JyPickingSendDestinationDetailEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".finishSendTask", entity);
    }

    public JyPickingSendDestinationDetailEntity getSendDetailBySiteId(JyPickingSendDestinationDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getSendDetailBySiteId", entity);
    }
    //删除批次
    public int delBatchCodes(JyPickingSendDestinationDetailCondition condition) {
        return this.getSqlSession().update(NAMESPACE + ".delBatchCodes", condition);
    }
    //批次列表查询
    public List<JyPickingSendDestinationDetailEntity> pageFetchSendBatchCodeDetailList(JyPickingSendDestinationDetailCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageFetchSendBatchCodeDetailList", condition);
    }
}