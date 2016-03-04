package com.jd.bluedragon.common.rpc.mock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

/**
 * 只支持简单的返回值，不支持嵌套的复杂对象。
 * 远程方法模拟代理组件。
 * 1.模拟返回值（由具体xml文件配置值）。
 * 2.模拟调用延时。可以根据接口线上的tp99或max值来设定接口延时。
 * @author yangwubing
 *
 */
public class RpcMockProxy {
	
	private static final Log logger = LogFactory.getLog(RpcMockProxy.class);
	
	private static final String BASEPATH = "/rpcMock/";
	
	/**
	 * 配置信息缓存。
	 */
	private static Map<String, RpcMockConfig> configCache = new HashMap<String, RpcMockConfig>();
	
	/**
	 * 模拟远程方法调用并且返回配置制定的时间。
	 * @param <T> 远程接口返回值参数。
	 * @param config 配置信息文件名。文件路径相对于路径classpath:rpcMock/下。
	 * @param args 远程调用方法的动态参数。
	 * @return 远程方法调用模拟返回值。
	 */
	public static <T> T invokeRpc(Class<T> clazz, String configFile, Object... args){
		
		//读取配置
		RpcMockConfig config = fileToConfig(configFile);
		
		//睡眠
		sleep(config);
		
		//解析出对应的返回值。
		return parseReturnObject(clazz, config, args);
	}
	
	private static RpcMockConfig fileToConfig(String configFile){

		if(!configFile.endsWith(".json")){
			configFile = configFile+".json";
		}
		
		if(configCache.get(configFile)!=null){
			return configCache.get(configFile);
		}
		
		InputStream inputStream = RpcMockProxy.class.getResourceAsStream(BASEPATH+configFile);
		try {
			if(inputStream==null){
				logger.error("配置文件不存在"+configFile);
				return null;
			}
			String json = IOUtils.toString(inputStream);
			RpcMockConfig config = JSON.parseObject(json, RpcMockConfig.class);
			configCache.put(configFile, config);
			return config;
		} catch (IOException e) {
			logger.error("没有到配置文件"+configFile, e);
		}catch (Throwable e) {
			logger.error("内容格式不是合法的json格式，文件是"+configFile, e);
		}
		return null;
	}
	
	
	private static void sleep(RpcMockConfig config){
		if(config!=null){
			try {
				//线程睡眠tp99毫秒。
				Thread.sleep(config.getTp99());
			} catch (InterruptedException e) {
				logger.error("线程睡眠异常", e);
			}
		}
	}
	
	private static <T> T parseReturnObject(Class<T> clazz, RpcMockConfig config, Object... args){
		if(config.getReturnValueConfig()==null){
			return null;
		}
		Map<String, String> returnValue = config.getReturnValueConfig();
		if(returnValue!=null){
			T bean = null;
			try {
				bean = clazz.newInstance();
				//遍历所有的属性值。将值转换到bean中。
				Set<String> keys = returnValue.keySet();
				for(String key:keys){
					BeanUtils.setProperty(bean, key, returnValue.get(key));
				}
			} catch (InstantiationException e) {
				logger.error("实例化bean抛出异常", e);
			} catch (IllegalAccessException e) {
				logger.error("构造函数访问权限有问题", e);
			} catch (InvocationTargetException e) {
				logger.error("设置属性值抛出异常", e);
			} 
			return bean;
		}
		return null;
	}

}
