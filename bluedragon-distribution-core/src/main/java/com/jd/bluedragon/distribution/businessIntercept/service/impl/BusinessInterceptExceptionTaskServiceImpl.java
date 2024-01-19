package com.jd.bluedragon.distribution.businessIntercept.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptDisposeRecord;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptExceptionTaskService;
import com.jd.bluedragon.distribution.jy.service.exception.JyBusinessInterceptExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.JyExceptionBusinessInterceptDetailService;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 拦截异常任务相关接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-14 17:30:22 周日
 */
@Slf4j
@Service
public class BusinessInterceptExceptionTaskServiceImpl implements IBusinessInterceptExceptionTaskService {

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private JyBusinessInterceptExceptionService jyBusinessInterceptExceptionService;

    @Autowired
    private JyExceptionBusinessInterceptDetailService jyExceptionBusinessInterceptDetailService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    // 拦截任务触发消息校验数据返回码-无网格码
    private final int businessParamCheck4ConsumeDmsBusinessInterceptReportCode = 101;

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
                if(checkResult.getCode() == businessParamCheck4ConsumeDmsBusinessInterceptReportCode){
                    log.info("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptReport no operatePositionCode {}", JsonHelper.toJson(businessInterceptReport));
                    return result.toSuccess();
                }
                return result.toFail(businessCheckResult.getMessage(), businessCheckResult.getCode());
            }

            // 3. 执行逻辑
            // 3.1 插入任务
            final ExpUploadScanReq expUploadScanReq = new ExpUploadScanReq();
            expUploadScanReq.setType(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode());
            expUploadScanReq.setSource(JyExpSourceEnum.OPERATE_INTERCEPT.getCode());
            expUploadScanReq.setBarCode(businessInterceptReport.getPackageCode());
            expUploadScanReq.setUserErp(businessInterceptReport.getCreateUser());
            expUploadScanReq.setPositionCode(businessInterceptReport.getOperatePositionCode());
            expUploadScanReq.setSiteId(businessInterceptReport.getSiteId().intValue());
            final JdCResponse<Object> addTaskResult = jyExceptionService.uploadScan(expUploadScanReq);
            // 3.2 插入明细

        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptReport exception param {} exception {}", JsonHelper.toJson(businessInterceptReport), e.getMessage(), e);
        }
        return result;
    }

    /**
     * 基础参数校验
     */
    Result<Void> checkParam4ConsumeDmsBusinessInterceptReport(BusinessInterceptReport businessInterceptReport){
        Result<Void> result = Result.success();
        return result;
    }

    /**
     * 业务条件校验
     */
    Result<Void> checkBusinessParam4ConsumeDmsBusinessInterceptReport(BusinessInterceptReport businessInterceptReport){
        Result<Void> result = Result.success();
        if (StringUtils.isBlank(businessInterceptReport.getOperatePositionCode())) {
            return result.toFail("操作所在网格为空，不处理", businessParamCheck4ConsumeDmsBusinessInterceptReportCode);
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

            // 3. 执行逻辑
            // 3.1 更新任务状态
            // 3.2 更新明细
        } catch (Exception e) {
            result.toFail("接口异常");
            log.error("BusinessInterceptExceptionTaskServiceImpl.consumeDmsBusinessInterceptDispose exception param {} exception {}", JsonHelper.toJson(businessInterceptDisposeRecord), e.getMessage(), e);
        }
        return result;
    }

    /**
     * 基础参数校验
     */
    Result<Void> checkParam4ConsumeDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord){
        Result<Void> result = Result.success();
        return result;
    }

    /**
     * 业务条件校验
     */
    Result<Void> checkBusinessParam4ConsumeDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord){
        Result<Void> result = Result.success();
        return result;
    }
}
