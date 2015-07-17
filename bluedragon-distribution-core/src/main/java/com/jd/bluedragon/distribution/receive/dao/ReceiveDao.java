package com.jd.bluedragon.distribution.receive.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.receive.domain.Receive;

public class ReceiveDao extends BaseDao<Receive> {

	public static final String namespace = ReceiveDao.class.getName();

	public int updateStatusSuccess(Receive receive) {
	    return super.getSqlSession().update(
					ReceiveDao.namespace + ".updateStatusSuccess", receive);
	}

	@SuppressWarnings("unchecked")
	public List<Receive> queryReceiveByBoxCode(Receive receive) {
		return super.getSqlSession().selectList(
				ReceiveDao.namespace + ".queryReceiveByBoxCode", receive);
	}

	@SuppressWarnings("unchecked")
	public List<Receive> findReceiveJoinList(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectList(namespace + ".findReceiveJoinList", paramMap);
		List<Receive> receives = (List<Receive>)((obj == null) ? null : obj);
		return receives;
	}
	
	public int findReceiveJoinTotalCount(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findReceiveJoinTotalCount", paramMap);
		int totalCount = (Integer)((obj == null) ? 0 : obj);
		return totalCount;
	}
}
