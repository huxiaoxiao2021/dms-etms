package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;

public class JyPickingSendDestinationDetailDao extends BaseDao<JyPickingSendDestinationDetailEntity> {
    private final static String NAMESPACE = JyPickingSendDestinationDetailDao.class.getName();


    public String fetchLatestNoCompleteBatchCode(Long curSiteId, Long nextSiteId) {
        JyPickingSendDestinationDetailEntity entity = new JyPickingSendDestinationDetailEntity();
        entity.setCreateSiteId(curSiteId);
        entity.setNextSiteId(nextSiteId);
        return this.getSqlSession().selectOne(NAMESPACE + ".fetchLatestNoCompleteBatchCode", entity);
    }

    public int insertSelective(JyPickingSendDestinationDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyPickingSendDestinationDetail record);
//
//    int insertSelective(JyPickingSendDestinationDetail record);
//
//    JyPickingSendDestinationDetail selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyPickingSendDestinationDetail record);
//
//    int updateByPrimaryKey(JyPickingSendDestinationDetail record);
}