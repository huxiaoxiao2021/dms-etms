package com.jd.bluedragon.distribution.external.intensive.service;

import java.util.List;
import java.util.Map.Entry;

import com.jd.bluedragon.distribution.api.request.material.batch.MaterialBatchSendRequest;
import com.jd.bluedragon.distribution.api.response.material.batch.MaterialTypeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;

/**
 * 循环物资发货-相关
 * @author wuyoude
 *
 */
public interface MaterialBatchSendJsfService {
	/**
	 * 循环物资发货
	 * @param restReq
	 * @return
	 */
	JdResult<Boolean> materialBatchSend(MaterialBatchSendRequest restReq);
	/**
	 * 循环物资取消发货
	 * @param restReq
	 * @return
	 */
	JdResult<Boolean> cancelMaterialBatchSend(MaterialBatchSendRequest restReq);
	/**
	 * 查询类型列表
	 * @param restReq
	 * @return
	 */
	JdResult<List<MaterialTypeResponse>> listMaterialType(MaterialBatchSendRequest restReq);
	/**
	 * 获取批次号信息
	 * @param sendCode
	 * @return
	 */
	InvokeResult<Entry<Integer, String>> getSendCodeDestination(String sendCode);
	
}
