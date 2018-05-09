package com.jd.bluedragon.common.utils;

import com.jd.bluedragon.Constants;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
/**
 * 
 * @ClassName: ProfilerHelper
 * @Description: 监控相关
 * @author: wuyoude
 * @date: 2018年5月7日 下午2:40:09
 *
 */
public class ProfilerHelper {
	/**
	 * 默认注册到dms.etms应用中，不开启心跳检测
	 * @param key 监控key
	 * @return
	 */
	public static CallerInfo registerInfo(String key){
		return ProfilerHelper.registerInfo(key, Constants.UMP_APP_NAME_DMSWEB, false);
	}
	/**
	 * 注册到appName应用中，不开启心跳检测
	 * @param key 监控key
	 * @param appName 监控应用名称
	 * @return
	 */
	public static CallerInfo registerInfo(String key,String appName){
		return ProfilerHelper.registerInfo(key, appName, false);
	}
	/**
	 * 注册到appName应用中
	 * @param key 监控key
	 * @param appName 监控应用名称
	 * @param enableHeartbeat 开启心跳检测标识
	 * @return
	 */
	public static CallerInfo registerInfo(String key,String appName,boolean enableHeartbeat){
		return Profiler.registerInfo(key, appName, enableHeartbeat, true);
	}
}
