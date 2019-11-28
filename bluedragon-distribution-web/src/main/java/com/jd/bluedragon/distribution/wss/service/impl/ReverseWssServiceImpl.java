package com.jd.bluedragon.distribution.wss.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.api.request.SpareSaleRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.spare.domain.SpareSale;
import com.jd.bluedragon.distribution.spare.service.SpareSaleService;
import com.jd.bluedragon.distribution.wss.service.ReverseWssService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.XmlHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reverseWssService")
@Deprecated
public class ReverseWssServiceImpl implements ReverseWssService {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ReverseRejectService reverseRejectService;
	
	@Autowired
	private ReverseReceiveService reverseReceiveService;
	
	@Autowired
	private SpareSaleService spareSaleService;
	
	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
	
	@Autowired
	private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;
	
	@Autowired
	private SendDatailDao sendDatailDao;
	
	public Boolean addRejectMessage(String message) {
		this.logger.info("逆向驳回消息：" + message);
		
		RejectRequest request = (RejectRequest) XmlHelper.toObject(message, RejectRequest.class);
		
		if (request == null) {
			this.logger.info("逆向驳回消息序列化失败！");
			return Boolean.TRUE;
		}
		
		ReverseReject reverseReject = new ReverseReject();
		BeanHelper.copyProperties(reverseReject, request);
		
		this.reverseRejectService.reject(reverseReject);
		
		return Boolean.TRUE;
	}
	
	public Boolean addReceivePopMessage(String message) {
		this.logger.info("逆向收货回传POP消息：" + message);
		String waybillCode = null;
		ReverseReceiveRequest request = (ReverseReceiveRequest) XmlHelper.toObject(message,
				ReverseReceiveRequest.class);
		if (request == null) {
			this.logger.info("消息序列化出现异常。消息：" + message);
			return Boolean.TRUE;
		} else if (ReverseReceive.RECEIVE_TYPE_SPWMS.equals(request.getReceiveType())) {
			waybillCode = WaybillUtil.getWaybillCode(request.getPackageCode());
		} else {
			this.logger.info("消息来源：" + request.getReceiveType());
			return Boolean.TRUE;
		}
		boolean result = this.reverseSendPopMessageService.sendPopMessage(waybillCode);
		this.logger.info("逆向收货回传POP消息【" + message + "】" + result);
		return result;
	}
	
	public Boolean addSaleMessage(String message) {
		this.logger.info("addSaleMessage --> 逆向接收配送损商品销售信息：" + message);
		SpareSaleRequest spareSaleRequest = JsonHelper.fromJson(message, SpareSaleRequest.class);
		SpareSale spareSale = this.checkToSpareSale(spareSaleRequest);
		if (spareSale != null) {
			this.spareSaleService.addOrUpdate(spareSale);
		}
		return Boolean.TRUE;
	}
	
	private SpareSale checkToSpareSale(SpareSaleRequest request) {
		if (request == null) {
			this.logger.info("checkSpareSale-->request is null");
			return null;
		}
		
		if (StringUtils.isBlank(request.getSpareCode()) || request.getProductId() == null
				|| StringUtils.isBlank(request.getProductName()) || request.getSaleAmount() == null
				|| StringUtils.isBlank(request.getSaleTime())) {
			this.logger.info("checkSpareSale-->param error，参数："
					+ ObjectMapHelper.makeObject2Map(request));
			return null;
		}
		
		SpareSale spareSale = new SpareSale();
		spareSale.setSpareCode(request.getSpareCode());
		spareSale.setProductId(request.getProductId());
		spareSale.setProductName(request.getProductName());
		spareSale.setSaleAmount(request.getSaleAmount());
		spareSale.setSaleTime(DateHelper.parseDate(request.getSaleTime(),
				Constants.DATE_TIME_FORMAT));
		return spareSale;
	}
}
