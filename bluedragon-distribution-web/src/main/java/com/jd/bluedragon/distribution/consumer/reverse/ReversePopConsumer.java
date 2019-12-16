package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
* 类描述： 逆向POP消息回传
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-5 下午4:37:48
* 版本号： v1.0
 */
@Service("reversePopConsumer")
public class ReversePopConsumer extends MessageBaseConsumer {
	private static Logger log = LoggerFactory.getLogger(ReversePopConsumer.class);
	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
    
    public void consume(Message message) throws Exception {
        String waybillCode = null;
        ReceiveRequest request = new ReceiveRequest();
        if (XmlHelper.isXml(message.getText(), ReceiveRequest.class, null)) {
        	request = (ReceiveRequest) XmlHelper.toObject(message.getText(),
        			ReceiveRequest.class);
        }else{
        	this.log.info("非xml的消息格式，直接返回");
        	return ;
        }
        if (request == null) {
            this.log.info("消息序列化出现异常。消息：{}", message);
            return ;
        } else if (ReverseReceive.RECEIVE_TYPE_SPWMS.toString().equals(request.getReceiveType())
                && ReverseReceive.RECEIVE.toString().equals(request.getCanReceive())) {
            waybillCode = request.getOrderId();
        } else {
            this.log.info("消息来源：{}", request.getReceiveType());
            return ;
        }
        
        if(StringUtils.isEmpty(waybillCode)){
        	 this.log.info("运单号为空：");
        	return ;
        }
        boolean result = this.reverseSendPopMessageService.sendPopMessage(waybillCode);
        this.log.info("逆向收货回传POP消息:{}-{}",message, result);
        if(!result) throw new Exception("逆向收货回传POP消息失败"+waybillCode);
    }
    
}
