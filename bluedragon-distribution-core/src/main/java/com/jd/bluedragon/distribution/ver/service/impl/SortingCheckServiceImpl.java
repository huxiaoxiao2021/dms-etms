package com.jd.bluedragon.distribution.ver.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.constants.HintModuleConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.service.RuleService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.IllegalWayBillCodeException;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.ver.filter.chains.*;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsMessageConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.etms.waybill.dto.WaybillAbilityAttrDto;
import com.jd.etms.waybill.dto.WaybillAbilityDto;
import com.jd.etms.waybill.dto.WaybillProductDto;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.constants.OperateDeviceTypeConstants;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("sortingCheckService")
public class SortingCheckServiceImpl implements SortingCheckService , BeanFactoryAware {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BeanFactory beanFactory;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private LogEngine logEngine;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    // 拦截报表发送服务
    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.sortingCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest) {
        return this.sortingCheck(pdaOperateRequest, false);
    }

    /**
     * 拦截校验
     * @param pdaOperateRequest 请求参数
     * @param reportIntercept 是否提交拦截
     * @return 校验结果
     * @author fanggang7
     * @time 2020-12-23 15:13:20 周三
     */
    private SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest, boolean reportIntercept) {
        if (pdaOperateRequest == null) {
            return new SortingJsfResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
        }
        SortingJsfResponse response = new SortingJsfResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        FilterContext filterContext = null;
        try {
            //初始化拦截链上下文
            filterContext = this.initContext(pdaOperateRequest);
            ProceedFilterChain proceedFilterChain = getProceedFilterChain();
            proceedFilterChain.doFilter(filterContext, proceedFilterChain);
            if (this.isNeedCheck(uccPropertyConfiguration.getSwitchVerToWebSites(), pdaOperateRequest.getCreateSiteCode())) {
                Integer businessType = pdaOperateRequest.getBusinessType();
                if (BusinessUtil.isForward(businessType)) {
                    filterContext.setFuncModule(HintModuleConstants.FORWARD_SORTING);
                    ForwardFilterChain forwardFilterChain = getForwardFilterChain();
                    forwardFilterChain.doFilter(filterContext, forwardFilterChain);
                } else if (BusinessUtil.isReverse(businessType)) {
                    filterContext.setFuncModule(HintModuleConstants.REVERSE_SORTING);
                    ReverseFilterChain reverseFilterChain = getReverseFilterChain();
                    reverseFilterChain.doFilter(filterContext, reverseFilterChain);
                }
            } else {
                SortingCheck sortingCheck = convertToSortingCheck(pdaOperateRequest);
                response = jsfSortingResourceService.check(sortingCheck);
            }

        } catch (IllegalWayBillCodeException e) {
            logger.error("分拣验证服务异常，非法运单号：IllegalWayBillCodeException", e);
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(e.getMessage());
        } catch (Exception ex) {
            if (ex instanceof SortingCheckException) {
                SortingCheckException checkException = (SortingCheckException) ex;
                response.setCode(checkException.getCode());
                response.setMessage(checkException.getMessage());
                if(reportIntercept){
                    // 发出拦截报表mq
                    this.sendInterceptMsg(filterContext, checkException);
                }
            } else {
                logger.error("分拣验证服务异常，参数：{}", JsonHelper.toJson(pdaOperateRequest), ex);
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }
        this.addSortingCheckStatisticsLog(pdaOperateRequest, response.getCode(), response.getMessage());
        return response;
    }

    /**
     * 分拣校验
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.sortingCheckAndReportIntercept", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse sortingCheckAndReportIntercept(PdaOperateRequest pdaOperateRequest){
        return this.sortingCheck(pdaOperateRequest, true);
    }

    /**
     * 发送拦截消息
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-10 11:21:39 周四
     */
    private boolean sendInterceptMsg(FilterContext filterContext, SortingCheckException checkException){
        if(filterContext == null || checkException == null){
            return true;
        }
        // logger.info("SortingCheckServiceImpl sendInterceptMsg filterContext: {} , checkException: {}", JSON.toJSONString(filterContext), JSON.toJSONString(checkException));

        try {
            SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
            saveInterceptMsgDto.setInterceptCode(checkException.getCode());
            saveInterceptMsgDto.setInterceptMessage(checkException.getMessage());
            saveInterceptMsgDto.setBarCode(filterContext.getPackageCode());
            saveInterceptMsgDto.setSiteCode(filterContext.getCreateSiteCode());
            saveInterceptMsgDto.setDeviceType(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.PDA));
            saveInterceptMsgDto.setDeviceCode(Constant.DEVICE_CODE_PDA);
            PdaOperateRequest pdaOperateRequest = filterContext.getPdaOperateRequest();
            long operateTimeMillis = System.currentTimeMillis();
            if(pdaOperateRequest.getOperateTime() != null){
                operateTimeMillis = DateUtil.parse(pdaOperateRequest.getOperateTime(), DateUtil.FORMAT_DATE_TIME).getTime();
            }
            saveInterceptMsgDto.setOperateTime(operateTimeMillis);
            saveInterceptMsgDto.setOperateNode(this.getOperateNode(pdaOperateRequest));
            saveInterceptMsgDto.setSiteName(pdaOperateRequest.getCreateSiteName());
            saveInterceptMsgDto.setOperateUserCode(pdaOperateRequest.getOperateUserCode());
            saveInterceptMsgDto.setOperateUserName(pdaOperateRequest.getOperateUserName());
            saveInterceptMsgDto.setOnlineStatus(filterContext.getOnlineStatus());

            try {
                businessInterceptReportService.sendInterceptMsg(saveInterceptMsgDto);
            } catch (Exception e) {
                String saveInterceptMqMsg = JSON.toJSONString(saveInterceptMsgDto);
                logger.error("SortingCheckServiceImpl call sendInterceptMsg exception [{}]" , saveInterceptMqMsg, e);
            }
        } catch (Exception e) {
            logger.error("SortingCheckServiceImpl sendInterceptMsg exception [{}]" , e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 根据操作节点获取拦截报表对应操作节点
     * @param pdaOperateRequest pda请求参数
     * @return 节点
     * @author fanggang7
     * @time 2020-12-17 20:48:27 周四
     */
    private int getOperateNode(PdaOperateRequest pdaOperateRequest){
        int operateNode = 0;
        if(pdaOperateRequest.getOperateNode() != null){
            if(pdaOperateRequest.getOperateNode() == OperateNodeConstants.SEND){
                operateNode = businessInterceptConfigHelper.getOperateNodeByConstants(OperateNodeConstants.SEND);
            }
            if(pdaOperateRequest.getOperateNode() == OperateNodeConstants.SORTING){
                operateNode = businessInterceptConfigHelper.getOperateNodeByConstants(OperateNodeConstants.SORTING);
            }
        }
        return operateNode;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.singleSendCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse singleSendCheck(SortingCheck sortingCheck) {
        return this.singleSendCheck(sortingCheck, false);
    }

    private SortingJsfResponse singleSendCheck(SortingCheck sortingCheck, boolean reportIntercept) {

        if (sortingCheck == null) {
            return new SortingJsfResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
        }

        SortingJsfResponse response = new SortingJsfResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        PdaOperateRequest pdaOperateRequest = this.getPdaOperateRequest(sortingCheck);

        if (pdaOperateRequest.getOperateType() != null && pdaOperateRequest.getOperateType() == Constants.OPERATE_TYPE_NEW_PACKAGE_SEND) {
            FilterContext filterContext = null;
            try {
                //初始化拦截链上下文
                filterContext = this.initContext(pdaOperateRequest);
                filterContext.setFuncModule(HintModuleConstants.FORWARD_DELIVERY);
                DeliveryFilterChain deliveryFilterChain = SendBizSourceEnum.WAYBILL_SEND.getCode().equals(sortingCheck.getBizSourceType()) ? getDeliveryByWaybillFilterChain() : getDeliveryFilterChain();
                deliveryFilterChain.doFilter(filterContext, deliveryFilterChain);
            } catch (IllegalWayBillCodeException e) {
                logger.error("新发货验证服务异常，非法运单号：IllegalWayBillCodeException", e);
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(e.getMessage());
            } catch (Exception ex) {
                if (ex instanceof SortingCheckException) {
                    SortingCheckException checkException = (SortingCheckException) ex;
                    response.setCode(checkException.getCode());
                    response.setMessage(checkException.getMessage());
                    if(reportIntercept){
                        // 发出拦截报表mq
                        this.sendInterceptMsg(filterContext, checkException);
                    }
                } else {
                    logger.error("新发货验证服务异常，参数：{}", JsonHelper.toJson(pdaOperateRequest), ex);
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                }
            }
        } else {
            //调用装箱的分拣验证
            return this.sortingCheck(pdaOperateRequest, reportIntercept);
        }

        this.addSortingCheckStatisticsLog(pdaOperateRequest, response.getCode(), response.getMessage());
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.singleSendCheckAndReportIntercept", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse singleSendCheckAndReportIntercept(SortingCheck sortingCheck) {
        return this.singleSendCheck(sortingCheck, true);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.boardCombinationCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest boardCombinationRequest) {
        return this.boardCombinationCheck(boardCombinationRequest, false);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.boardCombinationCheckAndReportIntercept", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoardCombinationJsfResponse boardCombinationCheckAndReportIntercept(BoardCombinationRequest boardCombinationRequest) {
        return this.boardCombinationCheck(boardCombinationRequest, true);
    }

    private BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest boardCombinationRequest, boolean reportIntercept) {

        if (boardCombinationRequest == null) {
            return new BoardCombinationJsfResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
        }
        BoardCombinationJsfResponse response = new BoardCombinationJsfResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        FilterContext filterContext = null;
        try {
            if (this.isNeedCheck(uccPropertyConfiguration.getBoardCombinationSwitchVerToWebSites(), boardCombinationRequest.getSiteCode())) {
                //初始化拦截链上下文
                filterContext = this.initFilterParam(boardCombinationRequest);
                filterContext.setFuncModule(HintModuleConstants.BOARD_COMBINATION);
                //获取校验链
                FilterChain boardCombinationChain = getBoardCombinationFilterChain();
                //校验过程
                boardCombinationChain.doFilter(filterContext, boardCombinationChain);
            } else {
                return jsfSortingResourceService.boardCombinationCheck(boardCombinationRequest);
            }

        } catch (IllegalWayBillCodeException e) {
            logger.error("非法运单号：IllegalWayBillCodeException", e);
        } catch (Exception ex) {
            if (ex instanceof SortingCheckException) {
                SortingCheckException checkException = (SortingCheckException) ex;
                if(reportIntercept){
                    // 发出拦截报表mq
                    this.sendInterceptMsg(filterContext, checkException);
                }
                return new BoardCombinationJsfResponse(checkException.getCode(), checkException.getMessage());
            } else {
                logger.error("组板验证服务异常，参数：{}", JsonHelper.toJson(boardCombinationRequest), ex);
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }

        if(response.getMessage() != null && response.getMessage().contains("装箱")) {
            response.setMessage(response.getMessage().replace("装箱", "组板"));
        }

        this.addSortingCheckStatisticsLog(this.convertPdaOperateRequest(boardCombinationRequest), response.getCode(), response.getMessage());
        return response;
    }


    /**
     * 是否是切换试用站点
     */
    @Override
    public boolean isNeedCheck(String uccStr, Integer siteCode) {
        if (siteCode == null) {
            return false;
        }
        if(StringUtils.isEmpty(uccStr)){
            return false;
        } else if ("1".equals(uccStr)) {
            return true;
        }
        List<String> siteCodes = Arrays.asList(uccStr.split(Constants.SEPARATOR_COMMA));
        return siteCodes.contains(String.valueOf(siteCode));
    }

    private PdaOperateRequest getPdaOperateRequest(SortingCheck sortingCheck) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setReceiveSiteCode(sortingCheck.getReceiveSiteCode());
        pdaOperateRequest.setCreateSiteCode(sortingCheck.getCreateSiteCode());
        pdaOperateRequest.setBoxCode(sortingCheck.getBoxCode());
        pdaOperateRequest.setPackageCode(sortingCheck.getPackageCode());
        pdaOperateRequest.setBusinessType(sortingCheck.getBusinessType());
        pdaOperateRequest.setOperateUserCode(sortingCheck.getOperateUserCode());
        pdaOperateRequest.setOperateUserName(sortingCheck.getOperateUserName());
        pdaOperateRequest.setOperateTime(sortingCheck.getOperateTime());
        pdaOperateRequest.setOperateType(sortingCheck.getOperateType());
        pdaOperateRequest.setOperateNode(sortingCheck.getOperateNode());
        pdaOperateRequest.setOnlineStatus(sortingCheck.getOnlineStatus());
        if(sortingCheck.getIsLoss() == null){
            pdaOperateRequest.setIsLoss(0);
        }else{
            pdaOperateRequest.setIsLoss(sortingCheck.getIsLoss());
        }
        return pdaOperateRequest;
    }

    /*
    * 初始化拦截链上下文
    * */
    private FilterContext initContext(PdaOperateRequest pdaOperateRequest) throws Exception {

        FilterContext filterContext = initFilterParam(pdaOperateRequest);

        //分拣规则
        filterContext.setRuleMap(this.getRuleList(filterContext.getCreateSiteCode()));

        // 箱子判断
        Box box = boxService.findBoxByCode(filterContext.getBoxCode());

        //只有boxCode符合箱号规则时才进行箱号不存在的判定
        if (BusinessUtil.isBoxcode(filterContext.getBoxCode()) && (box == null || StringHelper.isEmpty(box.getCode()))) {
            throw new SortingCheckException(SortingResponse.CODE_29001, SortingResponse.MESSAGE_29001);
        } else {
            filterContext.setBox(box);
        }

        // 站点判断
        Site receiveSite = this.siteService.get(filterContext.getReceiveSiteCode());
        if (receiveSite == null) {
            throw new SortingCheckException(SortingResponse.CODE_29202, filterContext.getReceiveSiteCode() + SortingResponse.MESSAGE_29202);
        } else {
            filterContext.setReceiveSite(receiveSite);
        }

        // 操作站点
        Site createSite = this.siteService.get(filterContext.getCreateSiteCode());
        filterContext.setCreateSite(createSite);

        String sReceiveSiteSubType = String.valueOf(receiveSite.getSubType());
        filterContext.setsReceiveSiteSubType(sReceiveSiteSubType);

        Integer sReceiveSiteCode = filterContext.getReceiveSiteCode();
        if (BusinessUtil.isBoxcode(filterContext.getBoxCode()) && box.getReceiveSiteCode() != null) {
            sReceiveSiteCode = box.getReceiveSiteCode();
        }
        filterContext.setsReceiveSiteCode(sReceiveSiteCode.toString());
        // 箱子的收货站点和站点类型 (中转站和速递中心判断使用)
        Site sReceiveBoxSite = this.siteService.get(sReceiveSiteCode);
        filterContext.setsReceiveBoxSite(sReceiveBoxSite);

        //运单判断
        WaybillCache waybillCache = this.waybillCacheService.getFromCache(filterContext.getWaybillCode());
        filterContext.setWaybillCache(waybillCache);
        if (waybillCache == null) {
            throw new SortingCheckException(SortingResponse.CODE_39002,
                    HintService.getHint(HintCodeConstants.WAYBILL_OR_PACKAGE_NOT_FOUND));
        }

        if (waybillCache.getOrgId() == null) {
            throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR, SortingResponse.WAYBILL_ERROR_ORGID);
        }

        if (waybillCache.getWaybillCode() == null) {
            throw new SortingCheckException(JdResponse.CODE_PARAM_ERROR, SortingResponse.WAYBILL_ERROR_WAYBILLCODE);
        }

        if (WaybillUtil.isWaybillCode(filterContext.getPackageCode())) {
            if (waybillCache.getQuantity() != null) {
                filterContext.setPackageNum(waybillCache.getQuantity());
            }
            if (! BusinessUtil.isBoxcode(filterContext.getPackageCode())) {
                if(waybillCache.getQuantity() == null || waybillCache.getQuantity().equals(0)) {
                    //防止特殊情况，需再去调用运单接口确认数据
                    WaybillCache waybillNoCache = waybillCacheService.getNoCache(filterContext.getPackageCode());
                    if (waybillNoCache == null || waybillNoCache.getQuantity() == null || waybillNoCache.getQuantity().equals(0)) {
                        //此时认为无运单数据
                        throw new SortingCheckException(SortingResponse.CODE_29412, SortingResponse.MESSAGE_29412);
                    } else{
                        filterContext.setPackageNum(waybillNoCache.getQuantity());
                    }
                }
            }
        } else if (WaybillUtil.isPackageCode(filterContext.getPackageCode())){
            filterContext.setPackageNum(1);
            if(uccPropertyConfiguration.isControlCheckPackage()){
                String packageCode = filterContext.getPackageCode();
                BaseEntity<List<DeliveryPackageD>> baseEntity = this.getPageBaseEntityByPackageCode(packageCode);

                if(baseEntity == null){
                    logger.error(String.format("没有查询到包裹数据packageCode[{%s}]",packageCode));
                    throw new SortingCheckException(SortingResponse.CODE_29409, SortingResponse.MESSAGE_29409);
                }
                if(baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()){
                    logger.error(String.format("查询运单接口返回失败packageCode[{%s}]resultCode[{%s}]message[{%s}]", packageCode,baseEntity.getResultCode(),
                            baseEntity.getMessage()));
                    throw new SortingCheckException(SortingResponse.CODE_29409, SortingResponse.MESSAGE_29409);
                }
                if(CollectionUtils.isEmpty(baseEntity.getData())){
                    logger.error(String.format("包裹号不存在packageCode[{%s}]", packageCode));
                    throw new SortingCheckException(SortingResponse.CODE_29409, SortingResponse.MESSAGE_29409);
                }
            }
        }
        //初始增值服务
        //增加接货仓 KA安踏预售逻辑 (只考虑ECLP仓配外单场景  减少调用)
        if(BusinessUtil.isEclpAndWmsForDistribution(waybillCache.getWaybillSign()) && filterContext.getCreateSite() != null && filterContext.getCreateSite().getSubType() !=null &&
                Integer.valueOf(Constants.JHC_SITE_TYPE).equals(filterContext.getCreateSite().getSubType())) {
            BaseEntity<List<WaybillProductDto>> productAbilities = waybillQueryManager.getProductAbilityInfoByWaybillCode(waybillCache.getWaybillCode());
            //获取能力信息
            if (productAbilities != null && !org.springframework.util.CollectionUtils.isEmpty(productAbilities.getData())) {
                filterContext.setWaybillProductDtos(productAbilities.getData());
            }
        }

        return filterContext;
    }

    /**
     * 上下文参数判断
     */
    private FilterContext initFilterParam(PdaOperateRequest pdaOperateRequest) throws SortingCheckException {

        if (StringHelper.isEmpty(pdaOperateRequest.getBoxCode())) {
            throw new SortingCheckException(SortingResponse.CODE_29000, SortingResponse.MESSAGE_29000);
        }
        if (! NumberHelper.isPositiveNumber(pdaOperateRequest.getCreateSiteCode())) {
            throw new SortingCheckException(SortingResponse.CODE_29200, SortingResponse.MESSAGE_29200);
        }
        if (! NumberHelper.isPositiveNumber(pdaOperateRequest.getReceiveSiteCode())) {
            throw new SortingCheckException(SortingResponse.CODE_29201, SortingResponse.MESSAGE_29201);
        }
        String waybillCode = WaybillUtil.getWaybillCode(pdaOperateRequest.getPackageCode());

        if (StringHelper.isEmpty(waybillCode)) {
            throw  new SortingCheckException(SortingResponse.CODE_29100, SortingResponse.MESSAGE_29100);
        }
        FilterContext filterContext = new FilterContext();
        filterContext.setWaybillCode(waybillCode);
        filterContext.setCreateSiteCode(pdaOperateRequest.getCreateSiteCode());
        filterContext.setBoxCode(pdaOperateRequest.getBoxCode());
        filterContext.setReceiveSiteCode(pdaOperateRequest.getReceiveSiteCode());
        filterContext.setBusinessType(pdaOperateRequest.getBusinessType());
        filterContext.setPackageCode(pdaOperateRequest.getPackageCode());
        filterContext.setPdaOperateRequest(pdaOperateRequest);
        filterContext.setOnlineStatus(pdaOperateRequest.getOnlineStatus());
        return filterContext;
    }

    /**
     * 上下文参数判断
     */
    private FilterContext initFilterParam(BoardCombinationRequest boardCombinationRequest) throws SortingCheckException {

        if (StringHelper.isEmpty(boardCombinationRequest.getBoxOrPackageCode())) {
            throw new SortingCheckException(SortingResponse.CODE_29000, SortingResponse.MESSAGE_29000);
        }
        if (! NumberHelper.isPositiveNumber(boardCombinationRequest.getSiteCode())) {
            throw new SortingCheckException(SortingResponse.CODE_29200, SortingResponse.MESSAGE_29200);
        }
        if (! NumberHelper.isPositiveNumber(boardCombinationRequest.getReceiveSiteCode())) {
            throw new SortingCheckException(SortingResponse.CODE_29201, SortingResponse.MESSAGE_29201);
        }

        FilterContext filterContext = new FilterContext();
        filterContext.setRuleMap(getRuleList(boardCombinationRequest.getSiteCode()));
        filterContext.setBoxCode(boardCombinationRequest.getBoxOrPackageCode());
        filterContext.setCreateSiteCode(boardCombinationRequest.getSiteCode());
        filterContext.setReceiveSiteCode(boardCombinationRequest.getReceiveSiteCode());
        filterContext.setBusinessType(boardCombinationRequest.getBusinessType());
        filterContext.setPackageCode(boardCombinationRequest.getBoxOrPackageCode());
        filterContext.setPdaOperateRequest(this.convertPdaOperateRequest(boardCombinationRequest));
        filterContext.setOnlineStatus(boardCombinationRequest.getOnlineStatus());

        return filterContext;
    }

    /**
     * 获取当前分拣中心的校验规则
     *
     */
    private Map<String, Rule> getRuleList(Integer createSiteCode) throws SortingCheckException {
        List<Rule> ruleList = ruleService.queryByParamNoPage(createSiteCode);
        if (ruleList == null || ruleList.size() == 0) {//未配置分拣规则
            throw new SortingCheckException(SortingResponse.CODE_29402, SortingResponse.MESSAGE_29402);
        }
        Map<String, Rule> ruleMap = new HashMap<String, Rule>();
        for (Rule rule : ruleList) {
            ruleMap.put(String.valueOf(rule.getType()), rule);
        }
        return ruleMap;
    }


    private BaseEntity<List<DeliveryPackageD>> getPageBaseEntityByPackageCode(String packageCode) {
        if(StringUtils.isBlank(packageCode)){
            return null;
        }
        List<String> packageCodes = Lists.newArrayList(packageCode);
        return  waybillPackageManager.queryPackageListForParcodes(packageCodes);
    }

    private void addSortingCheckStatisticsLog(PdaOperateRequest pdaOperateRequest, Integer responseCode, String responseMessage) {

        //日志
        JSONObject request = new JSONObject();
        request.put("operatorCode", pdaOperateRequest.getOperateUserCode());
        request.put("packageCode", pdaOperateRequest.getPackageCode());
        request.put("boxCode", pdaOperateRequest.getBoxCode());
        request.put("siteCode", pdaOperateRequest.getCreateSiteCode());
        request.put("siteName", pdaOperateRequest.getCreateSiteName());
        request.put("operatorName", pdaOperateRequest.getOperateUserName());
        request.put("receiveSiteCode", pdaOperateRequest.getReceiveSiteCode());
        request.put("businessType", pdaOperateRequest.getBusinessType());
        request.put("responseCode", responseCode);
        request.put("responseMessage", responseMessage);


        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_VERINWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_VER_FILTER_STATISTICS);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_VER_FILTER_STATISTICS);
        businessLogProfiler.setTimeStamp(new Date().getTime());
        businessLogProfiler.setRequestTime(new Date().getTime());
        businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));

        logEngine.addLog(businessLogProfiler);

    }

    /*
    * 参数转换
    * */
    private PdaOperateRequest convertPdaOperateRequest(BoardCombinationRequest boardCombinationRequest) {

        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setReceiveSiteCode(boardCombinationRequest.getReceiveSiteCode());
        pdaOperateRequest.setCreateSiteCode(boardCombinationRequest.getSiteCode());
        pdaOperateRequest.setBoxCode(boardCombinationRequest.getBoxOrPackageCode());
        pdaOperateRequest.setPackageCode(boardCombinationRequest.getBoxOrPackageCode());
        pdaOperateRequest.setBusinessType(boardCombinationRequest.getBusinessType());
        pdaOperateRequest.setOperateUserCode(boardCombinationRequest.getUserCode());
        pdaOperateRequest.setOperateUserName(boardCombinationRequest.getUserName());
        return pdaOperateRequest;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 获取正向分拣校验链
     * @return
     */
    private ForwardFilterChain getForwardFilterChain() {
        return (ForwardFilterChain) beanFactory.getBean("forwardFilterChain");
    }


    /**
     * 获取逆向分拣校验链
     * @return
     */
    private ReverseFilterChain getReverseFilterChain() {
        return (ReverseFilterChain) beanFactory.getBean("reverseFilterChain");
    }

    /**
     * 获取发货校验链
     * @return
     */
    private DeliveryFilterChain getDeliveryFilterChain(){
        return (DeliveryFilterChain) beanFactory.getBean("deliveryFilterChain");
    }
    /**
     * 获取按运单发货校验链
     */
    private DeliveryFilterChain getDeliveryByWaybillFilterChain(){
        return (DeliveryFilterChain) beanFactory.getBean("deliveryByWaybillFilterChain");
    }

    /**
     * 获取发货校验链
     * @return
     */
    private ProceedFilterChain getProceedFilterChain(){
        return (ProceedFilterChain) beanFactory.getBean("proceedFilterChain");
    }

    /**
     * 获取组板校验链
     * @return
     */
    public FilterChain getBoardCombinationFilterChain(){
        return (BoardCombinationChain) beanFactory.getBean("boardCombinationChain");
    }

    /**
     * 获取虚拟组板校验链
     * @return
     */
    public FilterChain getVirtualBoardCombinationFilterChain(){
        return (VirtualBoardCombinationChain) beanFactory.getBean("virtualBoardCombinationChain");
    }

    private SortingCheck convertToSortingCheck(PdaOperateRequest request){
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

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.coldChainSendCheckAndReportIntercept", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse coldChainSendCheckAndReportIntercept(DeliveryRequest request) {
        return this.coldChainSendCheck(request, true);
    }

    /**
     * 冷链发货主校验
     *
     * @param request 单个请求参数
     * @return 校验结果
     */
    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.coldChainSendCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse coldChainSendCheck(DeliveryRequest request) {
        return this.coldChainSendCheck(request, false);
    }

    /**
     * 冷链发货主校验
     * @param request 单个请求参数
     * @param reportIntercept 是否记录拦截记录到报表
     * @return 校验结果
     */
    @Override
    public SortingJsfResponse coldChainSendCheck(DeliveryRequest request, boolean reportIntercept) {
        if (request == null) {
            return new SortingJsfResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
        }
        SortingJsfResponse response = new SortingJsfResponse(com.jd.bluedragon.distribution.api.JdResponse.CODE_OK, com.jd.bluedragon.distribution.api.JdResponse.MESSAGE_OK);

        FilterContext filterContext = null;
        try {
            PdaOperateRequest pdaOperateRequest = this.convertPdaOperateRequest(request);
            //初始化拦截链上下文
            filterContext = this.initContext(pdaOperateRequest);
            ColdChainDeliveryFilterChain coldChainDeliveryFilterChain = this.getColdChainDeliveryFilterChain();
            coldChainDeliveryFilterChain.doFilter(filterContext, coldChainDeliveryFilterChain);

        } catch (IllegalWayBillCodeException e) {
            logger.error("发货验证服务异常，非法运单号：IllegalWayBillCodeException", e);
            response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_PARAM_ERROR);
            response.setMessage(e.getMessage());
        } catch (Exception ex) {
            if (ex instanceof SortingCheckException) {
                SortingCheckException checkException = (SortingCheckException) ex;
                response.setCode(checkException.getCode());
                response.setMessage(checkException.getMessage());
                if (reportIntercept) {
                    // 发出拦截报表mq
                    this.sendInterceptMsg(filterContext, checkException);
                }
            } else {
                logger.error("发货验证服务异常，参数：{}", JsonHelper.toJson(request), ex);
                response.setCode(com.jd.bluedragon.distribution.api.JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }
        return response;
    }

    /*
     * 参数转换
     * */
    private PdaOperateRequest convertPdaOperateRequest(DeliveryRequest request) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setOperateNode(OperateNodeConstants.SEND);
        pdaOperateRequest.setReceiveSiteCode(request.getReceiveSiteCode());
        pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setBoxCode(request.getBoxCode());
        pdaOperateRequest.setPackageCode(request.getBoxCode());
        pdaOperateRequest.setBusinessType(request.getBusinessType());
        pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
        if(request.getCurrentOperate() != null && request.getCurrentOperate().getOperateTime() != null){
            pdaOperateRequest.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(), DateUtil.FORMAT_DATE_TIME));
        } else {
            pdaOperateRequest.setOperateTime(DateUtil.format(new Date(), DateUtil.FORMAT_DATE_TIME));
        }
        // pdaOperateRequest.setOperateType(request.getOperateType());
        // pdaOperateRequest.setOperateNode(request.getOperateNode());
        return pdaOperateRequest;
    }

    /**
     * 获取冷链发货校验链
     * @return 校验链
     * @author fanggang7
     * @time 2021-03-25 20:56:42 周四
     */
    private ColdChainDeliveryFilterChain getColdChainDeliveryFilterChain() {
        return (ColdChainDeliveryFilterChain) this.beanFactory.getBean("coldChainDeliveryFilterChain");
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.virtualBoardCombinationCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoardCombinationJsfResponse virtualBoardCombinationCheck(PdaOperateRequest pdaOperateRequest) {
        return this.virtualBoardCombinationCheck(pdaOperateRequest, false);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.virtualBoardCombinationCheckAndReportIntercept", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoardCombinationJsfResponse virtualBoardCombinationCheckAndReportIntercept(PdaOperateRequest pdaOperateRequest) {
        return this.virtualBoardCombinationCheck(pdaOperateRequest, true);
    }

    /**
     * 虚拟组板
     * @param pdaOperateRequest
     * @param reportIntercept
     * @return
     */
    private BoardCombinationJsfResponse virtualBoardCombinationCheck(PdaOperateRequest pdaOperateRequest, boolean reportIntercept) {

        if (pdaOperateRequest == null) {
            return new BoardCombinationJsfResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
        }
        BoardCombinationJsfResponse response = new BoardCombinationJsfResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        FilterContext filterContext = null;
        try {
            //初始化拦截链上下文
            filterContext = this.initContext(pdaOperateRequest);
            ProceedFilterChain proceedFilterChain = getProceedFilterChain();
            proceedFilterChain.doFilter(filterContext, proceedFilterChain);

            final FilterChain virtualBoardCombinationFilterChain = getVirtualBoardCombinationFilterChain();
            virtualBoardCombinationFilterChain.doFilter(filterContext, virtualBoardCombinationFilterChain);
        } catch (IllegalWayBillCodeException e) {
            logger.error("非法运单号：IllegalWayBillCodeException", e);
        } catch (Exception ex) {
            if (ex instanceof SortingCheckException) {
                SortingCheckException checkException = (SortingCheckException) ex;
                if(reportIntercept){
                    // 发出拦截报表mq
                    this.sendInterceptMsg(filterContext, checkException);
                }
                return new BoardCombinationJsfResponse(checkException.getCode(), checkException.getMessage());
            } else {
                logger.error("组板验证服务异常，参数：{}", JsonHelper.toJson(pdaOperateRequest), ex);
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }

        if(response.getMessage() != null && response.getMessage().contains("装箱")) {
            response.setMessage(response.getMessage().replace("装箱", "组板"));
        }

        this.addSortingCheckStatisticsLog(pdaOperateRequest, response.getCode(), response.getMessage());
        return response;
    }

}
