package com.jd.bluedragon.configuration.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 配置开关类，一个JVM只能有一个实例
 * 
 * @author libin
 * 
 */
public class DefaultConfigManager {
	private static Log log = LogFactory.getLog(DefaultConfigManager.class);
	private static DefaultConfigManager instance = null;
	private DataSubscriber dataSubscriber;

	private DefaultConfigManager() {
		dataSubscriber = new DefaultDataSubscriber();
	}

	public synchronized static DefaultConfigManager getInstance() {
		if (instance == null) {
			log.debug("实例化配置管理类...");
			instance = new DefaultConfigManager();
			instance.init();
			log.debug("实例化配置管理类完成...");
		}
		return instance;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getConfigure(String key, boolean defaul) {
		return dealWithValue(getInstance().dataSubscriber.getConfigure(key),
				defaul);

	}

	public static boolean getConfigure(String key) {
		return getConfigure(key, true);

	}

	/**
	 * 根据键获取可信赖的键值,只从redis里获取
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getAvailableConfigure(String key, boolean defaul) {
		return dealWithValue(
				getInstance().dataSubscriber.getAvailableConfigure(key), defaul);

	}

	public static boolean getAvailableConfigure(String key) {
		return getAvailableConfigure(key, true);
	}

	private static boolean dealWithValue(String value, boolean defaul) {
		if (value == null) {
			return defaul;
		} else if (value.equals("1")) {
			return true;
		} else if (value.equals("0")) {
			return false;
		} else {
			return defaul;
		}
	}

	private void init() {
		dataSubscriber.init();
	}

}
