package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.etms.vos.dto.*;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.vos.ws.VosQueryWS;

@Service("vosManager")
public class VosManagerImpl implements VosManager{

	private static final Log logger = LogFactory.getLog(VosManagerImpl.class);

	@Autowired
	private VosQueryWS vosQueryWS;
	
	@Override
	public CommonDto<List<SendCarInfoDto>> getSendCar(SendCarParamDto dto){
		return vosQueryWS.getSendCar(dto);
	}
	@Override
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
				logger.error("调用运输接口[vosQueryWS.querySealCarByBatchCode()]根据批次号获取封车信息成功，返回异常状态码：" + commonDto.getCode() + "，异常信息：" + commonDto.getMessage());
			}
		}
		return null;
	}

}
