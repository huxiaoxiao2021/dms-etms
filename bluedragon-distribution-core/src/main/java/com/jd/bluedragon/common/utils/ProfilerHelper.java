package com.jd.bluedragon.common.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.StringHelper;
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
	/**
	 * 根据一个数量，分段生成一个监控key值,目前支持：key0,key100,key300,key500,key1000,key2000,key4000,key6000,key6000UP
	 * @param key
	 * @param quantity
	 * @return
	 */
	public static String genKeyByQuantity(String key,int quantity){
		if(StringHelper.isEmpty(key)){
			return null;
		}
		if(quantity<=0){
			return key + "0";
		}else if(quantity<=100){
			return key + "100";
		}else if(quantity<=300){
			return key + "300";
		}else if(quantity<=500){
			return key + "500";
		}else if(quantity<=1000){
			return key + "1000";
		}else if(quantity<=2000){
			return key + "2000";
		}else if(quantity<=4000){
			return key + "4000";
		}else if(quantity<=6000){
			return key + "6000";
		}else{
			return key + "6000UP";
		}
	}
}
