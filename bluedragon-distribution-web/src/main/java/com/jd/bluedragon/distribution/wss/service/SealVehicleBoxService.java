package com.jd.bluedragon.distribution.wss.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-13 下午02:55:44
 * 
 *             封箱封车WSS服务
 */
public interface SealVehicleBoxService {

	/**
	 * 批量增加封车信息
	 * 
	 * @param sealVehicleList
	 * @return
	 */
	public BaseEntity<Map<String, Integer>> batchAddSealVehicle(
			List<SealVehicleDto> sealVehicleList);

	/**
	 * 批量验证并保存解封车信息
	 * 
	 * @param sealVehicleList
	 * @return
	 */
	public BaseEntity<Map<String, Integer>> batchUpdateSealVehicle(
			List<SealVehicleDto> sealVehicleList);

	/**
	 * 批量保存封箱信息
	 * 
	 * @param sealBoxList
	 * @return
	 */
	public BaseEntity<Map<String, Integer>> batchAddSealBox(
			List<SealBoxDto> sealBoxList);
}
