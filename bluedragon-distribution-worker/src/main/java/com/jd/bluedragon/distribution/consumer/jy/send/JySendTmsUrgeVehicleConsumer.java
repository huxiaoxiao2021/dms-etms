package com.jd.bluedragon.distribution.consumer.jy.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.tms.TmsUrgeVehicleMq;
import com.jd.bluedragon.distribution.jy.service.send.JyNoTaskSendService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 自建任务触发催派运输车辆
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-09-15 09:46:20 周五
 */
@Service("ySendTmsUrgeVehicleConsumer")
public class JySendTmsUrgeVehicleConsumer  extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(JyTaskSendDetailFirstSendConsumer.class);

    @Autowired
    private JyNoTaskSendService jyNoTaskSendService;

    @Override
    public void consume(Message message) throws Exception{
        CallerInfo info = Profiler.registerInfo("JySendTmsUrgeVehicleConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("JySendTmsUrgeVehicleConsumer 消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            // 将mq消息体转换成SendDetail对象
            TmsUrgeVehicleMq tmsUrgeVehicleMq = JsonHelper.fromJson(message.getText(), TmsUrgeVehicleMq.class);
            final Result<Boolean> result = jyNoTaskSendService.remindTransJob(tmsUrgeVehicleMq);
            if(!result.isSuccess()){
                log.error("JySendTmsUrgeVehicleConsumer exception {} {}", message.getText(), JsonHelper.toJson(result));
                throw new Exception();
            }
        }catch(Exception e){
            Profiler.functionError(info);
            log.error("JySendTmsUrgeVehicleConsumer exception {}", message.getText(), e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
