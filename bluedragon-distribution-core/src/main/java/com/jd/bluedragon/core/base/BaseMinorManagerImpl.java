package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.etms.basic.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.etms.basic.cache.proxy.BasicForeignWebServiceProxy;
import com.jd.etms.basic.cache.proxy.BasicMinorWSProxy;
import com.jd.etms.basic.dto.BaseCrossDto;
import com.jd.etms.basic.dto.BaseGoodsPositionDto;
import com.jd.etms.basic.dto.BaseTradeInfoDto;
import com.jd.etms.basic.saf.BaseAirConfigSafService;
import com.jd.etms.basic.webservice.BasicForeignWebService;
import com.jd.etms.basic.wss.BasicMinorWS;
import com.jd.etms.utils.cache.annotation.Cache;

@Service("baseMinorManager")
public class BaseMinorManagerImpl implements BaseMinorManager {
	
	public static final String SEPARATOR_HYPHEN = "-";

	@Autowired
	@Qualifier("basicMinorWSSaf")
	private BasicMinorWS basicMinorWSSaf;
	
	@Autowired
	@Qualifier("basicMinorWSProxy")
	private BasicMinorWSProxy basicMinorWSProxy;
	
	/**
	 * 
	 * 获取道口号信息接口
	 */
	@Autowired
    private BasicForeignWebService basicForeignWebService;
	
	@Autowired
    private BasicForeignWebServiceProxy basicForeignWebServiceProxy;
	
	/**
	 * 
	 * 获取航空标示信息接口
	 */
	@Autowired
	BaseAirConfigSafService baseAirConfigSafService;
	
	
	
	@Cache(key = "basicMinorWSProxy.getBaseTraderById@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public BaseTradeInfoDto getBaseTraderById(Integer paramInteger) {
		return basicMinorWSSaf.getBaseTraderById(paramInteger);
	}

	@Cache(key = "basicMinorWSProxy.getMainBranchScheduleByTranCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public MainBranchSchedule getMainBranchScheduleByTranCode(String paramString) {
		return basicMinorWSSaf.getMainBranchScheduleByTranCode(paramString);
	}

	public BaseResult<List<MainBranchSchedule>> getMainBranchScheduleList(
			MainBranchSchedule mbs) {
		return basicMinorWSSaf.getMainBranchScheduleList(mbs);
	}

	@Cache(key = "basicForeignWebServiceProxy.getComplexCrossDetail@args0&@args1", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 5 * 60 * 1000)
	public BaseCrossDto getComplexCrossDetail(String dmsCode, String siteCode) {
		return basicForeignWebService.getComplexCrossDetail(dmsCode, siteCode);
	}

	@Cache(key = "baseAirConfigSafService.getAirConfig@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public AirTransport getAirConfig(Integer originalProvinceId,
			Integer originalCityId, Integer destinationProvinceId,
			Integer destinationCityId) {

		return baseAirConfigSafService
				.getAirConfig(originalProvinceId, originalCityId,
						destinationProvinceId, destinationCityId);
	}
	
	@Cache(key = "basicMinorWSProxy.getBaseGoodsPositionDmsCodeSiteCode@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID,String flage, Integer siteCode) {
		return ((List<BaseGoodsPositionDto>) basicMinorWSSaf.getBaseGoodsPositionDmsCodeSiteCode(dmsID, siteCode));
		 
	}

	@Cache(key = "basicMinorWSProxy.getBaseGoodsPositionTaskAreaNoDmsId@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(
			Integer dmsID,String flage, Integer taskAreaNo) {
		return (List<BaseGoodsPositionDto>) basicMinorWSSaf.getBaseGoodsPositionTaskAreaNoDmsId(String.valueOf(taskAreaNo), dmsID);
	}


	public BaseResult<CrossPackageTagNew> getCrossPackageTagByPara(
			BaseDmsStore bds, Integer siteCode, Integer startDmsCode) {
		// TODO Auto-generated method stub
		return basicMinorWSSaf.getCrossPackageTagByPara(bds, siteCode, startDmsCode);
	}

	@Cache(key = "basicMinorWSProxy.getBaseAllTrader", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseTradeInfoDto> getBaseAllTrader() {
		// TODO Auto-generated method stub
		return basicMinorWSProxy.getBaseAllTrader();
	}

	@Cache(key = "basicMinorWSProxy.getBaseTraderByName@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseTradeInfoDto> getBaseTraderByName(String name) {
		// TODO Auto-generated method stub
		return basicMinorWSSaf.getBaseTraderByName(name);
	}

    /**
     * 获取签约商家ID列表
     * @return
     */
    @Cache(key = "basicMinorWSProxy.getTraderInfoPopCodeAll", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
    public List<String> getSignCustomer(){
        return basicForeignWebServiceProxy.getTraderInfoPopCodeAll();
    }

	@Override
	@Cache(key = "baseMajorManagerImpl.getValidBaseDataDictList@args0@args1@args2" , memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000 )
	public List<BaseDataDict> getValidBaseDataDictList(Integer parentGroup, Integer nodeLevel, Integer typeGroup) {
		return basicMinorWSSaf.getValidDataDict(parentGroup, nodeLevel, typeGroup);
	}

}
