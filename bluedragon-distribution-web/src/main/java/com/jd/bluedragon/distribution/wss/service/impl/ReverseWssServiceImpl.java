package com.jd.bluedragon.distribution.wss.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.api.request.SpareSaleRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.spare.domain.SpareSale;
import com.jd.bluedragon.distribution.spare.service.SpareSaleService;
import com.jd.bluedragon.distribution.wss.service.ReverseWssService;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;

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
	
	public Boolean addReceiveMessage(String message) {
		this.logger.info("逆向收货消息WSS：" + message);
		
		ReverseReceiveRequest jrequest = null;
		ReceiveRequest xrequest = null;
		ReverseReceive reverseReceive = new ReverseReceive();
		
		if (XmlHelper.isXml(message, ReceiveRequest.class, null)) {
			xrequest = (ReceiveRequest) XmlHelper.toObject(message, ReceiveRequest.class);
			this.logger.info("逆向收货消息ReverseReceiveRequest：" + xrequest.toString());
			reverseReceive.setSendCode(xrequest.getSendCode());
			reverseReceive.setPackageCode(xrequest.getOrderId());
			reverseReceive.setOrderId(xrequest.getOrderId());
			reverseReceive.setReceiveType(Integer.parseInt(xrequest.getReceiveType()));
			reverseReceive.setReceiveTime(DateHelper.parseDateTime(xrequest.getOperateTime()));
			reverseReceive.setOperatorName(xrequest.getUserName());
			reverseReceive.setCanReceive(Integer.parseInt(xrequest.getCanReceive()));
			reverseReceive.setOperatorId(xrequest.getUserCode());
			if (null != xrequest.getRejectCode()) {
				reverseReceive.setRejectCode(Integer.parseInt(xrequest.getRejectCode()));
			} else {
				// 如果没有传code，设默认值100
				reverseReceive.setRejectCode(100);
			}
			reverseReceive.setRejectMessage(xrequest.getRejectMessage());
			
		} else if (JsonHelper.isJson(message, ReverseReceiveRequest.class)) {
			jrequest = JsonHelper.fromJson(message, ReverseReceiveRequest.class);
			this.logger.info("逆向收货消息ReverseReceiveRequest：" + jrequest.toString());
			
			try {
				BeanHelper.copyProperties(reverseReceive, jrequest);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				date = sdf.parse(jrequest.getReceiveTime());
				reverseReceive.setReceiveTime(date);
			} catch (Exception e) {
				this.logger.error("逆向收货消息转换失败：" + e);
				return Boolean.TRUE;
			}
		}
		
		
		this.reverseReceiveService.aftersaleReceive(reverseReceive);
		
		// 对于备件库系统,接受拒收消息后自动处理驳回接口
		if (reverseReceive.getReceiveType() == 3 && reverseReceive.getCanReceive() == 0) {
			List<String> waybillCodes = new ArrayList<String>();
			waybillCodes.add(reverseReceive.getOrderId());
			String waybillCodeIn = StringHelper.join(waybillCodes, ",", "(", ")",
					"'");
			// 根据运单号获得最后的批次号
			List<SendDetail> sendDatails = this.sendDatailDao	.querySendCodesByWaybills(waybillCodeIn);
			
			SendDetail sd =new SendDetail();
			if(sendDatails.size()>0){
				sd = sendDatails.get(0);
			}
			ReverseReject reverseReject = new ReverseReject();
			reverseReject.setBusinessType(reverseReceive.getReceiveType());
			reverseReject.setPackageCode(reverseReceive.getPackageCode());
			reverseReject.setOrderId(reverseReceive.getPackageCode());
			if(xrequest != null && null!=xrequest.getOrgId()){
				reverseReject.setOrgId(Integer.parseInt(xrequest.getOrgId()));
			}
			
			if(xrequest != null && null!=xrequest.getStoreId()){
				reverseReject.setStoreId(Integer.parseInt(xrequest.getStoreId()));
			}else{
				reverseReject.setStoreId(sd.getReceiveSiteCode());
				
			}
			
			reverseReject.setCreateSiteCode(sd.getCreateSiteCode());
			
			reverseReject.setInspector(reverseReceive.getOperatorName());
			reverseReject.setInspectorCode(reverseReceive.getOperatorId());
			reverseReject.setInspectTime(reverseReceive.getReceiveTime());
			reverseReject.setOperateTime(reverseReceive.getReceiveTime());
			reverseReject.setOperator(reverseReceive.getOperatorName());
			reverseReject.setOperatorCode(reverseReceive.getOperatorId());
			
			
			this.reverseRejectService.reject(reverseReject);
		}
		
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
	
	public Boolean addReceiveStockMessage(String message) throws Exception {
		this.logger.info("逆向收货回传库存消息：" + message);
		
		Long waybillCode = this.reverseReceiveNotifyStockService.receive(message);
		this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
		
		return Boolean.TRUE;
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
