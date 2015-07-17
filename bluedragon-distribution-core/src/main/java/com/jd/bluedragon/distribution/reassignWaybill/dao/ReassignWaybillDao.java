package com.jd.bluedragon.distribution.reassignWaybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;

public class ReassignWaybillDao extends BaseDao<ReassignWaybill> {
	public static final String namespace = ReassignWaybillDao.class.getName();

	public Boolean add(ReassignWaybill packTagPrint) {
		return this.getSqlSession().insert(namespace + ".add", packTagPrint) > 0;
	}

    public ReassignWaybill queryByPackageCode(String packageCode)  {
        return (ReassignWaybill)this.getSqlSession().selectOne(namespace + ".queryByPackageCode", packageCode);
    }

	public ReassignWaybill queryByWaybillCode(String waybillCode) {
		 return (ReassignWaybill)this.getSqlSession().selectOne(namespace + ".queryByWaybillCode", waybillCode);
	}
}
