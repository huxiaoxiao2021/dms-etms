package com.jd.bluedragon.distribution.consumer.arAbnormal;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author tangchunqing
 * @Description: 空铁转陆运 提报异常
 * @date 2018年12月04日 20时:40分
 */
@Service("arAbnormalConsumer")
public class ArAbnormalConsumer extends MessageBaseConsumer {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    ArAbnormalService arAbnormalService;
    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("ArAbnormalConsumer-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }

        /**将mq消息体转换成SendDetail对象**/
        ArAbnormalRequest arAbnormalRequest = JsonHelper.fromJsonUseGson(message.getText(), ArAbnormalRequest.class);
        if (arAbnormalRequest == null || StringHelper.isEmpty(arAbnormalRequest.getPackageCode())) {
            logger.error("ArAbnormalConsumer[" + message.getText() + "]转换实体失败或没有合法的扫描码");
            return;
        }
        arAbnormalService.dealArAbnormal(arAbnormalRequest);
    }
}
