package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.CalculateWaitPickingItemNumDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyBizTaskPickingGoodTransactionManager;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingTaskAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service("JyPickingGoodWaitScanItemNumUpdateConsumer")
public class JyPickingGoodWaitScanItemNumUpdateConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodWaitScanItemNumUpdateConsumer.class);
    private static final String DEFAULT_VALUE_1 = "1";

    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JyPickingTaskAggsService jyPickingTaskAggsService;
    @Autowired
    private JyBizTaskPickingGoodTransactionManager jyBizTaskPickingGoodTransactionManager;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyPickingGoodWaitScanItemNumUpdateConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logWarn("JyPickingGoodWaitScanItemNumUpdateConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyPickingGoodWaitScanItemNumUpdateConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        CalculateWaitPickingItemNumDto mqBody = JsonHelper.fromJson(message.getText(), CalculateWaitPickingItemNumDto.class);
        if(Objects.isNull(mqBody)){
            log.error("JyPickingGoodWaitScanItemNumUpdateConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        logInfo("航空提货待提明细件数消费开始，businessId={}, 内容{}", message.getBusinessId(), message.getText());
        if(!lock(mqBody.getBizId(), mqBody.getBatchCode(), mqBody.getNextSiteId())) {
            logWarn("提货初始化待提件数没有获取到锁，重试，msg={}", message.getText());
            throw new JyBizException(String.format("提货初始化待提件数没有获取到锁,businessId：%s", message.getBusinessId()));
        }
        try{
            if(!filterRepeatConsume(mqBody)) {
                return;
            }
            jyBizTaskPickingGoodTransactionManager.saveAggWaitScanItem(mqBody);
            logInfo("航空提货待提明细件数消费结束，businessId={}", message.getBusinessId());
        }catch (Exception ex) {
            unlock(mqBody.getBizId(), mqBody.getBatchCode(), mqBody.getNextSiteId());
            log.error("航空提货待提明细件数消费异常,businessId={},mqBody={}", message.getBusinessId(), message.getText());
            throw new JyBizException(String.format("航空提货待提明细件数消费异常,businessId：%s", message.getBusinessId()));
        }

    }


    private boolean filterRepeatConsume(CalculateWaitPickingItemNumDto mqBody) {
        if(!this.existCache(mqBody.getBizId(), mqBody.getBatchCode(), mqBody.getNextSiteId())) {
            this.saveCache(mqBody.getBizId(), mqBody.getBatchCode(), mqBody.getNextSiteId());
        }else {
            return false;
        }
        return true;
    }

    /**
     * 防重锁:航空主运单号
     */
    private boolean lock(String bizId, String batchCode, Long nextSiteId) {
        String lockKey = this.getLockKey(bizId, batchCode, nextSiteId);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, 5, TimeUnit.MINUTES);
    }
    private void unlock(String bizId, String batchCode, Long nextSiteId) {
        String lockKey = this.getLockKey(bizId, batchCode, nextSiteId);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKey(String bizId, String batchCode, Long nextSiteId) {
        return String.format("lock:aviation:picking:bizId:batchCode:nextSiteId:%s:%s:%s", batchCode, bizId, nextSiteId);
    }

    /**
     * 同一个key只能处理一次，下游计数非幂等
     * @return
     */
    private void saveCache(String bizId, String batchCode, Long nextSiteId) {
        String cacheKey = this.getCacheKey(bizId, batchCode, nextSiteId);
        redisClientOfJy.setEx(cacheKey, DEFAULT_VALUE_1, 30, TimeUnit.HOURS);
    }
    private boolean existCache(String bizId, String batchCode, Long nextSiteId) {
        String cacheKey = this.getCacheKey(bizId, batchCode, nextSiteId);
        return redisClientOfJy.exists(cacheKey);
    }
    private String getCacheKey(String bizId, String batchCode, Long nextSiteId) {
        return String.format("cache:picking:good:waitScanAggAlter:%s:%s:%s", bizId, batchCode, nextSiteId);
    }


}
