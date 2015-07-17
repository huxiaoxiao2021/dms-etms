package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.external.service.DmsExternalService;
import com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;
import com.jd.bluedragon.distribution.wss.service.PopAbnormalWssService;
import com.jd.bluedragon.distribution.wss.service.ReverseWssService;
import com.jd.bluedragon.distribution.wss.service.SealVehicleBoxService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("dmsExternalService")

public class DmsExternalServiceImpl implements DmsExternalService {
	
	private final Logger logger = Logger.getLogger(DeliveryServiceImpl.class);


	@Autowired
	private SealVehicleBoxService vehicleBoxService;

	@Autowired
	private ReverseWssService reverseWssService;

	@Autowired
	private PopAbnormalWssService popWssService;


	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.updatePopPackNum", mState = {JProEnum.TP})
	public Boolean updatePopPackNum(String message) {
		return popWssService.updatePopPackNum(message);
	}

	@Override
	public Boolean addRejectMessage(String message) {
		return reverseWssService.addRejectMessage(message);
	}

	@Override
	public Boolean addReceiveMessage(String message) {
		return reverseWssService.addReceiveMessage(message);
	}

	@Override
	public Boolean addReceivePopMessage(String message) {
		return reverseWssService.addReceivePopMessage(message);
	}

	@Override
	public Boolean addReceiveStockMessage(String message) throws Exception {
		return reverseWssService.addReceiveStockMessage(message);
	}

	@Override
	public Boolean addSaleMessage(String message) {
		return reverseWssService.addSaleMessage(message);
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
