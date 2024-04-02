package com.jd.bluedragon.distribution.consumer.router;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.router.IRouterDynamicLineReplacePlanService;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMq;
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
 * 路由线路临时动态更换消息消费
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-01 18:13:55 周一
 */
@Service("dynamicLineReplacePlanConsumer")
public class DynamicLineReplacePlanConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DynamicLineReplacePlanConsumer.class);

    @Autowired
    private IRouterDynamicLineReplacePlanService dynamicLineReplacePlanService;

    @Override
    public void consume(Message message) throws Exception{
        CallerInfo info = Profiler.registerInfo("DynamicLineReplacePlanConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("DynamicLineReplacePlanConsumer 拣运发货流向纬度首次扫描消息消费-消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            // 将mq消息体转换成SendDetail对象
            DynamicLineReplacePlanMq dynamicLineReplacePlanMq = JsonHelper.fromJson(message.getText(), DynamicLineReplacePlanMq.class);
            if (dynamicLineReplacePlanMq == null) {
                log.warn("DynamicLineReplacePlanConsumer:消息体[{}]转换实体失败或没有合法的业务数据", message.getText());
                return;
            }
            final Result<Boolean> result = dynamicLineReplacePlanService.consumeDynamicLineReplacePlan(dynamicLineReplacePlanMq);
            if(!result.isSuccess()){
                log.error("DynamicLineReplacePlanConsumer exception {} {}", message.getText(), JsonHelper.toJson(result));
                throw new Exception();
            }
        }catch(Exception e){
            Profiler.functionError(info);
            log.error("DynamicLineReplacePlanConsumer exception {}", message.getText(), e);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
