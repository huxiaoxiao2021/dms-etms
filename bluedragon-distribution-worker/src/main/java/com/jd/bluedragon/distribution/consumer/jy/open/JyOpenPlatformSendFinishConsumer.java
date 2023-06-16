package com.jd.bluedragon.distribution.consumer.jy.open;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.MessageException;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.openplateform.send.JyOpenSendExtraHandleService;
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
 * 消费开放平台发货完成消息
 * @author fanggang7
 * @time 2023-05-26 16:18:39 周五
 */
@Service("jyOpenPlatformSendFinishConsumer")
public class JyOpenPlatformSendFinishConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(JyOpenPlatformSendFinishConsumer.class);

    @Autowired
    private JyOpenSendExtraHandleService jyOpenSendExtraHandleService;

    @Override
    public void consume(Message message) throws Exception{
        CallerInfo info = Profiler.registerInfo("JyOpenPlatformSendFinishConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("JyOpenPlatformSendFinishConsumer 发货完成消息 消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            // 将mq消息体转换成SendDetail对象
            JYCargoOperateEntity jyCargoOperate = JsonHelper.fromJson(message.getText(), JYCargoOperateEntity.class);
            if (jyCargoOperate == null || StringHelper.isEmpty(jyCargoOperate.getBarcode())) {
                log.warn("JyOpenPlatformSendFinishConsumer:消息体[{}]转换实体失败或没有合法的包裹号",message.getText());
                return;
            }
            // 发送任务完成和发货明细消息给城配
            final Result<Boolean> handleResult = jyOpenSendExtraHandleService.afterOpenPlatformSendFinish(jyCargoOperate);
            if (!handleResult.isSuccess()) {
                log.error("JyOpenPlatformSendFinishConsumer handle fail jyOpenSendExtraHandleService.afterOpenPlatformSendFinish {}", JsonHelper.toJson(handleResult));
                throw new MessageException("处理失败 " +  handleResult.getMessage());
            }
        }catch(Exception e){
            Profiler.functionError(info);
            log.error("JyOpenPlatformSendFinishConsumer exception {}", message.getText(), e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

}
