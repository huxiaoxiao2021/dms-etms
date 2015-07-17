package com.jd.bluedragon.distribution.departure.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.departure.domain.BoxMeasure;

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
	
	@SuppressWarnings("unchecked")
	public List<BoxMeasure> queryByBoxCodes(String boxCodeIn) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("boxCodeIn", boxCodeIn);
		return this.getSqlSession().selectList(namespace + ".queryByBoxCodes",
				map);
	}

}
