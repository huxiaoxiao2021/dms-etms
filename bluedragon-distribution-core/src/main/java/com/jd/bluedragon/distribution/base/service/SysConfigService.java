package com.jd.bluedragon.distribution.base.service;

import java.util.List;

import com.jd.bluedragon.distribution.base.domain.SysConfig;

public interface SysConfigService {
	
	public List<SysConfig> getSwitchList();
	public List<SysConfig> getList(SysConfig sysConfig);
	public int del(Long pk);

	public List<SysConfig> getCachedList(String conName);

}
