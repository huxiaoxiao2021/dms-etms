package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.*;
import com.jd.ql.trace.api.WaybillTraceBusinessQueryApi;
import com.jd.ql.trace.api.core.APIResultDTO;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
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

    @Qualifier("waybillTraceBusinessQueryApi")
    @Autowired
	private WaybillTraceBusinessQueryApi waybillTraceBusinessQueryApi;

	@Autowired
	private SysConfigService sysConfigService;

	@Override
	public BaseEntity<Waybill> getWaybillByReturnWaybillCode(String waybillCode) {
		return waybillQueryApi.getWaybillByReturnWaybillCode(waybillCode);
	}
	
	@Override
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
			WChoice wChoice) {
		//增加一个开关，在支持两万个包裹，需要单独调用运单的分页接口过渡期使用
		if(isGetPackageByPageOpen()){
			Boolean isQueryPackList = wChoice.getQueryPackList();
			if(null == isQueryPackList){
				isQueryPackList = false;
			}
			wChoice.setQueryPackList(false);
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryApi.getDataByChoice(waybillCode, wChoice);

			//如果需要获取包裹信息，则调用运单分页获取包裹信息的接口，做此修改是为了支持2w包裹的订单
			if(isQueryPackList && null != baseEntity && null != baseEntity.getData()){
				BaseEntity<List<DeliveryPackageD>> packageDBaseEntity = getPackageByWaybillCode(waybillCode);
				if(null != packageDBaseEntity && null != packageDBaseEntity.getData() && packageDBaseEntity.getData().size()>0){
					baseEntity.getData().setPackageList(packageDBaseEntity.getData());
				}
			}

			return baseEntity;
		}else{
			return waybillQueryApi.getDataByChoice(waybillCode, wChoice);
		}
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
		return getDataByChoice(waybillCode, wChoice);
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
		return getDataByChoice(waybillCode, wChoice);
	}


	@Override
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDatasByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> waybillCodes, Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM, Boolean isPackList) {
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(isWaybillC);
		wChoice.setQueryWaybillE(isWaybillE);
		wChoice.setQueryWaybillM(isWaybillM);
		wChoice.setQueryPackList(isPackList);
		return getDatasByChoice(waybillCodes, wChoice);
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

    /**
	 * 根据运单号获取运单数据信息给打印用
	 * @param waybillCode
	 * @return
	 */
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public BaseEntity<BigWaybillDto> getWaybillDataForPrint(String waybillCode) {
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(Boolean.TRUE);
		wChoice.setQueryWaybillE(Boolean.TRUE);
		wChoice.setQueryWaybillM(Boolean.TRUE);
		wChoice.setQueryPackList(Boolean.TRUE);
		wChoice.setQueryWaybillExtend(Boolean.TRUE);
		return this.getDataByChoice(waybillCode, wChoice);
	}

	/**
	 * 根据操作单号和状态查询B网全程跟踪数据,包含extend扩展属性。
	 * @param operatorCode 运单号
	 * @param state 状态码
	 * @return
	 */
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.queryBillBTraceAndExtendByOperatorCode",
			mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
	@Override
	public List<BillBusinessTraceAndExtendDTO> queryBillBTraceAndExtendByOperatorCode(String operatorCode, String state) {
		APIResultDTO<List<BillBusinessTraceAndExtendDTO>> resultDTO =  waybillTraceBusinessQueryApi.queryBillBTraceAndExtendByOperatorCode( operatorCode,  state);
		if(resultDTO.isSuccess()){
			return  resultDTO.getResult();
		}
		return null;
	}

	/**
	 * 根据运单号获取包裹数据，通过调用运单的分页接口获得
	 * @param waybillCode
	 * @return
	 */
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getPackageByWaybillCode",jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCode(String waybillCode){
		logger.info("调用运单接口getPackageByParam,分页获取包裹数据,运单号:" + waybillCode);
		BaseEntity<List<DeliveryPackageD>> result = new BaseEntity<List<DeliveryPackageD>>();
		List<DeliveryPackageD> packageList = new ArrayList<DeliveryPackageD>();
		result.setData(packageList);

		//组织请求参数，从第一页开始，每页5000行
		Page<DeliveryPackageDto> pageParam = new Page<DeliveryPackageDto>();
		pageParam.setCurPage(1);
		pageParam.setPageSize(Constants.PACKAGE_NUM_ONCE_QUERY);

		//调用运单分页接口
		BaseEntity<Page<DeliveryPackageDto>> baseEntity = waybillPackageApiJsf.getPackageByParam(waybillCode, null);

		//调用接口异常，添加自定义报警
		if(null == baseEntity || baseEntity.getResultCode() != 1){
			String alarmInfo = "调用运单接口getPackageByParam失败.waybillCode:" + waybillCode;
			if(null != baseEntity){
				alarmInfo = alarmInfo + ",resultCode:" + baseEntity.getResultCode() + "-" + baseEntity.getMessage();
			}
			logger.error(alarmInfo);
			Profiler.businessAlarm("调用运单接口getPackageByParam失败",alarmInfo);
			return null;
		}

		//有包裹数据，则分页读取
		if(null != baseEntity && null != baseEntity.getData() &&
				null != baseEntity.getData().getResult() && baseEntity.getData().getResult().size()>0){

			packageList.addAll(changeToDeliveryPackageDBatch(baseEntity.getData().getResult()));

			logger.info("调用运单接口getPackageByParam,waybillCode:" + waybillCode + ",每次请求数:" +
					Constants.PACKAGE_NUM_ONCE_QUERY + ".返回包裹总数:" + baseEntity.getData().getTotalRow() +
					",总页数:" + baseEntity.getData().getTotalPage());

			//读取分页数
			int totalPage = baseEntity.getData().getTotalPage();

			//循环获取剩余数据
			for(int i = 2; i <= totalPage; i++){
				pageParam.setCurPage(i);
				List<DeliveryPackageDto> dtoList = waybillPackageApiJsf.getPackageByParam(waybillCode,pageParam).getData().getResult();
				packageList.addAll(changeToDeliveryPackageDBatch(dtoList));
			}
			logger.info("getPackageByWaybillCode获取包裹数据共" + packageList.size() + "条.waybillCode:"+waybillCode);
		}

		return result;
	}


	/**
	 * 根据包裹号列表批量获取包裹数据
	 * @param waybillCodes
	 * @return
	 */
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.queryPackageListForParcodes",jAppName = Constants.UMP_APP_NAME_DMSWEB,
			mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseEntity<List<DeliveryPackageD>> queryPackageListForParcodes(List<String> waybillCodes){
		return waybillPackageApiJsf.queryPackageListForParcodes(waybillCodes);
	}


	/**
	 * 将调用运单分页接口返回的dto转换成DeliveryPackageD
	 * @param dtoList
	 * @return
	 */
	private List<DeliveryPackageD> changeToDeliveryPackageDBatch(List<DeliveryPackageDto> dtoList){
		List<DeliveryPackageD> packageDList = new ArrayList<DeliveryPackageD>();

		for(DeliveryPackageDto dto: dtoList) {
			DeliveryPackageD packageD = new DeliveryPackageD();

			packageD.setPackageBarcode(dto.getPackageBarcode());
			packageD.setWaybillCode(dto.getWaybillCode());
			packageD.setCky2(dto.getCky2());
			packageD.setStoreId(dto.getStoreId());
			packageD.setPackageState(dto.getPackageState());
			packageD.setPackWkNo(dto.getPackwkNo());
			packageD.setRemark(dto.getRemark());

			packageD.setGoodWeight(dto.getGoodWeight());
			packageD.setGoodVolume(dto.getGoodVolume());
			packageD.setAgainWeight(dto.getAgainWeight());
			packageD.setAgainVolume(dto.getAgainVolume());
			packageD.setPackTime(dto.getPackTime());
			packageD.setWeighTime(dto.getWeighTime());
			packageD.setCreateTime(dto.getCreateTime());
			packageD.setUpdateTime(dto.getUpdateTime());

			packageD.setWeighUserName(dto.getWeighUserName());

			packageDList.add(packageD);
		}

		return packageDList;
	}

	/**
	 * 从sysconfig表里获取是否启用分页查询运单包裹的接口
	 * @return
	 */
	public boolean isGetPackageByPageOpen(){
		List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_CONFIG_PACKAGE_PAGE_SWITCH);
		if(sysConfigs != null && !sysConfigs.isEmpty()){
			String content = sysConfigs.get(0).getConfigContent();
			if(StringHelper.isNotEmpty(content) && "1".equals(content)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过包裹号获得运单信息
	 * @return
	 */
	@Override
	public BaseEntity<Waybill> getWaybillByPackCode(String code){
		return waybillQueryApi.getWaybillByPackCode(code);
	}

	/**
	 * 通过包裹号获得运单信息和包裹信息
	 * @return
	 */
	@Override
	public BaseEntity<BigWaybillDto> getWaybillAndPackByWaybillCode(String waybillCode){
		if(isGetPackageByPageOpen()) {
			WChoice wChoice = new WChoice();
			wChoice.setQueryWaybillC(Boolean.TRUE);
			wChoice.setQueryWaybillM(Boolean.TRUE);
			wChoice.setQueryPackList(Boolean.TRUE);
			return getDataByChoice(waybillCode, wChoice);
		}else{
			return waybillQueryApi.getWaybillAndPackByWaybillCode(waybillCode);
		}
	}

	/**
	 * 通过运单号获得运单信息
	 * @return
	 */
	@Override
	public BaseEntity<Waybill> getWaybillByWaybillCode(String waybillCode){
		return waybillQueryApi.getWaybillByWaybillCode(waybillCode);
	}

	/**
	 * 根据旧运单号获取新运单信息
	 *
	 * @param oldWaybillCode 旧的运单号
	 * @param wChoice 获取的运单信息中是否包含waybillC数据
	 * @return
	 */
	@JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getReturnWaybillByOldWaybillCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public BaseEntity<BigWaybillDto> getReturnWaybillByOldWaybillCode(String oldWaybillCode, WChoice wChoice){
		if(isGetPackageByPageOpen()) {
			Boolean isQueryPackList = wChoice.getQueryPackList();
			if(null == isQueryPackList){
				isQueryPackList = false;
			}
			wChoice.setQueryPackList(false);

			BaseEntity<BigWaybillDto> baseEntity = waybillQueryApi.getReturnWaybillByOldWaybillCode(oldWaybillCode, wChoice);

			//如果需要获取包裹信息，则调用运单分页获取包裹信息的接口，做此修改是为了支持2w包裹的订单
			if (isQueryPackList) {
				List<DeliveryPackageD> packageDList = getPackageByWaybillCode(oldWaybillCode).getData();
				if (null != baseEntity && null != baseEntity.getData()) {
					baseEntity.getData().setPackageList(packageDList);
				}
			}
			return baseEntity;
		}else{
			return waybillQueryApi.getReturnWaybillByOldWaybillCode(oldWaybillCode, wChoice);
		}
	}

	/**
	 * 批量获取运单信息
	 *
	 * @param waybillCodes 运单号列表
	 * @return
	 */
	public BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> waybillCodes,WChoice wChoice){
		if(isGetPackageByPageOpen()) {
			Boolean isQueryPackList = wChoice.getQueryPackList();
			if(null == isQueryPackList){
				isQueryPackList = false;
			}
			wChoice.setQueryPackList(false);

			BaseEntity<List<BigWaybillDto>> results = waybillQueryApi.getDatasByChoice(waybillCodes, wChoice);
			if (isQueryPackList && null != results) {
				for (BigWaybillDto bigWaybillDto : results.getData()) {
					if (null != bigWaybillDto.getWaybill() && StringHelper.isNotEmpty(bigWaybillDto.getWaybill().getWaybillCode())) {
						BaseEntity<List<DeliveryPackageD>> packageDBaseEntity = getPackageByWaybillCode(bigWaybillDto.getWaybill().getWaybillCode());
						if (null != packageDBaseEntity && null != packageDBaseEntity.getData() && packageDBaseEntity.getData().size() > 0) {
							bigWaybillDto.setPackageList(packageDBaseEntity.getData());
						}
					}
				}
			}

			return results;
		}else{
			return waybillQueryApi.getDatasByChoice(waybillCodes, wChoice);
		}
	}

}
