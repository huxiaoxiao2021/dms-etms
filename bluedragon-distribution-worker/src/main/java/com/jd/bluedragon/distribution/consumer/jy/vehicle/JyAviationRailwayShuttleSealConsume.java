package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.send.BatchCodeShuttleSealDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyAviationRailwaySendSealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Date 2023/8/7 10:52
 * @Description 监听摆渡封车成功，按批次处理空铁发货岗干支任务摆渡已封状态
 */
@Service("jyAviationRailwayShuttleSealConsume")
public class JyAviationRailwayShuttleSealConsume extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyAviationRailwayShuttleSealConsume.class);
    @Autowired
    private JyAviationRailwaySendSealService jyAviationRailwaySendSealService;

    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyAviationRailwayShuttleSealConsume.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyAviationRailwayShuttleSealConsume consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyAviationRailwayShuttleSealConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        BatchCodeShuttleSealDto mqBody = JsonHelper.fromJson(message.getText(), BatchCodeShuttleSealDto.class);
        if(mqBody == null){
            log.error("JyAviationRailwayShuttleSealConsume consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(CollectionUtils.isEmpty(mqBody.getSuccessSealBatchCodeList())) {
            log.error("JyAviationRailwayShuttleSealConsume consume -->JSON转换后为空，批次号内容为【{}】", message.getText());
            return;
        }

        if(log.isInfoEnabled()){
            log.info("消费处理JyAviationRailwayShuttleSealConsume开始，内容{}",message.getText());
        }
        try{

            jyAviationRailwaySendSealService.batchCodeShuttleSealMark(mqBody);
        }catch (Exception e) {
            Long time = System.currentTimeMillis();
            log.error("JyAviationRailwayShuttleSealConsume consume -->消费处理异常，businessId={},异常时间={}，内容为【{}】", message.getBusinessId(), time, message.getText(),e);
            throw new JyBizException(String.format("传摆封车处理空铁发货干支任务消费异常：%s,异常时间:%s",message.getBusinessId(), time));
        }

    }



}
