package com.jd.bluedragon.distribution.consumer.businessIntercept;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.businessIntercept.service.IOfflineTaskCheckBusinessInterceptService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 业务操作拦截后原始动作消费
 *
 * @author fanggang7
 * @time 2020-12-22 16:19:39 周二
 */
@Service("offlineTaskCheckBusinessInterceptConsumer")
public class OfflineTaskCheckBusinessInterceptConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(OfflineTaskCheckBusinessInterceptConsumer.class);

    @Autowired
    private IOfflineTaskCheckBusinessInterceptService offlineTaskCheckBusinessInterceptService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            // 消息格式错误，加入自定义告警
            String profilerKey = "DMSWEB.OfflineTaskCheckBusinessInterceptConsumer.consume.badJsonMessage";
            Profiler.businessAlarm(profilerKey, MessageFormat.format("分拣离线任务处理校验是否拦截动作消息MQ-消息体非JSON格式，businessId为【{0}】", message.getBusinessId()));
            return;
        }
        OfflineLogRequest msgDto = JSON.parseObject(message.getText(), OfflineLogRequest.class);
        Response<Boolean> handleResult = offlineTaskCheckBusinessInterceptService.handleOfflineTask(msgDto);
        if(!handleResult.getData()){
            log.error("OfflineTaskCheckBusinessInterceptConsumer fail " + JSON.toJSONString(handleResult));
            throw new RuntimeException("OfflineTaskCheckBusinessInterceptConsumer 处理失败 " + handleResult.getMessage());
        }
    }
}
