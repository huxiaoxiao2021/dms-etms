package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
* 类描述： 逆向POP消息回传
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-5 下午4:37:48
* 版本号： v1.0
 *
 *
 *
 *
 <ReceiveRequest>
 <sendCode>190132-25023-20190520115554061-JDT000001041368</sendCode>
 <orderId>95019632501</orderId>
 <operateTime>2019-05-20 14:07:04</operateTime>
 <userCode>whlvc</userCode>
 <userName>whlvc|</userName>
 <receiveType>3</receiveType>
 <canReceive>1</canReceive>
 </ReceiveRequest>
 */
@Service("reversePopConsumer")
public class ReversePopConsumer extends MessageBaseConsumer {
	private static Log logger= LogFactory.getLog(ReversePopConsumer.class);
	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
    @Autowired
	private WaybillQueryManager waybillQueryManager;

    public void consume(Message message) throws Exception {
        String waybillCode = null;
        ReceiveRequest request = new ReceiveRequest();
        if (XmlHelper.isXml(message.getText(), ReceiveRequest.class, null)) {
        	request = (ReceiveRequest) XmlHelper.toObject(message.getText(),
        			ReceiveRequest.class);
        }else{
        	this.logger.info("非xml的消息格式，直接返回");
        	return ;
        }
        if (request == null) {
            this.logger.info("消息序列化出现异常。消息：" + message);
            return ;
        } else if (ReverseReceive.RECEIVE_TYPE_SPWMS.toString().equals(request.getReceiveType())
                && ReverseReceive.RECEIVE.toString().equals(request.getCanReceive())) {
            // 获取原运单号
            if(request.getSendCode().indexOf("-")!=-1 && request.getSendCode().split("-").length==4){
                waybillCode = request.getSendCode().split("-")[3];
                BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
                if(oldWaybill1.getData()!=null && StringUtils.isNotBlank(oldWaybill1.getData().getWaybillCode())){
                    waybillCode = oldWaybill1.getData().getWaybillCode();
                }
            }else{
                logger.error("reversePopConsumer备件库回传收货消息格式不正确，未获取到对应运单号"+message.getText());
                return;
            }
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
        if(!result) throw new Exception("逆向收货回传POP消息失败"+waybillCode);
    }
    
}
