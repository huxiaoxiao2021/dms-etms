package com.jd.bluedragon.distribution.ver.service;

import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.ver.exception.IllegalWayBillCodeException;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;

public interface SortingCheckService {
    /**
     * 分拣校验
     *
     * @param pdaOperateRequest
     * @return
     */
    SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest);


}
