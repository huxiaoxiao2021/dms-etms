package com.jd.bluedragon.distribution.weight.service;

import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.domain.WeightAndVolumeDetailFlow;

import java.util.List;
import java.util.Map;

public interface WeightService {

	/**
	 * 将称重信息回传到运单中心
	 */
	boolean doWeightTrack(Task task);
	/**
	 * 保存分拣称重信息
	 * @param weightOperFlow
	 * @return
	 */
	boolean saveDmsWeight(WeightOperFlow weightOperFlow);
	/**
	 * 批量保存分拣称重信息
	 * @param weightFlowList
	 * @return
	 */
	boolean batchSaveDmsWeight(List<WeightOperFlow> weightFlowList);
	/**
	 * 获取单个包裹的分拣称重数据
	 * @param packageCode
	 * @return
	 */
	WeightOperFlow getDmsWeightByPackageCode(String packageCode);
	/**
	 * 根据运单号获取所有的包裹分拣中心称重信息
	 * @param waybillCode
	 * @return
	 */
	Map<String,WeightOperFlow> getDmsWeightsByWaybillCode(String waybillCode);

	/**
	 * 处理称重量方数据
	 * */
    InvokeResult<Boolean> dealWeightVolume(WeightAndVolumeDetailFlow weightAndVolumeDetailFlow);
}
