package com.jd.bluedragon.core.jsf.dms;

import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;

/**
 * 
 * @ClassName: CancelWaybillJsfManager
 * @Description: 运单拦截jsfmanager
 * @author: wuyoude
 * @date: 2019年4月30日 下午2:37:26
 *
 */
public interface CancelWaybillJsfManager {
    /**
     * 查询运单是否拦截完成
     * @param waybillCode
     * @param featureType
     * @return
     */
    BlockResponse checkWaybillBlock(String waybillCode, Integer featureType);
    /**
     * 查询包裹是否拦截完成
     * @param packageCode
     * @param featureType
     * @return
     */
    BlockResponse checkPackageBlock(String packageCode, Integer featureType);
}
