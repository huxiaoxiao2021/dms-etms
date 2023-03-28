package com.jd.bluedragon.distribution.consumer.jy.autoClose;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.JyBizTaskAutoCloseService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.java.utils.sdk.base.Result;
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

    @Autowired
    private JyBizTaskAutoCloseService jyBizTaskAutoCloseService;

    @Override
    public void consume(Message message) throws Exception {
        log.info("jyBizTaskAutoCloseConsumer -message {}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("ChangeOrderPrintConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("ChangeOrderPrintConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        AutoCloseTaskMq autoCloseTaskMq = JsonHelper.fromJson(message.getText(), AutoCloseTaskMq.class);
        String lockKey = String.format(CacheKeyConstants.CACHE_KEY_JY_BIZ_TASK_AUTO_CLOSE, autoCloseTaskMq.getTaskBusinessType(), autoCloseTaskMq.getBizId());
        try {
            Boolean lockFlag = redisClientOfJy.set(lockKey, "1", 30, TimeUnit.SECONDS, false);
            if (!lockFlag) {
                return;
            }
            final Result<Boolean> handleResult = jyBizTaskAutoCloseService.consumeJyBizTaskAutoCloseMq(autoCloseTaskMq);
            if(handleResult.isSuccess()){
                log.error("jyBizTaskAutoCloseConsumer consumeJyBizTaskAutoCloseMq fail {} {}",  JsonHelper.toJson(message), JsonHelper.toJson(handleResult));
                throw new RuntimeException("jyBizTaskAutoCloseConsumer consumeJyBizTaskAutoCloseMq fail ");
            }
        } finally {
            redisClientOfJy.del(lockKey);
        }

    }
}
