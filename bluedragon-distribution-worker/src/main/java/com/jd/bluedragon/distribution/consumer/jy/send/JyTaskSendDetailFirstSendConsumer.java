package com.jd.bluedragon.distribution.consumer.jy.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.JyTaskSendDetailFirstSendDto;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运发货流向纬度首次扫描消息消费
 * @author fanggang7
 * @time 2023-05-26 16:18:39 周五
 */
@Service("jyTaskSendDetailFirstSendConsumer")
public class JyTaskSendDetailFirstSendConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(JyTaskSendDetailFirstSendConsumer.class);

    @Autowired
    private IJySendVehicleService jySendVehicleService;

    @Override
    public void consume(Message message) throws Exception{
        CallerInfo info = Profiler.registerInfo("JyTaskSendDetailFirstSendConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("JyTaskSendDetailFirstSendConsumer 拣运发货流向纬度首次扫描消息消费-消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            // 将mq消息体转换成SendDetail对象
            JyTaskSendDetailFirstSendDto jyTaskSendDetailFirstSendDto = JsonHelper.fromJson(message.getText(), JyTaskSendDetailFirstSendDto.class);
            if (jyTaskSendDetailFirstSendDto == null || StringHelper.isEmpty(jyTaskSendDetailFirstSendDto.getSendVehicleDetailBizId())) {
                log.warn("JyTaskSendDetailFirstSendConsumer:消息体[{}]转换实体失败或没有合法的业务数据", message.getText());
                return;
            }
            final Result<Boolean> result = jySendVehicleService.handleOnlyLoadAttr(jyTaskSendDetailFirstSendDto);
            if(!result.isSuccess()){
                log.error("JyTaskSendDetailFirstSendConsumer exception {} {}", message.getText(), JsonHelper.toJson(result));
                throw new Exception();
            }
        }catch(Exception e){
            Profiler.functionError(info);
            log.error("JyTaskSendDetailFirstSendConsumer exception {}", message.getText(), e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

}
