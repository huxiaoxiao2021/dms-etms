package com.jd.bluedragon.distribution.receive.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;

public class CenConfirmDao extends BaseDao<CenConfirm> {
	public static final String namespace = CenConfirmDao.class.getName();

	public int updateFillField(CenConfirm cenConfirm) {
		return super.getSqlSession().update(
				CenConfirmDao.namespace + ".updateFillField", cenConfirm);
	}

	public int updateYnByPackage(CenConfirm cenConfirm) {
		return super.getSqlSession().update(namespace + ".updateYnByPackage",
				cenConfirm);
	}

	@SuppressWarnings("unchecked")
	public List<CenConfirm> queryHandoverInfo(CenConfirm cenConfirm) {
		return super.getSqlSession().selectList(
				namespace + ".queryHandoverInfo", cenConfirm);
	}
}
