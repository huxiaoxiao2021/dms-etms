package com.jd.bluedragon.distribution.base.service;

public interface AirTransportService {

	/**
	 * 获取航空标示打印包裹
	 * @return
	 */
	public int getAirConfig(Integer busiId , Integer startDmsCode , Integer siteCode );
	
	/**
	 * 获取商家航空信息
	 * @return
	 */
	public boolean getAirSigns(Integer busiId);
}
