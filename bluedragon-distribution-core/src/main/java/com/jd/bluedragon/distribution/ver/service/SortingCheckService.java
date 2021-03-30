package com.jd.bluedragon.distribution.ver.service;

import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
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
     * 分拣校验，并且有拦截是提交拦截信息
     * @author fanggang7
     * @time 2020-12-23 15:16:27 周三
     */
    SortingJsfResponse sortingCheckAndReportIntercept(PdaOperateRequest pdaOperateRequest);

    /**
     * 新发货校验
     */
    SortingJsfResponse singleSendCheck(SortingCheck sortingCheck);

    /**
     * 新发货校验，并且有拦截是提交拦截信息
     * @author fanggang7
     * @time 2020-12-23 15:16:27 周三
     */
    SortingJsfResponse singleSendCheckAndReportIntercept(SortingCheck sortingCheck);


    /*
     * 组板校验
     * */
    BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest request);

    /*
     * 组板校验，并且有拦截是提交拦截信息
     */
    BoardCombinationJsfResponse boardCombinationCheckAndReportIntercept(BoardCombinationRequest request);

    /*
    * 切换开关
    * */
    boolean isNeedCheck(String uccStr, Integer siteCode);

    /**
     * 冷链发货主校验
     *
     * @param request 单个请求参数
     * @return 校验结果
     */
    SortingJsfResponse coldChainSendCheck(DeliveryRequest request);

    /**
     * 冷链发货主校验
     * @param request 单个请求参数
     * @param reportIntercept 是否记录拦截记录到报表
     * @return 校验结果
     */
    public SortingJsfResponse coldChainSendCheck(DeliveryRequest request, boolean reportIntercept);
}
