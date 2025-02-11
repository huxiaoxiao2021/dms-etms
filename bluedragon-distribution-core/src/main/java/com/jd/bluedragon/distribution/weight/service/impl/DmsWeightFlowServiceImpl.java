package com.jd.bluedragon.distribution.weight.service.impl;

import java.util.HashMap;
import java.util.List;
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
}
