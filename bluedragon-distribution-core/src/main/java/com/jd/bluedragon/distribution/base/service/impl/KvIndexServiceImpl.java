package com.jd.bluedragon.distribution.base.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
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


	@Override
	public List<Integer> queryCreateSiteCodesByKey(String keyword) {
		return kvIndexDao.queryCreateSiteCodesByKey(keyword);
	}

	@Override
	public List<Integer> queryRecentSiteCodesByKey(String keyword) {
		return kvIndexDao.queryRecentSiteCodesByKey(keyword);
	}
}
