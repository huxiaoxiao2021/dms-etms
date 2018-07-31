package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintTrackService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

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
	@Qualifier("jimdbCacheService")
	private CacheService jimdbCacheService;

	@Autowired
	private DmsOperateHintTrackService dmsOperateHintTrackService;

	@Override
	public Dao<DmsOperateHint> getDao() {
		return this.dmsOperateHintDao;
	}

	@Override
	public boolean saveOrUpdate(DmsOperateHint dmsOperateHint) {
		boolean saveFlg = super.saveOrUpdate(dmsOperateHint);
		if(saveFlg){
			//wuyde-del 兼容之前的缓存，后续删除
			String oldRedisKey = Constants.CACHE_KEY_PRE_PDA_HINT + dmsOperateHint.getWaybillCode();
			//默认用户级别的缓存
			String cacheKeyPrefix = CacheKeyConstants.CACHE_KEY_USER_HINT_MSG;
			if(DmsOperateHint.HINT_TYPE_SYS.equals(dmsOperateHint.getHintType())){
				cacheKeyPrefix = CacheKeyConstants.CACHE_KEY_SYS_HINT_MSG;
			}
			String redisKey = cacheKeyPrefix + dmsOperateHint.getWaybillCode();
			if(Constants.INTEGER_FLG_FALSE.equals(dmsOperateHint.getIsEnable())){
				jimdbCacheService.del(oldRedisKey);
				jimdbCacheService.del(redisKey);
            }else{
            	//wuyde-del 兼容之前的缓存，非系统级别的提示，设置旧的缓存key
            	if(!DmsOperateHint.HINT_TYPE_SYS.equals(dmsOperateHint.getHintType())){
            		jimdbCacheService.setEx(oldRedisKey, dmsOperateHint.getHintMessage(), Constants.TIME_SECONDS_ONE_MONTH);
            	}
            	jimdbCacheService.setEx(redisKey, dmsOperateHint.getHintMessage(), Constants.TIME_SECONDS_ONE_MONTH);
            }
		}
		return saveFlg;
	}

	@Override
	public String getInspectHintMessageByWaybillCode(String waybillCode) {
		if(StringHelper.isNotEmpty(waybillCode)){
//			String userMsgKey = CacheKeyConstants.CACHE_KEY_USER_HINT_MSG + waybillCode;
			String sysMsgKey = CacheKeyConstants.CACHE_KEY_SYS_HINT_MSG + waybillCode;
			//wuyde-del 兼容之前的缓存，后续删除
			String oldMsgKey = Constants.CACHE_KEY_PRE_PDA_HINT + waybillCode;
//			String userHintMsg = redisManager.getCache(userMsgKey);
			String sysHintMsg = jimdbCacheService.get(sysMsgKey);
			String oldHintMsg = jimdbCacheService.get(oldMsgKey);
			String hintMsg = oldHintMsg;
			if(sysHintMsg != null){
				if(hintMsg != null){
					sysHintMsg += "\n";
				}
				hintMsg = StringHelper.append(sysHintMsg, hintMsg);
			}
			if(hintMsg!=null){
				return hintMsg;
			}
		}
		return "";
	}

	@Override
	public String getDeliveryHintMessageByWaybillCode(String waybillCode) {
		return getInspectHintMessageByWaybillCode(waybillCode);
	}
	/**
	 * 根据运单号获取，补打提醒信息
	 * 1、先判断运单号是否存在系统提示
	 * 2、查询数据库查询提示信息
	 */
	@Override
	public DmsOperateHint getNeedReprintHint(String waybillCode) {
		if(StringHelper.isNotEmpty(waybillCode)){
			String redisKey = CacheKeyConstants.CACHE_KEY_SYS_HINT_MSG + waybillCode;
			if(this.jimdbCacheService.exists(redisKey)){
				return this.dmsOperateHintDao.queryNeedReprintHintByWaybillCode(waybillCode);
			}
		}
		return null;
	}

	/**
	 * 根据查询条件分页获取提示信息
	 * @param dmsOperateHintCondition
	 * @return
	 */
	@Override
	public PagerResult<DmsOperateHint> queryByPagerCondition(DmsOperateHintCondition dmsOperateHintCondition){
		PagerResult<DmsOperateHint> baseInfo = this.getDao().queryByPagerCondition(dmsOperateHintCondition);
		List<DmsOperateHint> dmsOperateHints = baseInfo.getRows();
		for(DmsOperateHint hint:dmsOperateHints){
			//从track表里查出来一条
			DmsOperateHintTrack track = dmsOperateHintTrackService.queryFirstTrack(hint.getWaybillCode(),hint.getDmsSiteCode());
			if(track != null) {
				hint.setHintDmsName(track.getHintDmsName());
				hint.setHintOperateNode(track.getHintOperateNode());
				hint.setHintTime(track.getHintTime());
			}
		}
		return baseInfo;
	}

	/**
	 * 获取开启状态的提示信息配置
	 * @param dmsOperateHint
	 * @return
	 */
	@Override
	public DmsOperateHint getEnabledOperateHint(DmsOperateHint dmsOperateHint){
		return dmsOperateHintDao.getEnabledOperateHint(dmsOperateHint);
	}

}
