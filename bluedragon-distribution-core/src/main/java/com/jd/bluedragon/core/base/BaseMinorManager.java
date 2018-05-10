package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.*;
import com.jd.ql.basic.dto.*;


public interface BaseMinorManager {
	
	public abstract BasicTraderInfoDTO getBaseTraderById(Integer paramInteger);

	public abstract AirTransport getAirConfig(Integer originalProvinceId, Integer originalCityId,Integer destinationProvinceId, Integer destinationCityId);
	
	public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID,String flage, Integer siteCode) ;
	
	public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(Integer dmsID,String flage, Integer taskAreaNo) ;
	public BaseResult<CrossPackageTagNew> getCrossPackageTagByPara(BaseDmsStore bds, Integer siteCode, Integer startDmsCode);
	
	public List<BasicTraderInfoDTO> getBaseAllTrader();
	
	public List<BasicTraderInfoDTO> getBaseTraderByName(String name);
    /**
     * 获取签约商家ID列表
     * @return 签约商家ID列表
     */
    public List<String> getSignCustomer();
    
	/**
	 * 根据站点code查找三方承运超限指标
	 *
	 * @param siteCode 站点编码
	 * @return
	 */
	BaseSiteGoods getGoodsVolumeLimitBySiteCode(Integer siteCode);


	public BasicTraderInfoDTO getTraderInfoByPopCode(String popCode);

}
