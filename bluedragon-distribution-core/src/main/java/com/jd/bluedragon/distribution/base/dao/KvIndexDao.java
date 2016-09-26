package com.jd.bluedragon.distribution.base.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;

public class KvIndexDao extends BaseDao<KvIndex> {

	public static final String namespace = KvIndexDao.class.getName();

	public List<String> queryByKeyword(String keyword) {
		return this.getSqlSession().selectList(namespace + ".queryByKeyword", keyword);
	}
}