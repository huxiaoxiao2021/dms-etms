package com.jd.bluedragon.distribution.jy.dao.findgoods;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsDetail;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsDetailQueryDto;

import java.util.List;

public class JyBizTaskFindGoodsDetailDao extends BaseDao<JyBizTaskFindGoodsDetail> {
    private final static String NAMESPACE = JyBizTaskFindGoodsDetailDao.class.getName();

    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyBizTaskFindGoodsDetail record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    public int insertSelective(JyBizTaskFindGoodsDetail record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    public JyBizTaskFindGoods selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyBizTaskFindGoodsDetail record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    public int updateByPrimaryKey(JyBizTaskFindGoodsDetail record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public List<JyBizTaskFindGoodsDetail> findInventoryDetail(JyBizTaskFindGoodsDetailQueryDto param) {
        return this.getSqlSession().selectList(NAMESPACE + ".findInventoryDetail", param);
    }

    public JyBizTaskFindGoodsDetail findPackage(JyBizTaskFindGoodsDetail record) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findPackage", record);
    }

    public Integer countInventoryDetail(JyBizTaskFindGoodsDetailQueryDto param) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countInventoryDetail", param);
    }
}
