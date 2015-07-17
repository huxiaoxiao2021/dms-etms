package com.jd.bluedragon.distribution.offline.service;

import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;

/**
 * 离线分拣service
 * @author libin
 *
 */
public interface OfflineSortingService {
	/**
	 * 分拣
	 * @param request
	 * @return
	 */
	public int insert(OfflineLogRequest request);
	/**
	 * 分拣装箱
	 * @param request
	 * @return
	 */
	public int insertSealBox(OfflineLogRequest request);
	/**
	 * 三方分拣退回
	 * @param request
	 * @return
	 */
	public int exceedArea(OfflineLogRequest request);
	
	public int cancelSorting(OfflineLogRequest request);
	public int cancelThirdInspection(OfflineLogRequest request);
	

}
