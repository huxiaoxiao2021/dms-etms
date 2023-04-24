package com.jd.bluedragon.distribution.consumer.jy.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.collect.UnloadScanCollectDealDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jyInspectUpdateCollectStatusConsumer")
public class JyInspectUpdateCollectStatusConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyInspectUpdateCollectStatusConsumer.class);

    @Autowired
    private JyCollectService jyCollectService;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyInspectUpdateCollectStatusConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyInspectUpdateCollectStatusConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyInspectUpdateCollectStatusConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        UnloadScanCollectDealDto mqBody = JsonHelper.fromJson(message.getText(),UnloadScanCollectDealDto.class);
        if(mqBody == null){
            log.error("JyInspectUpdateCollectStatusConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(log.isInfoEnabled()){
            log.info("消费处理JyInspectUpdateCollectStatusConsumer开始，内容{}",message.getText());
        }

        boolean consumeRes = true;
        try{
            consumeRes = deal(mqBody);
        }catch (Exception e) {
            log.error("JyInspectUpdateCollectStatusConsumer.deal服务异常，businessId={}, errMsg={},内容{}", message.getBusinessId(), e.getMessage(), message.getText(), e);
            throw new JyBizException("JyInspectUpdateCollectStatusConsumer验货补偿修改集齐状态消费处理异常：" + message.getBusinessId());
        }

        if(!consumeRes){
            //处理失败 重试
            log.error("消费处理JyInspectUpdateCollectStatusConsumer失败，内容{}",message.getText());
            throw new JyBizException("JyInspectUpdateCollectStatusConsumer验货补偿修改集齐状态消费处理失败" + message.getBusinessId());
        }else{
            if(log.isInfoEnabled()) {
                log.info("消费处理JyInspectUpdateCollectStatusConsumer成功，内容{}", message.getText());
            }
        }
    }

    /**
     * 处理逻辑
     * @param dto
     * @return
     */
    private boolean deal(UnloadScanCollectDealDto dto){
        return jyCollectService.updateSingleCollectStatusHandler(dto);
    }

}
