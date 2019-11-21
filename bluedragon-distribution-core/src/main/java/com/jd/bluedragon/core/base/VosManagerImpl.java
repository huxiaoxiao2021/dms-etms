package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.etms.vos.dto.*;
import com.jd.etms.vos.ws.VosQueryWS;
import com.jd.etms.vos.ws.VosVehicleJobBusinessWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("vosManager")
public class VosManagerImpl implements VosManager{

	private static final Logger log = LoggerFactory.getLogger(VosManagerImpl.class);

	@Autowired
	private VosQueryWS vosQueryWS;

	@Autowired
	private VosVehicleJobBusinessWS vosVehicleJobBusinessWS;
	
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.vosQueryWS.getSendCar",mState={JProEnum.TP,JProEnum.FunctionError})
	public CommonDto<List<SendCarInfoDto>> getSendCar(SendCarParamDto dto){
		return vosQueryWS.getSendCar(dto);
	}
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.vosQueryWS.queryCarriagePlanDetails",mState={JProEnum.TP,JProEnum.FunctionError})
	public CommonDto<CarriagePlanDto> queryCarriagePlanDetails(String carriagePlanCode){
		return vosQueryWS.queryCarriagePlanDetails(carriagePlanCode);
	}

	@JProfiler(jKey = "DMS.BASE.VosManagerImpl.querySealCarByBatchCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public SealCarDto querySealCarByBatchCode(String batchCode) throws Exception {
		if (StringUtils.isEmpty(batchCode)) {
			return null;
		}
		CommonDto<SealCarDto> commonDto = vosQueryWS.querySealCarByBatchCode(batchCode);
		if (commonDto != null) {
			if (commonDto.getCode() == 1) {
				return commonDto.getData();
			} else {
				log.warn("调用运输接口[vosQueryWS.querySealCarByBatchCode()]根据批次号获取封车信息成功，返回异常状态码：{}，异常信息：{}"
                        ,commonDto.getCode(), commonDto.getMessage());
			}
		}
		return null;
	}


	/**
	 * VOS封车业务同时生成车次任务
	 * @param sealCarDto
	 * @return
	 */
	public CommonDto<String> doSealCarWithVehicleJob(SealCarDto sealCarDto){
		CommonDto<String> commonDto = null;
		CallerInfo info = Profiler.registerInfo("DMS.BASE.VosManagerImpl.doSealCarWithVehicleJob", Constants.UMP_APP_NAME_DMSWEB, false, true);

		try {
			commonDto = vosVehicleJobBusinessWS.doSealCarWithVehicleJob(sealCarDto);
			if(log.isDebugEnabled()){
				log.debug("调用VOS封车业务同时生成车次任务提交接口,参数:{},返回值：{}" ,JSON.toJSONString(sealCarDto) , JSON.toJSONString(commonDto));
			}
		} catch (Exception e) {
			log.error("调用调用VOS封车业务同时生成车次任务提交接口异常.参数:{}" , JSON.toJSONString(sealCarDto),e);
			Profiler.functionError(info);
		} finally {
			Profiler.registerInfoEnd(info);
		}
		return commonDto;
	}

	/**
	 * VOS校验车牌号能否封车创建车次任务
	 * @param verifyVehicleJobDto
	 * @return
	 */
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.VosManagerImpl.verifyVehicleJobByVehicleNumber",mState = {JProEnum.TP,JProEnum.FunctionError})
	public CommonDto<String> verifyVehicleJobByVehicleNumber(VerifyVehicleJobDto  verifyVehicleJobDto){
		CommonDto<String> commonDto = vosVehicleJobBusinessWS.verifyVehicleJobByVehicleNumber(verifyVehicleJobDto);
		if(log.isDebugEnabled()){
			log.debug("调用VOS校验车牌号能否封车创建车次任务接口,参数:{},返回值：{}" ,JSON.toJSONString(verifyVehicleJobDto), JSON.toJSONString(commonDto));
		}
		return commonDto;
	}
}
