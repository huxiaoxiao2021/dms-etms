
package com.jd.bluedragon.distribution.worker.jy.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 分拣操作流水mq消费
 *
 * @author wuyoude
 * @date 2023/8/03 5:45 PM
 */
@Service("jyOperateFlowMqConsumer")
public class JyOperateFlowMqConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(JyOperateFlowMqConsumer.class);
    
    @Autowired
	@Qualifier("jyOperateFlowService")
    private JyOperateFlowService jyOperateFlowService;
    
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("jyOperateFlowMqConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("JyOperateFlowMq消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyOperateFlowMqData jyOperateFlowMqData = JsonHelper.fromJsonUseGson(message.getText(), JyOperateFlowMqData.class);
            if(jyOperateFlowMqData == null) {
                logger.warn("JyOperateFlowMq消息内容无效，内容为【{}】", message.getText());
                return;
            }
            jyOperateFlowService.insert(BeanConverter.convertToJyOperateFlowDto(jyOperateFlowMqData));
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("JyOperateFlowMq-消费异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
