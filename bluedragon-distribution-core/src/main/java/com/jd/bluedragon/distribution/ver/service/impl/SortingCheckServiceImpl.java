package com.jd.bluedragon.distribution.ver.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.service.RuleService;
import com.jd.bluedragon.distribution.ver.domain.FilterRequest;
import com.jd.bluedragon.distribution.ver.exception.IllegalWayBillCodeException;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.chains.DeliveryFilterChain;
import com.jd.bluedragon.distribution.ver.filter.chains.ForwardFilterChain;
import com.jd.bluedragon.distribution.ver.filter.chains.ProceedFilterChain;
import com.jd.bluedragon.distribution.ver.filter.chains.ReverseFilterChain;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;

import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.ver.service.SortingCheckStatisticsService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.preseparate.jsf.PresortRoadQueryServiceAPI;
import com.jd.bluedragon.utils.ExperienceUtil;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("sortingCheckService")
public class SortingCheckServiceImpl implements SortingCheckService {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    ProceedFilterChain proceedFilterChain;
    @Autowired
    ForwardFilterChain forwardFilterChain;
    @Autowired
    ReverseFilterChain reverseFilterChain;
    @Autowired
    DeliveryFilterChain deliveryFilterChain;


    @Autowired
    JsfSortingResourceService jsfSortingResourceService;

    /**
     * 一车一单发货入口operaterType标识
     **/
    private static final int OPERATE_TYPE_NEW_PACKAGE_SEND = 60;


    @Autowired
    RuleService ruleService;


    @Autowired
    private SortingCheckStatisticsService sortingCheckStatisticsService;


    public SortingCheck convertToSortingCheck(PdaOperateRequest request) {
        SortingCheck sortingCheck = new SortingCheck();
        sortingCheck.setBoxCode(request.getBoxCode());
        sortingCheck.setBusinessType(request.getBusinessType());
        sortingCheck.setCreateSiteCode(request.getCreateSiteCode());
        sortingCheck.setCreateSiteName(request.getCreateSiteName());
        sortingCheck.setOperateTime(request.getOperateTime());
        sortingCheck.setOperateType(request.getOperateType());
        sortingCheck.setOperateUserCode(request.getOperateUserCode());
        sortingCheck.setOperateUserName(request.getOperateUserName());
        sortingCheck.setPackageCode(request.getPackageCode());
        sortingCheck.setReceiveSiteCode(request.getReceiveSiteCode());
        sortingCheck.setIsLoss(request.getIsLoss());
        return sortingCheck;
    }


