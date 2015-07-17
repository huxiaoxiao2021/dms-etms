package com.jd.bluedragon.core.base;

import com.jd.etms.basic.domain.BaseResult;
import com.jd.etms.basic.dto.BaseDmsStoreDto;


public interface BasicSafInterfaceManager {

	/**
	 * @param cky2
	 * @param orgId  机构号
	 * @param storeId 仓储号
	 * @return
	 */
	public abstract BaseResult<BaseDmsStoreDto> getDmsInfoByStoreInfo(Integer cky2, Integer orgId,
			Integer storeId);
}
