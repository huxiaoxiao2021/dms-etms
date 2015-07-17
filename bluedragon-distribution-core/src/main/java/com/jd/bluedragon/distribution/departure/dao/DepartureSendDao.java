package com.jd.bluedragon.distribution.departure.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.departure.domain.DepartureSend;

public class DepartureSendDao extends BaseDao<DepartureSend> {

	private static final String namespace = DepartureSendDao.class.getName();

	public int insert(DepartureSend departureSend) {
		return this.getSqlSession().insert(namespace + ".insert", departureSend);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DepartureSend> getDepartureSendByCarId(Long departureCarId){
		return this.getSqlSession().selectList(namespace+".getDepartureSendByCarId", departureCarId);
	}
	
	@SuppressWarnings("unchecked")
	public List<DepartureSend> getByThirdWaybillCode(String thirdWaybillCode){
		return this.getSqlSession().selectList(namespace+".getByThirdWaybillCode", thirdWaybillCode);
	}
	
}