    public SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest) {


        SortingJsfResponse sortingJsfResponse = dmsSortingCheck(pdaOperateRequest);
        sortingCheckStatisticsService.addSortingCheckStatisticsLog(pdaOperateRequest, sortingJsfResponse);

        return sortingJsfResponse;
    }

    //校验
    public SortingJsfResponse dmsSortingCheck(PdaOperateRequest pdaOperateRequest) {
        SortingJsfResponse sortingJsfResponse = jsfSortingResourceService.check(convertToSortingCheck(pdaOperateRequest));

        if (sortingJsfResponse.getCode() != 200) {
            return sortingJsfResponse;
        }

        String boxCode = pdaOperateRequest.getBoxCode();
        Integer createSiteCode = pdaOperateRequest.getCreateSiteCode();
        Integer receiveSiteCode = pdaOperateRequest.getReceiveSiteCode();
        Integer businessType = pdaOperateRequest.getBusinessType();
        String packageCode = pdaOperateRequest.getPackageCode();

        String roadCode = null;
        FilterRequest request = null;
        String bizKey = createSiteCode + "_" + packageCode;
        BusinessLogProfiler businessLogProfiler = ExperienceUtil.bulidBusinessLogProfiler(ExperienceUtil.SORTING_BIZ_TYPE, ExperienceUtil.SORTING_OPERATE_TYPE, bizKey);

        CallerInfo callerInfo = null;
        try {
            callerInfo = Profiler.registerInfo("Dmsver.MasterSortingCheckStrategy.dmsExecuteSortingCheck", Constants.UMP_APP_NAME_DMSVER, false, true);
            request = initFilterParam(boxCode, createSiteCode, receiveSiteCode
                    , businessType, packageCode, pdaOperateRequest);

            proceedFilterChain.doFilter(request, proceedFilterChain);
            //根据operateType判断入口，如果入口是一车一单发货，则走deliveryFilterChain
            if (pdaOperateRequest != null && pdaOperateRequest.getOperateType() != null
                    && pdaOperateRequest.getOperateType() == OPERATE_TYPE_NEW_PACKAGE_SEND) {
                deliveryFilterChain.doFilter(request, deliveryFilterChain);
                businessLogProfiler.setBizType(ExperienceUtil.SEND_BIZ_TYPE);
                businessLogProfiler.setOperateType(ExperienceUtil.SEND_OPERATE_TYPE);
            } else {
                if (BusinessUtil.isForward(businessType)) {
                    forwardFilterChain.doFilter(request, forwardFilterChain);
                } else if (BusinessUtil.isReverse(businessType)) {
                    reverseFilterChain.doFilter(request, reverseFilterChain);
                }
            }
            Waybill waybill = request.getWaybill();
            if (BusinessUtil.isTmsTransBill(waybill.getWaybillSign(), waybill.getSendPay())) {
                roadCode = request.getWaybill().getRoadCode();
            }
        } catch (IllegalWayBillCodeException e) {
            logger.error("非法运单号：IllegalWayBillCodeException", e);
            SortingJsfResponse sortingJsfResponse1 = new SortingJsfResponse(JdResponse.CODE_PARAM_ERROR, e.getMessage());
            return sortingJsfResponse1;
        } catch (Exception ex) {
            if (ex instanceof SortingCheckException) {
                businessLogProfiler.setLogType(ExperienceUtil.LOG_TYPE_ZA);
                SortingCheckException checkException = (SortingCheckException) ex;
                SortingJsfResponse response = new SortingJsfResponse();
                response.setCode(checkException.getCode());
                response.setMessage(checkException.getMessage());
//                if (request != null && request.getTipMessages() != null && request.getTipMessages().size() > 0) {
//                    response.setTipMessages(request.getTipMessages());
//                    response.setMessageShowType(SortingResponse.MESSAGE_SHOW_TYPE_TIP);
//                }
                if (!(checkException.getCode() >= 30000 && checkException.getCode() < 40000)) {
                    //强制拦截的
//                    response.setMessageShowType(SortingResponse.MESSAGE_SHOW_TYPE_INTERCEPT);
                    businessLogProfiler.setLogType(ExperienceUtil.LOG_TYPE_KD);
                }
                return response;
            } else {
                sortingJsfResponse.setCode(SortingJsfResponse.CODE_SERVICE_ERROR);
                sortingJsfResponse.setMessage(SortingJsfResponse.MESSAGE_SERVICE_ERROR_C);
                return sortingJsfResponse;
            }
        } finally {
            BusinessLogWriter.writeLog(businessLogProfiler);
            Profiler.registerInfoEnd(callerInfo);
        }
        SortingJsfResponse response = new SortingJsfResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
//        if (request.getTipMessages() != null && request.getTipMessages().size() > 0) {
//            response.setTipMessages(request.getTipMessages());
//            response.setMessageShowType(SortingResponse.MESSAGE_SHOW_TYPE_TIP);
//        }
        if (StringUtils.isNotBlank(roadCode)) {
            response.setMessage(roadCode);
        }
        return response;
    }

    /**
     * 构建分拣链的上下文FilterRequest
     *
     * @param boxCode
     * @param createSiteCode
     * @param receiveSiteCode
     * @param businessType
     * @param packageCode
     * @param pdaOperateRequest
     * @return
     * @throws SortingCheckException
     */
    protected FilterRequest initFilterParam(String boxCode,
                                            Integer createSiteCode,
                                            Integer receiveSiteCode,
                                            Integer businessType,
                                            String packageCode,
                                            PdaOperateRequest pdaOperateRequest) throws SortingCheckException {

        FilterRequest request = new FilterRequest();
        request.setRuleMap(getRuleList(createSiteCode));
        request.setBoxCode(boxCode);
        request.setCreateSiteCode(createSiteCode);
        request.setReceiveSiteCode(receiveSiteCode);
        request.setBusinessType(businessType);
        request.setPackageCode(packageCode);
        request.setPdaOperateRequest(pdaOperateRequest);
        return request;
    }

    /**
     * 获取当前分拣中心的校验规则
     *
     * @param createSiteCode
     * @return
     * @throws SortingCheckException
     */
    protected Map<String, Rule> getRuleList(Integer createSiteCode) throws SortingCheckException {
        Map<String, Integer> queryParam = new HashMap<String, Integer>();
        queryParam.put("siteCode", createSiteCode);
        List<Rule> ruleList = ruleService.queryByParamNoPage(queryParam);
        if (ruleList == null || ruleList.size() == 0) {//未配置分拣规则
            throw new SortingCheckException(SortingResponse.CODE_29402, SortingResponse.MESSAGE_29402);
        }
        Map<String, Rule> ruleMap = new HashMap<String, Rule>();
        for (Rule rule : ruleList) {
            ruleMap.put(String.valueOf(rule.getType()), rule);
        }
        return ruleMap;
    }


}
