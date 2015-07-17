package com.jd.bluedragon.distribution.weight.service;

import com.jd.bluedragon.distribution.task.domain.Task;

public interface WeightService {

	/**
	 * 将称重信息回传到运单中心
	 */
	boolean doWeightTrack(Task task);

}
