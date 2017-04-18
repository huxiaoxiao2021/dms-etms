package com.jd.bluedragon.core.message.consumer.gantry;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.utils.GantryPackageUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.postal.GetPrintDatasPortType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * created by zhanglei 20170313
 * 龙门架扫描mq消费
 * 消息topic:bd_gantry_scan_package
 */
@Service("gantryScanPackageConsumer")
public class GantryScanPackageConsumer extends MessageBaseConsumer{
    private static final Log logger = LogFactory.getLog(GantryScanPackageConsumer.class);

    @Autowired
    RedisCommonUtil redisCommonUtil;

    @Override
    public void consume(Message message) throws Exception {
        this.logger.info("消费龙门架扫描MQ：内容为" + message.getText());
        if(message == null || "".equals(message.getText()) || null == message.getText() ){
            this.logger.error("龙门架扫描消息为空");
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
