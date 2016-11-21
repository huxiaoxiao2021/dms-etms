package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.OrderTraceDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

@Service("waybillQueryManager")
public class WaybillQueryManagerImpl implements WaybillQueryManager {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private WaybillQueryApi waybillQueryApi;

	@Autowired
	private WaybillTraceApi waybillTraceApi;
	
	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;
	
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

	@Override
	public Integer checkReDispatch(String packageCode) {
		Integer result = REDISPATCH_NO;
		CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.checkReDispatch", false, true);
		BaseEntity<List<PackageState>> baseEntity = null;
		try {
			// http://cf.jd.com/pages/viewpage.action?pageId=73834851 取件单批量查询接口
			baseEntity = waybillTraceApi.getPkStateByWCodeAndState(packageCode, "140");
			if (baseEntity != null) {
				if (baseEntity.getResultCode() != 1) {
					this.logger.error("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：" + packageCode + ","
							+ baseEntity.getResultCode() + "," + baseEntity.getMessage());
					result = REDISPATCH_ERROR;
				} else{
					if(baseEntity.getData() != null && baseEntity.getData().size()>0){
						result = REDISPATCH_YES;
					}else{
						result = REDISPATCH_NO;
					}
				}
			} else {
				this.logger.error("检查是否反调度WaybillQueryManagerImpl.checkReDispatch返回空：" + packageCode);
				result = REDISPATCH_ERROR;
			}
		} catch (Exception e) {
			Profiler.functionError(info);
			this.logger.error("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：" + packageCode, e);
			result = REDISPATCH_ERROR;
		} finally {
			Profiler.registerInfoEnd(info);
		}
		return result;
	}
	
	@Override
	public String getChangeWaybillCode(String oldWaybillCode) {
		String changedWaybillCode = null;
		CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.checkReDispatch", false, true);
		BaseEntity<Map<String, String>> baseEntity = null;
		try {
			List<String> waybillCodes = new ArrayList<String>();
			waybillCodes.add(oldWaybillCode);
			//http://cf.jd.com/pages/viewpage.action?pageId=74538367 运单指查询面单接口
			baseEntity = waybillPickupTaskApi.batchQuerySurfaceCodes(waybillCodes);
			if (baseEntity != null) {
				if (baseEntity.getResultCode() != 1) {
					this.logger.error("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState异常：" + oldWaybillCode + ","
							+ baseEntity.getResultCode() + "," + baseEntity.getMessage());
				} else if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
					changedWaybillCode = baseEntity.getData().get(oldWaybillCode);
				}
			} else {
				this.logger.error("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState返回空：" + oldWaybillCode);
			}
		} catch (Exception e) {
			Profiler.functionError(info);
			this.logger.error("获取取件单对应的面单号W单号WaybillQueryManagerImpl.checkReDispatch异常：" + oldWaybillCode, e);
		} finally {
			Profiler.registerInfoEnd(info);
		}
		return changedWaybillCode;
	}
	
	
	public static void main(String [] args){
//		WaybillQueryManagerImpl manager = new WaybillQueryManagerImpl();
//		Integer changedWaybill = manager.checkReDispatchtest("1460638776");
//		
//		
//		System.out.println(changedWaybill);
	}
}
