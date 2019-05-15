package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.third.jsf.OrderShipsServiceJsf;
import com.jd.etms.third.service.dto.BaseResult;
import com.jd.etms.third.service.dto.OrderShipsReturnDto;

@Service("thirdPartyLogisticManager")
public class ThirdPartyLogisticManagerImpl implements ThirdPartyLogisticManager{

	@Autowired
	private OrderShipsServiceJsf orderShipsServiceJsf;

	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.OrderShipsServiceJsf.insertOrderShips",mState={JProEnum.TP,JProEnum.FunctionError})
	public BaseResult<List<OrderShipsReturnDto>> insertOrderShips(
			List<OrderShipsReturnDto> orderShipsReturnDtos, String encryptData){
		return orderShipsServiceJsf.insertOrderShips(orderShipsReturnDtos, encryptData);
	}

}
