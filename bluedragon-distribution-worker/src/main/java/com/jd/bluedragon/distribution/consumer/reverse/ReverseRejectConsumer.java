package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reverseRejectConsumer")
public class ReverseRejectConsumer extends MessageBaseConsumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ReverseRejectService reverseRejectService;
	
	@Autowired
    private SendDatailDao sendDatailDao;

	@Override
	public void consume(Message message) throws Exception {
		this.log.debug("逆向驳回消息：{}" , message.getText());

		RejectRequest request = (RejectRequest) XmlHelper.toObject(message.getText(),
				RejectRequest.class);

		if (request == null) {
			this.log.warn("逆向驳回消息序列化失败！");
			return;
		}else{
			String sourceCode = request.getSorceCode();
			if(StringHelper.isNotEmpty(sourceCode)&&sourceCode.toUpperCase().equals("ECLP"))
					return;
		}

		ReverseReject reverseReject = new ReverseReject();
		BeanHelper.copyProperties(reverseReject, request);
		
		//添加订单处理，判断是否是T单 2016-1-8
		SendDetail tsendDatail = new SendDetail();
		tsendDatail.setWaybillCode(Constants.T_WAYBILL + request.getOrderId());
		List<SendDetail> sendDatailist = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);//FIXME:无create_site_code有跨节点风险
		if (sendDatailist != null && !sendDatailist.isEmpty())
			request.setOrderId(Constants.T_WAYBILL + request.getOrderId());

		this.reverseRejectService.reject(reverseReject);
	}
	
	public static void main(String[] args){
		Message message = new Message();
		
		ReverseRejectConsumer consumer = new ReverseRejectConsumer();
		try {
			message.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><RejectRequest><businessType>1</businessType><orgId>6</orgId><cky2>6</cky2><storeId>5</storeId><operateTime>2015-07-06 15:43:50</operateTime><orderId>ASN20150706011</orderId><actualPackageQuantity>1</actualPackageQuantity><operator>linlingyue</operator><operatorCode>linlingyue</operatorCode><rejectReason>7</rejectReason><sorceCode>PRIME</sorceCode></RejectRequest>");
			consumer.consume(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
