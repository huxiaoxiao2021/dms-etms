package com.jd.bluedragon.distribution.base.service;

import java.util.List;

import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;

public interface SysConfigService {
    /**
     * 配置分拣中心站点列表-启用新统一包裹标签的print.dmsSiteCodes.useNewTemplate
     */
    public static final String SYS_CONFIG_NAME_DMS_SITE_CODES_USE_NEW_TEMPLATE = "print.dmsSiteCodes.useNewTemplate";
    
	public List<SysConfig> getSwitchList();
	public List<SysConfig> getList(SysConfig sysConfig);
	public int del(Long pk);

	public List<SysConfig> getCachedList(String conName);

	public SysConfig findConfigContentByConfigName(String configName);
    /**
     * 获取REDIS队列最大容量
     * @return
     */
    long getMaxRedisQueueSize();

	/**
	 * 专用于获取redis开关的接口,其他接口禁止调用
	 * @param conName
	 * @return
	 */
	public List<SysConfig> getRedisSwitchList(String conName);
	/**
	 * 根据配置名称获取配置列表
	 * @param configName
	 * @return
	 */
	public List<SysConfig> getListByConfigName(String configName);


	/**
	 * 从sysconfig表里查出来SysConfigContent对象
	 * @return
	 */
	SysConfigContent getSysConfigJsonContent(String key);

	/**
	 * 获取开关值使用
	 *
	 * 默认存1返回TRUE 0返回false
	 * @param configName
	 * @return
	 */
	boolean getConfigByName(String configName);
}
