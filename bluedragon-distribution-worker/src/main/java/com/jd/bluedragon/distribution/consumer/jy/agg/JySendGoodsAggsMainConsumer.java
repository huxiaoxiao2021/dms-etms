package com.jd.bluedragon.distribution.consumer.jy.agg;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import java.math.BigDecimal;
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
@Service("jySendGoodsAggsMainConsumer")
public class JySendGoodsAggsMainConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(JySendGoodsAggsMainConsumer.class);

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JySendAggsService jySendAggsService;
    @Autowired
    @Qualifier(value = "jySendVehicleService")
    IJySendVehicleService jySendVehicleService;
    @Autowired
    @Qualifier("jySendVehicleServiceTys")
    private IJySendVehicleService jySendVehicleServiceTys;
    @Autowired
    private BaseService baseService;
    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = ProfilerHelper.registerInfo("DMS.WORKER.JySendGoodsAggsMainConsumer.consume");
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JySendGoodsAggsMainConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JySendGoodsAggsMainConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JySendAggsEntity entity = JsonHelper.fromJson(message.getText(), JySendAggsEntity.class);
        boolean checkResult = checkParam(entity);
        if(!checkResult){
            return;
        }
        try {
            if (!dmsConfigManager.getPropertyConfig().getProductOperateProgressSwitch() && ObjectHelper.isNotNull(entity.getOperateSiteId())) {
                BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(entity.getOperateSiteId().intValue());
                if (ObjectHelper.isNotNull(baseStaffSiteOrgDto)){
                    BigDecimal operateProgress = BusinessHelper.isBSite(baseStaffSiteOrgDto.getSubType())?
                        jySendVehicleServiceTys.calculateOperateProgress(entity,true):
                        jySendVehicleService.calculateOperateProgress(entity,true);
                    if (logger.isInfoEnabled()){
                        logger.info("计算装车进度 {}：{}",message.getText(),operateProgress);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("计算发货操作进度异常",e);
        }
        String lockKey =String.format(CacheKeyConstants.JY_SEND_AGG_LOCK_KEY,entity.getUid());

        try{
            Boolean lock = redisClientOfJy.set(lockKey, "1", 1, TimeUnit.MINUTES, false);

            if(lock){
                //过滤旧版本数据
                String versionMutex = String.format(CacheKeyConstants.JY_SEND_AGG_KEY, entity.getBizId());
                if (redisClientOfJy.exists(versionMutex)) {
                    Long version = Long.valueOf(redisClientOfJy.get(versionMutex));
                    if (!NumberHelper.gt(entity.getVersion(), version)) {
                        logger.warn("JySendGoodsAggsMainConsumer receive old version data. curVersion: {}, 内容为【{}】", version, message.getText());
                        return;
                    }
                }
                Boolean result = jySendAggsService.insertOrUpdateJySendGoodsAggsMain(entity);
                if(result){
                    // 消费成功，记录数据版本号
                    if (NumberHelper.gt0(entity.getVersion())) {
                        logger.info("JySendGoodsAggsMainConsumer 卸车汇总消费的最新版本号. {}-{}", entity.getBizId(), entity.getVersion());
                        redisClientOfJy.set(versionMutex, entity.getVersion() + "");
                        redisClientOfJy.expire(versionMutex, 12, TimeUnit.HOURS);
                    }
                }
            }else {
                logger.warn("JySendGoodsAggsMainConsumer 获取锁失败");
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
    private boolean checkParam(JySendAggsEntity entity){
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
