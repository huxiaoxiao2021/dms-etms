package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.DriverViolationReportingAddRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.DriverViolationReportingRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.DriverViolationReportingDto;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.DriverViolationReportingResponse;
import com.jd.bluedragon.distribution.external.domain.JyDriverViolationReportingDto;
import com.jd.bluedragon.distribution.external.domain.QueryDriverViolationReportingReq;
import com.jd.bluedragon.distribution.jy.dao.send.JyDriverViolationReportingDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.dto.task.DriverViolationReportingQualityMq;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.send.JyDriverViolationReportingEntity;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.TimeUtils;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;

/**
 * @author pengchong28
 * @description 司机违规举报服务实现
 * @date 2024/4/12
 */
@Service
@Slf4j
public class JyDriverViolationReportingServiceImpl implements IJyDriverViolationReportingService {
    @Autowired
    private JyDriverViolationReportingDao driverViolationReportingDao;

    @Autowired
    private JyBizTaskSendVehicleDao jyBizTaskSendVehicleDao;

    @Autowired
    JimDbLock jimDbLock;

    @Autowired
    @Qualifier(value = "driverViolationReportingQualityProducer")
    private DefaultJMQProducer driverViolationReportingQualityProducer;

    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    /**
     * 司机违规举报最大时间
     */
    private static final Integer MAXIMUM_REPORTING_TIME = 4;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyDriverViolationReportingServiceImpl.checkViolationReporting", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {
        JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<DriverViolationReportingDto> checkViolationReporting(DriverViolationReportingRequest request) {
        if (log.isInfoEnabled()) {
            log.info("JyDriverViolationReportingServiceImpl.checkViolationReporting 入参：{}",
                JsonHelper.toJson(request));
        }
        InvokeResult<DriverViolationReportingDto> invokeResult = new InvokeResult<>();
        // 参数校验
        InvokeResult<DriverViolationReportingDto> checkedRequest = checkRequest(request, invokeResult);
        if (checkedRequest != null) {
            return checkedRequest;
        }
        // 进行数据查询
        getJyDriverViolationReportingEntity(request, invokeResult);
        // 已封车任务，封车时间大于4小时，不能进行举报
        if (Objects.equals(request.getTaskSendStatus(), JyBizTaskSendStatusEnum.SEALED.getCode())
            && !invokeResult.getData().getQueryFlag()) {
            // 根据bizId查询已封车发车任务
            JyBizTaskSendVehicleEntity sendVehicle = jyBizTaskSendVehicleDao.findByBizId(request.getBizId());
            if (Objects.nonNull(sendVehicle) && Objects.nonNull(sendVehicle.getLastSealCarTime())) {
                double hours = DateHelper.betweenHours(sendVehicle.getLastSealCarTime(), new Date());
                if (hours > MAXIMUM_REPORTING_TIME) {
                    invokeResult.error("超过4小时禁止举报");
                    return invokeResult;
                }
            }
        }
        invokeResult.success();
        return invokeResult;
    }

    /**
     * 该方法用于检查司机违规报告请求的有效性    。
     * 如果请求对象为空或者请求中的发车任务ID（bizId）为空，将会设置相应的参数错误信息到调用结果中    。
     *
     * @param request      司机违规报告的请求对象，用于校验其内容的有效性
     * @param invokeResult 用于存储校验结果的调用结果对象，如果校验不通过将被填充错误信息
     * @return 如果校验通过，返回null；如果校验不通过，返回包含错误信息的调用结果对象
     * @throws IllegalArgumentException 当请求对象为空或请求中的发车任务ID为空时，将通过调用结果对象抛出参数错误
     */
    private static InvokeResult<DriverViolationReportingDto> checkRequest(DriverViolationReportingRequest request,
        InvokeResult<DriverViolationReportingDto> invokeResult) {
        if (Objects.isNull(request)) {
            invokeResult.parameterError("请求对象不能为空");
            return invokeResult;
        }
        if (StringUtils.isBlank(request.getBizId())) {
            invokeResult.parameterError("发车任务bizId不能为空");
            return invokeResult;
        }
        if (Objects.isNull(request.getTaskSendStatus())) {
            invokeResult.parameterError("发车任务状态不能为空");
            return invokeResult;
        }
        return null;
    }

