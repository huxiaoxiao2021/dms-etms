package com.jd.bluedragon.distribution.consumer.jy.autoClose;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.print.domain.ChangeOrderPrintMq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 拣运作业工作台自动关闭任务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-28 14:49:35 周二
 */
@Slf4j
@Service("jyBizTaskAutoCloseConsumer")
public class JyBizTaskAutoCloseConsumer extends MessageBaseConsumer {

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Override
    public void consume(Message message) throws Exception {
        log.info("ChangeOrderPrintConsumer -message {}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("ChangeOrderPrintConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("ChangeOrderPrintConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        ChangeOrderPrintMq changeOrderPrintMq = JsonHelper.fromJson(message.getText(), ChangeOrderPrintMq.class);
        String lockKey =String.format(CacheKeyConstants.CACHE_KEY_CHANGE_ORDER_PRINT_KEY,changeOrderPrintMq.getWaybillCode());
        try{
            Boolean result = redisClientOfJy.set(lockKey, "1", 20, TimeUnit.SECONDS, false);
            if(!result){
                return ;
            }
        }finally {
            redisClientOfJy.del(lockKey);
        }

    }
}
