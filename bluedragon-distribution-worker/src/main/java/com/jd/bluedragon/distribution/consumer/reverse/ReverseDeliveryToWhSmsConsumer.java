package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.log.BizTypeConstants;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.log.OperateTypeConstants;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import com.jd.bluedragon.external.crossbow.whems.domain.WuHanEMSResponse;
import com.jd.bluedragon.external.crossbow.whems.domain.WuHanEMSWaybillEntityDto;
import com.jd.bluedragon.external.crossbow.whems.manager.WuHanEMSBusinessManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by wuzuxiang on 2017/2/7.
 */
@Service("reverseDeliveryToWhSmsConsumer")
public class ReverseDeliveryToWhSmsConsumer extends MessageBaseConsumer{

    private static final Logger log = LoggerFactory.getLogger(ReverseDeliveryToWhSmsConsumer.class);

    @Autowired
    private ReverseDeliveryService reverseDelivery;

    @Autowired
    @Qualifier("wuHanEMSBusinessManager")
    private WuHanEMSBusinessManager wuHanEMSBusinessManager;
    @Autowired
    private LogEngine logEngine;

    @Override
    public void consume(Message message) throws Exception {
        long startTime=new Date().getTime();


        log.debug("反向推送武汉邮政的自消费，内容为：{}" , message.getText());

        CallerInfo callerInfo = Profiler.registerInfo("dms.worker.ReverseDeliveryToWhjmq.topic.bd.dms.whSms.mqSmsConsumer.consume",
                Constants.UMP_APP_NAME_DMSWORKER, false, true);
        WuHanEMSWaybillEntityDto whEmsDto = JsonHelper.fromJson(message.getText(),WuHanEMSWaybillEntityDto.class);
        if (whEmsDto == null) {
            log.warn("武汉邮政消费消息反序列化失败,jmq.businessId:{}", message.getBusinessId());
            return;
        }

        WuHanEMSResponse response = wuHanEMSBusinessManager.doRestInterface(whEmsDto);

        if (response == null || response.getPlaintextData() == null) {
            log.warn("反向推送武汉邮政消息异常，将重新发送消息，businessID：{}", message.getBusinessId());
            reverseDelivery.pushWhemsWaybill(message.getBusinessId());//消息的业务主键就是运单号
            return;
        }

        if (!"000".equals(response.getPlaintextData().getResultCode())) {
            log.error("反向推送武汉邮政消息失败，返回内容为：{}，只记录日志不重新发送，businessID:{}",
                    JsonHelper.toJson(response), message.getBusinessId());
        }
        long endTime = new Date().getTime();

        JSONObject request=new JSONObject();
        request.put("waybillCode",message.getBusinessId());


        JSONObject logResponse=new JSONObject();
        logResponse.put("keyword1", message.getBusinessId());
        logResponse.put("keyword2", whEmsDto.getBagId());
        logResponse.put("keyword3", message.getTopic());
        logResponse.put("keyword4", Long.parseLong(response.getPlaintextData().getResultCode()));
        logResponse.put("content", JsonHelper.toJson(response));


        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                .bizType(BizTypeConstants.DELIVERY)
                .operateType(OperateTypeConstants.REVERSE_DELIVERY)
                .operateRequest(request)
                .operateResponse(logResponse)
                .methodName("ReverseReceiveNotifyStockService#nodifyStock")
                .build();
        logEngine.addLog(businessLogProfiler);

        logEngine.addLog(businessLogProfiler);


        //记录systemLog 方便查询 参数顺序依次为 1.waybillCode，2.packageCode，3.mq的topic，4.接口返回的code，5.武汉邮政返回结果，6.自定义的type
        SystemLogUtil.log(message.getBusinessId(),whEmsDto.getBagId(),message.getTopic(),
                Long.parseLong(response.getPlaintextData().getResultCode()),JsonHelper.toJson(response),89757L);



        Profiler.registerInfoEnd(callerInfo);
    }

}
