package com.jd.bluedragon.distribution.ver.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.service.RuleService;
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
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.fastjson.JSON;
import com.jd.fastjson.JSONObject;
import com.jd.ql.basic.util.DateUtil;
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
    // 拦截报表操作节点分拣类型
    @Value("${businessIntercept.operate.node.sorting}")
    private Integer interceptOperateNodeSorting;
    // 拦截报表操作节点发货类型
    @Value("${businessIntercept.operate.node.send}")
    private Integer interceptOperateNodeSend;
    // 拦截报表操作节点组板类型
    @Value("${businessIntercept.operate.node.boardCombination}")
    private Integer interceptOperateNodeBoardCombination;
    // 拦截报表操作节点设备类型
    @Value("${businessIntercept.device.type.pda}")
    private Integer interceptOperateDeviceTypePda;

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
                    ForwardFilterChain forwardFilterChain = getForwardFilterChain();
                    forwardFilterChain.doFilter(filterContext, forwardFilterChain);
                } else if (BusinessUtil.isReverse(businessType)) {
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
            saveInterceptMsgDto.setDeviceType(interceptOperateDeviceTypePda);
            saveInterceptMsgDto.setDeviceCode(Constant.PDA_DEVICE_CODE);
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

            String saveInterceptMqMsg = JSON.toJSONString(saveInterceptMsgDto);
            try {
                businessInterceptReportService.sendInterceptMsg(saveInterceptMsgDto);
            } catch (Exception e) {
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
                operateNode = interceptOperateNodeSend;
            }
            if(pdaOperateRequest.getOperateNode() == OperateNodeConstants.SORTING){
                operateNode = interceptOperateNodeSorting;
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
                DeliveryFilterChain deliveryFilterChain = getDeliveryFilterChain();
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
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.singleSendCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse singleSendCheckAndReportIntercept(SortingCheck sortingCheck) {
        return this.singleSendCheck(sortingCheck, true);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.boardCombinationCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BoardCombinationJsfResponse boardCombinationCheck(BoardCombinationRequest boardCombinationRequest) {
        return this.boardCombinationCheck(boardCombinationRequest, false);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.boardCombinationCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
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
            throw new SortingCheckException(SortingResponse.CODE_39002, SortingResponse.MESSAGE_39002);
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

}
