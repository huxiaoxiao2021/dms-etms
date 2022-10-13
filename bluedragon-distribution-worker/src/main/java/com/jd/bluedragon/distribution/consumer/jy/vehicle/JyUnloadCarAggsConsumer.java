package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
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
 * @Date: 2022/10/11 17:04
 * @Description: 卸车进度汇总消费
 */
@Service("jyUnloadCarAggsConsumer")
public class JyUnloadCarAggsConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyUnloadCarAggsConsumer.class);

    @Autowired
    private JyUnloadCarAggsService jyUnloadCarAggsService;

    @Override
    public void consume(Message message) throws Exception {
        if(logger.isInfoEnabled()){
            logger.info("JyUnloadCarAggsConsumer consume 消息体-{}",message.getText());
        }
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyUnloadCarAggsConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyUnloadCarAggsConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyUnloadCarAggsEntity entity = JsonHelper.fromJson(message.getText(), JyUnloadCarAggsEntity.class);
        boolean result = checkParam(entity);
        if(!result){
            return;
        }
        jyUnloadCarAggsService.insertOrUpdateJyUnloadCarAggs(entity);
    }

    /**
     * 入参校验
     * @param entity
     * @return
     */
    private boolean checkParam(JyUnloadCarAggsEntity entity){
        if(entity == null){
            logger.warn("卸车进度汇总实体为空!");
            return false;
        }
        if(StringUtils.isBlank(entity.getBizId())){
            logger.warn("卸车进度 bizID 为空!");
            return false;
        }
        return true;
    }
}
