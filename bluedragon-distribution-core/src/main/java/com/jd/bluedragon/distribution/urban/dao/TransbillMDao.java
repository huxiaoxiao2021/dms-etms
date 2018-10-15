package com.jd.bluedragon.distribution.urban.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;

/**
 * 城配运单M表--Dao接口实现
 * 
 * @ClassName: TransbillMDaoImpl
 * @Description: MQ不再推送，改为调运单接口，以后不再从transbill_M表取数据
 * @author wuyoude
 * @date 2017年04月28日 13:30:01
 *
 */
@Deprecated
public class TransbillMDao extends BaseDao<TransbillM> {
	public static final String namespace = TransbillMDao.class.getName();
	
    public Integer insert(TransbillM entity) {
        return this.getSqlSession().insert(namespace + ".insert", entity);
    }
    public Integer updateBySelective(TransbillM entity) {
        return this.getSqlSession().update(namespace + ".updateBySelective", entity);
    }

    public TransbillM findById(Long mid) {
        return (TransbillM) this.getSqlSession().selectOne(namespace + ".findById", mid);
    }
    public TransbillM findByWaybillCode(String waybillCode) {
        return (TransbillM) this.getSqlSession().selectOne(namespace + ".findByWaybillCode", waybillCode);
    }
    public List<String> findEffectWaybillCodesByScheduleBillCode(String scheduleBillCode) {
       return this.getSqlSession().selectList(namespace + ".findEffectWaybillCodesByScheduleBillCode", scheduleBillCode);
    }
}
