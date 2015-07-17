package com.jd.bluedragon.core.base;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.OrderTraceDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.wss.WaybillAddWS;
import com.jd.etms.waybill.wss.WaybillQueryWS;

@Service("waybillQueryManager")
public class WaybillQueryManagerImpl implements WaybillQueryManager {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private WaybillQueryWS waybillQueryWS;

	@Autowired
	private WaybillAddWS waybillAddWS;
	
	@Override
	public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			WChoice wChoice) {
		return waybillQueryWS.getDataByChoice(waybillCode, wChoice);
	}

	@Override
	public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM,
			Boolean isPackList) {
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(isWaybillC);
		wChoice.setQueryWaybillE(isWaybillE);
		wChoice.setQueryWaybillM(isWaybillM);
		wChoice.setQueryPackList(isPackList);
		return waybillQueryWS.getDataByChoice(waybillCode, wChoice);
	}

	@Override
	public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM,
			Boolean isGoodList, Boolean isPackList, Boolean isPickupTask,
			Boolean isServiceBillPay) {
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(isWaybillC);
		wChoice.setQueryWaybillE(isWaybillE);
		wChoice.setQueryWaybillM(isWaybillM);
		wChoice.setQueryGoodList(isGoodList);
		wChoice.setQueryPackList(isPackList);
		wChoice.setQueryPickupTask(isPickupTask);
		wChoice.setQueryServiceBillPay(isServiceBillPay);
		return waybillQueryWS.getDataByChoice(waybillCode, wChoice);
	}
	
	@Override
	public boolean sendOrderTrace(String businessKey, int msgType, String title, String content, String operatorName, Date operateTime) {
		OrderTraceDto orderTraceDto = new OrderTraceDto();
		orderTraceDto.setBusinessKey(businessKey);
		orderTraceDto.setMsgType(msgType);
		orderTraceDto.setTitle(title);
		orderTraceDto.setContent(content);
		orderTraceDto.setOperatorName(operatorName);
		orderTraceDto.setOperateTime(operateTime==null?new Date():operateTime);
		BaseEntity<Boolean> baseEntity = waybillAddWS.sendOrderTrace(orderTraceDto);
		if(baseEntity!=null){
			if(!baseEntity.getData()){
				this.logger.error("分拣数据回传全程跟踪sendOrderTrace异常："+baseEntity.getMessage()+baseEntity.getData());
				return false;
			}
		}else{
			this.logger.error("分拣数据回传全程跟踪接口sendOrderTrace异常");
			return false;
		}
		
		return true;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean sendBdTrace(BdTraceDto bdTraceDto) {
		BaseEntity baseEntity = waybillAddWS.sendBdTrace(bdTraceDto);
		if(baseEntity!=null){
			if(baseEntity.getResultCode()!=1){
				this.logger.error("分拣数据回传全程跟踪sendBdTrace异常："+baseEntity.getMessage());
				return false;
			}
		}else{
			this.logger.error("分拣数据回传全程跟踪接口sendBdTrace异常");
			return false;
		}
		return true;
	}
}
