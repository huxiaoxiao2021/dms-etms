package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.spotcheck.dao.SpotCheckAppealDao;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckIssueMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckAppealService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 抽检申诉服务实现
 */
@Service("spotCheckAppealService")
public class SpotCheckAppealServiceImpl implements SpotCheckAppealService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckAppealServiceImpl.class);


    @Autowired
    private SpotCheckAppealDao spotCheckAppealDao;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    @Qualifier("spotCheckIssueProducer")
    private DefaultJMQProducer spotCheckIssueProducer;


    @Override
    public void insertRecord(SpotCheckAppealEntity spotCheckAppealEntity) {
        spotCheckAppealDao.insertRecord(spotCheckAppealEntity);
    }

    @Override
    public List<SpotCheckAppealEntity> findByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.findByCondition(spotCheckAppealEntity);
    }

    @Override
    public Integer countByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.countByCondition(spotCheckAppealEntity);
    }

    @Override
    public void updateById(SpotCheckAppealEntity spotCheckAppealEntity) {
        spotCheckAppealDao.updateById(spotCheckAppealEntity);
    }

    @Override
    public void batchUpdateByIds(SpotCheckAppealEntity spotCheckAppealEntity) {
        spotCheckAppealDao.batchUpdateByIds(spotCheckAppealEntity);
    }

    @Override
    public SpotCheckAppealEntity findById(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.findById(spotCheckAppealEntity);
    }

    @Override
    public List<SpotCheckAppealEntity> batchFindByIds(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.batchFindByIds(spotCheckAppealEntity);
    }

    @Override
    public List<SpotCheckAppealEntity> batchFindByWaybillCodes(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.batchFindByWaybillCodes(spotCheckAppealEntity);
    }

    @Override
    public SpotCheckAppealEntity findByBizId(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.findByBizId(spotCheckAppealEntity);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealServiceImpl.dealSpotCheckAppealByNotConfirmAndOverTime", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealSpotCheckAppealByNotConfirmAndOverTime() {
        // 默认超时时间为48小时
        int timeout = Constants.FORTY_EIGHT_HOURS;
        // 默认每半小时最多执行1000条
        int totalCount = Constants.CONSTANT_ONE_THOUSAND;
        // 默认每次查200条
        int pageSize = Constants.CONSTANT_TWO_HUNDRED;
        // 当前日期
        Date currentDate = new Date();
        // 组装查询参数
        SpotCheckAppealEntity params = new SpotCheckAppealEntity();
        params.setPageSize(pageSize);
        // 组装更新参数
        params.setConfirmStatus(Constants.NUMBER_ONE);
        params.setAutoStatus(Constants.NUMBER_ONE);
        params.setUpdateTime(currentDate);
        params.setUpdateUserErp(Constants.SYS_NAME);
        try {
            // 查询配置信息-设备抽检申诉核对超时未确认时长
            SysConfig overTimeConfig = sysConfigService.findConfigContentByConfigName(Constants.SPOT_CHECK_APPEAL_TIME_OUT);
            if (overTimeConfig != null) {
                logger.warn("dealSpotCheckAppealByNotConfirmAndOverTime|查询配置信息-设备抽检申诉核对超时未确认时长:content={}", overTimeConfig.getConfigContent());
                timeout = Integer.parseInt(overTimeConfig.getConfigContent());
            }
            // 超时日期
            Date overTimeDate = DateHelper.addHours(currentDate, -timeout);
            params.setCreateTime(overTimeDate);
            // 分批获取数据
            for (int i = 1; i <= totalCount / pageSize; i++) {
                List<Long> overTimeList = spotCheckAppealDao.findListByNotConfirm(params);
                if (CollectionUtils.isEmpty(overTimeList)) {
                    logger.warn("dealSpotCheckAppealByNotConfirmAndOverTime|根据条件查询设备抽检申诉核对超时未确认列表返回空:当前第{}页,params={}", i, JsonHelper.toJson(params));
                    break;
                }
                // 设置要更新的id列表
                params.setIdList(overTimeList);
                // 批量更新
                spotCheckAppealDao.batchUpdateByIds(params);
                // 根据ID查询设备抽检申诉核对记录
                List<SpotCheckAppealEntity> entityList = spotCheckAppealDao.batchFindByIds(params);
                if (CollectionUtils.isEmpty(entityList)) {
                    logger.warn("dealSpotCheckAppealByNotConfirmAndOverTime|根据ID列表批量查询设备抽检申诉核对记录返回空:当前第{}页,params={}", i, JsonHelper.toJson(params));
                    break;
                }
                // 批量通知称重再造系统
                batchNotifyRemakeSystem(entityList);
            }
        } catch (Exception e) {
            logger.error("dealSpotCheckAppealByNotConfirmAndOverTime|定时处理超时未确认的设备抽检申诉核对记录出现异常:params={},e={}", JsonHelper.toJson(params), e);
        }
    }

    /**
     * 通知称重再造系统
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealServiceImpl.notifyRemakeSystem", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void notifyRemakeSystem(SpotCheckAppealEntity entity) {
        // 组装消息text
        SpotCheckIssueMQ spotCheckIssueMQ = createSpotCheckIssueMQ(entity);
        // 业务主键：升级状态-运单号
        String businessId = SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode() + spotCheckIssueMQ.getWaybillCode();
        spotCheckIssueProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(spotCheckIssueMQ));
    }

    /**
     * 批量通知称重再造系统
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealServiceImpl.batchNotifyRemakeSystem", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void batchNotifyRemakeSystem(List<SpotCheckAppealEntity> entityList) {
        List<Message> messageList = new ArrayList<>();
        for (SpotCheckAppealEntity entity : entityList) {
            // 组装消息text
            SpotCheckIssueMQ spotCheckIssueMQ = createSpotCheckIssueMQ(entity);
            // 组装消息实体
            Message message = new Message();
            // 业务主键：升级状态-运单号
            String businessId = SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode() + spotCheckIssueMQ.getWaybillCode();
            message.setBusinessId(businessId);
            message.setTopic(spotCheckIssueProducer.getTopic());
            message.setText(JsonHelper.toJson(spotCheckIssueMQ));
            messageList.add(message);
        }
        spotCheckIssueProducer.batchSendOnFailPersistent(messageList);
    }

    /**
     * 批量通知称重再造系统
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.SpotCheckAppealServiceImpl.batchNotifyRemakeSystem", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void batchNotifyRemakeSystemWithNoSplit(List<SpotCheckAppealEntity> entityList) {
        List<Message> messageList = new ArrayList<>();
        for (SpotCheckAppealEntity entity : entityList) {
            // 组装消息text
            SpotCheckIssueMQ spotCheckIssueMQ = createSpotCheckIssueMQ(entity);
            // 组装消息实体
            Message message = new Message();
            // 业务主键：升级状态-运单号
            String businessId = SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode() + spotCheckIssueMQ.getWaybillCode();
            message.setBusinessId(businessId);
            message.setTopic(spotCheckIssueProducer.getTopic());
            message.setText(JsonHelper.toJson(spotCheckIssueMQ));
            messageList.add(message);
        }
        spotCheckIssueProducer.batchSendOnFailPersistentWithNoSplit(messageList);
    }

    /**
     * 通知称重再造系统
     */
    private SpotCheckIssueMQ createSpotCheckIssueMQ(SpotCheckAppealEntity spotCheckDto) {
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
        spotCheckIssueMQ.setFlowId(spotCheckDto.getBizId());
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
        spotCheckIssueMQ.setStartTime(spotCheckDto.getStartTime());
        // 流程状态变更时间
        spotCheckIssueMQ.setStatusUpdateTime(spotCheckDto.getUpdateTime());
        // 判责结果描述
        spotCheckIssueMQ.setComment(spotCheckDto.getRejectReason());

        if (logger.isInfoEnabled()) {
            logger.info("下发运单号:{}的设备抽检申诉核对数据至称重再造流程,明细如下:{}", spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        }
        return spotCheckIssueMQ;
    }

}
