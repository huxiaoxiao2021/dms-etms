package com.jd.bluedragon.distribution.config.dao.impl;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.config.dao.ConfigStrandReasonDao;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;

/**
 * @ClassName: ConfigStrandReasonDaoImpl
 * @Description: 滞留原因配置表--Dao接口实现
 * @author liuduo8
 * @date 2022年02月18日 16:12:56
 *
 */
public class ConfigStrandReasonDaoImpl extends BaseDao<ConfigStrandReason> implements ConfigStrandReasonDao{

    private final static String NAMESPACE = ConfigStrandReasonDao.class.getName();
	@Override
	public int insert(ConfigStrandReason insertData) {
		return this.getSqlSession().insert(NAMESPACE + ".insert", insertData);
	}

	@Override
	public int updateById(ConfigStrandReason updateData) {
		return this.getSqlSession().update(NAMESPACE + ".updateById", updateData);
	}

	@Override
	public int deleteById(ConfigStrandReason deleteData) {
		return this.getSqlSession().update(NAMESPACE + ".deleteById", deleteData);
	}

	@Override
	public ConfigStrandReason queryById(Long id) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryById", id);
	}

	@Override
	public List<ConfigStrandReason> queryList(ConfigStrandReasonQuery query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryList", query);
	}

	@Override
	public int queryCount(ConfigStrandReasonQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryCount", query);
	}

	@Override
	public ConfigStrandReason queryByReasonCode(Integer reasonCode) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryByReasonCode", reasonCode);
	}

}
