package com.jd.ql.dms.common.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * 
 * @ClassName: CacheService
 * @Description: 缓存基础
 * @author wuyoude
 * @date 2017年5月21日 上午11:51:47
 *
 */
public interface CacheService {
	/**
	 * 从缓存中获取一个字符串
	 * @param key
	 * @return
	 */
	String get(String key);
	/**
	 * 从缓存中获取一个任意对象
	 * @param key
	 * @param responseType
	 * @return
	 */
	<T> T get(String key,Class<T> responseType);

	/**
	 * 从缓存中获取一个List对象
	 * @param key
	 * @param responseType
	 * @return
	 */
	<T> List<T> getList(String key, Class<T> responseType);
	/**
	 * 缓存中放入一个可序列化的对象
	 * @param key
	 * @param val
	 * @return
	 */
	<T> boolean set(String key, T val);
	/**
	 * 缓存中放入一个可序列化的对象，指定过期时间（单位秒）
	 * @param key
	 * @param val
	 * @param exTime 过期时间（单位秒）
	 * @return
	 */
	<T> boolean setEx(String key, T val,long exTime);
	<T> boolean setEx(String key, T val ,long exTime,TimeUnit exTimeUnit);
	/**
	 * 缓存中放入一个任意对象，默认有效时间为30分钟
	 * @param key
	 * @param val
	 * @return
	 */
	<T> boolean setNx(String key, T val);
	<T> boolean setNx(String key, T val, long exTime);
	<T> boolean setNx(String key, T val ,long exTime,TimeUnit exTimeUnit);
	
	<T> boolean hSet(String key, String keyField, T val);
	<T> boolean hSetEx(String key, String keyField, T val,long exTime);
	<T> boolean hSetEx(String key, String keyField, T val,long exTime,TimeUnit exTimeUnit);
	<T> boolean hSetNx(String key, String keyField, T val);
	<T> boolean hSetNx(String key, String keyField, T val,long exTime);
	<T> boolean hSetNx(String key, String keyField, T val,long exTime,TimeUnit exTimeUnit);
	/**
	 * 从缓存中hash结构中获取一个字符串
	 * @param key
	 * @param keyField
	 * @return
	 */
	String hGet(String key, String keyField);
	/**
	 * 从缓存中hash结构中获取一个字符串
	 * @param key
	 * @param keyField
	 * @param responseType
	 * @return
	 */
	<T> T hGet(String key, String keyField,Class<T> responseType);
	/**
	 * 从缓存中获取hash结构中的所有对象
	 * @param key
	 * @return
	 */
	Map<String,String> hGetAll(String key);
	/**
	 * 从缓存中获取hash结构中的所有对象
	 * @param key
	 * @param responseType
	 * @return
	 */
	<T> Map<String,T> hGetAll(String key,Class<T> responseType);
	/**
	 * 从hash结构中删除一个值
	 * @param key
	 * @param keyField
	 * @return
	 */
	boolean hDel(String key, String keyField);
	<T> boolean sAdd(String key,T... vals);
	<T> boolean sAddEx(String key,long exTime, T... vals);
	<T> boolean sAddEx(String key,long exTime,TimeUnit exTimeUnit, T... vals);
	/**
	 * 获取key所有成员，返回set集合
	 * @param key
	 * @param responseType
	 * @return
	 */
	Set<String> sMembers(String key);
	/**
	 * 获取key所有成员，返回set集合
	 * @param key
	 * @param responseType
	 * @return
	 */
	<T> Set<T> sMembers(String key,Class<T> responseType);
	/**
	 * 删除某个key
	 * @param key
	 * @return
	 */
	boolean del(String key);
	/**
	 * 检查是否存在
	 * @param key
	 * @return
	 */
	boolean exists(String key);
}
