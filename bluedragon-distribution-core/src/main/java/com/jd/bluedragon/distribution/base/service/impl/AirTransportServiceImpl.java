package com.jd.bluedragon.distribution.base.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.etms.basic.domain.AirTransport;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.basic.dto.BaseTradeInfoDto;

@Service("airTransportService")
public class AirTransportServiceImpl implements AirTransportService{
	
	/** 日志 */
	private Logger log = Logger.getLogger(AirTransportServiceImpl.class);
	
	@Autowired
	SiteService siteService;
	
	@Autowired
    private BaseMinorManager baseMinorManager;
	
	@Override
	public int getAirConfig(Integer busiId, Integer startDmsCode, Integer siteCode) {
		try {
			BaseStaffSiteOrgDto baseDto = siteService.getSite(startDmsCode);
			if (baseDto == null) {
				log.error("基础资料获取分拣中心站点信息为空"+startDmsCode);
				return 0;
			}
			Integer originalProvinceId = baseDto.getProvinceId();
			Integer originalCityId = baseDto.getCityId();
			baseDto = siteService.getSite(siteCode);
			if (baseDto == null) {
				log.error("基础资料获取目的站点信息为空"+siteCode);
				return 0;
			}
			Integer destinationProvinceId = baseDto.getProvinceId();
			Integer destinationCityId = baseDto.getCityId();
			BaseTradeInfoDto infoDto = siteService.getTrader(busiId);
			if (infoDto == null || infoDto.getAirTransport()==null) {
				return 0;
			}
			if (infoDto.getAirTransport().equals("Y")) {
				log.info("商家航空标示不为空");
				AirTransport tAirTransport = baseMinorManager
						.getAirConfig(originalProvinceId, originalCityId,
								destinationProvinceId, destinationCityId);
				if (tAirTransport == null) {
					log.info("省市信息查询航空配置信息为空");
					tAirTransport = baseMinorManager.getAirConfig(
							originalProvinceId, null, destinationProvinceId,
							null);
					if (tAirTransport != null) {
						log.info("省信息查询航空配置信息不为空");
						return 1;
					}
				} else {
					return 1;
				}
			}
		} catch (Exception e) {
			this.log.error("基础资料接口调用异常", e);
		}
		return 0;
	}

	@Override
	public boolean getAirSigns(Integer busiId) {

		BaseTradeInfoDto infoDto = siteService.getTrader(busiId);

		if (infoDto == null || infoDto.getAirTransport() == null) {
			return false;
		}

		if (infoDto.getAirTransport().equals("Y")) {
			return true;
		}

		return false;
	}

}
