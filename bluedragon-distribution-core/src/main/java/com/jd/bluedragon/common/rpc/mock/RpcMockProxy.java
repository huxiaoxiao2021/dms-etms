package com.jd.bluedragon.common.rpc.mock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
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
	 * jexl表达式前缀。
	 */
	private static final String JEXL_PREFIX = "#jexl#";
	
	/**
	 * 配置信息缓存。
	 */
	private static Map<String, RpcMockConfig> configCache = new ConcurrentHashMap<String, RpcMockConfig>();
	
	/**
	 * jexl表达式引擎。
	 */
	private static JexlEngine engine = new JexlEngine();
	
	/**
	 * jexl表达式缓存。
	 */
	private static Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>();
	
	private static Random random = new Random();
	
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
	
	
	/**
	 * 实现随机睡眠。
	 * 99%概率睡眠tp99毫秒。
	 * 1%概率睡眠max毫秒。
	 * @param config
	 */
	private static void sleep(RpcMockConfig config){
		if(config!=null){
			try {
				//线程睡眠tp99毫秒。
				Thread.sleep(radomSleeptime(config));
			} catch (InterruptedException e) {
				logger.error("线程睡眠异常", e);
			}
		}
	}
	
	/**
	 * 按照概率随机出睡眠时间。99%tp99,1%max。
	 * @param config
	 * @return 睡眠时间。
	 */
	public static long radomSleeptime(RpcMockConfig config){
		//0-98是tp99，99是max。
		int r = random.nextInt(100);
		if(r>=0 && r<99){
			return config.getTp99();
		}
		else{
			return config.getMax();
		}
	}
	
	/**
	 * 解析出bean的值。
	 * @param <T>
	 * @param clazz
	 * @param config
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> T parseReturnObject(Class<T> clazz, RpcMockConfig config, Object... args){
		if(config.getReturnValueConfig()==null){
			return null;
		}
		Map<String, String> returnValue = config.getReturnValueConfig();
		if(returnValue!=null){
			T bean = null;
			try {
				Object finalValue = null;
				bean = clazz.newInstance();
				//遍历所有的属性值。将值转换到bean中。
				Set<String> keys = returnValue.keySet();
				for(String key:keys){
					String value = returnValue.get(key);
					if(value==null){
						continue;
					}
					value = value.trim();
					finalValue = value;
					//如果以jexl表达式标志开头，则识别为jexl表达式。 //执行此表达式。
					if(value.startsWith(JEXL_PREFIX)){
						String jexl = value.substring(JEXL_PREFIX.length());
						Expression expression = expressionCache.get(jexl);
						if(expression==null){
							expression = engine.createExpression( jexl );
							expressionCache.put(jexl, expression);
						}
						JexlContext context = buildJexlContext(args);
						finalValue = expression.evaluate(context);
					}
					
					BeanUtils.setProperty(bean, key, finalValue);
				}
				
				//给没有设置值的属性设置默认值。
				Map<String, Object> map = BeanUtils.describe(bean);
				if(map!=null && map.size()>0){
					Set<Entry<String, Object>> set = map.entrySet();
					for(Entry<String, Object> entry:set){
						if(entry.getValue()==null){
							Field field = clazz.getDeclaredField(entry.getKey());
							if(field!=null){
								finalValue = generatePropertyValue(field.getType());
								BeanUtils.setProperty(bean, entry.getKey(), finalValue);
							}
						}
					}
				}
				
			} catch (InstantiationException e) {
				logger.error("实例化bean抛出异常", e);
			} catch (IllegalAccessException e) {
				logger.error("构造函数访问权限有问题", e);
			} catch (InvocationTargetException e) {
				logger.error("设置属性值抛出异常", e);
			} catch (NoSuchMethodException e) {
				logger.error("没有这个方法", e);
			} catch (SecurityException e) {
				logger.error("不允许访问", e);
			} catch (NoSuchFieldException e) {
				logger.error("没有这个字段", e);
			} 
			return bean;
		}
		return null;
	}
	
	private static Object generatePropertyValue(Class<?> clazz){
		
		if(clazz.isPrimitive()){
			//整形。
			if(clazz.equals(int.class)){
				return random.nextInt();
			}
			else if(clazz.equals(long.class)){
				return random.nextLong();
			}
			else if(clazz.equals(float.class)){
				return random.nextFloat();
			}
			else if(clazz.equals(double.class)){
				return random.nextDouble();
			}
		}
		else if(clazz.equals(Date.class)){
			return new Date();
		}
		else if(clazz.equals(Integer.class)){
			return random.nextInt();
		}
		else if(clazz.equals(Long.class)){
			return random.nextLong();
		}
		else if(clazz.equals(Float.class)){
			return random.nextFloat();
		}
		else if(clazz.equals(Double.class)){
			return random.nextDouble();
		}
		else if(clazz.equals(String.class)){
			return "hello";
		}
		else{
			logger.error("不支持的类型:"+clazz);
		}
		
		return null;
	}
	
	/**
	 * 构建Jexl上下文环境。
	 * @param args 参数。
	 * @return jexl上下文环境。
	 */
	private static JexlContext buildJexlContext(Object... args){
		JexlContext context = new MapContext();

		//将参数设置到上下文中。
		if(args!=null && args.length>0){
			int i=0;
			for(Object o:args){
				context.set("args"+i, o);
				i++;
			}
		}
		
		//将
		
		return context;
	}

}
