package com.jd.bluedragon.distribution.base.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<String> queryByKeyword(String keyword) {
		return kvIndexDao.queryByKeyword(keyword);
	}

	@Override
	public int add(String keyword, String value) {
		KvIndex kvIndex = new KvIndex();
		kvIndex.setKeyword(keyword);
		kvIndex.setKeyword(value);
		return kvIndexDao.add(KvIndexDao.namespace, kvIndex);
	}
}
