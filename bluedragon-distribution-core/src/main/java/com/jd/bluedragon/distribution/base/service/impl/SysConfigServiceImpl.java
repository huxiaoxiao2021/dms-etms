package com.jd.bluedragon.distribution.base.service.impl;

import java.util.List;

import com.jd.etms.utils.cache.annotation.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
@Service
public class SysConfigServiceImpl implements SysConfigService {
	@Autowired
	private SysConfigDao sysConfigDao;

	@Override
	public List<SysConfig> getSwitchList(){
		return this.sysConfigDao.getSwitchList();
	}

	@Override
	public List<SysConfig> getList(SysConfig sysConfig) {
		return this.sysConfigDao.getList(sysConfig);
	}

	@Override
	public int del(Long pk) {
		return this.sysConfigDao.del(pk);
	}

	@Override
	@Cache(key = "SysConfigServiceImpl.getCachedList@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000
			,redisEnable = true, redisExpiredTime = 30 * 60 * 1000)
	public List<SysConfig> getCachedList(String conName) {
		return this.sysConfigDao.getListByConName(conName);
	}
}
