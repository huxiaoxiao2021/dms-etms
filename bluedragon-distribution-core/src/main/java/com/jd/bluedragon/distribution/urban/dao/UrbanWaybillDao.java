package com.jd.bluedragon.distribution.urban.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;

/**
 * 城配运单同步表--Dao接口实现
 * 
 * @ClassName: UrbanWaybillDao
 * @Description: (类描述信息)
 * @author wuyoude
 * @date 2017年4月20日 下午6:46:23
 *
 */
public class UrbanWaybillDao extends BaseDao<UrbanWaybill> {
	public static final String namespace = UrbanWaybillDao.class.getName();
	
    public Integer insert(UrbanWaybill entity) {
        return this.getSqlSession().insert(namespace + ".insert", entity);
    }
    public Integer updateBySelective(UrbanWaybill entity) {
        return this.getSqlSession().update(namespace + ".updateBySelective", entity);
    }
    public Integer deleteById(Long id) {
        return this.getSqlSession().delete(namespace + ".deleteById", id);
    }
    public UrbanWaybill findById(Long id) {
        return (UrbanWaybill) this.getSqlSession().selectOne(namespace + ".findById", id);
    }
    public UrbanWaybill findByWaybillCode(String waybillCode) {
    	List<UrbanWaybill> list = this.getSqlSession().selectList(namespace + ".findByWaybillCode", waybillCode);
    	if(list!=null&&list.size()>0){
    		return list.get(0);
    	}
        return null;
    }
    public List<UrbanWaybill> findByScheduleBillCode(String scheduleBillCode) {
        return this.getSqlSession().selectList(namespace + ".findByScheduleBillCode", scheduleBillCode);
    }
}
