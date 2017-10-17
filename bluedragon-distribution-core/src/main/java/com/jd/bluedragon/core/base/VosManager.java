package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.etms.vos.dto.CarriagePlanDto;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SendCarInfoDto;
import com.jd.etms.vos.dto.SendCarParamDto;

/**
 * @author huangliang
 *
 */
public interface VosManager {

	/**
	 * 车辆电子围栏（VOS）: 测试分组：VOS-TEST，线上分组： VOS-JSF
	 * @param dto
	 * @return
	 */
	public abstract CommonDto<List<SendCarInfoDto>> getSendCar(SendCarParamDto dto);

	/***
	 * 根据运输计划号获取运输计划详情
	 * @param carriagePlanCode
	 * @return
     */
	public abstract CommonDto<CarriagePlanDto> queryCarriagePlanDetails(String carriagePlanCode);
}
