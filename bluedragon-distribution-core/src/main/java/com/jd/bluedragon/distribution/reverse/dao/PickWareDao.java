package com.jd.bluedragon.distribution.reverse.dao;

import java.util.HashMap;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reverse.domain.PickWare;

public class PickWareDao extends BaseDao<PickWare> {

	public static final String namespace = PickWareDao.class.getName();
	
	    public Integer findByFingerprint(PickWare pickWare) {
	        Map<String, Object> request = new HashMap<String, Object>();
	        request.put("fingerprint", pickWare.getFingerprint());
	        return  (Integer)super.getSqlSession().selectOne(PickWareDao.namespace + ".findByFingerprint",
	                request);
	    }
}
