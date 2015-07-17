package com.jd.bluedragon.distribution.electron.service;

import com.jd.bluedragon.distribution.electron.domain.ElectronSite;

 


/**
 * 电子标签提供服务
 * @author guoyongzhi
 *
 */
public interface ElectronSiteService {

	/**
	 * 根据分拣中心ID和点单号 获取对应的电子标签区域信息
	 * @param dmsID
	 * @param waybillCode
	 * @return
	 */
	public ElectronSite getElecSiteInfo(Integer dmsID, String waybillorPackCode);
	
	/**
	 * 根据分拣中心ID / 任务区信息 判断是否存在任务区
	 * @param dmsID
	 * @param taskAreaNo
	 * @return
	 */
	public ElectronSite getTaskAreaNo(Integer dmsID, Integer taskAreaNo);
 
}
