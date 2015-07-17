package com.jd.bluedragon.core.message.consumer.reverse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.message.Message;
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
	private static Log logger= LogFactory.getLog(ReversePopConsumer.class);
	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
    
    public void consume(Message message) {
        String waybillCode = null;
        ReverseReceiveRequest request = (ReverseReceiveRequest) XmlHelper.toObject(message.getContent(),
                ReverseReceiveRequest.class);
        if (request == null) {
            this.logger.info("消息序列化出现异常。消息：" + message);
            return ;
        } else if (ReverseReceive.RECEIVE_TYPE_SPWMS.equals(request.getReceiveType())
                && ReverseReceive.RECEIVE.equals(request.getCanReceive())) {
            waybillCode = request.getOrderId();
        } else {
            this.logger.info("消息来源：" + request.getReceiveType());
            return ;
        }
        
        if(StringUtils.isEmpty(waybillCode)){
        	 this.logger.info("运单号为空：");
        	return ;
        }
        boolean result = this.reverseSendPopMessageService.sendPopMessage(waybillCode);
        this.logger.info("逆向收货回传POP消息【" + message + "】" + result);
    
    	
    	
    }
    
}
