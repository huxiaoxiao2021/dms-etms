package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyAviationRailwayPickingGoodsService;
import com.jd.bluedragon.distribution.jy.service.picking.strategy.PickingGoodDetailInitService;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
@Service("jyPickingGoodDetailInitConsumer")
public class JyPickingGoodDetailInitConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodDetailInitConsumer.class);


    private static final String DEFAULT_VALUE_1 = "1";


    @Autowired
    private JyAviationRailwayPickingGoodsService pickingGoodsService;
    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;





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
    @JProfiler(jKey = "DMSWORKER.jy.JyPickingGoodDetailInitConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyPickingGoodDetailInitConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyPickingGoodDetailInitConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        PickingGoodTaskDetailInitDto mqBody = JsonHelper.fromJson(message.getText(), PickingGoodTaskDetailInitDto.class);
        if(Objects.isNull(mqBody)){
            log.error("JyPickingGoodDetailInitConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        logInfo("空铁提货待提明细初始化批次维度开始，businessId={}, 内容{}", message.getBusinessId(), message.getText());
        this.deal(mqBody);
        logInfo("空铁提货待提明细初始化批次维度结束，businessId={}，任务BizId={}", message.getBusinessId(), mqBody.getBizId());
    }

    private void deal(PickingGoodTaskDetailInitDto mqBody) {

        if(!this.lock(mqBody.getBizId(), mqBody.getBatchCode())) {
            log.error("空铁提货待提明细初始化批次维度数据正在处理，获取锁失败，mqBody={}", JsonHelper.toJson(mqBody));
            throw new JyBizException(String.format("提货明细初始化批次维度数据获取锁失败，businessId=%s", mqBody.getBusinessId()));
        }
        try{

            if(this.existCache(mqBody.getBizId(), mqBody.getBatchCode())) {
                logWarn("提货明细初始化批次维度消费，该批次已经处理，不在消费，bizId={},batchCode={}", mqBody.getBizId(), mqBody.getBatchCode());
                return;
            }

            PickingGoodDetailInitService detailInitService;
            if(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getSource().equals(mqBody.getStartSiteType())) {
                detailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getTargetCode());
            }else {
                detailInitService = PickingGoodDetailInitServiceFactory.getPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING.getTargetCode());
            }

            detailInitService.pickingGoodDetailInitSplit(mqBody);

            //avoid repeat consume， must save cache before return
            saveCache(mqBody.getBizId(), mqBody.getBatchCode());
        }catch (Exception ex) {
            log.error("空铁提货待提明细初始化批次维度消费异常，mqBody={}", JsonHelper.toJson(mqBody));
            throw new JyBizException(String.format("提货明细初始化批次维度数据消费异常，businessId=%s", mqBody.getBusinessId()));
        }finally {
            this.unlock(mqBody.getBizId(), mqBody.getBatchCode());
        }
    }



    /**
     * 提货场地+任务锁
     * @param bizId
     * @param batchCode
     * @return
     */
    public boolean lock(String bizId, String batchCode) {
        String lockKey = this.getLockKey(bizId, batchCode);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, 2, TimeUnit.MINUTES);
    }
    public void unlock(String bizId, String batchCode) {
        String lockKey = this.getLockKey(bizId, batchCode);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKey(String bizId, String batchCode) {
        return String.format("lock:picking:detail:init:batchCode:%s:%s", bizId, batchCode);
    }


    /**
     * 批货任务按批次拆分、统计数据防重缓存
     */
    public void saveCache(String bizId, String batchCode) {
        String cacheKey = this.getCacheKey(bizId, batchCode);
        redisClientOfJy.setEx(cacheKey, DEFAULT_VALUE_1, 10, TimeUnit.DAYS);
    }
    public boolean existCache(String bizId, String batchCode) {
        String key = this.getCacheKey(bizId, batchCode);
        if(Objects.isNull(redisClientOfJy.get(key))) {
            return false;
        }
        return true;

    }
    private String getCacheKey(String bizId, String batchCode){
        return String.format("cache:picking:detail:init:batchCode:%s:%s", bizId, batchCode);
    }
}