    private static InvokeResult<DriverViolationReportingResponse> checkRequestBase(
        QueryDriverViolationReportingReq request, InvokeResult<DriverViolationReportingResponse> invokeResult) {
        if (Objects.isNull(request)) {
            invokeResult.parameterError("请求对象不能为空");
            return invokeResult;
        }
        if (Objects.isNull(request.getBizId())) {
            invokeResult.parameterError("发车任务bizId不能为空");
            return invokeResult;
        }
        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyDriverViolationReportingServiceImpl.submitViolationReporting", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {
        JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Void> submitViolationReporting(DriverViolationReportingAddRequest request) {
        if (log.isInfoEnabled()) {
            log.info("JyDriverViolationReportingServiceImpl.submitViolationReporting 入参：{}",
                JsonHelper.toJson(request));
        }
        InvokeResult<Void> invokeResult = new InvokeResult<>();
        // 参数校验
        InvokeResult<Void> checkedRequest = checkRequest(request, invokeResult);
        if (checkedRequest != null) {
            return checkedRequest;
        }
        InvokeResult<Void> result = checkAuthority(request, invokeResult);
        if (result != null) {
            return result;
        }

        String driverViolationReportingLockKey =
            String.format(Constants.JY_DRIVER_VIOLATION_REPORTING_LOCK_PREFIX, request.getBizId());
        if (!jimDbLock.lock(driverViolationReportingLockKey, Constants.NUMBER_ONE.toString(), LOCK_EXPIRE,
            TimeUnit.SECONDS)) {
            invokeResult.error("其他用户正在举报该任务，请稍后再试");
            return invokeResult;
        }
        JyDriverViolationReportingEntity entity;
        try {
            entity = getJyDriverViolationReportingEntity(request);
            int i = driverViolationReportingDao.insertSelective(entity);
            if (i <= Constants.NUMBER_ZERO) {
                invokeResult.error("提交司机违规数据失败");
                return invokeResult;
            }
        } catch (Exception e) {
            log.error("JyDriverViolationReportingServiceImpl.submitViolationReporting 提交司机违规数据失败", e);
            invokeResult.error("提交司机违规数据失败");
            return invokeResult;
        } finally {
            jimDbLock.releaseLock(driverViolationReportingLockKey, request.getBizId());
        }
        invokeResult.success();
        // 发送质控mq
        sendQualityMq(request, entity);
        return invokeResult;
    }

    /**
     * 发送质控消息
     * @param request 驾驶员违规上报请求对象
     * @param entity 驾驶员违规上报实体对象
     * @throws Exception 发送质控消息可能抛出异常
     */
    private void sendQualityMq(DriverViolationReportingAddRequest request, JyDriverViolationReportingEntity entity) {
        if (Objects.isNull(entity)) {
            log.error("JyDriverViolationReportingServiceImpl.sendQualityMq 待上报的数据未空");
            return;
        }
        DriverViolationReportingQualityMq qualityMq = null;
        try {
            String key = String.format(Constants.JY_DRIVER_VIOLATION_REPORTING_KEY_PREFIX, request.getBizId());
            // 获取封车编码，过期时间10个小时
            String sealCarCode = cacheService.get(key);
            if (StringUtils.isBlank(sealCarCode)) {
                log.error("JyDriverViolationReportingServiceImpl.sendQualityMq 获取封车编码为空，key：{}", key);
                return;
            }
            JyBizTaskSendVehicleDetailEntity detailEntity =
                jyBizTaskSendVehicleDetailService.findBySendVehicleBizId(request.getBizId());
            if (Objects.isNull(detailEntity)) {
                log.error(
                    "JyDriverViolationReportingServiceImpl.sendQualityMq 获取发车任务明细为空，sendVehicleBizId:{}",
                    request.getBizId());
                return;
            }
            qualityMq = getDriverViolationReportingQualityMq(request, entity, sealCarCode, detailEntity);

            driverViolationReportingQualityProducer.sendOnFailPersistent(detailEntity.getTransWorkItemCode(),
                JsonHelper.toJson(qualityMq));
        } catch (Exception e) {
            log.error("同步司机违规数据到质控消息mq异常[errMsg={}]，mqBody={}", e.getMessage(),
                JsonHelper.toJson(qualityMq), e);
        } finally {
            if (log.isInfoEnabled()) {
                log.info("同步司机违规数据到质控，msg={}", JsonHelper.toJson(qualityMq));
            }
        }
    }

    /**
     * 获取司机违章上报质量消息队列
     * @param request 司机违章上报添加请求
     * @param entity JyDriverViolationReportingEntity实体
     * @param sealCarCode 封存车辆代码
     * @param detailEntity JyBizTaskSendVehicleDetailEntity实体
     * @return 质量消息队列
     */
    private static DriverViolationReportingQualityMq getDriverViolationReportingQualityMq(
        DriverViolationReportingAddRequest request, JyDriverViolationReportingEntity entity, String sealCarCode,
        JyBizTaskSendVehicleDetailEntity detailEntity) {
        DriverViolationReportingQualityMq qualityMq = new DriverViolationReportingQualityMq();
        if (Objects.nonNull(entity.getCreateTime())){
            qualityMq.setReportingDate(TimeUtils.format(entity.getCreateTime(), TimeUtils.yyyy_MM_dd_HH_mm_ss));
        }
        qualityMq.setSealCarCode(sealCarCode);
        qualityMq.setTransWorkItemCode(detailEntity.getTransWorkItemCode());
        qualityMq.setEndSiteId(detailEntity.getEndSiteId());
        qualityMq.setReportingSiteCode(entity.getSiteCode());
        qualityMq.setImgUrl(request.getImgUrlList());
        qualityMq.setVideoUrl(request.getVideoUlr());
        return qualityMq;
    }

    /**
     * 检查权限
     *
     * @param request      驱动程序违规举报添加请求
     * @param invokeResult 调用结果
     * @return 调用结果
     * @throws Exception 异常信息
     */
    private InvokeResult<Void> checkAuthority(DriverViolationReportingAddRequest request,
        InvokeResult<Void> invokeResult) {
        JyBizTaskSendVehicleEntity sendVehicle = jyBizTaskSendVehicleDao.findByBizId(request.getBizId());
        if (Objects.nonNull(sendVehicle) && Objects.equals(sendVehicle.getVehicleStatus(),
            JyBizTaskSendStatusEnum.SEALED.getCode()) && Objects.nonNull(sendVehicle.getLastSealCarTime())) {
            double hours = DateHelper.betweenHours(sendVehicle.getLastSealCarTime(), new Date());
            if (hours > MAXIMUM_REPORTING_TIME) {
                invokeResult.error("超过4小时禁止举报");
                return invokeResult;
            }
        }
        return null;
    }

    /**
     * 根据驾驶员违章举报请求信息创建并初始化一个驾驶员违章举报实体对象
     *
     * @param request 包含违章举报信息的请求对象
     * @return JyDriverViolationReportingEntity 初始化后的驾驶员违章举报实体对象
     */
    private static JyDriverViolationReportingEntity getJyDriverViolationReportingEntity(
        DriverViolationReportingAddRequest request) {
        JyDriverViolationReportingEntity jyDriverViolationReportingEntity = new JyDriverViolationReportingEntity();
        jyDriverViolationReportingEntity.setBizId(request.getBizId());
        String urlString = String.join(",", request.getImgUrlList());
        jyDriverViolationReportingEntity.setImgUrl(urlString);
        jyDriverViolationReportingEntity.setVideoUrl(request.getVideoUlr());
        jyDriverViolationReportingEntity.setCreateUserErp(request.getUser().getUserErp());
        jyDriverViolationReportingEntity.setCreateUserName(request.getUser().getUserName());
        jyDriverViolationReportingEntity.setCreateTime(new Date());
        jyDriverViolationReportingEntity.setUpdateUserErp(request.getUser().getUserErp());
        jyDriverViolationReportingEntity.setUpdateUserName(request.getUser().getUserName());
        jyDriverViolationReportingEntity.setUpdateTime(new Date());
        jyDriverViolationReportingEntity.setSiteCode((long)request.getCurrentOperate().getSiteCode());
        return jyDriverViolationReportingEntity;
    }

    private static InvokeResult<Void> checkRequest(DriverViolationReportingAddRequest request,
        InvokeResult<Void> invokeResult) {
        if (Objects.isNull(request)) {
            invokeResult.parameterError("请求对象不能为空");
            return invokeResult;
        }
        if (StringUtils.isBlank(request.getBizId())) {
            invokeResult.parameterError("发车任务bizId不能为空");
            return invokeResult;
        }
        if (CollectionUtils.isEmpty(request.getImgUrlList())) {
            invokeResult.parameterError("图片url集合不能为空");
            return invokeResult;
        }
        if (StringUtils.isBlank(request.getVideoUlr())) {
            invokeResult.parameterError("视频url不能为空");
            return invokeResult;
        }
        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyDriverViolationReportingServiceImpl.queryViolationReporting", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {
        JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<DriverViolationReportingResponse> queryViolationReporting(
        QueryDriverViolationReportingReq request) {
        if (log.isInfoEnabled()) {
            log.info("JyDriverViolationReportingServiceImpl.queryViolationReporting 入参：{}",
                JsonHelper.toJson(request));
        }
        InvokeResult<DriverViolationReportingResponse> invokeResult = new InvokeResult<>();
        // 参数校验
        InvokeResult<DriverViolationReportingResponse> checkedRequest = checkRequestBase(request, invokeResult);
        if (checkedRequest != null) {
            return checkedRequest;
        }
        JyDriverViolationReportingEntity entity = null;
        try {
            entity = driverViolationReportingDao.findByBizId(request.getBizId());
        } catch (Exception e) {
            log.error("JyDriverViolationReportingServiceImpl.queryViolationReporting 查询司机违规举报异常", e);
            invokeResult.error("查询司机违规举报异常");
            return invokeResult;
        }
        DriverViolationReportingResponse response = new DriverViolationReportingResponse();
        if (Objects.nonNull(entity)) {
            JyDriverViolationReportingDto reportingDto = getJyDriverViolationReportingDtos(entity);
            response.setDto(reportingDto);
        }
        invokeResult.success();
        invokeResult.setData(response);
        return invokeResult;
    }

    /**
     * 获取交运驾驶员违章举报数据传输对象列表
     *
     * @param entity 交运驾驶员违章举报实体列表
     * @return dtos 交运驾驶员违章举报数据传输对象列表
     */
    private static JyDriverViolationReportingDto getJyDriverViolationReportingDtos(
        JyDriverViolationReportingEntity entity) {
        JyDriverViolationReportingDto reportingDto = new JyDriverViolationReportingDto();
        reportingDto.setBizId(entity.getBizId());
        reportingDto.setVideoUrl(entity.getVideoUrl());
        List<String> imgUrlList = Arrays.asList(entity.getImgUrl().split(","));
        reportingDto.setImgUrlList(imgUrlList);
        reportingDto.setCreateTime(entity.getCreateTime());
        reportingDto.setSiteCode(entity.getSiteCode());

        return reportingDto;
    }

    /**
     * 根据业务ID查询驾驶员违章报告实体，并将其数据转换后设置到调用结果中
     *
     * @param request      请求对象，包含查询所需的业务ID
     * @param invokeResult 调用结果对象，用于封装查询结果和数据状态
     * @throws IllegalArgumentException 如果请求对象或调用结果对象为空
     */
    private void getJyDriverViolationReportingEntity(DriverViolationReportingRequest request,
        InvokeResult<DriverViolationReportingDto> invokeResult) {
        JyDriverViolationReportingEntity violationReporting =
            driverViolationReportingDao.findByBizId(request.getBizId());
        DriverViolationReportingDto driverViolationReportingDto = new DriverViolationReportingDto();
        invokeResult.success();
        if (Objects.isNull(violationReporting)) {
            driverViolationReportingDto.setQueryFlag(Boolean.FALSE);
        } else {
            driverViolationReportingDto.setQueryFlag(Boolean.TRUE);
            BeanCopyUtil.copy(violationReporting, driverViolationReportingDto);
            List<String> imgUrlList = Arrays.asList(violationReporting.getImgUrl().split(","));
            driverViolationReportingDto.setImgUrlList(imgUrlList);
        }
        invokeResult.setData(driverViolationReportingDto);
    }
}
