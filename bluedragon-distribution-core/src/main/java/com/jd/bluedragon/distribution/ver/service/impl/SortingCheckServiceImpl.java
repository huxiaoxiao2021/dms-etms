package com.jd.bluedragon.distribution.ver.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.rule.service.RuleService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.IllegalWayBillCodeException;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.chains.DeliveryFilterChain;
import com.jd.bluedragon.distribution.ver.filter.chains.ForwardFilterChain;
import com.jd.bluedragon.distribution.ver.filter.chains.ReverseFilterChain;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
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
import com.jd.fastjson.JSONObject;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
//
//    @Qualifier("forwardFilterChain")
//    @Autowired()
//    private ForwardFilterChain forwardFilterChain;

//    @Qualifier("reverseFilterChain")
//    @Autowired()
//    private ReverseFilterChain reverseFilterChain;

//    @Qualifier("deliveryFilterChain")
//    @Autowired()
//    private DeliveryFilterChain deliveryFilterChain;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


    @Override
    @JProfiler(jKey = "DMSWEB.SortingCheckServiceImpl.sortingCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SortingJsfResponse sortingCheck(PdaOperateRequest pdaOperateRequest) {

        if (pdaOperateRequest == null) {
            return new SortingJsfResponse(SortingResponse.CODE_PARAM_IS_NULL, SortingResponse.MESSAGE_PARAM_IS_NULL);
        }

        SortingJsfResponse response = new SortingJsfResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);

        if (this.isNotNeedCheck(pdaOperateRequest.getCreateSiteCode())) {
            return response;
        }
        try {
            //初始化拦截链上下文
            logger.info("分拣检测-初始化filterContext:pdaOperateRequest={}", new Gson().toJson(pdaOperateRequest));
            FilterContext filterContext = this.initContext(pdaOperateRequest);
            Integer businessType = pdaOperateRequest.getBusinessType();
            logger.info("分拣检测-完成初始化filterContext:businessType={}", businessType);
            //根据operateType判断入口，如果入口是一车一单发货，则走deliveryFilterChain
            if (pdaOperateRequest.getOperateType() != null && businessType == Constants.OPERATE_TYPE_NEW_PACKAGE_SEND) {
                DeliveryFilterChain deliveryFilterChain = getDeliveryFilterChain();
                deliveryFilterChain.doFilter(filterContext, deliveryFilterChain);
            } else {
                if (BusinessUtil.isForward(businessType)) {
                    logger.info("分拣检测-调用正向处理链路开始");
                    ForwardFilterChain forwardFilterChain = getForwardFilterChain();
                    forwardFilterChain.doFilter(filterContext, forwardFilterChain);
                    logger.info("分拣检测-调用正向处理链路结束");
                } else if (BusinessUtil.isReverse(businessType)) {
                    ReverseFilterChain reverseFilterChain = getReverseFilterChain();
                    reverseFilterChain.doFilter(filterContext, reverseFilterChain);
                }
            }
        } catch (IllegalWayBillCodeException e) {
            logger.error("非法运单号：IllegalWayBillCodeException", e);
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(e.getMessage());
        } catch (Exception ex) {
            if (ex instanceof SortingCheckException) {
                SortingCheckException checkException = (SortingCheckException) ex;
                response.setCode(checkException.getCode());
                response.setMessage(checkException.getMessage());
            } else {
                logger.error("服务异常，参数：{}", JsonHelper.toJson(pdaOperateRequest), ex);
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            }
        }
        this.addSortingCheckStatisticsLog(pdaOperateRequest, response);
        return response;
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

    private void addSortingCheckStatisticsLog(PdaOperateRequest pdaOperateRequest, SortingJsfResponse sortingJsfResponse) {

        //日志
        JSONObject request = new JSONObject();
        request.put("operatorCode", pdaOperateRequest.getOperateUserCode());
        request.put("pakcageCode", pdaOperateRequest.getPackageCode());
        request.put("boxCode", pdaOperateRequest.getBoxCode());
        request.put("siteCode", pdaOperateRequest.getCreateSiteCode());
        request.put("siteName", pdaOperateRequest.getCreateSiteName());
        request.put("operatorName", pdaOperateRequest.getOperateUserName());

        request.put("responseCode", sortingJsfResponse.getCode());
        request.put("responseMessage", sortingJsfResponse.getMessage());

        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_VERINWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_VER_FILTER_STATISTICS);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_VER_FILTER_STATISTICS);
        businessLogProfiler.setTimeStamp(new Date().getTime());
        businessLogProfiler.setRequestTime(new Date().getTime());
        businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));

        logEngine.addLog(businessLogProfiler);

    }

    /**
     * 是否是切换试用站点
     */
    private boolean isNotNeedCheck(Integer siteCode) {
        if (siteCode == null) {
            return true;
        }
        String switchVerToWebSites = uccPropertyConfiguration.getSwitchVerToWebSites();
        if(StringUtils.isEmpty(switchVerToWebSites)){
            return true;
        } else if ("1".equals(switchVerToWebSites)) {
            return false;
        }
        List<String> siteCodes = Arrays.asList(switchVerToWebSites.split(Constants.SEPARATOR_COMMA));
        return ! siteCodes.contains(String.valueOf(siteCode));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 获取正向分拣校验链
     * @return
     */
    public ForwardFilterChain getForwardFilterChain() {
        return (ForwardFilterChain) beanFactory.getBean("forwardFilterChain");
    }


    /**
     * 获取逆向分拣校验链
     * @return
     */
    public ReverseFilterChain getReverseFilterChain() {
        return (ReverseFilterChain) beanFactory.getBean("reverseFilterChain");
    }

    /**
     * 获取发货校验链
     * @return
     */
    public DeliveryFilterChain getDeliveryFilterChain(){
        return (DeliveryFilterChain) beanFactory.getBean("deliveryFilterChain");
    }

}
