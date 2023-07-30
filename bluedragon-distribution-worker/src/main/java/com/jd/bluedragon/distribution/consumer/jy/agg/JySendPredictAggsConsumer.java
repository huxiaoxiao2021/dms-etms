package com.jd.bluedragon.distribution.consumer.jy.agg;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.dbrouter.NeedChangeDataSources;
import com.jd.bluedragon.dbrouter.SendAggsChangeDataSources;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsDto;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.service.send.JySendPredictAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JySendPredictAggsServiceImpl;
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
 * @Description: 发货波次预测汇总数据 消费
 */
@SendAggsChangeDataSources
@Service("jySendPredictAggsConsumer")
public class JySendPredictAggsConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JySendPredictAggsConsumer.class);

    @Autowired
    private JySendPredictAggsServiceImpl jySendPredictAggsService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Override
    public void consume(Message message) throws Exception {

        logger.info("JySendPredictAggsConsumer consume -->{}", message.getText());
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JySendPredictAggsConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JySendPredictAggsConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        jySendPredictAggsService.dealJySendPredictAggsMessage(message);
    }

}
