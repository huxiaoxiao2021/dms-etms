package com.jd.bluedragon.distribution.abnormal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;

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
			if(DmsOperateHint.HINT_CODE_NEED_REPRINT.equals(dmsOperateHint.getHintCode())){
				redisKey = CacheKeyConstants.CACHE_KEY_HINT_MSG_NEED_REPRINT + dmsOperateHint.getWaybillCode();
			}
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
		if(StringHelper.isNotEmpty(waybillCode)){
			String redisKey = CacheKeyConstants.CACHE_KEY_HINT_MSG_NEED_REPRINT + waybillCode;
			String msg = redisManager.getCache(redisKey);
			if(msg==null){
				redisKey = Constants.CACHE_KEY_PRE_PDA_HINT + waybillCode;
				msg = redisManager.getCache(redisKey);
				if(msg!=null){
					return msg;
				}
			}else{
				return msg;
			}
		}
		return "";
	}

	@Override
	public String getDeliveryHintMessageByWaybillCode(String waybillCode) {
		return getInspectHintMessageByWaybillCode(waybillCode);
	}

	@Override
	public DmsOperateHint getNeedReprintHintMsg(String waybillCode) {
		if(StringHelper.isNotEmpty(waybillCode)){
			String redisKey = CacheKeyConstants.CACHE_KEY_HINT_MSG_NEED_REPRINT + waybillCode;
			String msg = redisManager.getCache(redisKey);
			if(msg!=null){
				return JsonHelper.fromJson(msg, DmsOperateHint.class);
			}
		}
		return null;
	}

	@Override
	public DmsOperateHint saveNeedReprintHintMsg(DmsOperateHint dmsOperateHint) {
		return null;
	}

	@Override
	public boolean closeNeedReprintHintMsg(String waybillCode) {
		return false;
	}

}
