package com.jd.bluedragon.distribution.ver.jsf.impl;

import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.ver.service.SortingCheckJsfService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-25 19:47:25 周四
 */
@Service("sortingCheckJsfService")
public class SortingCheckJsfServiceImpl implements SortingCheckJsfService {

    @Autowired
    private SortingCheckService sortingCheckService;

    /**
     * 分拣校验
     *
     * @param pdaOperateRequest
     */
    @Override
    public SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest) {
        return sortingCheckService.sortingCheck(pdaOperateRequest);
    }

    /**
     * 分拣校验，并且有拦截是提交拦截信息
     *
     * @param pdaOperateRequest
     * @author fanggang7
     * @time 2020-12-23 15:16:27 周三
     */
    @Override
    public SortingJsfResponse sortingCheckAndReportIntercept(PdaOperateRequest pdaOperateRequest) {
        return sortingCheckService.sortingCheckAndReportIntercept(pdaOperateRequest);
    }

    /**
     * 新发货校验
     *
     * @param sortingCheck
     */
    @Override
    public SortingJsfResponse singleSendCheck(SortingCheck sortingCheck) {
        return sortingCheckService.singleSendCheck(sortingCheck);
    }

    /**
     * 新发货校验，并且有拦截是提交拦截信息
     *
     * @param sortingCheck
     * @author fanggang7
     * @time 2020-12-23 15:16:27 周三
     */
    @Override
    public SortingJsfResponse singleSendCheckAndReportIntercept(SortingCheck sortingCheck) {
        return sortingCheckService.singleSendCheckAndReportIntercept(sortingCheck);
    }

    @Override
    public BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest request) {
        return sortingCheckService.boardCombinationCheck(request);
    }

    @Override
    public BoardCombinationJsfResponse boardCombinationCheckAndReportIntercept(BoardCombinationRequest request) {
        return sortingCheckService.boardCombinationCheckAndReportIntercept(request);
    }

    @Override
    public boolean isNeedCheck(String uccStr, Integer siteCode) {
        return sortingCheckService.isNeedCheck(uccStr, siteCode);
    }
}
