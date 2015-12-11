package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.etms.third.service.dto.BaseResult;
import com.jd.etms.third.service.dto.OrderShipsReturnDto;

/**
 * @author huangliang
 *
 */
public interface ThirdPartyLogisticManager {

	/**
	 * 
	 * @param orderShipsReturnDtos
	 * @param encryptData
	 * @return
	 * http://cf.jd.com/pages/viewpage.action?pageId=63639176
	 */
	public abstract BaseResult<List<OrderShipsReturnDto>> insertOrderShips(
			List<OrderShipsReturnDto> orderShipsReturnDtos, String encryptData);

}
