package com.jd.bluedragon.distribution.popPickup.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface PopPickupService {

	void insertOrUpdate(PopPickup popPickup);

	void doPickup(Task task);

	/**
	 * 按条件查询POP上门接货清单总数
	 * @param paramMap
	 * @return
	 */
	public int findPopPickupTotalCount(Map<String, Object> paramMap);


	/**
	 * 按条件查询POP上门接货清单集合
	 * @param paramMap
	 * @return
	 */
	public List<PopPickup> findPopPickupList(Map<String, Object> paramMap);
	
	public void pushPopPickupRequest(PopPickupRequest popPickupRequest) throws Exception;
	
	public void doPickup(PopPickupRequest popPickupRequest);
}
