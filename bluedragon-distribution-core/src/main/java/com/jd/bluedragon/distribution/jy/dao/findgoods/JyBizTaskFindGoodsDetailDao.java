package com.jd.bluedragon.distribution.jy.dao.findgoods;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsDetail;

public class JyBizTaskFindGoodsDetailDao extends BaseDao<JyBizTaskFindGoodsDetail> {
    private final static String NAMESPACE = JyBizTaskFindGoodsDetailDao.class.getName();

    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    int insert(JyBizTaskFindGoodsDetail record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    int insertSelective(JyBizTaskFindGoodsDetail record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    JyBizTaskFindGoods selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    int updateByPrimaryKeySelective(JyBizTaskFindGoodsDetail record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyBizTaskFindGoodsDetail record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }
}
