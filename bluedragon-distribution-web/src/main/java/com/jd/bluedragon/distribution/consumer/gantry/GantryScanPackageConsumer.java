package com.jd.bluedragon.distribution.consumer.gantry;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.utils.GantryPackageUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by zhanglei 20170313
 * 龙门架扫描mq消费
 * 消息topic:bd_gantry_scan_package
 */
@Service("gantryScanPackageConsumer")
public class GantryScanPackageConsumer extends MessageBaseConsumer{
    private static final Logger log = LoggerFactory.getLogger(GantryScanPackageConsumer.class);

    @Autowired
    RedisCommonUtil redisCommonUtil;

    @Override
    public void consume(Message message) throws Exception {
        this.log.debug("消费龙门架扫描MQ：内容为：{}", message.getText());
        if(message == null || "".equals(message.getText()) || null == message.getText() ){
            this.log.error("龙门架扫描消息为空");
            return;
        }
        String body = message.getText();

        UploadData gantryScanPackage = JsonHelper.fromJsonUseGson(body,UploadData.class);

        String redisKey = GantryPackageUtil.getDateRegion(gantryScanPackage.getRegisterNo(),gantryScanPackage.getScannerTime());

        Integer curCount = redisCommonUtil.getData(redisKey);

        if(curCount != null){
            curCount ++;
        }

        redisCommonUtil.cacheDataEx(redisKey,curCount,4 * 60 * 60 * 1000);
    }
}
