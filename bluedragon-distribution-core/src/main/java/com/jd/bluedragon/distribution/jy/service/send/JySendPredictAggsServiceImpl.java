package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.dbrouter.SendAggsChangeDataSources;
import com.jd.bluedragon.distribution.jy.dao.send.JySendPredictAggsDao;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsDto;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;
import com.jd.bluedragon.distribution.jy.send.JySendPredictProductType;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 11:05
 * @Description:
 */
@SendAggsChangeDataSources
@Service("jySendPredictAggsService")
public class JySendPredictAggsServiceImpl implements JySendPredictAggsService{

    private static final Logger logger = LoggerFactory.getLogger(JySendPredictAggsServiceImpl.class);

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JySendPredictAggsDao JySendPredictAggsDao;


    @Override
    public Long getToScanCountSum(JySendPredictAggsRequest query) {
        return JySendPredictAggsDao.getunScanSumByCondition(query);
    }

    @Override
    public List<JySendPredictProductType> getSendPredictProductTypeList(JySendPredictAggsRequest query) {
        return JySendPredictAggsDao.getSendPredictProductTypeList(query);
    }


    /**
     * 处理消息
     * @param message
     */
    public void dealJySendPredictAggsMessage(Message message){

        JySendPredictAggsDto dto = JsonHelper.fromJson(message.getText(), JySendPredictAggsDto.class);
        boolean checkResult = checkParam(dto);
        if (!checkResult) {
            return;
        }
        String topic = message.getTopic();
        String lockKey = String.format(CacheKeyConstants.JY_SEND_PREDICT_AGG_LOCK_KEY + topic, dto.getUid());
        try {
            //加锁处理
            Boolean lock = redisClientOfJy.set(lockKey, "1", 1, TimeUnit.MINUTES, false);
            if (lock) {
                //过滤旧版本数据
                String versionMutex = String.format(CacheKeyConstants.JY_SEND_PREDICT_AGG_KEY + topic, dto.getUid());
                if (redisClientOfJy.exists(versionMutex)) {
                    Long version = Long.valueOf(redisClientOfJy.get(versionMutex));
                    if (!NumberHelper.gt(dto.getVersion(), version)) {
                        logger.warn("JySendPredictAggsConsumer receive old version data. curVersion: {}, 内容为【{}】", version, message.getText());
                        return;
                    }
                }
                JySendPredictAggsPO po = coverToJySendPredictAggsPO(dto);
                boolean result = JySendPredictAggsDao.updateByBizProduct(po) > 0;
                if(!result){
                    result =JySendPredictAggsDao.insert(po) > 0;
                }
                if (result) {
                    // 消费成功，记录数据版本号
                    if (NumberHelper.gt0(dto.getVersion())) {
                        logger.info("JySendPredictAggsConsumer 卸车汇总消费的最新版本号. {}-{}", dto.getUid(), dto.getVersion());
                        redisClientOfJy.set(versionMutex, dto.getVersion() + "");
                        redisClientOfJy.expire(versionMutex, 12, TimeUnit.HOURS);
                    }
                }
            } else {
                logger.warn("JySendPredictAggsConsumer 获取锁失败");
                throw new RuntimeException("数据正在处理中...");
            }
        } catch (Exception e){
            logger.error("JySendPredictAggsConsumer 处理数据异常",e);
        }finally {
            redisClientOfJy.del(lockKey);
        }
    }

    /**
     * 入参校验
     *
     * @param entity
     * @return
     */
    private boolean checkParam(JySendPredictAggsDto entity) {
        if (entity == null) {
            logger.warn("发货汇总实体为空!");
            return false;
        }
        if (StringUtils.isBlank(entity.getUid())) {
            logger.warn("业务主键不能为空");
            return false;
        }
        if (entity.getSiteCode() == null) {
            logger.warn("站定id 为空!");
            return false;
        }
        if (StringUtils.isBlank(entity.getProductType())) {
            logger.warn("产品类型 为空!");
            return false;
        }

        return true;
    }

    private JySendPredictAggsPO coverToJySendPredictAggsPO(JySendPredictAggsDto dto) {

        JySendPredictAggsPO po = new JySendPredictAggsPO();
        po.setuId(dto.getUid());
        po.setSiteId(dto.getSiteCode());
        po.setPlanNextSiteId(dto.getPlanNextSiteCode());
        po.setProductType(dto.getProductType());
        po.setUnScanCount(dto.getUnScanCount());
        po.setFlag(dto.getFlag());
        po.setCreateTime(new Date());
        po.setYn(1);
        return po;
    }
}
