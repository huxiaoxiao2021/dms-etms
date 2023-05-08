package com.jd.bluedragon.distribution.consumer.jy.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CargoDetailServiceManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectInitSplitServiceFactory;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectInitSplitService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运集齐
 * 按批拆分之后，批处理初始化
 */
@Service("JyCollectDataSplitBatchInitConsumer")
public class JyCollectDataSplitBatchInitConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(JyCollectDataSplitBatchInitConsumer.class);

    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    private JyCollectService jyCollectService;
    @Autowired
    private VosManager vosManager;
    @Autowired
    private BaseMajorManager baseMajorManager;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyCollectDataSplitBatchInitConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyCollectDataSplitBatchInitConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyCollectDataSplitBatchInitConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        InitCollectSplitDto mqBody = JsonHelper.fromJson(message.getText(),InitCollectSplitDto.class);
        if(mqBody == null){
            logger.error("JyCollectDataSplitBatchInitConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("消费处理JyCollectDataSplitBatchInitConsumer开始，内容{}",message.getText());
        }

        boolean consumeRes = true;
        try {
            consumeRes = deal(mqBody);
        }catch (Exception e) {
            logger.error("JyCollectDataSplitBatchInitConsumer.deal服务异常，businessId={}, errMsg={},内容{}", message.getBusinessId(), e.getMessage(), message.getText(), e);
            throw new JyBizException("JyCollectDataSplitBatchInitConsumer集齐拆分后初始化服务消费处理异常：" + message.getBusinessId());
        }

        if(!consumeRes){
            //处理失败 重试
            logger.error("消费处理JyCollectDataSplitBatchInitConsumer失败，内容{}",message.getText());
            throw new JyBizException("JyCollectDataSplitBatchInitConsumer集齐拆分后初始化服务消费处理失败" + message.getBusinessId());
        }else{
            if(logger.isInfoEnabled()) {
                logger.info("消费处理JyCollectDataSplitBatchInitConsumer成功，内容{}", message.getText());
            }
        }
    }

    /**
     * 处理逻辑
     * @param initCollectSplitDto
     * @return
     */
    private boolean deal(InitCollectSplitDto initCollectSplitDto){
        CollectInitSplitService collectInItSplitService = CollectInitSplitServiceFactory.getCollectInitSplitService(initCollectSplitDto.getOperateNode());
        return collectInItSplitService.initAfterSplit(initCollectSplitDto);
    }

}
