package com.jd.bluedragon.distribution.urban.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;

/**
 * 城配运单M表--Dao接口实现
 * 
 * @ClassName: TransbillMDaoImpl
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月28日 13:30:01
 *
 */
@Repository("transbillMDao")
public class TransbillMDao extends BaseDao<TransbillM> {
	public static final String namespace = TransbillMDao.class.getName();
	
    public Integer insert(TransbillM entity) {
        return this.getSqlSession().insert(namespace + ".insert", entity);
    }
    public Integer updateBySelective(TransbillM entity) {
        return this.getSqlSession().update(namespace + ".updateBySelective", entity);
    }
    public Integer deleteById(Long id) {
        return this.getSqlSession().delete(namespace + ".deleteById", id);
    }
    public UrbanWaybill findById(Long id) {
        return (UrbanWaybill) this.getSqlSession().selectOne(namespace + ".findById", id);
    }
    public TransbillM findByWaybillCode(String waybillCode) {
    	List<TransbillM> list = this.getSqlSession().selectList(namespace + ".findByWaybillCode", waybillCode);
    	if(list!=null&&list.size()>0){
    		return list.get(0);
    	}
        return null;
    }
    public List<TransbillM> findByScheduleBillCode(String scheduleBillCode) {
        return this.getSqlSession().selectList(namespace + ".findByScheduleBillCode", scheduleBillCode);
    }
}
