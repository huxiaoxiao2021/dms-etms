package com.jd.bluedragon.distribution.router.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.router.dynamicLine.enums.RouterDynamicLineStatusEnum;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.router.IRouterDynamicLineReplacePlanService;
import com.jd.bluedragon.distribution.router.dao.RouterDynamicLineReplacePlanDao;
import com.jd.bluedragon.distribution.router.dao.RouterDynamicLineReplacePlanLogDao;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMq;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMqContext;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanMatchedEnableLineReq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.bluedragon.distribution.router.manager.IRouterDynamicLineReplacePlanManager;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 动态线路切换方案接口实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-01 18:23:22 周一
 */
@Service("routerDynamicLineReplacePlanService")
public class RouterDynamicLineReplacePlanServiceImpl implements IRouterDynamicLineReplacePlanService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RouterDynamicLineReplacePlanDao routerDynamicLineReplacePlanDao;

    @Autowired
    private RouterDynamicLineReplacePlanLogDao routerDynamicLineReplacePlanLogDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private IRouterDynamicLineReplacePlanManager routerDynamicLineReplacePlanManager;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    /**
     * 消费路由消息服务
     *
     * @param dynamicLineReplacePlanMq 路由消息实体
     * @return 处理结果
     */
    @Override
    public Result<Boolean> consumeDynamicLineReplacePlan(DynamicLineReplacePlanMq dynamicLineReplacePlanMq) {
        if(log.isInfoEnabled()){
            log.info("RouterDynamicLineReplacePlanServiceImpl.consumeDynamicLineReplacePlan param: {}", JsonHelper.toJson(dynamicLineReplacePlanMq));
        }
        Result<Boolean> result = Result.success();
        try {
            // step 基础入参校验
            final Result<Void> checkResult = this.checkParam4consumeDynamicLineReplacePlan(dynamicLineReplacePlanMq);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            // todo 相同老-新线路如何更新的逻辑待确认

            // step 处理上下文
            DynamicLineReplacePlanMqContext dynamicLineReplacePlanMqContext = new DynamicLineReplacePlanMqContext();
            final Result<Boolean> handleContextResult = this.handleRouterDynamicLineReplacePlanMqContext(dynamicLineReplacePlanMq, dynamicLineReplacePlanMqContext);
            if(!handleContextResult.isSuccess()){
                return result.toFail(handleContextResult.getMessage());
            }

            // step 查看是否有相同始发、原始目的、替换目的、versionId、pushTime的数据，这几个条件相同视为同一条推送数据
            final RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery = getRouterDynamicLineReplacePlanQuery(dynamicLineReplacePlanMq, dynamicLineReplacePlanMqContext);
            final RouterDynamicLineReplacePlan routerDynamicLineReplacePlanExist = routerDynamicLineReplacePlanDao.selectOne(routerDynamicLineReplacePlanQuery);
            if (routerDynamicLineReplacePlanExist != null) {
                log.warn("RouterDynamicLineReplacePlanServiceImpl.consumeDynamicLineReplacePlan same data {}", JsonHelper.toJson(dynamicLineReplacePlanMq));
                return result;
            }

            // step 插入新数据
            final RouterDynamicLineReplacePlan routerDynamicLineReplacePlanAdd = new RouterDynamicLineReplacePlan();
            this.assembleRouterDynamicLineReplacePlan(routerDynamicLineReplacePlanAdd, dynamicLineReplacePlanMqContext);
            routerDynamicLineReplacePlanDao.insertSelective(routerDynamicLineReplacePlanAdd);
        }catch (Exception e){
            log.error("RouterDynamicLineReplacePlanServiceImpl.consumeDynamicLineReplacePlan param: {}", JsonHelper.toJson(dynamicLineReplacePlanMq), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private RouterDynamicLineReplacePlanQuery getRouterDynamicLineReplacePlanQuery(DynamicLineReplacePlanMq dynamicLineReplacePlanMq, DynamicLineReplacePlanMqContext dynamicLineReplacePlanMqContext) {
        final RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery = new RouterDynamicLineReplacePlanQuery();
        routerDynamicLineReplacePlanQuery.setStartSiteId(dynamicLineReplacePlanMqContext.getBaseSiteStart().getSiteCode());
        routerDynamicLineReplacePlanQuery.setOldEndSiteId(dynamicLineReplacePlanMqContext.getBaseSiteEndOld().getSiteCode());
        routerDynamicLineReplacePlanQuery.setNewEndSiteId(dynamicLineReplacePlanMqContext.getBaseSiteEndNew().getSiteCode());
        routerDynamicLineReplacePlanQuery.setVersionId(dynamicLineReplacePlanMq.getVersionId());
        routerDynamicLineReplacePlanQuery.setPushTime(dynamicLineReplacePlanMq.getPushTime());
        return routerDynamicLineReplacePlanQuery;
    }

    private Result<Void> checkParam4consumeDynamicLineReplacePlan(DynamicLineReplacePlanMq dynamicLineReplacePlanMq){
        Result<Void> result = Result.success();
        if (dynamicLineReplacePlanMq.getVersionId() == null) {
            return result.toFail("报文异常，versionId不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getStartNodeCode())) {
            return result.toFail("报文异常，startNodeCode不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getOldEndNodeCode())) {
            return result.toFail("报文异常，oldEndNodeCode不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getNewEndNodeCode())) {
            return result.toFail("报文异常，newEndNodeCode不能为空");
        }
        if (dynamicLineReplacePlanMq.getEnableTime() == null) {
            return result.toFail("报文异常，enableTime不能为空");
        }
        if (dynamicLineReplacePlanMq.getDisableTime() == null) {
            return result.toFail("报文异常，disableTime不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getOldPlanLineCode())) {
            return result.toFail("报文异常，oldPlanLineCode不能为空");
        }
        if (dynamicLineReplacePlanMq.getOldPlanDepartureTime() == null) {
            return result.toFail("报文异常，oldPlanDepartureTime不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getOldPlanFlowCode())) {
            return result.toFail("报文异常，oldPlanFlowCode不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getNewPlanLineCode())) {
            return result.toFail("报文异常，newPlanLineCode不能为空");
        }
        if (dynamicLineReplacePlanMq.getNewPlanDepartureTime() == null) {
            return result.toFail("报文异常，newPlanDepartureTime不能为空");
        }
        if (StringUtils.isBlank(dynamicLineReplacePlanMq.getNewPlanFlowCode())) {
            return result.toFail("报文异常，newPlanFlowCode不能为空");
        }
        if (dynamicLineReplacePlanMq.getPushTime() == null) {
            return result.toFail("报文异常，pushTime不能为空");
        }
        return result;
    }

    private Result<Boolean> handleRouterDynamicLineReplacePlanMqContext(DynamicLineReplacePlanMq dynamicLineReplacePlanMq, DynamicLineReplacePlanMqContext dynamicLineReplacePlanMqContext){
        Result<Boolean> result = Result.success();
        dynamicLineReplacePlanMqContext.setDynamicLineReplacePlanMq(dynamicLineReplacePlanMq);
        final BaseStaffSiteOrgDto baseSiteStart = baseMajorManager.getBaseSiteByDmsCode(dynamicLineReplacePlanMq.getStartNodeCode());
        if (baseSiteStart == null) {
            log.error("handleRouterDynamicLineReplacePlanMqContext empty baseSiteStart {}", JsonHelper.toJson(dynamicLineReplacePlanMq));
            return result.toFail("始发场地数据查询为空");
        }
        dynamicLineReplacePlanMqContext.setBaseSiteStart(baseSiteStart);
        final BaseStaffSiteOrgDto baseSiteEndOld = baseMajorManager.getBaseSiteByDmsCode(dynamicLineReplacePlanMq.getOldEndNodeCode());
        if (baseSiteEndOld == null) {
            log.error("handleRouterDynamicLineReplacePlanMqContext empty baseSiteEndOld {}", JsonHelper.toJson(dynamicLineReplacePlanMq));
            return result.toFail("原目的场地数据查询为空");
        }
        dynamicLineReplacePlanMqContext.setBaseSiteEndOld(baseSiteEndOld);
        final BaseStaffSiteOrgDto baseSiteEndNew = baseMajorManager.getBaseSiteByDmsCode(dynamicLineReplacePlanMq.getNewEndNodeCode());
        dynamicLineReplacePlanMqContext.setBaseSiteEndNew(baseSiteEndNew);
        return result;
    }

    private void assembleRouterDynamicLineReplacePlan(RouterDynamicLineReplacePlan routerDynamicLineReplacePlan, DynamicLineReplacePlanMqContext dynamicLineReplacePlanMqContext){
        final DynamicLineReplacePlanMq dynamicLineReplacePlanMq = dynamicLineReplacePlanMqContext.getDynamicLineReplacePlanMq();
        routerDynamicLineReplacePlan.setVersionId(dynamicLineReplacePlanMq.getVersionId());
        routerDynamicLineReplacePlan.setStartSiteId(dynamicLineReplacePlanMqContext.getBaseSiteStart().getSiteCode());
        routerDynamicLineReplacePlan.setStartSiteName(dynamicLineReplacePlanMqContext.getBaseSiteStart().getSiteName());
        routerDynamicLineReplacePlan.setOldEndSiteId(dynamicLineReplacePlanMqContext.getBaseSiteEndOld().getSiteCode());
        routerDynamicLineReplacePlan.setOldEndSiteName(dynamicLineReplacePlanMqContext.getBaseSiteEndOld().getSiteName());
        routerDynamicLineReplacePlan.setOldPlanLineCode(dynamicLineReplacePlanMq.getOldPlanLineCode());
        routerDynamicLineReplacePlan.setOldPlanFlowCode(dynamicLineReplacePlanMq.getOldPlanFlowCode());
        routerDynamicLineReplacePlan.setOldPlanDepartureTime(dynamicLineReplacePlanMq.getOldPlanDepartureTime());
        routerDynamicLineReplacePlan.setNewEndSiteId(dynamicLineReplacePlanMqContext.getBaseSiteEndNew().getSiteCode());
        routerDynamicLineReplacePlan.setNewEndSiteName(dynamicLineReplacePlanMqContext.getBaseSiteEndNew().getSiteName());
        routerDynamicLineReplacePlan.setNewPlanLineCode(dynamicLineReplacePlanMq.getNewPlanLineCode());
        routerDynamicLineReplacePlan.setNewPlanFlowCode(dynamicLineReplacePlanMq.getNewPlanFlowCode());
        routerDynamicLineReplacePlan.setNewPlanDepartureTime(dynamicLineReplacePlanMq.getNewPlanDepartureTime());
        routerDynamicLineReplacePlan.setCreateTime(new Date());
        routerDynamicLineReplacePlan.setEnableTime(dynamicLineReplacePlanMq.getEnableTime());
        routerDynamicLineReplacePlan.setDisableTime(dynamicLineReplacePlanMq.getDisableTime());
        routerDynamicLineReplacePlan.setPushTime(dynamicLineReplacePlanMq.getPushTime());
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Override
    public Result<List<RouterDynamicLineReplacePlan>> queryListByCondition(RouterDynamicLineReplacePlanQuery req) {
        if(log.isInfoEnabled()){
            log.info("RouterDynamicLineReplacePlanServiceImpl.queryListByCondition param: {}", JsonHelper.toJson(req));
        }
        Result<List<RouterDynamicLineReplacePlan>> result = Result.success();
        try {
            // step 校验参数
            final Result<Void> checkResult = this.checkParam4queryListByCondition(req);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // step 查询数据
            final List<RouterDynamicLineReplacePlan> dataList = routerDynamicLineReplacePlanDao.queryList(req);
            result.setData(dataList);
        }catch (Exception e){
            log.error("RouterDynamicLineReplacePlanServiceImpl.queryListByCondition param: {}", JsonHelper.toJson(req), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4queryListByCondition(RouterDynamicLineReplacePlanQuery req){
        Result<Void> result = Result.success();
        if (req.getStartSiteId() == null) {
            return result.toFail("参数错误, startSiteId不能为空");
        }
        return result;
    }

    /**
     * 根据条件查询可用的动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-03 17:45:38 周三
     */
    @Override
    public Result<RouterDynamicLineReplacePlan> getMatchedEnableLine(RouterDynamicLineReplacePlanMatchedEnableLineReq req) {
        if(log.isInfoEnabled()){
            log.info("RouterDynamicLineReplacePlanServiceImpl.getMatchedEnableLine param: {}", JsonHelper.toJson(req));
        }
        Result<RouterDynamicLineReplacePlan> result = Result.success();
        try {
            // step 校验参数
            final Result<Void> checkResult = this.checkParam4getMatchedEnableLine(req);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            // step 缓存设置
            final String uniqueCacheKey = this.getUniqueCacheKey(req);
            final String configExistStr = redisClientOfJy.get(uniqueCacheKey);
            if(StringUtils.isNotBlank(configExistStr)){
                if (Objects.equals(CacheKeyConstants.CACHE_KEY_ROUTER_DYNAMIC_LINE_REPLACE_VAL_NULL, configExistStr)) {
                    return result;
                }
                final RouterDynamicLineReplacePlan routerDynamicLineReplacePlanCacheExist = JsonHelper.fromJson(configExistStr, RouterDynamicLineReplacePlan.class);
                if(routerDynamicLineReplacePlanCacheExist != null && routerDynamicLineReplacePlanCacheExist.getDisableTime() != null
                        && routerDynamicLineReplacePlanCacheExist.getDisableTime().getTime() > req.getEffectTime().getTime()){
                    return result.setData(routerDynamicLineReplacePlanCacheExist);
                }
            }

            // step 查询符合条件的数据
            final RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery = getRouterDynamicLineReplacePlanQuery(req);
            final RouterDynamicLineReplacePlan routerDynamicLineReplacePlanExist = routerDynamicLineReplacePlanDao.selectLatestOne(routerDynamicLineReplacePlanQuery);
            // step 设置缓存 为空设置为-1
            if (routerDynamicLineReplacePlanExist != null) {
                redisClientOfJy.setEx(uniqueCacheKey, JsonHelper.toJson(routerDynamicLineReplacePlanExist), CacheKeyConstants.CACHE_KEY_ROUTER_DYNAMIC_LINE_REPLACE_TIME_OUT, TimeUnit.MINUTES);
            } else {
                redisClientOfJy.setEx(uniqueCacheKey, CacheKeyConstants.CACHE_KEY_ROUTER_DYNAMIC_LINE_REPLACE_VAL_NULL, CacheKeyConstants.CACHE_KEY_ROUTER_DYNAMIC_LINE_REPLACE_TIME_OUT, TimeUnit.MINUTES);
            }
            result.setData(routerDynamicLineReplacePlanExist);
        }catch (Exception e){
            log.error("RouterDynamicLineReplacePlanServiceImpl.getMatchedEnableLine param: {}", JsonHelper.toJson(req), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private RouterDynamicLineReplacePlanQuery getRouterDynamicLineReplacePlanQuery(RouterDynamicLineReplacePlanMatchedEnableLineReq req) {
        final RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery = new RouterDynamicLineReplacePlanQuery();
        routerDynamicLineReplacePlanQuery.setYn(Constants.YN_YES);
        routerDynamicLineReplacePlanQuery.setStartSiteId(req.getStartSiteId());
        routerDynamicLineReplacePlanQuery.setOldEndSiteId(req.getOldEndSiteId());
        routerDynamicLineReplacePlanQuery.setNewEndSiteId(req.getNewEndSiteId());
        routerDynamicLineReplacePlanQuery.setEffectTime(req.getEffectTime());
        routerDynamicLineReplacePlanQuery.setEnableStatusList(new ArrayList<>(Collections.singletonList(RouterDynamicLineStatusEnum.ENABLE.getCode())));
        return routerDynamicLineReplacePlanQuery;
    }

    private Result<Void> checkParam4getMatchedEnableLine(RouterDynamicLineReplacePlanMatchedEnableLineReq req){
        Result<Void> result = Result.success();
        if (req.getStartSiteId() == null) {
            return result.toFail("参数错误, startSiteId不能为空");
        }
        if (req.getOldEndSiteId() == null) {
            return result.toFail("参数错误, oldEndSiteId不能为空");
        }
        if (req.getNewEndSiteId() == null) {
            return result.toFail("参数错误, newEndSiteId不能为空");
        }
        if (req.getEffectTime() == null) {
            req.setEffectTime(new Date());
        }
        return result;
    }

    private String getUniqueCacheKey(RouterDynamicLineReplacePlanMatchedEnableLineReq req) {
        return String.format(CacheKeyConstants.CACHE_KEY_ROUTER_DYNAMIC_LINE_REPLACE, String.format("%s_%s_%s", req.getStartSiteId(), req.getOldEndSiteId(), req.getNewEndSiteId()));
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Override
    public Result<PageData<RouterDynamicLineReplacePlanVo>> queryListByCondition4Client(RouterDynamicLineReplacePlanListReq req) {
        if(log.isInfoEnabled()){
            log.info("RouterDynamicLineReplacePlanServiceImpl.queryListByCondition4Client param: {}", JsonHelper.toJson(req));
        }
        Result<PageData<RouterDynamicLineReplacePlanVo>> result = Result.success();
        List<RouterDynamicLineReplacePlanVo> dataList = new ArrayList<>();
        PageData<RouterDynamicLineReplacePlanVo> pageData = new PageData<>(req.getPageNumber(), req.getPageSize(), (long) 0, dataList);
        result.setData(pageData);
        try {
            // step 校验参数
            final Result<Void> checkResult = this.checkParam4queryListByCondition4Client(req);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            // step 拼接查询参数
            final RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery = new RouterDynamicLineReplacePlanQuery();
            routerDynamicLineReplacePlanQuery.setStartSiteId(req.getCurrentOperate().getSiteCode());
            routerDynamicLineReplacePlanQuery.setEffectTime(new Date());
            routerDynamicLineReplacePlanQuery.setYn(Constants.YN_YES);
            final Long total = routerDynamicLineReplacePlanDao.queryCount(routerDynamicLineReplacePlanQuery);
            pageData.setTotal(total);

            if (total > 0) {
                final long currentTimeMillis = System.currentTimeMillis();
                final List<RouterDynamicLineReplacePlan> dataRawList = routerDynamicLineReplacePlanDao.queryList(routerDynamicLineReplacePlanQuery);

                // step 组装数据实体
                if(CollectionUtils.isNotEmpty(dataRawList)){
                    for (RouterDynamicLineReplacePlan routerDynamicLineReplacePlan : dataRawList) {
                        final RouterDynamicLineReplacePlanVo routerDynamicLineReplacePlanVo = new RouterDynamicLineReplacePlanVo();
                        this.assembleRouterDynamicLineReplacePlanVo(routerDynamicLineReplacePlan, routerDynamicLineReplacePlanVo, currentTimeMillis);
                        dataList.add(routerDynamicLineReplacePlanVo);
                    }
                    pageData.setRecords(dataList);
                }
            }

        }catch (Exception e){
            log.error("RouterDynamicLineReplacePlanServiceImpl.queryListByCondition4Client param: {}", JsonHelper.toJson(req), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4queryListByCondition4Client(RouterDynamicLineReplacePlanListReq req){
        Result<Void> result = Result.success();
        final Result<Void> checkCommonResult = this.checkParam4BaseReq(req);
        if (!checkCommonResult.isSuccess()) {
            return result.toFail(checkCommonResult.getMessage(), checkCommonResult.getCode());
        }
        if (req.getPageNumber() == null) {
            return result.toFail("参数错误, pageNumber不能为空");
        }
        if (req.getPageSize() == null) {
            return result.toFail("参数错误, pageSize不能为空");
        }
        return result;
    }

    private void assembleRouterDynamicLineReplacePlanVo(RouterDynamicLineReplacePlan routerDynamicLineReplacePlan, RouterDynamicLineReplacePlanVo routerDynamicLineReplacePlanVo, long currentTimeMillis) {
        BeanCopyUtil.copy(routerDynamicLineReplacePlan, routerDynamicLineReplacePlanVo);
        // 失效状态
        if(routerDynamicLineReplacePlan.getEnableTime().getTime() > currentTimeMillis || routerDynamicLineReplacePlan.getDisableTime().getTime() < currentTimeMillis){
            routerDynamicLineReplacePlanVo.setEnableStatus(RouterDynamicLineStatusEnum.OUT_OF_DATE.getCode());
        }
    }

    private Result<Void> checkParam4BaseReq(BaseReq req){
        Result<Void> result = Result.success();
        if(req == null){
            return result.toFail("参数错误，参数不能为空");
        }
        final CurrentOperate currentOperate = req.getCurrentOperate();
        if(currentOperate == null){
            return result.toFail("参数错误，currentOperate不能为空");
        }
        if (currentOperate.getOperatorId() == null) {
            return result.toFail("参数错误，currentOperate.operatorId不能为空");
        }
        if (currentOperate.getSiteCode() <= 0) {
            return result.toFail("参数错误，currentOperate.siteCode不合法");
        }
        final User user = req.getUser();
        if(user == null){
            return result.toFail("参数错误，user不能为空");
        }
        if (StringUtils.isBlank(user.getUserErp())) {
            return result.toFail("参数错误，user.userErp不能为空");
        }
        if (StringUtils.isBlank(user.getUserName())) {
            return result.toFail("参数错误，user.userName不能为空");
        }
        return result;
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Override
    public Result<Boolean> changeStatus4Client(RouterDynamicLineReplacePlanChangeStatusReq req) {
        if(log.isInfoEnabled()){
            log.info("RouterDynamicLineReplacePlanServiceImpl.changeStatus4Client param: {}", JsonHelper.toJson(req));
        }
        Result<Boolean> result = Result.success(true);
        try {
            // step 校验参数
            final Result<Void> checkResult = this.checkParam4changeStatus4Client(req);
            if(!checkResult.isSuccess()){
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }

            // step 查询已有数据
            final RouterDynamicLineReplacePlanQuery routerDynamicLineReplacePlanQuery = new RouterDynamicLineReplacePlanQuery();
            routerDynamicLineReplacePlanQuery.setId(req.getId());
            routerDynamicLineReplacePlanQuery.setYn(Constants.YN_YES);
            final RouterDynamicLineReplacePlan routerDynamicLineReplacePlanExist = routerDynamicLineReplacePlanDao.selectOne(routerDynamicLineReplacePlanQuery);

            // step 检查是否能更新
            if (routerDynamicLineReplacePlanExist == null) {
                return result.toFail("数据不存在，请刷新页面");
            }
            Date currentTime = new Date();
            String targetStatusStr = RouterDynamicLineStatusEnum.getEnumNameByCode(req.getEnableStatus());
            if (Objects.equals(routerDynamicLineReplacePlanExist.getEnableStatus(), req.getEnableStatus())) {
                return result.toFail(String.format("已经是%s状态，请刷新页面", targetStatusStr));
            }
            if (routerDynamicLineReplacePlanExist.getEnableTime().getTime() < currentTime.getTime() || currentTime.getTime() > routerDynamicLineReplacePlanExist.getDisableTime().getTime()) {
                return result.toFail("该数据已过生效时间范围");
            }

            // step 更新数据+写入日志
            final boolean executeFlag = routerDynamicLineReplacePlanManager.changeStatus4Client(req, routerDynamicLineReplacePlanExist);
            if(executeFlag){
                // step 主动删除缓存
                final String uniqueCacheKey = this.getUniqueCacheKey(routerDynamicLineReplacePlanExist);
                redisClientOfJy.del(uniqueCacheKey);
            }

        }catch (Exception e){
            log.error("RouterDynamicLineReplacePlanServiceImpl.changeStatus4Client param: {}", JsonHelper.toJson(req), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4changeStatus4Client(RouterDynamicLineReplacePlanChangeStatusReq req){
        Result<Void> result = Result.success();
        final Result<Void> checkCommonResult = this.checkParam4BaseReq(req);
        if (!checkCommonResult.isSuccess()) {
            return result.toFail(checkCommonResult.getMessage(), checkCommonResult.getCode());
        }
        if (req.getEnableStatus() == null) {
            return result.toFail("参数错误, status不能为空");
        }
        return result;
    }

    private String getUniqueCacheKey(RouterDynamicLineReplacePlan routerDynamicLineReplacePlan) {
        return String.format(CacheKeyConstants.CACHE_KEY_ROUTER_DYNAMIC_LINE_REPLACE, String.format("%s_%s_%s", routerDynamicLineReplacePlan.getStartSiteId(), routerDynamicLineReplacePlan.getOldEndSiteId(), routerDynamicLineReplacePlan.getNewEndSiteId()));
    }
}
