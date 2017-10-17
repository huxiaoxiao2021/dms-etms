package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.etms.vos.dto.CarriagePlanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SendCarInfoDto;
import com.jd.etms.vos.dto.SendCarParamDto;
import com.jd.etms.vos.ws.VosQueryWS;

@Service("vosManager")
public class VosManagerImpl implements VosManager{

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

}
