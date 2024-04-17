package com.jd.bluedragon.distribution.base.service;

import java.util.List;

import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.domain.SysConfigJobCodeHoursContent;

public interface SysConfigService {
    /**
     * 配置分拣中心站点列表-不启用新统一包裹标签的print.dmsSiteCodes.nonuseNewTemplate
     */
    public static final String SYS_CONFIG_NAME_DMS_SITE_CODES_NONUSE_NEW_TEMPLATE = "print.dmsSiteCodes.nonuseNewTemplate";
    /**
     * 是否开启云打印
     */
    public static final String SYS_CONFIG_NAME_DMS_PRINT_USE_JD_CLOUD = "print.config.useJdCloudPrint";
    /**
     * 是否开启PDA重复登录校验
     */
    public static final String SYS_CONFIG_PDA_CHECK_MULTIPLE_LOGIN = "sys.config.pda.check.multiple.login";

	/**
	 * 是否开启回传售后场景的备件库回传收货完成全程跟踪逻辑
	 */
	public static final String SYS_CONFIG_WRITER_SH_RECEIVE_WAYBILL_TRACE = "sys.config.sh.receive.waybill.trace";
    /*
	 * 是否开启补打2023年2月1日运单异常数据 不运行补打
     */
	String SYS_CONFIG_CHECK_REPRINT_230201 = "sys.config.check.reprint.230201";

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
	 * 根据配置名称获取配置列表
	 * @param configName
	 * @return
	 */
	public List<SysConfig> getListByConfigName(String configName);

	/**
	 * 根据配置名称获取配置列表 无缓存
	 * @param configName
	 * @return
	 */
	List<SysConfig> getListByConfigNameNoCache(String configName);

	/**
	 * 修改内容
	 * @return
	 */
	boolean updateContent(SysConfig sysConfig);

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
	/**
	 * 获取字符串配置列表，没有配置返回空列表，缓存3分钟（redis）/1分钟（本地）
	 * @param configName
	 * @return
	 */
	List<String> getStringListConfig(String configName);

	/**
	 * 获取是否匹配具体的字符串，如果配置内容是ALL那么就直接返回成功
	 * @param configName 配置项
	 * @param containStr 包含的字符串
	 * @return
	 */
	boolean getByListContainOrAllConfig(String configName,String containStr);

	/**
	 * 从sysconfig表里获取工种自动签退时间配置
	 * @return
	 */
	SysConfigJobCodeHoursContent getSysConfigJobCodeHoursContent(String key);
}
