package com.jd.bluedragon.core.base;

import java.util.Date;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.OrderTraceDto;
import com.jd.etms.waybill.dto.WChoice;

@Service("waybillQueryManager")
public class WaybillQueryManagerImpl implements WaybillQueryManager {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private WaybillQueryApi waybillQueryApi;

	@Autowired
	private WaybillTraceApi waybillTraceApi;
	
	@Override
	public BaseEntity<Waybill> getWaybillByReturnWaybillCode(String waybillCode) {
		return waybillQueryApi.getWaybillByReturnWaybillCode(waybillCode);
	}
	
	@Override
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			WChoice wChoice) {
		return waybillQueryApi.getDataByChoice(waybillCode, wChoice);
	}

	@Override
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM,
			Boolean isPackList) {
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(isWaybillC);
		wChoice.setQueryWaybillE(isWaybillE);
		wChoice.setQueryWaybillM(isWaybillM);
		wChoice.setQueryPackList(isPackList);
		return waybillQueryApi.getDataByChoice(waybillCode, wChoice);
	}

	@Override
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
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
		return waybillQueryApi.getDataByChoice(waybillCode, wChoice);
	}
	
	@Override
	public boolean sendOrderTrace(String businessKey, int msgType, String title, String content, String operatorName, Date operateTime) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.sendOrderTrace", false, true);
		try{
			OrderTraceDto orderTraceDto = new OrderTraceDto();
			orderTraceDto.setBusinessKey(businessKey);
			orderTraceDto.setMsgType(msgType);
			orderTraceDto.setTitle(title);
			orderTraceDto.setContent(content);
			orderTraceDto.setOperatorName(operatorName);
			orderTraceDto.setOperateTime(operateTime==null?new Date():operateTime);
			BaseEntity<Boolean> baseEntity = waybillTraceApi.sendOrderTrace(orderTraceDto);
			if(baseEntity!=null){
				if(!baseEntity.getData()){
					this.logger.error("分拣数据回传全程跟踪sendOrderTrace异常："+baseEntity.getMessage()+baseEntity.getData());
					Profiler.functionError(info);
					return false;
				}
			}else{
				this.logger.error("分拣数据回传全程跟踪接口sendOrderTrace异常");
				Profiler.functionError(info);
				return false;
			}
		}catch(Exception e){
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		return true;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean sendBdTrace(BdTraceDto bdTraceDto) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.sendBdTrace", false, true);
		try{
			BaseEntity baseEntity = waybillTraceApi.sendBdTrace(bdTraceDto);
			if(baseEntity!=null){
				if(baseEntity.getResultCode()!=1){
					this.logger.error(JsonHelper.toJson(bdTraceDto));
					this.logger.error(bdTraceDto.getWaybillCode());
					this.logger.error("分拣数据回传全程跟踪sendBdTrace异常："+baseEntity.getMessage());
					Profiler.functionError(info);
					return false;
				}
			}else{
				this.logger.error("分拣数据回传全程跟踪接口sendBdTrace异常"+bdTraceDto.getWaybillCode());
				Profiler.functionError(info);
				return false;
			}
		}catch(Exception e){
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		return true;
	}


	public static void main(String [] args){
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryWaybillE(true);
		wChoice.setQueryWaybillM(true);
		wChoice.setQueryGoodList(true);
		wChoice.setQueryPackList(true);
		wChoice.setQueryPickupTask(true);
		wChoice.setQueryServiceBillPay(true);

		System.out.println(JsonHelper.toJson(wChoice));


	}
}
