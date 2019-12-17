package com.jd.bluedragon.distribution.consumer.abnormalorder;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrderMq;
import com.jd.bluedragon.distribution.abnormalorder.service.AbnormalOrderService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("fxmAbnormalConsumer")
public class FxmAbnormalConsumer  extends MessageBaseConsumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	AbnormalOrderService abnormalOrderService;
	
	@Override
	public void consume(Message message) {
		// 处理消息体
		this.log.debug("FxmAbnormalMessageConsumer consume --> 消息Body为：{}",message.getText());
		
		AbnormalOrderMq abnormalOrderMq = JsonHelper.fromJson(message.getText(),AbnormalOrderMq.class);
		if(abnormalOrderMq.getOrderId()!=null && abnormalOrderMq.getSystemFlag().equals("DMS")){
			// 更新数据库		
			AbnormalOrder abnormalOrder = new AbnormalOrder();
			if(abnormalOrderMq.getMemo()!=null && abnormalOrderMq.getMemo().length()>100){
				abnormalOrder.setMemo(abnormalOrderMq.getMemo().substring(0,100));
			}else{
				abnormalOrder.setMemo(abnormalOrderMq.getMemo());
			}
			/*
			 * 默认都是1 分拣中心都是取消
			 * abnormalOrder.setIsCancel(abnormalOrderMq.getIsCancel());
			 */
			abnormalOrder.setIsCancel(1);
			/***/
			abnormalOrder.setOrderId(abnormalOrderMq.getOrderId());
			abnormalOrderService.updateResult(abnormalOrder);
			this.log.debug("FxmAbnormalMessageConsumer consume : 更新完毕！");
		}else{
			this.log.warn("FxmAbnormalMessageConsumer consume : 运单号为空，数据丢弃！消息Body为：{}",message.getText());
		}
	}

}
