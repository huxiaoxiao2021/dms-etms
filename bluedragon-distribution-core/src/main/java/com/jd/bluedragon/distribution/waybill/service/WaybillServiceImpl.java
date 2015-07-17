package com.jd.bluedragon.distribution.waybill.service;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.wss.WaybillQueryWS;

@Service
public class WaybillServiceImpl implements WaybillService {

	@Autowired
	@Qualifier("waybillQueryWSProxy")
	private WaybillQueryWS waybillQueryWs;

	@Profiled(tag = "WaybillService.getWaybill")
	public BigWaybillDto getWaybill(String waybillCode) {
		String aWaybillCode = BusinessHelper.getWaybillCode(waybillCode);

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryWaybillE(true);
		wChoice.setQueryWaybillM(true);
		wChoice.setQueryPackList(true);
		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryWs.getDataByChoice(aWaybillCode,
		        wChoice);

		return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
	}

	@Profiled(tag = "WaybillService.getWaybillProduct")
	public BigWaybillDto getWaybillProduct(String waybillCode) {
		String aWaybillCode = BusinessHelper.getWaybillCode(waybillCode);
		
		WChoice wChoice = new WChoice();
		wChoice.setQueryGoodList(true);

		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryWs.getDataByChoice(aWaybillCode,
		        wChoice);
		
		return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
	}
	
	@Profiled(tag = "WaybillService.getWaybillState")
	public BigWaybillDto getWaybillState(String waybillCode) {

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillM(true);
		BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryWs.getDataByChoice(waybillCode,
		        wChoice);

		return baseEntity != null && baseEntity.getData() != null ? baseEntity.getData() : null;
	}

}
