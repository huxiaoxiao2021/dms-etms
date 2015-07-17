package com.jd.bluedragon.distribution.receiveInspectionExc.service;

import com.jd.bluedragon.distribution.receiveInspectionExc.domain.ShieldsError;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface ShieldsErrorService {
	/**
	 * 记录封签异常
	 * 
	 * @param shieldsErrors
	 * @return
	 */
	public boolean doAddShieldsError(ShieldsError shieldsError);

	/**
	 * 解析json数据
	 */
	public ShieldsError doParseShieldsCar(Task task);

	/**
	 * 解析json数据
	 */
	public ShieldsError doParseShieldsBox(Task task);
}
