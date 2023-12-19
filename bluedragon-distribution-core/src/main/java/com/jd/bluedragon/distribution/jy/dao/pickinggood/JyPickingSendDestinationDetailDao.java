package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.FinishSendTaskReq;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendDestinationDetailEntity;

public class JyPickingSendDestinationDetailDao extends BaseDao<JyPickingSendDestinationDetailEntity> {
    private final static String NAMESPACE = JyPickingSendDestinationDetailDao.class.getName();
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

    public int updateSendTaskStatus(JyPickingSendDestinationDetailEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateSendTaskStatus", entity);
    }
}