package com.jd.bluedragon.distribution.consumer.jy.agg;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendProductAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 17:39
 * @Description:
 */
@Service("jySendProductAggsMainConsumer")
public class JySendProductAggsMainConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(JySendProductAggsMainConsumer.class);

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JySendProductAggsService jySendProductAggsService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = ProfilerHelper.registerInfo("DMS.WORKER.JySendProductAggsMainConsumer.consume");
        logger.info("JySendProductAggsMainConsumer consume 消息体-{}",message.getText());

        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JySendProductAggsMainConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JySendProductAggsMainConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JySendProductAggsEntity entity = JsonHelper.fromJson(message.getText(), JySendProductAggsEntity.class);
        logger.info("JySendProductAggsMainConsumer entity 消息体-{}", JSON.toJSONString(entity));
        boolean checkResult = checkParam(entity);
        if(!checkResult){
            return;
        }

        String lockKey =String.format(CacheKeyConstants.JY_SEND_PRODUCT_AGG_LOCK_KEY,entity.getUid());
        try{
            Boolean lock = redisClientOfJy.set(lockKey, "1", 1, TimeUnit.MINUTES, false);
            if(lock){
                //过滤旧版本数据
                String versionMutex = String.format(CacheKeyConstants.JY_SEND_PRODUCT_AGG_KEY, entity.getBizId());
                if (redisClientOfJy.exists(versionMutex)) {
                    Long version = Long.valueOf(redisClientOfJy.get(versionMutex));
                    if (!NumberHelper.gt(entity.getVersion(), version)) {
                        logger.warn("JySendProductAggsMainConsumer receive old version data. curVersion: {}, 内容为【{}】", version, message.getText());
                        return;
                    }
                }
                Boolean result = jySendProductAggsService.insertOrUpdateJySendProductAggsMain(entity);
                if(result){
                    // 消费成功，记录数据版本号
                    if (NumberHelper.gt0(entity.getVersion())) {
                        logger.info("JySendProductAggsMainConsumer 卸车汇总消费的最新版本号. {}-{}", entity.getBizId(), entity.getVersion());
                        redisClientOfJy.set(versionMutex, entity.getVersion() + "");
                        redisClientOfJy.expire(versionMutex, 12, TimeUnit.HOURS);
                    }
                }
            }else {
                logger.warn("JySendProductAggsMainConsumer 获取锁失败");
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
    private boolean checkParam(JySendProductAggsEntity entity){
        if(entity == null){
            logger.warn("发货汇总实体为空!");
            return false;
        }
        if(StringUtils.isBlank(entity.getBizId())){
            logger.warn("发货进度 bizID 为空!");
            return false;
        }
        return true;
    }
}
