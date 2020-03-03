package com.jd.bluedragon.distribution.interceptconfig.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.interceptconfig.domain.InterceptConfigInfo;

public class InterceptConfigInfoDao extends BaseDao<InterceptConfigInfo> {

	public static final String namespace = InterceptConfigInfoDao.class.getName();

	public InterceptConfigInfo queryByCode(String code) {
		 return super.getSqlSession().selectOne(InterceptConfigInfoDao.namespace + ".queryByCode", code);
	 }
}
