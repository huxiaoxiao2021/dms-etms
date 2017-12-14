package com.jd.bluedragon.common.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @ClassName: RdWmsStoreService
 * @Description: 库房接口
 * @author: wuyoude
 * @date: 2017年12月13日 下午5:13:52
 *
 */
public interface RdWmsStoreService {
	/**
	 * 通过机构和库房号获取条码前缀
	 * @param orgId
	 * @param storeId
	 * @return
	 */
	InvokeResult<String> getOrgStoreTag(Integer orgId, Integer storeId);
}
