package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.constants.JyAggsTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyAggsDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadTaskCompleteDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("jyAggsConsumer")
public class JyAggsConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(JyAggsConsumer.class);

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("jyAggsConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("jyAggsConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyAggsDto jyAggsDto = JsonHelper.fromJson(message.getText(), JyAggsDto.class);
        if (jyAggsDto == null) {
            logger.error("jyAggsConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if (JyAggsTypeEnum.JY_COMBOARD_AGGS == jyAggsDto.getJyAggsTypeEnum()) {
            //执行逻辑
        }

    }
}
