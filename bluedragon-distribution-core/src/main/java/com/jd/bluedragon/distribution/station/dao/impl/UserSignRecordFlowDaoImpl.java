package com.jd.bluedragon.distribution.station.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordFlowDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;

/**
 * 人员签到流程表--Dao接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Repository("userSignRecordFlowDao")
public class UserSignRecordFlowDaoImpl extends BaseDao<UserSignRecord> implements UserSignRecordFlowDao {

    private final static String NAMESPACE = UserSignRecordFlowDao.class.getName();
	@Override
	public int insert(UserSignRecordFlow insertData) {
		return this.getSqlSession().insert(NAMESPACE+".insert",insertData);
	}
	@Override
	public int updateFlowStatusById(UserSignRecordFlow updateData) {
		return this.getSqlSession().update(NAMESPACE+".updateFlowStatusById",updateData);
	}
	@Override
	public UserSignRecordFlow queryByFlowBizCode(String flowBizCode) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryByFlowBizCode",flowBizCode);
	}
	@Override
	public List<UserSignRecordFlow> queryDataList(UserSignRecordFlowQuery query) {
	    return this.getSqlSession().selectList(NAMESPACE+".queryDataList",query);
	}
	@Override
	public Integer queryDataCount(UserSignRecordFlowQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE+".queryDataCount",query);
	}
}
