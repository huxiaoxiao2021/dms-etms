package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.task.UnloadVehicleMqDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.TagSignHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.client.consumer.MessageListener;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName InitUnloadVehicleConsumer
 * @Description 初始化卸车任务，接收卸车进度
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
@Service("refreshUnloadVehicleProgressConsumer")
public class RefreshUnloadVehicleProgressConsumer implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RefreshUnloadVehicleProgressConsumer.class);

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @JProfiler(jKey = "DMS.WORKER.jy.initUnloadVehicleConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {

        logger.info("开始消费拣运卸车进度:{}", message.getText());

        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("InitUnloadVehicleConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("InitUnloadVehicleConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        UnloadVehicleMqDto mqDto = JsonHelper.fromJson(message.getText(), UnloadVehicleMqDto.class);
        if (filterDiscardData(mqDto)) {
            return;
        }

        // 旧版本数据不再消费
        String versionMutex = String.format(CacheKeyConstants.JY_UNLOAD_SEAL_CAR_MONITOR_SEAL_CAR_CODE, mqDto.getSealCarCode());
        if (redisClientOfJy.exists(versionMutex)) {
            Integer version = Integer.valueOf(redisClientOfJy.get(versionMutex));
            if (!NumberHelper.gt(mqDto.getVersion(), version)) {
                logger.warn("InitUnloadVehicleConsumer receive old version data. curVersion: {}, 内容为【{}】", version, message.getText());
                return;
            }
        }

        if (saveUnloadTaskData(message, mqDto)) {
            // 消费成功，记录数据版本号
            if (NumberHelper.gt0(mqDto.getVersion())) {
                logger.info("卸车任务消费的最新版本号. {}-{}", mqDto.getSealCarCode(), mqDto.getVersion());
                redisClientOfJy.set(versionMutex, mqDto.getVersion() + "");
                redisClientOfJy.expire(versionMutex, 12, TimeUnit.HOURS);
            }
        }
    }

    /**
     * 需要丢弃的数据
     * @param mqDto
     * @return true：丢弃
     */
    private boolean filterDiscardData(UnloadVehicleMqDto mqDto) {
        if (mqDto == null) {
            logger.warn("InitUnloadVehicleConsumer consume -->JSON转换后为空");
            return true;
        }

        if (StringUtils.isEmpty(mqDto.getSealCarCode()) || null == mqDto.getEndSiteId()) {
            logger.warn("InitUnloadVehicleConsumer consume -->关键数据为空，内容为【{}】", JsonHelper.toJson(mqDto));
            return true;
        }

        Integer endSiteId = mqDto.getEndSiteId();
        //检查目的地是否是拣运中心
        BaseStaffSiteOrgDto siteInfo = baseMajorManager.getBaseSiteBySiteId(endSiteId);
        if (siteInfo == null || !BusinessUtil.isSorting(siteInfo.getSiteType())) {
            //丢弃数据
            logger.info("InitUnloadVehicleConsumer不需要关心的数据丢弃, 目的站点:{}, 目的站点类型:{}, 消息:{}",
                    endSiteId, siteInfo == null ? null : siteInfo.getSiteType(), JsonHelper.toJson(mqDto));
            return true;
        }

        return false;
    }

    /**
     * 保存卸车任务数据
     * @param message
     * @param mqDto
     * @return
     */
    private boolean saveUnloadTaskData(Message message, UnloadVehicleMqDto mqDto) {
        boolean saveData;

        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = convertEntityFromDto(mqDto);
        try {
            saveData = jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(unloadVehicleEntity);
        }
        catch (JyBizException bizException) {
            logger.warn("初始化卸车任务发生业务异常，将重试! {}", message.getText(), bizException);
            throw bizException;
        }
        catch (Exception e) {
            Profiler.businessAlarm("dms.web.InitUnloadVehicleConsumer.consume", "拣运接收卸车任务失败");
            logger.error("初始化卸车任务发生业务失败. {}", message.getText(), e);
            throw new RuntimeException("初始化卸车任务发生业务失败");
        }

        return saveData;
    }

    private JyBizTaskUnloadVehicleEntity convertEntityFromDto(UnloadVehicleMqDto mqDto) {
        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = new JyBizTaskUnloadVehicleEntity();
        unloadVehicleEntity.setBizId(mqDto.getSealCarCode());
        unloadVehicleEntity.setSealCarCode(mqDto.getSealCarCode());
        if (mqDto.getOrderTime() != null) {
            unloadVehicleEntity.setSortTime(mqDto.getOrderTime());
        }
        if (mqDto.getRanking() != null) {
            unloadVehicleEntity.setRanking(mqDto.getRanking());
        }
        if (mqDto.getPredictionArriveTime() != null) {
            unloadVehicleEntity.setPredictionArriveTime(mqDto.getPredictionArriveTime());
        }
        if (mqDto.getActualArriveTime() != null) {
            unloadVehicleEntity.setActualArriveTime(mqDto.getActualArriveTime());
        }
        if (mqDto.getDesealCarTime() != null) {
            unloadVehicleEntity.setDesealCarTime(mqDto.getDesealCarTime());
        }
        if (mqDto.getLineType() != null) {
            unloadVehicleEntity.setLineType(mqDto.getLineType());
        }
        if (mqDto.getLineTypeName() != null) {
            unloadVehicleEntity.setLineTypeName(mqDto.getLineTypeName());
        }
        if (mqDto.getTotalCount() != null) {
            unloadVehicleEntity.setTotalCount(mqDto.getTotalCount());
        }

        // 处理卸车任务标位
        unloadVehicleEntity.setTagsSign(TagSignHelper.initDefaultPlaceholder());
        dealTagSign(mqDto, unloadVehicleEntity);

        // 处理卸车进度
        dealUnloadProgress(mqDto, unloadVehicleEntity);

        return unloadVehicleEntity;
    }

    private void dealUnloadProgress(UnloadVehicleMqDto mqDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        Map<String, Object> extendInfo = mqDto.getExtendInfo();
        if (MapUtils.isEmpty(extendInfo)) {
            return;
        }
        Object totalScannedPackageProgress = extendInfo.get(UnloadVehicleMqDto.EXTEND_KEY_SCAN_PROGRESS);
        if (totalScannedPackageProgress != null) {
            if (NumberHelper.isBigDecimal(totalScannedPackageProgress + "")) {
                BigDecimal progress = new BigDecimal(totalScannedPackageProgress + "");
                unloadVehicleEntity.setUnloadProgress(progress);
            }
        }
    }

    /**
     * 处理卸车任务标位
     * @param mqDto
     * @param unloadVehicleEntity
     */
    private void dealTagSign(UnloadVehicleMqDto mqDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {

        // 标记抽检标识
        if (mqDto.getCheckType() != null) {
            if (mqDto.getCheckType() == Constants.CONSTANT_NUMBER_ONE) {
                unloadVehicleEntity.setTagsSign(TagSignHelper.setPositionSign(unloadVehicleEntity.getTagsSign(), JyUnloadTaskSignConstants.POSITION_1, JyUnloadTaskSignConstants.CHAR_1_1));
            }
        }

        Map<String, Object> extendInfo = mqDto.getExtendInfo();
        if (MapUtils.isEmpty(extendInfo)) {
            return;
        }

        // 标记逐单卸标识
        Object damageCntObj = extendInfo.get(UnloadVehicleMqDto.EXTEND_KEY_DAMAGE_CNT);
        Object lostCntObj = extendInfo.get(UnloadVehicleMqDto.EXTEND_KEY_LOST_CNT);
        if (null != damageCntObj && null != lostCntObj) {
            Long damageCnt = Long.valueOf(damageCntObj + "");
            Long lostCnt = Long.valueOf(lostCntObj + "");
            if (dmsConfigManager.getPropertyConfig().getJyUnloadSingleWaybillThreshold() < (damageCnt + lostCnt)) {
                unloadVehicleEntity.setTagsSign(TagSignHelper.setPositionSign(unloadVehicleEntity.getTagsSign(), JyUnloadTaskSignConstants.POSITION_2, JyUnloadTaskSignConstants.CHAR_2_1));
            }
        }
    }

    @Override
    public void onMessage(List<Message> messages) throws Exception {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        for (Message message : messages) {
            this.consume(message);
        }
    }
}
