package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.ql.basic.domain.*;
import com.jd.ql.basic.dto.*;


public interface BaseMinorManager {
	
	public abstract BaseTradeInfoDto getBaseTraderById(Integer paramInteger);
	
	public abstract MainBranchSchedule getMainBranchScheduleByTranCode(String paramString);

	public abstract BaseResult<List<MainBranchSchedule>> getMainBranchScheduleList(
			MainBranchSchedule mbs);
	
	public abstract AirTransport getAirConfig(Integer originalProvinceId, Integer originalCityId,Integer destinationProvinceId, Integer destinationCityId);
	
	public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID,String flage, Integer siteCode) ;
	
	public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(Integer dmsID,String flage, Integer taskAreaNo) ;
	public BaseResult<CrossPackageTagNew> getCrossPackageTagByPara(BaseDmsStore bds, Integer siteCode, Integer startDmsCode);
	
	public List<BaseTradeInfoDto> getBaseAllTrader();
	
	public List<BaseTradeInfoDto> getBaseTraderByName(String name);
    /**
     * 获取签约商家ID列表
     * @return 签约商家ID列表
     */
    public List<String> getSignCustomer();
    
    public BasePdaUserDto pdaUserLogin(String erpcode, String password);

}
