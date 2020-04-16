package com.jd.bluedragon.distribution.consumer.abnormalorder;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分拣滞留
 * @author jinjingcheng
 * @date 2020/4/1
 */
@Service("strandConsumer")
public class StrandConsumer extends MessageBaseConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private StrandService strandService;


    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        log.info("StrandConsumer consume --> 消息Body为【{}】",message.getText());
        if(StringUtils.isBlank(message.getText())){
            log.error("消息topic:{}的消息体内容为空", message.getTopic());
            return;
        }
        StrandReportRequest reportRequest = JsonHelper.fromJson(message.getText(),StrandReportRequest.class);
        if(reportRequest == null){
            log.error("消息topic:{}的消息体内容转StrandReportRequest为null,消息体内容:{}", message.getTopic(),
                    message.getText());
            return;
        }
        //执行滞留上报 和发货取消
        strandService.strandReportAndCancelDelivery(reportRequest);
    }
}
