package com.jd.bluedragon.distribution.external.service.impl;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.LoadBillReportRequest;
import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.service.LoadBillService;
import com.jd.jsf.gd.util.StringUtils;
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

	@Autowired
	private LoadBillService loadBillService;

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

	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.updateLoadBillStatus", mState = {JProEnum.TP})
	public LoadBillReportResponse updateLoadBillStatus(LoadBillReportRequest request) {
		LoadBillReportResponse response = new LoadBillReportResponse(1, JdResponse.MESSAGE_OK);
		try{
			if(request == null || StringUtils.isBlank(request.getReportId())
					|| StringUtils.isBlank(request.getLoadId())
					|| StringUtils.isBlank(request.getOrderId())
					|| StringUtils.isBlank(request.getLoadId())
					|| StringUtils.isBlank(request.getWarehouseId())
					|| request.getStatus() == null
					|| request.getStatus() < 1){
				return new LoadBillReportResponse(2, "参数错误");
			}
			loadBillService.updateLoadBillStatusByReport(resolveLoadBillReport(request));
		}catch(Exception e){
			response = new LoadBillReportResponse(2, "操作异常");
			logger.error("GlobalTradeController 发生异常,异常信息 : ", e);
		}
		return response;
	}

	private LoadBillReport resolveLoadBillReport(LoadBillReportRequest request) {
		LoadBillReport report = new LoadBillReport();
		report.setReportId(request.getReportId());
		report.setLoadId(request.getLoadId());
		report.setProcessTime(request.getProcessTime());
		report.setStatus(request.getStatus());
		report.setWarehouseId(request.getWarehouseId());
		report.setNotes(request.getNotes());
		report.setOrderId(request.getOrderId());
		return report;
	}

}
