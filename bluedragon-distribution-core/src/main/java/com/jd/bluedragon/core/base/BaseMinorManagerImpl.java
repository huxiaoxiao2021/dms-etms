package com.jd.bluedragon.core.base;

import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.domain.*;
import com.jd.ql.basic.dto.BaseGoodsPositionDto;
import com.jd.ql.basic.dto.BasePdaUserDto;
import com.jd.ql.basic.dto.BaseTradeInfoDto;
import com.jd.ql.basic.proxy.BasicSecondaryWSProxy;
import com.jd.ql.basic.ws.BasicAirConfigWS;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("baseMinorManager")
public class BaseMinorManagerImpl implements BaseMinorManager {
	
	public static final String SEPARATOR_HYPHEN = "-";

	@Autowired
	@Qualifier("basicSecondaryWS")
	private BasicSecondaryWS basicSecondaryWS;
	
	@Autowired
	@Qualifier("basicSecondaryWSProxy")
	private BasicSecondaryWSProxy basicSecondaryWSProxy;
	
	/**
	 * 
	 * 获取航空标示信息接口
	 */
	@Autowired
	private BasicAirConfigWS basicAirConfigWS;
	
	
	
	@Cache(key = "baseMinorManagerImpl.getBaseTraderById@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderById", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseTradeInfoDto getBaseTraderById(Integer paramInteger) {
		return basicSecondaryWS.getBaseTraderById(paramInteger);
	}

	@Cache(key = "baseMinorManagerImpl.getMainBranchScheduleByTranCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getMainBranchScheduleByTranCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public MainBranchSchedule getMainBranchScheduleByTranCode(String paramString) {
		return basicSecondaryWS.getMainBranchScheduleByTranCode(paramString);
	}

	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getMainBranchScheduleList", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResult<List<MainBranchSchedule>> getMainBranchScheduleList(
			MainBranchSchedule mbs) {
		return basicSecondaryWS.getMainBranchScheduleList(mbs);
	}

	@Cache(key = "baseMinorManagerImpl.getAirConfig@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getAirConfig", mState = {JProEnum.TP, JProEnum.FunctionError})
	public AirTransport getAirConfig(Integer originalProvinceId,
			Integer originalCityId, Integer destinationProvinceId,
			Integer destinationCityId) {

		return basicAirConfigWS
				.getAirConfig(originalProvinceId, originalCityId,
						destinationProvinceId, destinationCityId);
	}
	
	@Cache(key = "baseMinorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID,String flage, Integer siteCode) {
		return ((List<BaseGoodsPositionDto>) basicSecondaryWS.getBaseGoodsPositionDmsCodeSiteCode(dmsID, siteCode));
		 
	}

	@Cache(key = "baseMinorManagerImpl.getBaseGoodsPositionTaskAreaNoDmsId@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseGoodsPositionTaskAreaNoDmsId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(
			Integer dmsID,String flage, Integer taskAreaNo) {
		return (List<BaseGoodsPositionDto>) basicSecondaryWS.getBaseGoodsPositionTaskAreaNoDmsId(String.valueOf(taskAreaNo), dmsID);
	}

	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getCrossPackageTagByPara", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResult<CrossPackageTagNew> getCrossPackageTagByPara(
			BaseDmsStore bds, Integer siteCode, Integer startDmsCode) {
		// TODO Auto-generated method stub
		return basicSecondaryWS.getCrossPackageTagByPara(bds, siteCode, startDmsCode);
	}

	@Cache(key = "baseMinorManagerImpl.getBaseAllTrader", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseAllTrader", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseTradeInfoDto> getBaseAllTrader() {
		// TODO Auto-generated method stub
		return basicSecondaryWSProxy.getBaseAllTrader();
	}

	@Cache(key = "baseMinorManagerImpl.getBaseTraderByName@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderByName", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseTradeInfoDto> getBaseTraderByName(String name) {
		// TODO Auto-generated method stub
		return basicSecondaryWS.getBaseTraderByName(name);
	}

    /**
     * 获取签约商家ID列表
     * @return
     */
    @Cache(key = "baseMinorManagerImpl.getTraderInfoPopCodeAll", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getSignCustomer", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<String> getSignCustomer(){
        return basicSecondaryWS.getTraderInfoPopCodeAll();
    }

	@Override
	public BasePdaUserDto pdaUserLogin(String erpcode, String password) {
		// TODO Auto-generated method stub
		return basicSecondaryWS.pdaUserLogin(erpcode, password);
	}

}
