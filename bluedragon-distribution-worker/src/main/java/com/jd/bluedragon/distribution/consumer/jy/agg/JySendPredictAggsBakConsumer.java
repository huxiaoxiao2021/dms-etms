package com.jd.bluedragon.distribution.consumer.jy.agg;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsDto;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendPredictAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 11:21
 * @Description: 发货波次预测汇总数据备库 消费
 */
@Service("jySendPredictAggsBakConsumer")
public class JySendPredictAggsBakConsumer  extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JySendPredictAggsBakConsumer.class);

    @Autowired
    private JySendPredictAggsService jySendPredictAggsService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = ProfilerHelper.registerInfo("DMS.WORKER.JySendPredictAggsBakConsumer.consume");
        logger.info("JySendPredictAggsBakConsumer consume -->{}",message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JySendPredictAggsBakConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JySendPredictAggsBakConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JySendPredictAggsDto dto = JsonHelper.fromJson(message.getText(), JySendPredictAggsDto.class);
        boolean checkResult = checkParam(dto);
        if(!checkResult){
            return;
        }
        String lockKey =String.format(CacheKeyConstants.JY_SEND_PREDICT_AGG_BAK_LOCK_KEY,dto.getUid());
        try{
            //加锁处理
            Boolean lock = redisClientOfJy.set(lockKey, "1", 1, TimeUnit.MINUTES, false);
            if(lock){
                //过滤旧版本数据
                String versionMutex = String.format(CacheKeyConstants.JY_SEND_PREDICT_AGG_BAK_KEY, dto.getUid());
                if (redisClientOfJy.exists(versionMutex)) {
                    Long version = Long.valueOf(redisClientOfJy.get(versionMutex));
                    if (!NumberHelper.gt(dto.getVersion(), version)) {
                        logger.warn("JySendPredictAggsBakConsumer receive old version data. curVersion: {}, 内容为【{}】", version, message.getText());
                        return;
                    }
                }

                JySendPredictAggsPO po = coverToJySendPredictAggsPO(dto);
                Boolean result = jySendPredictAggsService.insertOrUpdateJySendPredictAggsBak(po);
                if(result){
                    // 消费成功，记录数据版本号
                    if (NumberHelper.gt0(dto.getVersion())) {
                        //logger.info("JySendPredictAggsBakConsumer 卸车汇总消费的最新版本号. {}-{}", entity.getBizId(), entity.getVersion());
                        logger.info("JySendPredictAggsBakConsumer 卸车汇总消费的最新版本号. {}", dto.getVersion());
                        redisClientOfJy.set(versionMutex, dto.getVersion() + "");
                        redisClientOfJy.expire(versionMutex, 12, TimeUnit.HOURS);
                    }
                }
            }else {
                logger.warn("JySendPredictAggsBakConsumer 获取锁失败");
                throw new RuntimeException("数据正在处理中...");
            }
        }finally {
            redisClientOfJy.del(lockKey);
            Profiler.registerInfoEnd(info);
        }
    }


    /**
     * 入参校验
     * @param entity
     * @return
     */
    private boolean checkParam(JySendPredictAggsDto entity){
        if(entity == null){
            logger.warn("发货汇总实体为空!");
            return false;
        }
        if(StringUtils.isBlank(entity.getUid())){
            logger.warn("业务主键不能为空");
            return false;
        }
        if(entity.getSiteId() == null){
            logger.warn("站定id 为空!");
            return false;
        }
        if(StringUtils.isBlank(entity.getProductType())){
            logger.warn("产品类型 为空!");
            return false;
        }

        return true;
    }

    private JySendPredictAggsPO coverToJySendPredictAggsPO(JySendPredictAggsDto dto){

        JySendPredictAggsPO po = new JySendPredictAggsPO();
        po.setuId(dto.getUid());
        po.setSiteId(dto.getSiteId());
        po.setPlanNextSiteId(dto.getPlanNextSiteId());
        po.setProductType(dto.getProductType());
        po.setUnScanCount(dto.getUnScanCount());
        po.setFlag(dto.getFlag());
        po.setCreateTime(new Date());
        po.setYn(1);
        return po;
    }
}
