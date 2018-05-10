package com.jd.ql.dms.common.cache;
/**
 * 
 * @ClassName: CacheKeyGenerator
 * @Description: 缓存key生成器
 * @author wuyoude
 * @date 2017年5月19日 下午4:53:08
 *
 */
public class CacheKeyGenerator {
	
	/**
	 * 缓存KEY格式 {系统标识}:{业务编码}:{业务主键}
	 */
	private String keyForamt = "sysId:%s:%s";
	
	public String getKeyForamt() {
		return keyForamt;
	}
	public void setKeyForamt(String keyForamt) {
		this.keyForamt = keyForamt;
	}
	/**
	 * 获取key值
	 * 
	 * @param keyFormat
	 * @param busKey
	 * @param key
	 * @return
	 */
	public String getCacheKey(String keyFormat,String busKey,String key){
		return String.format(keyFormat, busKey,key);
	}
	/**
	 * 获取key字符串表示，返回格式"sysId:busKey:key"
	 * @param busKey-业务类型
	 * @param key-业务主键
	 * @return
	 */
	public String getCacheKey(String busKey,String key){
		return getCacheKey(keyForamt, busKey,key);
	}
}
