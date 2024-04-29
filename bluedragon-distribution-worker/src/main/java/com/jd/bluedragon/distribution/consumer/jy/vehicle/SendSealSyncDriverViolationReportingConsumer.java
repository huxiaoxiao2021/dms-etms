package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.DriverViolationReportingResponse;
import com.jd.bluedragon.distribution.external.domain.JyDriverViolationReportingDto;
import com.jd.bluedragon.distribution.external.domain.QueryDriverViolationReportingReq;
import com.jd.bluedragon.distribution.jy.dao.send.JyDriverViolationReportingDao;
import com.jd.bluedragon.distribution.jy.dto.task.DriverViolationReportingQualityMq;
import com.jd.bluedragon.distribution.jy.dto.task.SyncDriverViolationReportingDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.send.JyDriverViolationReportingEntity;
import com.jd.bluedragon.distribution.jy.service.send.IJyDriverViolationReportingService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.TimeUtils;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author pengchong28
 * @description 封车状态已封车时同步司机违规数据到质控
 * @date 2024/4/23
 */
@Service("sendSealSyncDriverViolationReportingConsumer")
public class SendSealSyncDriverViolationReportingConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(SendSealSyncDriverViolationReportingConsumer.class);

    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private IJyDriverViolationReportingService driverViolationReportingService;

    @Autowired
    @Qualifier(value = "driverViolationReportingQualityProducer")
    private DefaultJMQProducer driverViolationReportingQualityProducer;

    @Autowired
    private JyDriverViolationReportingDao driverViolationReportingDao;

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("SendSealSyncDriverViolationReportingConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("SendSealSyncDriverViolationReportingConsumer consume -->消息体非JSON格式，内容为【{}】",
                message.getText());
            return;
        }
        SyncDriverViolationReportingDto mqBody =
            JsonHelper.fromJson(message.getText(), SyncDriverViolationReportingDto.class);
        if (mqBody == null) {
            logger.error("SendSealSyncDriverViolationReportingConsumer consume -->JSON转换后为空，内容为【{}】",
                message.getText());
            return;
        }
        if (StringUtils.isBlank(mqBody.getTransWorkItemCode())) {
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("消费处理 driver_violation_reporting 开始，内容{}", message.getText());
        }

        if (!deal(mqBody)) {
            //处理失败 重试
            logger.error("消费处理 driver_violation_reporting 失败，内容{}", message.getText());
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("消费处理 driver_violation_reporting 成功，内容{}", message.getText());
            }
        }
    }

    /**
     * 处理同步司机违规上报数据
     *
     * @param mqBody 同步司机违规上报数据传输对象
     * @return 返回处理结果，处理成功返回true，处理失败返回false
     */
    private boolean deal(SyncDriverViolationReportingDto mqBody) {
        String transWorkItemCode = mqBody.getTransWorkItemCode();
        JyBizTaskSendVehicleDetailEntity query = new JyBizTaskSendVehicleDetailEntity();
        query.setTransWorkItemCode(transWorkItemCode);
        JyBizTaskSendVehicleDetailEntity entity = jyBizTaskSendVehicleDetailService.findByTransWorkItemCode(query);
        if (Objects.isNull(entity)) {
            logger.error("SendSealSyncDriverViolationReportingConsumer"
                + " 根据派车明细单号查询派车明细任务为空，派车明细单号：{}", transWorkItemCode);
            return false;
        }
        String key = String.format(Constants.JY_DRIVER_VIOLATION_REPORTING_KEY_PREFIX, entity.getSendVehicleBizId());
        String content = cacheService.get(key);
        if (StringUtils.isBlank(content)) {
            cacheService.setEx(key, mqBody.getSealCarCode(), 5, TimeUnit.HOURS);
        }
        // 查询当前发车任务是否存在，司机违规
        JyDriverViolationReportingEntity violationReporting =
            driverViolationReportingDao.findByBizId(entity.getSendVehicleBizId());
        if (Objects.nonNull(violationReporting)) {
            // 组装数据，同步质控
            DriverViolationReportingQualityMq qualityMq =
                getDriverViolationReportingQualityMq(mqBody, violationReporting, transWorkItemCode, entity);

            driverViolationReportingQualityProducer.sendOnFailPersistent(transWorkItemCode,
                JsonHelper.toJson(qualityMq));
            return true;
        }
        return false;
    }

    /**
     * 获取司机违章上报质量消息
     *
     * @param mqBody            同步司机违章上报DTO
     * @param reportingDto            调用结果
     * @param transWorkItemCode 交通工作项代码
     * @param entity            车辆详情实体
     * @return 质量消息实体
     */
    private static DriverViolationReportingQualityMq getDriverViolationReportingQualityMq(
        SyncDriverViolationReportingDto mqBody, JyDriverViolationReportingEntity reportingDto, String transWorkItemCode,
        JyBizTaskSendVehicleDetailEntity entity) {
        DriverViolationReportingQualityMq qualityMq = new DriverViolationReportingQualityMq();
        if (Objects.nonNull(reportingDto.getCreateTime())) {
            qualityMq.setReportingDate(TimeUtils.format(reportingDto.getCreateTime(), TimeUtils.yyyy_MM_dd_HH_mm_ss));
        }
        qualityMq.setSealCarCode(mqBody.getSealCarCode());
        qualityMq.setTransWorkItemCode(transWorkItemCode);
        qualityMq.setEndSiteId(entity.getEndSiteId());
        qualityMq.setReportingSiteCode(reportingDto.getSiteCode());
        List<String> imgUrlList = Arrays.asList(reportingDto.getImgUrl().split(","));
        qualityMq.setImgUrl(imgUrlList);
        qualityMq.setVideoUrl(reportingDto.getVideoUrl());
        return qualityMq;
    }
}
