package com.jd.bluedragon.distribution.consumer.weightAndVolumeCheck;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 称重抽检处理消息消费
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2020-08-25 09:24:48 周二
 */
@Service("weightAndVolumeCheckHandleConsumer")
public class WeightAndVolumeCheckHandleConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(WeightAndVolumeCheckHandleConsumer.class);

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Override
    @JProfiler(jKey = "DMSWEB.WeightAndVolumeCheckHandleConsumer.consume", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            // 消息格式错误，加入自定义告警
            String profilerKey = "DMSWEB.WeightAndVolumeCheckHandleConsumer.consume.badJsonMessage";
            Profiler.businessAlarm(profilerKey, MessageFormat.format("城配运单推送MQ-消息体非JSON格式，businessId为【{0}】", message.getBusinessId()));
            return;
        }
        WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage = JsonHelper.fromJsonUseGson(message.getText(), WeightAndVolumeCheckHandleMessage.class);
        InvokeResult<Boolean> handleResult = weightAndVolumeCheckService.handleAfterUploadImgMessageOrAfterSend(weightAndVolumeCheckHandleMessage);
        if(!handleResult.getData()){
            log.error("WeightAndVolumeCheckHandleConsumer fail " + JSON.toJSONString(handleResult));
            throw new RuntimeException("WeightAndVolumeCheckHandleConsumer 处理失败 " + handleResult.getMessage());
        }
    }
}
