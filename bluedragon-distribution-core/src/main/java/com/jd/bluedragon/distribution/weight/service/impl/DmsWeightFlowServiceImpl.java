package com.jd.bluedragon.distribution.weight.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.weight.dao.DmsWeightFlowDao;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlowCondition;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.cache.CacheKeyGenerator;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;

/**
 *
 * @ClassName: DmsWeightFlowServiceImpl
 * @Description: 分拣中心称重操作流水--Service接口实现
 * @author wuyoude
 * @date 2018年03月09日 16:02:53
 *
 */
@Service("dmsWeightFlowService")
public class DmsWeightFlowServiceImpl extends BaseService<DmsWeightFlow> implements DmsWeightFlowService {

	@Autowired
	@Qualifier("dmsWeightFlowDao")
	private DmsWeightFlowDao dmsWeightFlowDao;
	
	@Autowired
	@Qualifier("cacheKeyGenerator")
	private CacheKeyGenerator cacheKeyGenerator;
	
	@Autowired
	private RedisManager redisManager;
	
	@Override
	public Dao<DmsWeightFlow> getDao() {
		return this.dmsWeightFlowDao;
	}

	@Override
	public boolean checkTotalWeight(String waybillCode) {
		if(StringHelper.isNotEmpty(waybillCode)){
			DmsWeightFlowCondition dmsWeightFlowCondition = new DmsWeightFlowCondition();
			dmsWeightFlowCondition.setOperateType(Constants.OPERATE_TYPE_WEIGHT_BY_WAYBILL);
			dmsWeightFlowCondition.setWaybillCode(waybillCode);
			int num = dmsWeightFlowDao.queryNumByCondition(dmsWeightFlowCondition);
			return num > 0;
		}
		return false;
	}

	public boolean saveDmsWeightFlow(DmsWeightFlow dmsWeightFlow) {
		String[] hashKey = BusinessHelper.getHashKeysByPackageCode(dmsWeightFlow.getPackageCode());
		if(hashKey != null){
			String key = this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.REDIS_KEY_DMS_WEIGHT_FLOW, hashKey[0]);
			String keyField = hashKey[1];
			boolean rest = redisManager.hset(key, keyField, JsonHelper.toJson(dmsWeightFlow));
			if(rest){
				redisManager.expire(key, Constants.TIME_SECONDS_ONE_WEEK);
			}
			return rest;
		}
		return false;
	}
	public DmsWeightFlow getDmsWeightFlowByPackageCode(String packageCode) {
		String[] hashKey = BusinessHelper.getHashKeysByPackageCode(packageCode);
		if(hashKey != null){
			String key = this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.REDIS_KEY_DMS_WEIGHT_FLOW, hashKey[0]);
			String keyField = hashKey[1];
			String value = this.redisManager.hget(key, keyField);
			if(StringHelper.isNotEmpty(value)){
				return JsonHelper.fromJson(value, DmsWeightFlow.class);
			}
		}
		return null;
	}
	public Map<String,DmsWeightFlow> getDmsWeightFlowsByWaybillCode(String waybillCode) {
		int pageIndex = 1;
		String hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
		if(hashKey != null){
			Map<String,String> values = this.redisManager.hgetall(hashKey);
			while(!values.isEmpty()){
				hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
				values = this.redisManager.hgetall(hashKey);
				++ pageIndex;
			}
		}
		return null;
	}
}
