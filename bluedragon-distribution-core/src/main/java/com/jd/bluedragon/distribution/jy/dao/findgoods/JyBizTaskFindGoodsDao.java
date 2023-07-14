package com.jd.bluedragon.distribution.jy.dao.findgoods;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;

public class JyBizTaskFindGoodsDao extends BaseDao<JyBizTaskFindGoods> {

    private final static String NAMESPACE = JyBizTaskFindGoodsDao.class.getName();

    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    int insert(JyBizTaskFindGoods record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    int insertSelective(JyBizTaskFindGoods record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    JyBizTaskFindGoods selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    int updateByPrimaryKeySelective(JyBizTaskFindGoods record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyBizTaskFindGoods record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }
}
