package com.jd.bluedragon.distribution.ver.service;

import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;

public interface SortingCheckService {

    /**
     * 分拣校验
     */
    SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest);


}
