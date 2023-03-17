package com.jd.bluedragon.distribution.consumer.jy.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CargoDetailServiceManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
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
 * 大维度分批拆分批次， 下游批处理
 */
@Service("jyCollectDataInitSplitConsumer")
public class JyCollectDataInitSplitConsumer extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(JyCollectDataInitSplitConsumer.class);

    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    private JyCollectService jyCollectService;
    @Autowired
    private VosManager vosManager;
    @Autowired
    private BaseMajorManager baseMajorManager;


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyCollectDataInitSplitConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyCollectDataInitSplitConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyCollectDataInitSplitConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        InitCollectDto mqBody = JsonHelper.fromJson(message.getText(),InitCollectDto.class);
        if(mqBody == null){
            logger.error("JyCollectDataInitSplitConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("消费处理 jyCollectDataInitSplitConsumer 开始，内容{}",message.getText());
        }
        if(!deal(mqBody)){
            //处理失败 重试
            logger.error("消费处理 jyCollectDataInitSplitConsumer 失败，内容{}",message.getText());
            throw new JyBizException("jyCollectDataInitSplitConsumer消费处理失败" + message.getBusinessId());
        }else{
            if(logger.isInfoEnabled()) {
                logger.info("消费处理 jyCollectDataInitSplitConsumer 成功，内容{}", message.getText());
            }
        }
    }

    /**
     * 处理逻辑
     * @param initCollectDto
     * @return
     */
    private boolean deal(InitCollectDto initCollectDto){
        CollectInitSplitService collectInItSplitService = CollectInitSplitServiceFactory.getCollectInitSplitService(initCollectDto.getOperateNode());
        return collectInItSplitService.splitBeforeInit(initCollectDto);
    }

}
