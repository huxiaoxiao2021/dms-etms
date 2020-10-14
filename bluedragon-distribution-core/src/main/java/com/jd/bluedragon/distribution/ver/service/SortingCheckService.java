package com.jd.bluedragon.distribution.ver.service;

import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;

public interface SortingCheckService {

    /**
     * 分拣校验
     */
    SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest);

    /**
     * 新发货校验
     */
    SortingJsfResponse singleSendCheck(SortingCheck sortingCheck);


    /*
     * 组板校验
     * */
    BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest request);

    /*
    * 切换开关
    * */
    boolean isNeedCheck(String uccStr, Integer siteCode);
}
