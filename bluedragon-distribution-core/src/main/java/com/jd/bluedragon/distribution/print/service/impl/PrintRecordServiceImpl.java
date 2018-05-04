package com.jd.bluedragon.distribution.print.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.print.service.PrintRecordService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ql.dms.common.cache.CacheKeyGenerator;
import com.jd.ql.dms.common.cache.CacheService;
/**
 * 
 * @ClassName: PrintRecordServiceImpl
 * @Description: 打印记录处理逻辑
 * @author: wuyoude
 * @date: 2018年5月3日 上午10:23:44
 *
 */
@Service
public class PrintRecordServiceImpl implements PrintRecordService{
	
	@Autowired
	@Qualifier("cacheKeyGenerator")
	private CacheKeyGenerator cacheKeyGenerator;
	
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;
    
	/**
	 * 保存
	 * @param packageCode
	 * @return
	 */
	public boolean saveReprintRecord(String packageCode){
		String[] hashKey = BusinessHelper.getHashKeysByPackageCode(packageCode);
		if(hashKey != null){
			String key = this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_REPRINT_RECORDS, hashKey[0]);
			String keyField = hashKey[1];
			boolean rest = jimdbCacheService.hSetEx(key, keyField,Constants.STRING_FLG_TRUE,Constants.TIME_SECONDS_ONE_MONTH);
			return rest;
		}
		return false;
	}
	/**
	 * 获取运单号对应的补打记录
	 * @param waybillCode
	 * @return
	 */
	public Set<String> getHasReprintPackageCodes(String waybillCode){
		int pageIndex = 1;
		String hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
		if(hashKey != null){
			Map<String,String> result = new HashMap<String,String>();
			Map<String,String> values = this.jimdbCacheService.hGetAll(
					this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_REPRINT_RECORDS, hashKey)
					);
			//循环所有分页获取数据
			while(values!=null && !values.isEmpty()){
					result.putAll(values);
				++ pageIndex;
				hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
				values = this.jimdbCacheService.hGetAll(
						this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_REPRINT_RECORDS, hashKey)						);
			}
			return result.keySet();
		}
		return null;
	}
	/**
	 * 删除运单的补打记录
	 * @param waybillCode
	 * @return
	 */
	public boolean deleteReprintRecordsByWaybillCode(String waybillCode){
		int pageIndex = 1;
		String hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
		if(hashKey != null){
			String redisKey = this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_REPRINT_RECORDS, hashKey);
			//循环所有分页获取数据
			while(this.jimdbCacheService.exists(redisKey)){
				this.jimdbCacheService.del(redisKey);
				++ pageIndex;
				redisKey = this.cacheKeyGenerator.getCacheKey(
						CacheKeyConstants.CACHE_KEY_REPRINT_RECORDS,
						BusinessHelper.getHashKey(waybillCode,pageIndex));
			}
			return true;
		}
		return false;
	}
}
