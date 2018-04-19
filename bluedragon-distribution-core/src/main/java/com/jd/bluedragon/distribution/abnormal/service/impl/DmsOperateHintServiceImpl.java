package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintDao;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;

/**
 *
 * @ClassName: DmsOperateHintServiceImpl
 * @Description: 运单操作提示--Service接口实现
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
@Service("dmsOperateHintService")
public class DmsOperateHintServiceImpl extends BaseService<DmsOperateHint> implements DmsOperateHintService {

	@Autowired
	@Qualifier("dmsOperateHintDao")
	private DmsOperateHintDao dmsOperateHintDao;
	
	@Autowired
	private RedisManager redisManager;

	@Override
	public Dao<DmsOperateHint> getDao() {
		return this.dmsOperateHintDao;
	}

	@Override
	public boolean saveOrUpdate(DmsOperateHint dmsOperateHint) {
		boolean saveFlg = super.saveOrUpdate(dmsOperateHint);
		if(saveFlg){
			String redisKey = Constants.CACHE_KEY_PRE_PDA_HINT + dmsOperateHint.getWaybillCode();
			if(Constants.INTEGER_FLG_FALSE.equals(dmsOperateHint.getIsEnable())){
				redisManager.del(redisKey);
            }else{
            	redisManager.setex(redisKey, Constants.TIME_SECONDS_ONE_MONTH, dmsOperateHint.getHintMessage());
            }
		}
		return saveFlg;
	}

	@Override
	public String getInspectHintMessageByWaybillCode(String waybillCode) {
		String redisKey = Constants.CACHE_KEY_PRE_PDA_HINT + waybillCode;
		String msg = redisManager.getCache(redisKey);
		if(msg==null){
			return "";
		}
		return msg;
	}

	@Override
	public String getDeliveryHintMessageByWaybillCode(String waybillCode) {
		String redisKey = Constants.CACHE_KEY_PRE_PDA_HINT + waybillCode;
		String msg = redisManager.getCache(redisKey);
		if(msg==null){
			return "";
		}
		return msg;
	}

}
