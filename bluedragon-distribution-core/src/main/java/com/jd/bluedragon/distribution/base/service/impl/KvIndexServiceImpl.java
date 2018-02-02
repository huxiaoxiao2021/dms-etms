package com.jd.bluedragon.distribution.base.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.base.service.KvIndexService;

/**
 * 
 * @author huangliang
 *
 */
@Service("KvIndexService")
public class KvIndexServiceImpl implements KvIndexService {

	private final static Logger logger = Logger.getLogger(KvIndexServiceImpl.class);

	@Autowired
	private KvIndexDao kvIndexDao;
	
	@Autowired
	private RedisManager redisManager;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<Integer> queryCreateSiteCodesByKey(String keyword) {
		return kvIndexDao.queryCreateSiteCodesByKey(keyword);
	}

	@Override
	public int add(String keyword, String value) {
		KvIndex kvIndex = new KvIndex();
		kvIndex.setKeyword(keyword);
		kvIndex.setKeyword(value);
		if(redisManager.exists(kvIndex.toUniqueString())){
			return 1;
		}else{
			return kvIndexDao.add(KvIndexDao.namespace, kvIndex);
		}
	}

	@Override
	public List<Integer> queryRecentSiteCodesByKey(String keyword) {
		return kvIndexDao.queryRecentSiteCodesByKey(keyword);
	}
}
