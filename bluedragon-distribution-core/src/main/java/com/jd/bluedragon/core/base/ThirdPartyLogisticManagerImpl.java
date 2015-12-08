package com.jd.bluedragon.core.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.third.jsf.OrderShipsServiceJsf;
import com.jd.etms.third.service.dto.BaseResult;
import com.jd.etms.third.service.dto.OrderShipsReturnDto;

@Service("thirdPartyLogisticManager")
public class ThirdPartyLogisticManagerImpl implements ThirdPartyLogisticManager{

	@Autowired
	private OrderShipsServiceJsf orderShipsServiceJsf;
	
	public BaseResult<List<OrderShipsReturnDto>> insertOrderShips(
			List<OrderShipsReturnDto> orderShipsReturnDtos, String encryptData){
		return orderShipsServiceJsf.insertOrderShips(orderShipsReturnDtos, encryptData);
	}

}
