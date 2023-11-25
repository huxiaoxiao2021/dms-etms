package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckIssueMQ;
import com.jd.bluedragon.distribution.spotcheck.entity.SpotCheckAppealAppendixResult;
import com.jd.bluedragon.distribution.spotcheck.entity.SpotCheckAppealDto;
import com.jd.bluedragon.distribution.spotcheck.entity.SpotCheckAppealResult;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckAppealJsfService;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckAppealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("spotCheckAppealJsfService")
public class SpotCheckAppealJsfServiceImpl implements SpotCheckAppealJsfService {

    private final Logger logger = LoggerFactory.getLogger(SpotCheckAppealJsfServiceImpl.class);

    @Autowired
    private SpotCheckAppealService spotCheckAppealService;

    @Autowired
    private JyAttachmentDetailService jyAttachmentDetailService;

    @Autowired
    @Qualifier("spotCheckIssueProducer")
    private DefaultJMQProducer spotCheckIssueProducer;


    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealJsfServiceImpl.findByCondition", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<PagerResult<SpotCheckAppealResult>> findByCondition(SpotCheckAppealDto request) {
        logger.info("findByCondition|根据条件查询设备抽检申诉记录列表:request={}", JsonHelper.toJson(request));
        Response<PagerResult<SpotCheckAppealResult>> response = new Response<>();
        PagerResult<SpotCheckAppealResult> pagerResult = new PagerResult<>();
        response.setData(pagerResult);
        response.toSucceed();
        // 参数校验
        SpotCheckAppealEntity params = new SpotCheckAppealEntity();
        try {
            BeanUtils.copyProperties(request, params);
            // 查询总数
            int total = spotCheckAppealService.countByCondition(params);
            pagerResult.setTotal(total);
            if (total <= Constants.CONSTANT_NUMBER_ZERO) {
                logger.warn("findByCondition|根据条件查询设备抽检申诉记录总数返回0:request={}", JsonHelper.toJson(request));
                return response;
            }
            // 查询详细
            List<SpotCheckAppealEntity> list = spotCheckAppealService.findByCondition(params);
            if (CollectionUtils.isEmpty(list)) {
                logger.warn("findByCondition|根据条件查询设备抽检申诉记录列表返回空:request={}", JsonHelper.toJson(request));
                return response;
            }
            List<SpotCheckAppealResult> resultList = new ArrayList<>(list.size());
            // 转换对象
            for (SpotCheckAppealEntity entity : list) {
                SpotCheckAppealResult result = new SpotCheckAppealResult();
                BeanUtils.copyProperties(entity, result);
                resultList.add(result);
            }
            pagerResult.setRows(resultList);
            return response;
        } catch (Exception e) {
            logger.error("findByCondition|根据条件查询设备抽检申诉记录明细出现异常:request={},e=", JsonHelper.toJson(request), e);
            response.toError("服务端异常");
            return response;
        }
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealJsfServiceImpl.updateById", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Void> updateById(SpotCheckAppealDto request) {
        logger.info("updateById|根据ID更新设备抽检申诉记录:request={}", JsonHelper.toJson(request));
        Response<Void> response = new Response<>();
        response.toSucceed();
        try {
            SpotCheckAppealEntity params = new SpotCheckAppealEntity();
            BeanUtils.copyProperties(request, params);
            // 更新确认状态
            spotCheckAppealService.updateById(params);
            // 根据ID查询设备抽检申诉核对记录
            SpotCheckAppealEntity entity = spotCheckAppealService.findById(params);
            if (entity == null) {
                logger.warn("updateById|根据ID查询设备抽检申诉核对记录返回空:request={}", JsonHelper.toJson(request));
                response.toWarn("根据ID查询设备抽检申诉核对记录返回空");
                return response;
            }
            // 通知称重再造系统
            notifyRemakeSystem(entity);
            return response;
        } catch (Exception e) {
            logger.error("updateById|根据ID更新设备抽检申诉记录明细出现异常:request={},e=", JsonHelper.toJson(request), e);
            response.toError("服务端异常");
            return response;
        }
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealJsfServiceImpl.batchUpdateByIds", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Void> batchUpdateByIds(SpotCheckAppealDto request) {
        logger.info("batchUpdateByIds|根据ID列表批量更新设备抽检申诉记录:request={}", JsonHelper.toJson(request));
        Response<Void> response = new Response<>();
        response.toSucceed();
        try {
            SpotCheckAppealEntity params = new SpotCheckAppealEntity();
            BeanUtils.copyProperties(request, params);
            // 批量更新确认状态
            spotCheckAppealService.batchUpdateByIds(params);
            // 根据ID查询设备抽检申诉核对记录
            List<SpotCheckAppealEntity> entityList = spotCheckAppealService.batchFindByIds(params);
            if (CollectionUtils.isEmpty(entityList)) {
                logger.warn("batchUpdateByIds|根据ID列表批量查询设备抽检申诉核对记录返回空:request={}", JsonHelper.toJson(request));
                response.toWarn("根据ID列表批量查询设备抽检申诉核对记录返回空");
                return response;
            }
            // 通知称重再造系统
            for (SpotCheckAppealEntity entity : entityList) {
                notifyRemakeSystem(entity);
            }
            return response;
        } catch (Exception e) {
            logger.error("batchUpdateByIds|根据ID列表批量更新设备抽检申诉记录明细出现异常:request={},e=", JsonHelper.toJson(request), e);
            response.toError("服务端异常");
            return response;
        }
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealJsfServiceImpl.findAppendixByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<SpotCheckAppealAppendixResult>> findAppendixByBizId(SpotCheckAppealDto request) {
        logger.info("findAppendixByBizId|根据BizId查询设备抽检申诉记录附件:request={}", JsonHelper.toJson(request));
        Response<List<SpotCheckAppealAppendixResult>> response = new Response<>();
        response.toSucceed();
        try {
            // 组装参数
            JyAttachmentDetailQuery params = new JyAttachmentDetailQuery();
            params.setBizType(JyAttachmentBizTypeEnum.DEVICE_SPOT_APPEAL.getCode());
            params.setBizId(request.getBizId());
            params.setSiteCode(Integer.valueOf(request.getStartSiteCode()));
            // 查询附件表
            List<JyAttachmentDetailEntity> attachmentList = jyAttachmentDetailService.queryDataListByCondition(params);
            if (CollectionUtils.isEmpty(attachmentList)) {
                logger.warn("findAppendixByBizId|根据BizId查询设备抽检申诉记录附件返回空:request={}", JsonHelper.toJson(request));
                return response;
            }
            List<SpotCheckAppealAppendixResult> resultList = new ArrayList<>();
            List<String> pictureList = new ArrayList<>();
            List<String> videoList = new ArrayList<>();
            // 组装结果
            for (JyAttachmentDetailEntity entity : attachmentList) {
                if (entity.getAttachmentType() != null) {
                    if (entity.getAttachmentType().equals(JyAttachmentTypeEnum.PICTURE.getCode())) {
                        pictureList.add(entity.getAttachmentUrl());
                    } else if (entity.getAttachmentType().equals(JyAttachmentTypeEnum.VIDEO.getCode())) {
                        videoList.add(entity.getAttachmentUrl());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(pictureList)) {
                SpotCheckAppealAppendixResult appendixResult = new SpotCheckAppealAppendixResult();
                appendixResult.setCode(JyAttachmentTypeEnum.PICTURE.getCode());
                appendixResult.setAppendixUrls(pictureList);
                resultList.add(appendixResult);
            }
            if (CollectionUtils.isNotEmpty(videoList)) {
                SpotCheckAppealAppendixResult appendixResult = new SpotCheckAppealAppendixResult();
                appendixResult.setCode(JyAttachmentTypeEnum.VIDEO.getCode());
                appendixResult.setAppendixUrls(videoList);
                resultList.add(appendixResult);
            }
            response.setData(resultList);
            return response;
        } catch (Exception e) {
            logger.error("findAppendixByBizId|根据BizId查询设备抽检申诉记录附件出现异常:request={},e=", JsonHelper.toJson(request), e);
            response.toError("服务端异常");
            return response;
        }
    }


    /**
     * 通知称重再造系统
     */
    private void notifyRemakeSystem(SpotCheckAppealEntity spotCheckDto) {
        SpotCheckIssueMQ spotCheckIssueMQ = new SpotCheckIssueMQ();
        // 流程发起系统
        spotCheckIssueMQ.setFlowSystem(SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        // 流程发起环节
        spotCheckIssueMQ.setInitiationLink(String.valueOf(SpotCheckConstants.DMS_SPOT_CHECK_ISSUE));
        // 数据来源系统
        spotCheckIssueMQ.setSysSource(String.valueOf(SpotCheckConstants.SYS_DMS_DWS));
        // 数据操作类型
        spotCheckIssueMQ.setOperateType(Constants.CONSTANT_NUMBER_TWO);
        // 流程唯一标识
        spotCheckIssueMQ.setFlowId(spotCheckIssueMQ.getFlowSystem() + Constants.UNDERLINE_FILL + spotCheckDto.getWaybillCode()
                + Constants.UNDERLINE_FILL + spotCheckDto.getStartSiteCode() + Constants.UNDERLINE_FILL + SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode());
        // 运单号
        spotCheckIssueMQ.setWaybillCode(spotCheckDto.getWaybillCode());
        // 如果同意申诉，则状态为判责无效
        if (Constants.NUMBER_ONE.equals(spotCheckDto.getConfirmStatus())) {
            spotCheckIssueMQ.setStatus(SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_INVALID.getCode());
            // 如果驳回申诉，则状态为判责有效
        } else if (String.valueOf(Constants.CONSTANT_NUMBER_TWO).equals(String.valueOf(spotCheckDto.getConfirmStatus()))) {
            spotCheckIssueMQ.setStatus(SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_EFFECT.getCode());
        }
        // 责任类型
        spotCheckIssueMQ.setDutyType(spotCheckDto.getDutyType());
        // 发起人账号
        spotCheckIssueMQ.setStartStaffAccount(spotCheckDto.getUpdateUserErp());
        // 发起人类型
        spotCheckIssueMQ.setStartStaffType(Constants.CONSTANT_NUMBER_ONE);
        // 核对(被举报)重量 单位为kg
        spotCheckIssueMQ.setConfirmWeight(spotCheckDto.getConfirmWeight());
        // 核对(被举报)体积 单位为cm3
        spotCheckIssueMQ.setConfirmVolume(spotCheckDto.getConfirmVolume());
        // 复核(举报)重量 单位为kg
        spotCheckIssueMQ.setReConfirmWeight(spotCheckDto.getReConfirmWeight());
        // 复核(举报)体积 单位为cm3
        spotCheckIssueMQ.setReConfirmVolume(spotCheckDto.getReConfirmVolume());
        // 差异标准
        spotCheckIssueMQ.setStanderDiff(spotCheckDto.getStanderDiff());
        // 流程发起时间
        spotCheckIssueMQ.setStartTime(new Date());
        // 流程状态变更时间
        spotCheckIssueMQ.setStatusUpdateTime(new Date());
        // 判责结果描述
        spotCheckIssueMQ.setComment(spotCheckDto.getRejectReason());

        if(logger.isInfoEnabled()){
            logger.info("下发运单号:{}的设备抽检申诉核对数据至称重再造流程,明细如下:{}", spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        }
        spotCheckIssueProducer.sendOnFailPersistent(SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode() + spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
    }

}
