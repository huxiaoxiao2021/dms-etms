package com.jd.bluedragon.distribution.consumer.cold;

import com.google.gson.JsonSyntaxException;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionMQModel;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.coldChain.domain.SendMQModel;
import com.jd.bluedragon.distribution.coldChain.domain.SendVO;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainExternalServiceImpl;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/12
 * @Description:
 */
@Service("coldChainSendConsumer")
public class SendConsumer extends MessageBaseConsumer {

    private static Logger log = LoggerFactory.getLogger(InspectionConsumer.class);

    @Autowired
    private ColdChainExternalServiceImpl coldChainExternalService;

    @Override
    public void consume(Message message) throws Exception {

        if(null == message || null == message.getText()){
            this.log.warn("coldChainSendConsumer消息为空，执行结束。。。");
            return;
        }
        SendMQModel model = null;
        try{
            model = JsonHelper.fromJsonUseGson(message.getText(), SendMQModel.class);
        }catch(JsonSyntaxException e){
            this.log.error("coldChainSendConsumer消息消费失败，消息businessID为{}，内容为{}",message.getBusinessId(),message.getText(),e);
        }

        if(model == null ){
            this.log.error("coldChainSendConsumer消息消费失败,解析JSON后为空，消息businessID为{}，内容为{}",message.getBusinessId(),message.getText());
            return;
        }

        SendVO req = new SendVO();

        req.setTransPlanCode(model.getTransPlanCode());
        req.setReceiveSiteCode(model.getReceiveSiteCode());
        req.setBarCodes(model.getBarCodes());
        req.setSiteCode(model.getOperateSiteCode());
        req.setUserCode(model.getUserCode());
        req.setUserName(model.getUserName());
        req.setOperateTime(model.getOperateTime());
        req.setNeedCheck(false);

        //不需要关心返回值
        InvokeResult<Boolean> result = coldChainExternalService.send(req);
        if(log.isInfoEnabled()){
            log.info("coldChainSendConsumer done {} , {}",JsonHelper.toJson(req),JsonHelper.toJson(result));
        }
    }
}
