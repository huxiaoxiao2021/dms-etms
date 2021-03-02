package com.jd.bluedragon.core.jsf.tms;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.etms.vos.dto.SealVehicleJobDto;

/**
 * 运输任务查询接口
 * @author wuyoude
 *
 */
public interface VosVehicleJobQueryWSManager {
	/**
	 * 根据任务编码查询任务信息
	 * @param vehicleJobCode
	 * @return
	 */
	JdResult<SealVehicleJobDto> getSealVehicleJobByVehicleJobCode(String vehicleJobCode);
}
