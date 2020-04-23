package com.jd.bluedragon.common.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @ClassName: CacheService
 * @Description: (类描述信息)
 * @author wuyoude
 * @date 2017年5月21日 上午11:51:47
 *
 */
public class JimdbCacheServiceImpl implements CacheService{
	private static final Logger log = LoggerFactory.getLogger(CacheService.class);
	/**
	 * 默认过期时间为30分钟
	 */
	private long exTime = 30*60;
	private TimeUnit exTimeUnit = TimeUnit.SECONDS;
	
	private Cluster jimdbClient;
    private static final byte EXPIRE_FLAG_VALUE = 1;
    /**
     * hSet 存储时暂存已设置过期时间的key
     */
    private static Cache<String, Byte> hasSetExpireTimeKeySet
            = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.HOURS)
            .maximumSize(10240)
            .concurrencyLevel(8)
            .initialCapacity(1024)
            .build();
	public JimdbCacheServiceImpl(Cluster jimdbClient) {
		super();
		this.jimdbClient = jimdbClient;
	}
	public JimdbCacheServiceImpl(Cluster jimdbClient,long exTime, TimeUnit exTimeUnit) {
		super();
		this.exTime = exTime;
		this.exTimeUnit = exTimeUnit;
		this.jimdbClient = jimdbClient;
	}
	/**
	 * 从缓存中获取一个字符串
	 * @param key
	 * @return
	 */
	public String get(String key) {
		if(!verifyGetParams(key)){
			return null;
		}
		return jimdbClient.get(key);
	}
	/**
	 * 从缓存中获取一个任意对象
	 * @param key
	 * @param responseType
	 * @return
	 */
	public <T> T get(String key,Class<T> responseType) {
		return deserialize(get(key),responseType);
	}
	/**
	 * 缓存中放入一个可序列化的对象
	 * @param key
	 * @param val
	 * @return
	 */
	public <T> boolean set(String key, T val) {
		return setEx(key, val ,exTime,exTimeUnit);
	}
	/**
	 * 缓存中放入一个可序列化的对象，指定过期时间（单位秒）
	 * @param key
	 * @param val
	 * @param exTime 过期时间（单位秒）
	 * @return
	 */
	public <T> boolean setEx(String key, T val,long exTime) {
		return setEx(key, val ,exTime,exTimeUnit);
	}
	public <T> boolean setEx(String key, T val ,long exTime,TimeUnit exTimeUnit){
		if(!verifySetParams(key, val)){
			return false;
		}
		jimdbClient.setEx(key, serialize(val), exTime, exTimeUnit);
		return true;
	}
	/**
	 * 缓存中放入一个可序列化的对象
	 * @param key
	 * @param val
	 * @return
	 */
	public <T> boolean setNx(String key, T val) {
		return setNx(key, val ,exTime,exTimeUnit);
	}
	public <T> boolean setNx(String key, T val, long exTime) {
		return setNx(key, val ,exTime,exTimeUnit);
	}
	public <T> boolean setNx(String key, T val ,long exTime,TimeUnit exTimeUnit){
		return jimdbClient.set(key, serialize(val), exTime, exTimeUnit, false);
	}
	
	public <T> boolean hSet(String key, String keyField, T val) {
		return hSetEx(key, keyField, val, exTime, exTimeUnit);
	}
	public <T> boolean hSetEx(String key, String keyField, T val,long exTime) {
		return hSetEx(key, keyField, val, exTime, exTimeUnit);
	}
	public <T> boolean hSetEx(String key, String keyField, T val,long exTime,TimeUnit exTimeUnit) {
		if(jimdbClient.hSet(key, keyField, serialize(val))){
	        if (null == hasSetExpireTimeKeySet.getIfPresent(key)) {
	            jimdbClient.expire(key, exTime, exTimeUnit);
	            hasSetExpireTimeKeySet.put(key, EXPIRE_FLAG_VALUE);
	        }
	        return true;
		}
		return false;
	}
	public <T> boolean hSetNx(String key, String keyField, T val) {
		return hSetNx(key, keyField, val, exTime, exTimeUnit);
	}
	public <T> boolean hSetNx(String key, String keyField, T val,long exTime) {
		return hSetNx(key, keyField, val, exTime, exTimeUnit);
	}
	public <T> boolean hSetNx(String key, String keyField, T val,long exTime,TimeUnit exTimeUnit) {
		if(!verifySetParams(key, keyField, val)){
			return false;
		}
		if(jimdbClient.hSetNX(key, keyField, serialize(val))){
	        if (null == hasSetExpireTimeKeySet.getIfPresent(key)) {
	            jimdbClient.expire(key, exTime, exTimeUnit);
	            hasSetExpireTimeKeySet.put(key, EXPIRE_FLAG_VALUE);
	        }
	        return true;
		}
		return false;
	}
	/**
	 * 从缓存中hash结构中获取一个任意对象
	 * @param key
	 * @param keyField
	 * @return
	 */
	public String hGet(String key, String keyField) {
		if(!verifyGetParams(key, keyField)){
			return null;
		}
		return jimdbClient.hGet(key, keyField);
	}
	/**
	 * 从缓存中hash结构中获取一个任意对象
	 * @param key
	 * @param keyField
	 * @param responseType
	 * @return
	 */
	public <T> T hGet(String key, String keyField,Class<T> responseType) {
		return deserialize(hGet(key, keyField),responseType);
	}
	/**
	 * 从缓存中获取hash结构中的所有对象
	 * @param key
	 * @return
	 */
	public Map<String,String> hGetAll(String key) {
		if(!verifyGetParams(key)){
			return null;
		}
		return jimdbClient.hGetAll(key);
	}
	/**
	 * 从缓存中获取hash结构中的所有对象
	 * @param key
	 * @param responseType
	 * @return
	 */
	public <T> Map<String,T> hGetAll(String key,Class<T> responseType) {
		if(!verifyGetParams(key)){
			return null;
		}
		Map<String,String> cacheMap = jimdbClient.hGetAll(key);
		if(cacheMap!=null){
			Map<String,T> mapRest = new HashMap<String,T>(16);
			for(String keyField : cacheMap.keySet()){
				T val = deserialize(cacheMap.get(keyField),responseType);
				mapRest.put(keyField, val);
			}
			return mapRest;
		}
		return null;
	}
	public boolean hDel(String key, String keyField){
		if(!verifyGetParams(key, keyField)){
			return false;
		}
		jimdbClient.hDel(key, keyField);
		return true;
	}
	public <T> boolean sAdd(String key,T... vals) {
		return sAddEx(key,exTime,exTimeUnit,vals);
	}
	public <T> boolean sAddEx(String key,long exTime, T... vals) {
		return sAddEx(key,exTime,exTimeUnit,vals);
	}
	public <T> boolean sAddEx(String key,long exTime,TimeUnit exTimeUnit, T... vals) {
		if(!verifySetParams(key,vals)){
			return false;
		}
		if(vals.length > 0){
			String[] jsonVals = new String[vals.length];
			for(int i=0;i<vals.length;i++){
				jsonVals[i] = serialize(vals[i]);
			}
			if(jimdbClient.sAdd(key, jsonVals) > 0){
		        if (null == hasSetExpireTimeKeySet.getIfPresent(key)) {
		            jimdbClient.expire(key, exTime, exTimeUnit);
		            hasSetExpireTimeKeySet.put(key, EXPIRE_FLAG_VALUE);
		        }
			}
			return true;
		}
		return false;
	}
	/**
	 * 获取key所有成员，返回set集合
	 * @param key
	 * @return
	 */
	public Set<String> sMembers(String key){
		if(!verifyGetParams(key)){
			return null;
		}
		return jimdbClient.sMembers(key);
	}
	/**
	 * 获取key所有成员，返回set集合
	 * @param key
	 * @param responseType
	 * @return
	 */
	public <T> Set<T> sMembers(String key,Class<T> responseType){
		Set<String> cacheSet = sMembers(key);
		if(cacheSet!=null){
			Set<T> setRest = new HashSet<T>(16);
			for(String jsonVal : cacheSet){
				T val = deserialize(jsonVal,responseType);
				setRest.add(val);
			}
			return setRest;
		}
		return null;
	}
	public boolean del(String key){
		if(!verifyGetParams(key)){
			return false;
		}
		jimdbClient.del(key);
		return true;
	}
	@Override
	public boolean exists(String key) {
		return jimdbClient.exists(key);
	}
	/**
	 * 校验方法
	 * @param key
	 * @return
	 */
	private boolean verifyGetParams(String key){
		return key!=null;
	}
	private boolean verifyGetParams(String key, String keyField){
		return key!=null&&keyField!=null;
	}
	private <T> boolean verifySetParams(String key, T val){
		return key!=null&&val!=null;
	}
	private <T> boolean verifySetParams(String key, String keyField, T val){
		return key!=null&&keyField!=null&&val!=null;
	}
	/**
	 * 对象序列化
	 * 
	 * @param value
	 * @return
	 */
	private <T> String serialize(T value) {
		if (value == null) {
			throw new NullPointerException("Can't serialize null");
		}
		try {
			//String类型无需序列化
			if(value instanceof String){
				return (String)value;
			}else{
				return JsonHelper.toJsonMs(value);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		}
	}

	/**
	 * 对象反序列化
	 */
	private <T> T deserialize(String in,Class<T> responseType) {
		if (in == null || in.length()==0) {
			return null;
		}
		try {
			return JsonHelper.fromJsonMs(in, responseType);
		} catch (Exception e) {
			log.error("Caught Exception decoding {}",in, e);
		}
		return null;
	}
}
