package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptDisposeRecord;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionInterceptDetailKvDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetailKv;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailKvQuery;
import com.jd.bluedragon.distribution.jy.exception.query.PackageWithInterceptTypeLastHandleSiteQuery;
import com.jd.bluedragon.distribution.jy.service.exception.JyBusinessInterceptExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.helper.JyExceptionBusinessInterceptTaskConstants;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拦截异常任务相关实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-14 17:30:22 周日
 */
@Slf4j
@Service
public class JyBusinessInterceptExceptionServiceImpl implements JyBusinessInterceptExceptionService {

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private JyExceptionInterceptDetailKvDao jyExceptionInterceptDetailKvDao;

    // 拦截任务触发消息校验数据返回码-无网格码
    private final int businessParamCheckNoWorkStationGrid4ConsumeDmsBusinessInterceptReportCode = 101;

    /**
     * 消费拦截报表数据
     *
     * @param businessInterceptReport 拦截记录
     * @return 消费结果
     * @author fanggang7
     * @time 2024-01-14 17:37:50 周日
     */
    @Override
    public Result<Boolean> consumeDmsBusinessInterceptReport(BusinessInterceptReport businessInterceptReport) {
        if(log.isInfoEnabled()){
            log.info("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptReport param: {}", JsonHelper.toJson(businessInterceptReport));
        }
        Result<Boolean> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4ConsumeDmsBusinessInterceptReport(businessInterceptReport);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 业务条件判断
            final Result<Void> businessCheckResult = this.checkBusinessParam4ConsumeDmsBusinessInterceptReport(businessInterceptReport);
            if (!businessCheckResult.isSuccess()) {
                if(businessCheckResult.getCode() == businessParamCheckNoWorkStationGrid4ConsumeDmsBusinessInterceptReportCode){
                    log.info("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptReport no operateGridInfo {}", JsonHelper.toJson(businessInterceptReport));
                    return result.toSuccess();
                }
                return result.toFail(businessCheckResult.getMessage(), businessCheckResult.getCode());
            }

            // 3. 执行逻辑
            jyExceptionService.handleDmsBusinessInterceptReportUpload(businessInterceptReport);

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptReport exception param {} exception {}", JsonHelper.toJson(businessInterceptReport), e.getMessage(), e);
        }
        return result;
    }

    /**
     * 基础参数校验
     */
    private Result<Void> checkParam4ConsumeDmsBusinessInterceptReport(BusinessInterceptReport businessInterceptReport){
        Result<Void> result = Result.success();
        if (businessInterceptReport.getSiteCode() == null) {
            return result.toFail("数据异常，siteCode为空");
        }
        if (StringUtils.isBlank(businessInterceptReport.getPackageCode())) {
            return result.toFail("数据异常，packageCode为空");
        }
        return result;
    }

    /**
     * 业务条件校验
     */
    private Result<Void> checkBusinessParam4ConsumeDmsBusinessInterceptReport(BusinessInterceptReport businessInterceptReport){
        Result<Void> result = Result.success();
        if (StringUtils.isBlank(businessInterceptReport.getOperateWorkStationGridKey()) &&
                StringUtils.isBlank(businessInterceptReport.getOperateWorkGridKey())) {
            return result.toFail("操作所在网格信息为空，不处理", businessParamCheckNoWorkStationGrid4ConsumeDmsBusinessInterceptReportCode);
        }
        return result;
    }

    /**
     * 消费拦截处理消息
     *
     * @param businessInterceptDisposeRecord 拦截处理
     * @return 消费结果
     * @author fanggang7
     * @time 2024-01-14 17:38:09 周日
     */
    @Override
    public Result<Boolean> consumeDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord) {
        if(log.isInfoEnabled()){
            log.info("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptDispose param: {}", JsonHelper.toJson(businessInterceptDisposeRecord));
        }
        Result<Boolean> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4ConsumeDmsBusinessInterceptDispose(businessInterceptDisposeRecord);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 业务条件校验
            final Result<Void> businessCheckResult = this.checkBusinessParam4ConsumeDmsBusinessInterceptDispose(businessInterceptDisposeRecord);
            if (!businessCheckResult.isSuccess()) {
                return result.toFail(businessCheckResult.getMessage(), businessCheckResult.getCode());
            }

            jyExceptionService.handleDmsBusinessInterceptDispose(businessInterceptDisposeRecord);

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptDispose exception param {} exception {}", JsonHelper.toJson(businessInterceptDisposeRecord), e.getMessage(), e);
        }
        return result;
    }

    /**
     * 基础参数校验
     */
    private Result<Void> checkParam4ConsumeDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord){
        Result<Void> result = Result.success();
        if (businessInterceptDisposeRecord.getSiteCode() == null) {
            return result.toFail("数据异常，siteCode为空");
        }
        if (StringUtils.isBlank(businessInterceptDisposeRecord.getPackageCode())) {
            return result.toFail("数据异常，packageCode为空");
        }
        return result;
    }

    /**
     * 业务条件校验
     */
    private Result<Void> checkBusinessParam4ConsumeDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord){
        Result<Void> result = Result.success();
        return result;
    }

    private String getInterceptDetailKvKeyword(String packageCode, Integer interceptType){
        return String.format(JyExceptionBusinessInterceptTaskConstants.PACKAGE_CODE_ASSOCIATE_SITE_KEY, packageCode, interceptType);
    }

    /**
     * 根据包裹和拦截类型查询上次处理过的场地处理记录
     *
     * @param query 查询入参
     * @return 结果列表
     */
    @Override
    public Result<List<JyBizTaskExceptionEntity>> queryPackageWithInterceptTypeLastHandleSiteRecord(PackageWithInterceptTypeLastHandleSiteQuery query) {
        Result<List<JyBizTaskExceptionEntity>> result = Result.success();
        try {
            JyExceptionInterceptDetailKvQuery jyExceptionInterceptDetailKvQuery = new JyExceptionInterceptDetailKvQuery();
            jyExceptionInterceptDetailKvQuery.setKeyword(this.getInterceptDetailKvKeyword(query.getPackageCode(), query.getInterceptType()));
            final List<JyExceptionInterceptDetailKv> jyExceptionInterceptDetailKvs = jyExceptionInterceptDetailKvDao.queryList(jyExceptionInterceptDetailKvQuery);
            if (CollectionUtils.isEmpty(jyExceptionInterceptDetailKvs)) {
                return result;
            }
        } catch (Exception e) {
            result.toFail("查询异常");
            log.error("querySiteLastHandleRecord exception {}", JsonHelper.toJson(query), e);
        }
        return result;
    }
}
