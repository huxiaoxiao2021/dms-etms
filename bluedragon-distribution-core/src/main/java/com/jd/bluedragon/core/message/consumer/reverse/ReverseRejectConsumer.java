package com.jd.bluedragon.core.message.consumer.reverse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.etms.message.Message;

@Service("reverseRejectConsumer")
public class ReverseRejectConsumer extends MessageBaseConsumer {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseRejectService reverseRejectService;

	@Override
	public void consume(Message message) throws Exception {
		this.logger.info("逆向驳回消息：" + message.getContent());

		RejectRequest request = (RejectRequest) XmlHelper.toObject(message.getContent(),
				RejectRequest.class);

		if (request == null) {
			this.logger.info("逆向驳回消息序列化失败！");
			return;
		}else{
			String sourceCode = request.getSorceCode();
			if(StringHelper.isNotEmpty(sourceCode)&&sourceCode.toUpperCase().equals("ECLP"))
					return;
		}

		ReverseReject reverseReject = new ReverseReject();
		BeanHelper.copyProperties(reverseReject, request);

		this.reverseRejectService.reject(reverseReject);
	}
	
	public static void main(String[] args){
		Message message = new Message();
		
		ReverseRejectConsumer consumer = new ReverseRejectConsumer();
		try {
			message.setContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><RejectRequest><businessType>1</businessType><orgId>6</orgId><cky2>6</cky2><storeId>5</storeId><operateTime>2015-07-06 15:43:50</operateTime><orderId>ASN20150706011</orderId><actualPackageQuantity>1</actualPackageQuantity><operator>linlingyue</operator><operatorCode>linlingyue</operatorCode><rejectReason>7</rejectReason><sorceCode>PRIME</sorceCode></RejectRequest>");
			consumer.consume(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
