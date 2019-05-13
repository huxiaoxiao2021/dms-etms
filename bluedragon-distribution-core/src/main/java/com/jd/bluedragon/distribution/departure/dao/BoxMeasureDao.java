package com.jd.bluedragon.distribution.departure.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.departure.domain.BoxMeasure;

import java.util.List;

public class BoxMeasureDao extends BaseDao<BoxMeasure> {

	public static final String namespace = BoxMeasureDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<BoxMeasure> getUnHandledBoxMeasures(int fetchNum) {
		return this.getSqlSession().selectList(
				namespace + ".getUnHandledBoxMeasures", fetchNum);
	}
	
	public boolean updateBySelective(BoxMeasure boxMeasure) {
		int res = getSqlSession().update(namespace + ".updateBySelective",
				boxMeasure);
		return res > 0 ? true : false;
	}

}
