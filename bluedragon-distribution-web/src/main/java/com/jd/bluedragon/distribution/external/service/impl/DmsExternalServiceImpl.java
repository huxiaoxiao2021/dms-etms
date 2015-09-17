package com.jd.bluedragon.distribution.external.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.external.service.DmsExternalService;
import com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;
import com.jd.bluedragon.distribution.wss.service.PopAbnormalWssService;
import com.jd.bluedragon.distribution.wss.service.SealVehicleBoxService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

@Service("dmsExternalService")

public class DmsExternalServiceImpl implements DmsExternalService {
	
	private final Logger logger = Logger.getLogger(DeliveryServiceImpl.class);


	@Autowired
	private SealVehicleBoxService vehicleBoxService;

	@Autowired
	private PopAbnormalWssService popWssService;


	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.updatePopPackNum", mState = {JProEnum.TP})
	public Boolean updatePopPackNum(String message) {
		return popWssService.updatePopPackNum(message);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.batchAddSealVehicle", mState = {JProEnum.TP})
	public BaseEntity<Map<String, Integer>> batchAddSealVehicle(List<SealVehicleDto> sealVehicleList) {
		return vehicleBoxService.batchAddSealVehicle(sealVehicleList);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.batchUpdateSealVehicle", mState = {JProEnum.TP})
	public BaseEntity<Map<String, Integer>> batchUpdateSealVehicle(List<SealVehicleDto> sealVehicleList) {
		return vehicleBoxService.batchUpdateSealVehicle(sealVehicleList);
	}

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.batchAddSealBox", mState = {JProEnum.TP})
	public BaseEntity<Map<String, Integer>> batchAddSealBox(List<SealBoxDto> sealBoxList) {
		return vehicleBoxService.batchAddSealBox(sealBoxList);
	}
}
