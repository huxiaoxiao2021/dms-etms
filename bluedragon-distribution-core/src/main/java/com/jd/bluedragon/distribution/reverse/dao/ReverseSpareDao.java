package com.jd.bluedragon.distribution.reverse.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;

public class ReverseSpareDao extends BaseDao<ReverseSpare> {
	
	public static final String namespace = ReverseSpareDao.class.getName();
	
	public ReverseSpare queryBySpareCode(String spareCode) {
		return (ReverseSpare) super.getSqlSession().selectOne(
		        ReverseSpareDao.namespace + ".queryBySpareCode", spareCode);
	}
	
	@SuppressWarnings("unchecked")
	public List<ReverseSpare> queryByWayBillCode(ReverseSpare rs) {
		return super.getSqlSession().selectList(ReverseSpareDao.namespace + ".queryByWayBillCode",
		        rs);
	}
	
	@SuppressWarnings("unchecked")
	public List<ReverseSpare> queryBySpareTranCode(String spareTranCode) {
		return super.getSqlSession().selectList(
		        ReverseSpareDao.namespace + ".queryBySpareTranCode", spareTranCode);
	}
	
	@SuppressWarnings("unchecked")
	public List<ReverseSpare> findByWayBillCode(ReverseSpare rs) {
		return super.getSqlSession().selectList(ReverseSpareDao.namespace + ".findByWayBillCode",
		        rs);
	}
}
