package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 17:04
 * @Description: 卸车进度汇总消费
 */
@Service("jyUnloadCarAggsConsumer")
public class JyUnloadCarAggsConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyUnloadCarAggsConsumer.class);

    @Autowired
    private IJyUnloadVehicleService jyUnloadVehicleService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Override
    @JProfiler(jKey = "DMS.WORKER.JyUnloadCarAggsConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyUnloadCarAggsConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyUnloadCarAggsConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyUnloadAggsEntity entity = JsonHelper.fromJson(message.getText(), JyUnloadAggsEntity.class);
        boolean checkResult = checkParam(entity);
        if(!checkResult){
            return;
        }
        //过滤旧版本数据
        String versionMutex = String.format(CacheKeyConstants.JY_UNLOAD_AGG_KEY, entity.getBizId()+entity.getProductType());
        if (redisClientOfJy.exists(versionMutex)) {
            Integer version = Integer.valueOf(redisClientOfJy.get(versionMutex));
            if (!NumberHelper.gt(entity.getVersion(), version)) {
                logger.warn("JyUnloadCarAggsConsumer receive old version data. curVersion: {}, 内容为【{}】", version, message.getText());
                return;
            }
        }
        int result = jyUnloadVehicleService.insertOrUpdateJyUnloadCarAggs(entity);
        if(result >0){
            // 消费成功，记录数据版本号
            if (NumberHelper.gt0(entity.getVersion())) {
                logger.info("JyUnloadCarAggsConsumer 卸车汇总消费的最新版本号. {}-{}", entity.getBizId(), entity.getVersion());
                redisClientOfJy.set(versionMutex, entity.getVersion() + "");
                redisClientOfJy.expire(versionMutex, 12, TimeUnit.HOURS);
            }
        }
    }

    /**
     * 入参校验
     * @param entity
     * @return
     */
    private boolean checkParam(JyUnloadAggsEntity entity){
        if(entity == null){
            logger.warn("卸车汇总实体为空!");
            return false;
        }
        if(StringUtils.isBlank(entity.getBizId())){
            logger.warn("卸车汇总 bizID 为空!");
            return false;
        }
        return true;
    }
}
