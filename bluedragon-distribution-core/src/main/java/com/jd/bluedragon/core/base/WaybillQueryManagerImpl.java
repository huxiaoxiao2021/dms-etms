package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
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
	
    @Autowired
    private WaybillPackageApi waybillPackageApiJsf;
	
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
					this.logger.warn("分拣数据回传全程跟踪sendOrderTrace异常："+baseEntity.getMessage()+baseEntity.getData());
					Profiler.functionError(info);
					return false;
				}
			}else{
				this.logger.warn("分拣数据回传全程跟踪接口sendOrderTrace异常");
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
					this.logger.warn(JsonHelper.toJson(bdTraceDto));
					this.logger.warn(bdTraceDto.getWaybillCode());
					this.logger.warn("分拣数据回传全程跟踪sendBdTrace异常："+baseEntity.getMessage());
					Profiler.functionError(info);
					return false;
				}
			}else{
				this.logger.warn("分拣数据回传全程跟踪接口sendBdTrace异常"+bdTraceDto.getWaybillCode());
				Profiler.functionError(info);
				return false;
			}
		}catch(Exception e){
			logger.error("分拣数据回传全程跟踪sendBdTrace异常："+bdTraceDto.getWaybillCode(), e);
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		return true;
	}

	@Override
	public Integer checkReDispatch(String waybillCode) {
		Integer result = REDISPATCH_NO;
		CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.checkReDispatch", false, true);
		BaseEntity<List<PackageState>> baseEntity = null;
		try {
			// http://cf.jd.com/pages/viewpage.action?pageId=73834851 取件单批量查询接口
			baseEntity = waybillTraceApi.getPkStateByWCodeAndState(waybillCode, WAYBILL_STATUS_REDISPATCH);
			if (baseEntity != null) {
				if (baseEntity.getResultCode() != 1) {
					this.logger.warn("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：" + waybillCode + ","
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
				this.logger.warn("检查是否反调度WaybillQueryManagerImpl.checkReDispatch返回空：" + waybillCode);
				result = REDISPATCH_ERROR;
			}
		} catch (Exception e) {
			Profiler.functionError(info);
			this.logger.error("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：" + waybillCode, e);
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
					this.logger.warn("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState异常：" + oldWaybillCode + ","
							+ baseEntity.getResultCode() + "," + baseEntity.getMessage());
				} else if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
					changedWaybillCode = baseEntity.getData().get(oldWaybillCode);
				}
			} else {
				this.logger.warn("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState返回空：" + oldWaybillCode);
			}
		} catch (Exception e) {
			Profiler.functionError(info);
			this.logger.error("获取取件单对应的面单号W单号WaybillQueryManagerImpl.checkReDispatch异常：" + oldWaybillCode, e);
		} finally {
			Profiler.registerInfoEnd(info);
		}
		return changedWaybillCode;
	}

	/**
	 * 根据旧运单号获取新运单信息
	 *
	 * @param waybillCode 运单号
	 * @param queryC 获取的运单信息中是否包含waybillC数据
	 * @param queryE 获取的运单信息中是否包含waybillE数据
	 * @param queryM 获取的运单信息中是否包含waybillM数据
	 * @param queryPackList 获取的运单信息中是否包含PackList数据
	 * @return
	 */
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getReturnWaybillByOldWaybillCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public BigWaybillDto getReturnWaybillByOldWaybillCode(String waybillCode, boolean queryC, boolean queryE, boolean queryM, boolean queryPackList) {
		if (StringUtils.isNotEmpty(waybillCode)) {
			CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.getReturnWaybillByOldWaybillCode", false, true);
			BaseEntity<BigWaybillDto> baseEntity = null;
			try {
				WChoice wChoice = new WChoice();
				wChoice.setQueryWaybillC(queryC);
				wChoice.setQueryWaybillE(queryE);
				wChoice.setQueryWaybillM(queryM);
				wChoice.setQueryPackList(queryPackList);
				baseEntity = this.waybillQueryApi.getReturnWaybillByOldWaybillCode(waybillCode, wChoice);
			} catch (Exception e) {
				Profiler.functionError(info);
				logger.error("根据旧运单号调用接口(waybillQueryApi.getReturnWaybillByOldWaybillCode)获取新运单信息时发生异常，waybillCode:" + waybillCode, e);
			}
			if (baseEntity != null) {
				if (baseEntity.getResultCode() == 1) {
					return baseEntity.getData();
				} else if (baseEntity.getResultCode() == -3) {
					logger.warn("根据旧运单号调用接口(waybillQueryApi.getReturnWaybillByOldWaybillCode)获取运单信息反馈该运单信息不存在，waybillCode:" + waybillCode);
				}
			}
		}
		return null;
	}
    /**
     * 包裹称重和体积测量数据上传
     * 来源 PackOpeController
     *
     * @param packOpeJson 称重和体积测量信息
     * @return map data:true or false,code:-1:参数非法 -3:服务端内部处理异常 1:处理成功,message:code对应描述
     */
	@JProfiler(jKey = "DMS.BASE.Jsf.WaybillPackageApi.uploadOpe", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Map<String, Object> uploadOpe(String packOpeJson){
    	return waybillPackageApiJsf.uploadOpe(packOpeJson);
    }
}
