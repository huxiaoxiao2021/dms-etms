package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;

public class JyPickingSendRecordDao extends BaseDao<JyPickingSendRecordEntity> {
    private final static String NAMESPACE = JyPickingSendRecordDao.class.getName();

    public String fetchPickingBizIdByBarCode(JyPickingSendRecordEntity recordEntity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".fetchPickingBizIdByBarCode", recordEntity);
    }
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyPickingSendRecord record);
//
//    int insertSelective(JyPickingSendRecord record);
//
//    JyPickingSendRecord selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyPickingSendRecord record);
//
//    int updateByPrimaryKey(JyPickingSendRecord record);
}