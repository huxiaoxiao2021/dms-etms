package com.jd.bluedragon.distribution.consumer.businessIntercept;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.bluedragon.distribution.jy.service.exception.JyBusinessInterceptExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 拦截报表加工后的拦截明细消息消费逻辑
 *
 * @author fanggang7
 * @time 2024-01-14 17:30:22 周日
 */
@Service("dmsBusinessInterceptReportConsumer")
public class DmsBusinessInterceptReportConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(DmsBusinessInterceptReportConsumer.class);

    @Autowired
    private JyBusinessInterceptExceptionService businessInterceptExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            // 消息格式错误，加入自定义告警
            String profilerKey = "DMSWEB.DmsBusinessInterceptReportConsumer.consume.badJsonMessage";
            Profiler.businessAlarm(profilerKey, MessageFormat.format("分拣业务操作拦截消息MQ-消息体非JSON格式，businessId为【{0}】", message.getBusinessId()));
            return;
        }
        BusinessInterceptReport msgDto = JsonHelper.fromJsonUseGson(message.getText(), BusinessInterceptReport.class);
        Result<Boolean> handleResult = businessInterceptExceptionService.consumeDmsBusinessInterceptReport(msgDto);
        if(!handleResult.isSuccess()){
            log.error("DmsBusinessInterceptReportConsumer fail " + JSON.toJSONString(handleResult));
            throw new RuntimeException("DmsBusinessInterceptReportConsumer 处理失败 " + handleResult.getMessage());
        }
    }
}
