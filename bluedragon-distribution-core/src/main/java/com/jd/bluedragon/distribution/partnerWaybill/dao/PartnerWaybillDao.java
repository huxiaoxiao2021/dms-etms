package com.jd.bluedragon.distribution.partnerWaybill.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.partnerWaybill.domain.PartnerWaybill;

public class PartnerWaybillDao extends BaseDao<PartnerWaybill>{
	
	public static final String namespace = PartnerWaybillDao.class.getName();
	
	@SuppressWarnings("unchecked")
	public List<PartnerWaybill> queryByCondition(PartnerWaybill waybillPackageRel) {
		return super.getSqlSession().selectList(
				PartnerWaybillDao.namespace + ".queryByCondition", waybillPackageRel);
	}
	public int updateStatus(PartnerWaybill waybillPackageRel) {
	     return super.getSqlSession().update(
					PartnerWaybillDao.namespace + ".updateStatus", waybillPackageRel);
	}
	public int checkHas(PartnerWaybill partnerWaybill) {
		return super.getSqlSession().update(
				PartnerWaybillDao.namespace + ".checkHas", partnerWaybill);
	}
	
}
