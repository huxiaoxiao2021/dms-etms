package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
public class JyBizTaskPickingGoodDao extends BaseDao<JyBizTaskPickingGoodEntity> {

    private final static String NAMESPACE = JyBizTaskPickingGoodDao.class.getName();

    public JyBizTaskPickingGoodEntity findByBizIdWithYn(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizIdWithYn", entity);
    }


//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyBizTaskPickingGood record);
//
//    int insertSelective(JyBizTaskPickingGood record);
//
//    JyBizTaskPickingGood selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyBizTaskPickingGood record);
//
//    int updateByPrimaryKey(JyBizTaskPickingGood record);
}