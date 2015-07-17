package com.jd.bluedragon.distribution.popPickup.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;

public class PopPickupDao extends BaseDao<PopPickup>{

	public static final String namespace = PopPickupDao.class.getName();

	public int findPopPickupTotalCount(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findPopPickupTotalCount", paramMap);
		int totalCount = (Integer)((obj == null) ? 0 : obj);
		return totalCount;
	}

	@SuppressWarnings("unchecked")
	public List<PopPickup> findPopPickupList(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectList(namespace + ".findPopPickupList", paramMap);
		List<PopPickup> popPickups = (List<PopPickup>)((obj == null) ? null : obj);
		return popPickups;
	}
}
