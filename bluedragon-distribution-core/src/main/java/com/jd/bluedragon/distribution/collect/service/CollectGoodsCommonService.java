package com.jd.bluedragon.distribution.collect.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDTO;

/**
 * 集货功能业务接口
 */
public interface CollectGoodsCommonService {

    /**
     * 集货作业
     * @param req
     * @return
     */
    InvokeResult<CollectGoodsDTO> put(CollectGoodsDTO req);


    /**
     * 集货释放
     * @param req
     * @return
     */
    InvokeResult<Boolean> clean(CollectGoodsDTO req);

    /**
     * 集货转移
     * @param req
     * @return
     */
    InvokeResult<Boolean> transfer(CollectGoodsDTO req);
}
