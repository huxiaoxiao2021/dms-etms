package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.send.JySendGoodsAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendGoodsAggsService;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadCarAggsService;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadCarAggsEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 17:39
 * @Description:
 */
@Service("jySendGoodsAggsConsumer")
public class JySendGoodsAggsConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(JySendGoodsAggsConsumer.class);

    @Autowired
    private JySendGoodsAggsService jySendGoodsAggsService;

    @Override
    public void consume(Message message) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("JySendGoodsAggsConsumer consume 消息体-{}",message.getText());
        }
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JySendGoodsAggsConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JySendGoodsAggsConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JySendGoodsAggsEntity entity = JsonHelper.fromJson(message.getText(), JySendGoodsAggsEntity.class);
        if(entity == null){
            logger.warn("发货明细实体对象为空!");
            return;
        }
        jySendGoodsAggsService.insertOrUpdateJySendGoodsAggs(entity);
    }
}
