package com.jd.bluedragon.distribution.reverse.service;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.DtcDataReceiverManager;
import com.jd.bluedragon.core.message.consumer.MessageConstant;
import com.jd.bluedragon.core.message.producer.MessageProducer;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.reverse.domain.*;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.spare.domain.Spare;
import com.jd.bluedragon.distribution.spare.service.SpareService;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.etms.message.produce.client.MessageClient;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.loss.client.BlueDragonWebService;
import com.jd.loss.client.LossProduct;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Service("reverseSendService")
public class ReverseSendServiceImpl implements ReverseSendService {

	private final Logger logger = Logger.getLogger(ReverseSendServiceImpl.class);

	@Autowired
	private MessageClient messageClient;

	@Autowired
	WaybillQueryApi waybillQueryApi;

	@Autowired
	private WaybillCommonService waybillCommonService;

	@Autowired
	private DtcDataReceiverManager dtcDataReceiverManager;
//	private Inbound inbound;

	@Autowired
	ReverseSpareService reverseSpareService;

	@Autowired
	SpareService spareService;

	@Autowired
	private SendMDao sendMDao;

	@Autowired
	private SendDatailDao sendDatailDao;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private BaseService tBaseService;
	
	@Autowired
	private BaseWmsService baseWmsService;

	@Autowired
	private BlueDragonWebService lossWebService;

    @Autowired
    private MessageProducer messageProducer;

	// 自营
	public static final Integer businessTypeONE = 10;
	// 退货
	public static final Integer businessTypeTWO = 20;
	// 第三方
	public static final Integer businessTypeTHR = 30;
	
	// 退仓储
	public static final Integer RECEIVE_TYPE_WMS = 1;
	// 退售后
	public static final Integer RECEIVE_TYPE_AMS = 2;
	// 退备件库
	public static final Integer RECEIVE_TYPE_SPARE = 3;
	
	
	public static final Map<String, String> tempMap = new ConcurrentHashMap<String, String>();
	
	public static final List<Integer> ASION_NO_ONE_SITE_CODE_LIST = new ArrayList<Integer>();

	static {
		ReverseSendServiceImpl.tempMap.put("101", "1");
		ReverseSendServiceImpl.tempMap.put("102", "16");
		ReverseSendServiceImpl.tempMap.put("103", "256");
		ReverseSendServiceImpl.tempMap.put("201", "2");
		ReverseSendServiceImpl.tempMap.put("202", "32");
		ReverseSendServiceImpl.tempMap.put("203", "512");
		ReverseSendServiceImpl.tempMap.put("301", "64");
		ReverseSendServiceImpl.tempMap.put("302", "1024");
		ReverseSendServiceImpl.tempMap.put("303", "4");
		ReverseSendServiceImpl.tempMap.put("401", "8");
		ReverseSendServiceImpl.tempMap.put("402", "128");
		initAsionNoOneSiteCodeList();
	}
	
	/**
	 * 初始化亚一中件仓站点集合
	 */
	private static void initAsionNoOneSiteCodeList() {
		String asionNoOneSiteCodesRaw = PropertiesHelper.newInstance()
				.getValue(Constants.ASION_NO_ONE_SITE_CODES_KEY);
		if (asionNoOneSiteCodesRaw != null) {
			String[] asionNoOneSiteCodes = asionNoOneSiteCodesRaw
					.split(Constants.SEPARATOR_COMMA);
			for (String siteCodeRaw : asionNoOneSiteCodes) {
				if (siteCodeRaw != null) {
					Integer siteCode = null;
					try {
						siteCode = Integer.valueOf(siteCodeRaw.trim());
					} catch (Exception e) {
						// do nothing
					}
					if (siteCode != null) {
						ASION_NO_ONE_SITE_CODE_LIST.add(siteCode);
					}
				}
			}
		}
		System.out.println("initAsionNoOneSiteCodeList result:"
				+ ASION_NO_ONE_SITE_CODE_LIST);
	}

	@JProfiler(jKey= "DMSWORKER.ReverseSendService.findSendwaybillMessage", mState = {JProEnum.TP})
	public boolean findSendwaybillMessage(Task task) throws Exception {
		if (task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null || task.getKeyword2() == null) {
			return true;
		}
		this.logger.info("task处理的批次号为" + task.getBoxCode());
		try {
			List<SendM> allsendList = null;
			List<SendM> sendList = new ArrayList<SendM>();

			SendM tSendM = null;
			Boolean bl = false;

			allsendList = this.sendMDao.selectBySiteAndSendCode(task.getCreateSiteCode(), task.getBoxCode());

			for (int i = 0; i < allsendList.size(); i++) {
				tSendM = allsendList.get(i);
				if (tSendM.getSendType() == 20) {
					sendList.add(tSendM);
				}
			}

			this.logger.info("处理退货数据开始");
			this.logger.info("处理SendM数量为" + sendList.size());
			if (sendList == null || sendList.isEmpty()) {
				return true;
			}
			SendM sendM = new SendM();
			Integer siteType = 0;
			Integer baseOrgId = 0;
			String baseStoreId = "";

			sendM = sendList.get(0);
			BaseStaffSiteOrgDto bDto = null;
			try {
				bDto = this.baseMajorManager.getBaseSiteBySiteId(sendM.getReceiveSiteCode());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null != bDto) {
				siteType = bDto.getSiteType();
				baseOrgId = bDto.getOrgId();
				baseStoreId = bDto.getCustomCode();
				this.logger.info("站点类型为" + siteType);
				this.logger.info("baseOrgId" + baseOrgId);
				this.logger.info("baseStoreId" + baseStoreId);
			}

			String asm_type = PropertiesHelper.newInstance().getValue("asm_type");
			String wms_type = PropertiesHelper.newInstance().getValue("wms_type");
			String spwms_type = PropertiesHelper.newInstance().getValue("spwms_type");
			if (siteType == Integer.parseInt(asm_type)) {
				bl = this.sendReverseMessageToAms(sendM);
			} else if (siteType == Integer.parseInt(wms_type)) {
				// 采用批次号和目的地组合的方式来判断退货方向是否是亚一仓站点
				// 原因为批次号更加可靠，目的地判断方式需要依赖配置文件，而且目的地站点编码有变更的可能性
				// 组合方式可以保障在目的地变更或者判断失效的情况下，保障库内返的流程继续正常运行
				if (sendM.getSendCode().startsWith(Box.BOX_TYPE_WEARHOUSE)
						|| ((sendM != null) && isAsionNoOneSiteCode(sendM
								.getReceiveSiteCode())))
					bl = this.sendReverseMessageToAsiaWms(sendM, bDto);
				else
					bl = this.sendReverseMessageToWms(sendM, bDto);
			} else if (siteType == Integer.parseInt(spwms_type)) { 
				bl = this.sendReverseMessageToSpwms(sendM, baseOrgId, baseStoreId);
			} else{
				StringBuilder sb = new StringBuilder().append(asm_type).append(",").append(wms_type).append(",").append(spwms_type).append(",");
				this.logger.info("站点类型不在逆向处理范围("+sb+")内, 默认处理成功!siteName:"+bDto.getSiteName());
				bl = true;
			}
			//直接以bl的值做返回值
			return bl;
		} catch (Exception e) {
			this.logger.error("青龙逆向发货异常", e);
			return false;
		}
	}

	public boolean sendReverseMessageToAms(SendM sendM) throws Exception {
		this.logger.info("处理售后退货数据开始");
		if (sendM == null) {
			return true;
		}

		List<SendDetail> allsendList = null;
		SendDetail query = this.paramSendDetail(sendM);
		allsendList = this.sendDatailDao.queryBySiteCodeAndSendCode(query);
		for (SendDetail tSendDetail : allsendList) {
			ReverseSend send = new ReverseSend();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			send.setDispatchTime(df.format(tSendDetail.getOperateTime()));
			send.setOperatorId(tSendDetail.getCreateUserCode().toString());
			send.setOperatorName(tSendDetail.getCreateUser());
			send.setPackageCode(tSendDetail.getPackageBarcode());
			send.setSendCode(tSendDetail.getSendCode());
			send.setPickWareCode(tSendDetail.getPickupCode());

			try {
				this.messageClient.sendCustomMessage("dms_send", "VirtualTopic.bd_dms_reverse_send",
						"java.util.String", JsonHelper.toJson(send), MessageConstant.ReverseSend.getName()
								+ tSendDetail.getPackageBarcode());
				try{
					//业务流程监控, 售后埋点
					Map<String, String> data = new HashMap<String, String>();
					data.put("packageCode", tSendDetail.getPackageBarcode());
					Profiler.bizNode("Reverse_ws_dms2ams", data);
				} catch (Exception e) {
					this.logger.error("推送UMP发生异常.", e);
				}
				this.logger.info("青龙发货至售后MQ消息成功，批次号为" + sendM.getSendCode());
			} catch (Exception e) {
				throw new Exception("青龙发货至售后MQ消息异常", e);
			}
		}

		return true;
	}
	
	private void removeDuplicatedProduct(ReverseSendAsiaWms send) {
		List<com.jd.bluedragon.distribution.reverse.domain.Product> products = send
				.getProList();
		Map<String, com.jd.bluedragon.distribution.reverse.domain.Product> productIds = new HashMap<String, com.jd.bluedragon.distribution.reverse.domain.Product>();
		if (products != null) {
			for (com.jd.bluedragon.distribution.reverse.domain.Product product : products) {
				String productId = product.getProductId();
				if (productIds.containsKey(productId)) {
					productIds.get(productId).setProductNum(
							productIds.get(productId).getProductNum()
									+ product.getProductNum());
				} else {
					productIds.put(productId, product);
				}
			}
		}
		send.setProList(new ArrayList<com.jd.bluedragon.distribution.reverse.domain.Product>(productIds.values()));
	}

	@SuppressWarnings("rawtypes")
	public boolean sendReverseMessageToAsiaWms(SendM sendM, BaseStaffSiteOrgDto bDto)
			throws Exception {
		if (sendM == null) {
			return true;
		}
		
 		this.logger.info("处理亚一仓储退货数据开始,目的站点:"+sendM.getReceiveSiteCode()+" 批次号:"+sendM.getSendCode());

		try {
			Map<String, String> orderpackMap = new ConcurrentHashMap<String, String>();
			Map<String, String> orderpackMapLoss = new ConcurrentHashMap<String, String>();
			Set<String> packSet = new HashSet<String>();
			List<SendDetail> allsendList = this.sendDatailDao.findSendDetails(this.paramSendDetail(sendM));
			
			int allsendListSize = allsendList!=null?allsendList.size():0;
			this.logger.info("获得发货明细数量:"+allsendListSize);
			
			int index = 0;
			for (SendDetail tSendDetail : allsendList) {
				this.logger.info("发货明细"+(index++)+":"+tSendDetail.toString());
				packSet.add(tSendDetail.getPackageBarcode());
				if (tSendDetail.getSendType() == 20
						&& null != tSendDetail.getIsLoss()
						&& tSendDetail.getIsLoss() == 1) {
					addMapWms(orderpackMapLoss, tSendDetail.getWaybillCode(),
							tSendDetail.getPackageBarcode());
				} else {
					addMapWms(orderpackMap, tSendDetail.getWaybillCode(),
							tSendDetail.getPackageBarcode());
				}
			}
			int orderSum = orderpackMapLoss.size()+orderpackMap.size();
			int packSum = packSet.size();
			this.logger.info("orderpackMapLoss数量:"+orderpackMapLoss.size());
			this.logger.info("orderpackMap数量:"+orderpackMap.size());
			this.logger.info("总包裹数量:"+packSum);
			
			//记录一个针对任务的日志到日志表中
			SystemLog sLogAll = new SystemLog();
			sLogAll.setKeyword2(sendM.getSendCode());
			sLogAll.setKeyword3(bDto.getSiteType().toString());
			sLogAll.setKeyword4(Long.valueOf(0));
			sLogAll.setType(Long.valueOf(12004));
			sLogAll.setContent("获得发货明细数量:"+allsendListSize+",orderpackMapLoss数量:"+orderpackMapLoss.size()+",orderpackMap数量:"+orderpackMap.size()+",总包裹数量:"+packSum);
			SystemLogUtil.log(sLogAll);
			
			// 获取站点信息
			WmsSite site = new WmsSite();
			Integer orgId = bDto.getOrgId();
			String storeCode = bDto.getStoreCode();
			Integer cky2 = Integer.parseInt(storeCode.split("-")[1]);
			Integer storeId = Integer.parseInt(bDto.getCustomCode());
			site.setOrgId(orgId);
			site.setCky2(cky2);
			site.setStoreId(storeId);

			Iterator<Entry<String, String>> iter = orderpackMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String wallBillCode = (String) entry.getKey();

				ReverseSendAsiaWms newsend = null;
				String packcodes = (String) entry.getValue();
				newsend = baseWmsService.getWaybillByOrderCode(wallBillCode,packcodes,site, false);
				removeDuplicatedProduct(newsend);
				newsend.setOrderSum(orderSum);//加入总订单数及总的包裹数
				newsend.setPackSum(packSum);
				sendAsiaWMS(newsend, wallBillCode, sendM, entry, 0, bDto,orderpackMap);
			}

			// 包丢订单发车
			Iterator iterator = orderpackMapLoss.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String wallBillCode = (String) entry.getKey();
				String packcodes = (String) entry.getValue();
				// 报损总数
				int lossCount = 0;
				try {
					lossCount = this.lossWebService.getLossProductCountOrderId(wallBillCode);
				} catch (Exception e1) {
					this.logger.error("调用报损订单接口失败, 运单号为" + wallBillCode);
					throw new Exception("调用报损订单接口失败, 运单号为" + wallBillCode);
				}

				ReverseSendAsiaWms newsend = null;
				newsend = baseWmsService.getWaybillByOrderCode(wallBillCode,packcodes,site,false);
				removeDuplicatedProduct(newsend);
				if (lossCount != 0) {
					// 运单系统拿出的商品明细
					List<com.jd.bluedragon.distribution.reverse.domain.Product> sendProducts = newsend.getProList();
					List<com.jd.bluedragon.distribution.reverse.domain.Product> sendLossProducts = new ArrayList<com.jd.bluedragon.distribution.reverse.domain.Product>();
					// 报损系统拿出的报损明细
					List<LossProduct> lossProducts = this.lossWebService.getLossProductByOrderId(wallBillCode);

					int loss_Count = 0;
					if(sendProducts!=null && !sendProducts.isEmpty()){
						for (com.jd.bluedragon.distribution.reverse.domain.Product sendProduct : sendProducts) {
							for (LossProduct lossProduct : lossProducts) {
								if(lossProduct.getLossCount()==0||sendProduct.getProductNum()==0) continue;//说明已经计算完了，或者不符合计算条件，理论上无<0数据
								
								if (lossProduct.getSku().equals(sendProduct.getProductId())) {
									if (null == sendProduct.getProductLoss()|| "".equals(sendProduct.getProductLoss())) {
										loss_Count = 0;
									} else {
										loss_Count = Integer.parseInt(sendProduct.getProductLoss());
									}
	
									if (sendProduct.getProductNum()- lossProduct.getLossCount() < 0) {//说明产品全部报丢,产品num清0.报损产品项减去产品num
										lossProduct.setLossCount(lossProduct.getLossCount()-sendProduct.getProductNum());
										sendProduct.setProductLoss(String.valueOf(loss_Count+sendProduct.getProductNum()));
										sendProduct.setProductNum(0);
									} else {//说明部分报丢
										sendProduct.setProductLoss(String.valueOf(loss_Count+ lossProduct.getLossCount()));
										sendProduct.setProductNum(sendProduct.getProductNum()- lossProduct.getLossCount());
										lossProduct.setLossCount(0);
									}
								}
							}
							sendProduct.setProductPrice("0");
							sendLossProducts.add(sendProduct);
						}
					}
					newsend.setProList(sendLossProducts);
				}
				newsend.setOrderSum(orderSum);//加入总订单数及总的包裹数
				newsend.setPackSum(packSum);
				sendAsiaWMS(newsend, wallBillCode, sendM, entry, lossCount,bDto, orderpackMap);
			}
			return true;
		} catch (Exception e) {
			this.logger.error(sendM.getSendCode() + "wms发货库房失败", e);
			return false;
		}

	}	
	
	@SuppressWarnings("rawtypes")
	public boolean sendReverseMessageToWms(SendM sendM, BaseStaffSiteOrgDto bDto)
			throws Exception {

		this.logger.info("处理仓储退货数据开始");
		if (sendM == null) {
			return true;
		}
		this.logger.info("处理仓储退货数据开始,目的站点:"+sendM.getReceiveSiteCode()+" 批次号:"+sendM.getSendCode());
		
		try {
			Map<String, String> orderpackMap = new ConcurrentHashMap<String, String>();
			Map<String, String> orderpackMapLoss = new ConcurrentHashMap<String, String>();
			List<SendDetail> allsendList = this.sendDatailDao.findSendDetails(this.paramSendDetail(sendM));
			
			int allsendListSize = allsendList!=null?allsendList.size():0;
			this.logger.info("获得发货明细数量:"+allsendListSize);
			
			int index = 0;
			for (SendDetail tSendDetail : allsendList) {
				this.logger.info("发货明细"+(index++)+":"+tSendDetail.toString());
				if (tSendDetail.getSendType() == 20
						&& null != tSendDetail.getIsLoss()
						&& tSendDetail.getIsLoss() == 1) {
					addMapWms(orderpackMapLoss, tSendDetail.getWaybillCode(),
							tSendDetail.getPackageBarcode());
				} else {
					addMapWms(orderpackMap, tSendDetail.getWaybillCode(),
							tSendDetail.getPackageBarcode());
				}
			}
			this.logger.info("orderpackMapLoss数量:"+orderpackMapLoss.size());
			this.logger.info("orderpackMap数量:"+orderpackMap.size());
			
			//记录一个针对任务的日志到日志表中
			SystemLog sLogAll = new SystemLog();
			sLogAll.setKeyword2(sendM.getSendCode());
			sLogAll.setKeyword3(bDto.getSiteType().toString());
			sLogAll.setKeyword4(Long.valueOf(0));
			sLogAll.setType(Long.valueOf(12004));
			sLogAll.setContent("获得发货明细数量:"+allsendListSize+",orderpackMapLoss数量:"+orderpackMapLoss.size()+",orderpackMap数量:"+orderpackMap.size());
			SystemLogUtil.log(sLogAll);
			
			Iterator<Entry<String, String>> iter = orderpackMap.entrySet()
					.iterator();

			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String wallBillCode = (String) entry.getKey();

				ReverseSendWms send = null;
				send = tBaseService.getWaybillByOrderCode(wallBillCode);
				if (send == null){
					this.logger.info("调用运单接口获得数据为空,运单号" + wallBillCode);
					continue;
				}

                //迷你仓、 ECLP单独处理
                if(!isSpecial(send,wallBillCode)){
                    sendWMS(send, wallBillCode, sendM, entry, 0, bDto);
                }
			}

			// 报丢订单发车
			Iterator iterator = orderpackMapLoss.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String wallBillCode = (String) entry.getKey();

				// 报损总数
				int lossCount = 0;
				try {
					lossCount = this.lossWebService
							.getLossProductCountOrderId(wallBillCode);
				} catch (Exception e1) {
					this.logger.error("调用报损订单接口失败, 运单号为" + wallBillCode);
					throw new Exception("调用报损订单接口失败, 运单号为" + wallBillCode);
				}

				ReverseSendWms send = null;
				send = tBaseService.getWaybillByOrderCode(wallBillCode);
				if (send == null){
					this.logger.info("调用运单接口获得数据为空,运单号" + wallBillCode);
					continue;
				}
				if (lossCount != 0) {
					// 运单系统拿出的商品明细
					List<com.jd.bluedragon.distribution.reverse.domain.Product> sendProducts = null;
					sendProducts = send.getProList();
					List<com.jd.bluedragon.distribution.reverse.domain.Product> sendLossProducts = new ArrayList<com.jd.bluedragon.distribution.reverse.domain.Product>();

					// 报损系统拿出的报损明细
					List<LossProduct> lossProducts = this.lossWebService.getLossProductByOrderId(wallBillCode);

					int loss_Count = 0;
					if(sendProducts!=null && !sendProducts.isEmpty()){
						for (com.jd.bluedragon.distribution.reverse.domain.Product sendProduct : sendProducts) {
							for (LossProduct lossProduct : lossProducts) {
								if(lossProduct.getLossCount()==0||sendProduct.getProductNum()==0) continue;//说明已经计算完了，或者不符合计算条件，理论上无<0数据
								
								if (lossProduct.getSku().equals(sendProduct.getProductId())&& 
										(lossProduct.getPrice().compareTo(new BigDecimal(sendProduct.getProductPrice())) == 0)) {
									if (null == sendProduct.getProductLoss()|| "".equals(sendProduct.getProductLoss())) {
										loss_Count = 0;
									} else {
										loss_Count = Integer.parseInt(sendProduct.getProductLoss());
									}
	
									if (sendProduct.getProductNum()- lossProduct.getLossCount() < 0) {//说明产品全部报丢,产品num清0.报损产品项减去产品num
										lossProduct.setLossCount(lossProduct.getLossCount()-sendProduct.getProductNum());
										sendProduct.setProductLoss(String.valueOf(loss_Count+sendProduct.getProductNum()));
										sendProduct.setProductNum(0);
									} else {//说明部分报丢
										sendProduct.setProductLoss(String.valueOf(loss_Count+ lossProduct.getLossCount()));
										sendProduct.setProductNum(sendProduct.getProductNum()- lossProduct.getLossCount());
										lossProduct.setLossCount(0);
									}
								}
							}
							sendLossProducts.add(sendProduct);
						}
					}
					send.setProList(sendLossProducts);
				}

				sendWMS(send, wallBillCode, sendM, entry, lossCount, bDto);
			}
			return true;
		} catch (Exception e) {
			this.logger.error(sendM.getSendCode() + "wms发货库房失败", e);
			return false;
		}

	}

	@SuppressWarnings("rawtypes")
	public void sendWMS(ReverseSendWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
			BaseStaffSiteOrgDto bDto) throws Exception {
		Integer orgId = bDto.getOrgId();
		String dmdStoreId = bDto.getStoreCode();

		String[] cky2AndStoreId = dmdStoreId.split("-");
		String cky2 = cky2AndStoreId[1];
		String storeId = cky2AndStoreId[2];

		String target = orgId + "," + cky2 + "," + storeId;

		String messageValue = "";
		String outboundNo = wallBillCode;
		String outboundType = "OrderBackDl"; // OrderBackDl
		String source = "DMS";

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.logger.info("仓储逆向发货信息为：" + send.getCky2()+ send.getOrgId()+ send.getStoreId());
		send.setOperateTime(df.format(sendM.getOperateTime()));
		send.setUserName(sendM.getCreateUser());
		send.setLossQuantity(lossCount);
		send.setSendCode(sendM.getSendCode());
		send.setOrderId(wallBillCode);
		send.setIsInStore(0);
		send.setPackageCodes((String) entry.getValue());
		try{
		//按收货仓进行赋值，覆盖运单中发货仓值，支持异仓退货
		send.setOrgId(orgId);
		send.setCky2(Integer.valueOf(cky2.trim()));
		send.setStoreId(Integer.valueOf(storeId.trim()));
		} catch(Exception e) {
			this.logger.error("青龙发货至仓储WS消息send收货仓信息赋值出错", e);
		}

		messageValue = XmlHelper.toXml(send, ReverseSendWms.class);
		this.logger.info("仓储逆向发货XML为：" + messageValue);
		this.logger.info("仓储逆向发货target为：" + target);
		com.jd.staig.receiver.rpc.Result result = null;
		try {
			result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
			try{
				//业务流程监控, 仓储埋点
				Map<String, String> data = new HashMap<String, String>();
				data.put("outboundNo", outboundNo);
				Profiler.bizNode(outboundType, data);
			} catch (Exception e) {
				this.logger.error("推送UMP发生异常.", e);
			}
			this.logger.info(result.getResultCode());
			this.logger.info(result.getResultMessage());
			this.logger.info(result.getResultValue());
		} catch (Exception e) {
			this.logger.error("青龙发货至仓储WS消息异常", e);
			throw new Exception("青龙发货至仓储WS消息失败，运单号为" + wallBillCode);
		} finally{
			//增加系统日志
	        SystemLog sLogDetail = new SystemLog();
	        sLogDetail.setKeyword1(wallBillCode);
	        sLogDetail.setKeyword2(sendM.getSendCode());
	        sLogDetail.setKeyword3(target);
	        sLogDetail.setKeyword4(Long.valueOf(result.getResultCode()));
	        sLogDetail.setType(Long.valueOf(12004));
	        sLogDetail.setContent(messageValue);
			SystemLogUtil.log(sLogDetail);
		}

		this.logger.info("青龙发货至仓储WS接口访问成功，result.getResultCode()=" + result.getResultCode());
		this.logger.info("青龙发货至仓储WS接口访问成功，result.getResultMessage()=" + result.getResultMessage());
		this.logger.info("青龙发货至仓储WS接口访问成功，result.getResultValue()=" + result.getResultValue());
		
		if (result.getResultCode() == 1) {
			this.logger.info("青龙发货至仓储WS消息成功，运单号为" + wallBillCode);
			//向报丢系统发送订单消息，锁定报丢，不再允许报丢，直至被驳回
			sendReportLoss(wallBillCode, RECEIVE_TYPE_WMS, sendM.getCreateSiteCode(), sendM.getReceiveSiteCode());
		} else {
			this.logger.error("青龙发货至仓储WS消息失败，result.getResultCode()=" + result.getResultCode());
			this.logger.error("青龙发货至仓储WS消息失败，result.getResultMessage()=" + result.getResultMessage());
			this.logger.error("青龙发货至仓储WS消息失败，result.getResultValue()=" + result.getResultValue());
			throw new Exception("青龙发货至仓储WS消息失败，运单号为" + wallBillCode);
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public void sendAsiaWMS(ReverseSendAsiaWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
			BaseStaffSiteOrgDto bDto, Map<String, String> isPackageFullMap) throws Exception {
		Integer orgId = bDto.getOrgId();
		String dmdStoreId = bDto.getStoreCode();

		String[] cky2AndStoreId = dmdStoreId.split("-");
		String cky2 = cky2AndStoreId[1];
		String storeId = cky2AndStoreId[2];

		String target = orgId + "," + cky2 + "," + storeId;

		String messageValue = "";
		String outboundNo = wallBillCode;
		String outboundType = "OrderBackDl"; // OrderBackDl
		String source = "DMS";

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.logger.info("仓储逆向发货信息为：" + send.getCky2()+ send.getOrgId()+ send.getStoreId());
		send.setOperateTime(df.format(sendM.getOperateTime()));
		send.setUserName(sendM.getCreateUser());
		send.setLossQuantity(lossCount);
		send.setSendCode(sendM.getSendCode());
		send.setOrderId(wallBillCode);
		if (sendM.getSendCode().startsWith(Box.BOX_TYPE_WEARHOUSE)) {
			// 库内返
			send.setIsInStore(1);
		} else {
			// 库外返
			send.setIsInStore(0);
		}
		send.setPackageCodes((String) entry.getValue());
		//FIXME:已经在获得运单时将收货仓信息赋值，不用重复赋值
//		send.setOrgId(orgId);
//		send.setCky2(Integer.valueOf(cky2.trim()));
//		send.setStoreId(Integer.valueOf(storeId.trim()));
		
		messageValue = XmlHelper.toXml(send, ReverseSendAsiaWms.class);
		
		this.logger.info("仓储逆向发货XML为：" + messageValue);
		this.logger.info("仓储逆向发货target为：" + target);
		com.jd.staig.receiver.rpc.Result result = null;
		try {
			result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
			try{
				//业务流程监控, 仓储埋点
				Map<String, String> data = new HashMap<String, String>();
				data.put("outboundNo", outboundNo);
				Profiler.bizNode(outboundType, data);
			} catch (Exception e) {
				this.logger.error("推送UMP发生异常.", e);
			}
			this.logger.info(result.getResultCode());
			this.logger.info(result.getResultMessage());
			this.logger.info(result.getResultValue());
		} catch (Exception e) {
			this.logger.error("青龙发货至仓储WS消息异常", e);
			throw new Exception("青龙发货至仓储WS消息失败，运单号为" + wallBillCode);
		} finally {
			//增加系统日志
	        SystemLog sLogDetail = new SystemLog();
	        sLogDetail.setKeyword1(wallBillCode);
	        sLogDetail.setKeyword2(sendM.getSendCode());
	        sLogDetail.setKeyword3(target);
	        sLogDetail.setKeyword4(Long.valueOf(result.getResultCode()));
	        sLogDetail.setType(Long.valueOf(12004));
	        sLogDetail.setContent(messageValue);
			SystemLogUtil.log(sLogDetail);
		}

		this.logger.info("青龙发货访问仓储WS接口成功，result.getResultCode()=" + result.getResultCode());
		this.logger.info("青龙发货访问仓储WS接口成功，result.getResultMessage()=" + result.getResultMessage());
		this.logger.info("青龙发货访问仓储WS接口成功，result.getResultValue()=" + result.getResultValue());
		if (result.getResultCode() == 1) {
			this.logger.info("青龙发货至仓储WS消息成功，运单号为" + wallBillCode);
			//向报丢系统发送订单消息，锁定报丢，不再允许报丢，直至被驳回
			sendReportLoss(wallBillCode, RECEIVE_TYPE_WMS, sendM.getCreateSiteCode(), sendM.getReceiveSiteCode());
		} else {
			this.logger.error("青龙发货至仓储WS消息失败，result.getResultCode()=" + result.getResultCode());
			this.logger.error("青龙发货至仓储WS消息失败，result.getResultMessage()=" + result.getResultMessage());
			this.logger.error("青龙发货至仓储WS消息失败，result.getResultValue()=" + result.getResultValue());
			throw new Exception("青龙发货至仓储WS消息失败，运单号为" + wallBillCode);
		}
	}

	public static void addMapWms(Map<String, String> m, String a, String b) {
		if (m.containsKey(a)) {
			//如果有重复包裹去重、
			if(!m.get(a).contains(b)){
				m.put(a, m.get(a) + "," + b);
			}
		} else {
			m.put(a, b);
		}
	}
	
	public static void addMapSpwms(Map<String, SendDetail> map, String waybillCode, SendDetail sendDetail) {
		if (sendDetail.getSendType() == 20 && !map.containsKey(waybillCode)) {
			map.put(waybillCode, sendDetail);
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean sendReverseMessageToSpwms(SendM sendM, Integer baseOrgId, String baseStoreId) throws Exception {
		String spwmsToken = PropertiesHelper.newInstance().getValue("spwms_token");
		String spwmsUrl = PropertiesHelper.newInstance().getValue("spwms.service.url");
		this.logger.info("spwmsToken=" + spwmsToken + ",spwmsUrl=" + spwmsUrl);

		if (sendM == null) {
			this.logger.error("reverse_spwms:sendM数据为空");
			return true;
		}

		List<SendDetail> sendDetails = this.sendDatailDao.queryBySiteCodeAndSendCode(this.paramSendDetail(sendM));
		if (sendDetails == null || sendDetails.size() == 0) {
			this.logger.error("reverse_spwms:sendD数据为空." + sendM.getSendCode());
			return true;
		}

		// 增加判断d表中数据为逆向数据
		Map<String, SendDetail> sendDetailMap = new ConcurrentHashMap<String, SendDetail>();
		for (SendDetail aSendDetail : sendDetails) {
			ReverseSendServiceImpl.addMapSpwms(sendDetailMap, aSendDetail.getWaybillCode(), aSendDetail);
		}

		Iterator<Entry<String, SendDetail>> iterator = sendDetailMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = iterator.next();
			String waybillCode = (String) entry.getKey();
			SendDetail sendDetail = (SendDetail) entry.getValue();
			try {
				Waybill waybill = this.getWaybill(waybillCode);
				if (null == waybill) {
					this.logger.error(waybillCode + "获取订单中间件ooms历史表订单数据失败");
					continue;
				}

				List<ReverseSpare> sendReverseSpare = new ArrayList<ReverseSpare>();
				// 如果D表的包裹号不为备件库条码时，需要新增spare表数据并关联备件库条码
				if (!BusinessHelper.isReverseSpareCode(sendDetail.getPackageBarcode())) {
					List<ReverseSpare> reverseSpare = this.reverseSpareService.queryByWayBillCode(waybillCode,
							sendDetail.getSendCode());
					List<Product> products = waybill.getProList();
					List<Spare> spares = this.getSpare(baseOrgId, sendDetail, products);

					int spare_num = 0;

					List<ReverseSendSpwmsOrder> spwmsOrders = new ArrayList<ReverseSendSpwmsOrder>();
					for (int i = 0; i < products.size(); i++) {
						for (int j = 0; j < products.get(i).getQuantity(); j++) {
							ReverseSendSpwmsOrder spwmsOrder = new ReverseSendSpwmsOrder();
							spwmsOrder.setWaybillCode(waybillCode);
							spwmsOrder.setSendCode(sendDetail.getSendCode());
							spwmsOrder.setSpareCode(spares.get(spare_num++).getCode());
							spwmsOrder.setProductId(products.get(i).getProductId());
							spwmsOrder.setProductName(products.get(i).getName());
							spwmsOrder.setProductPrice(products.get(i).getPrice().doubleValue());
							spwmsOrders.add(spwmsOrder);
						}
					}

					List<ReverseSpare> reverseSpares = this.setReverseSpares(sendDetail, spwmsOrders);
					if (null == reverseSpare || reverseSpare.size() == 0) {
						sendReverseSpare = reverseSpares;
					} else {
						for (ReverseSpare rs1 : reverseSpare) {
							for (ReverseSpare rs2 : reverseSpares) {
								if (rs1.getWaybillCode().equals(rs2.getWaybillCode())
										&& rs1.getProductId().equals(rs2.getProductId())) {
									if (null != rs1.getSpareTranCode()) {
										reverseSpares.remove(rs2);
										break;
									} else {
										String temp = rs2.getSpareCode();
										org.apache.commons.beanutils.BeanUtils.copyProperties(rs2, rs1);
										rs2.setSpareCode(temp);
										break;
									}
								}
							}
						}

						sendReverseSpare = reverseSpares;
						this.logger.info(waybillCode + "处理备件库退货,List<ReverseSpare>=" + sendReverseSpare.size());
					}
				} else {
					sendReverseSpare = this.reverseSpareService.queryByWayBillCode(waybillCode,
							sendDetail.getSendCode());
					if (null == sendReverseSpare || sendReverseSpare.size() == 0) {
						this.logger.error("spwms处理备件库退货出错waybillCode=" + waybillCode);
						continue;
					}
				}

				// 开始包装发货对象
				InOrder order = new InOrder();
				order.setSourceId(sendDetail.getCreateSiteCode());
				order.setFromPin(sendDetail.getCreateUserCode().toString());
				order.setFromName(sendDetail.getCreateUser());
				order.setCreateReason(sendDetail.getSpareReason());
				order.setOrderId(Long.parseLong(sendDetail.getWaybillCode()));
				order.setCreateReason(sendDetail.getSpareReason());
				order.setAimOrgId(baseOrgId);

				try {
					order.setAimStoreId(Integer.parseInt(baseStoreId));
				} catch (Exception e1) {
					this.logger.error("处理备件库退货,baseStoreId转换出错" + baseStoreId);
					continue;
				}

				if (waybill.getType() >= 21 && waybill.getType() <= 25) {
					// 200pop订单
					order.setOrderType(200);
				} else if (waybill.getType() == 2) {
					// 100夺宝岛订单
					order.setOrderType(100);
				} else {
					// 其他值其他订单
					order.setOrderType(300);
				}

				// 判断是否奢侈品订单
				// 还需要再次确认运单返回的结果
				List<OrderDetail> de = new ArrayList<OrderDetail>();
				for (ReverseSpare reverseSpare : sendReverseSpare) {
					OrderDetail orderDetail = new OrderDetail();
					orderDetail.setWareId(reverseSpare.getProductId());
					orderDetail.setWareName(reverseSpare.getProductName());
					orderDetail.setLossType(this.getLossType(waybill, sendDetail));

					if (reverseSpare.getArrtCode3() == null || reverseSpare.getArrtCode3().intValue() == 0) {
						orderDetail.setMainWareFunctionType("1024");
					} else {
						orderDetail.setMainWareFunctionType(ReverseSendServiceImpl.tempMap.get(reverseSpare
								.getArrtCode3().toString()));
					}

					if (reverseSpare.getArrtCode2() == null || reverseSpare.getArrtCode2().intValue() == 0) {
						orderDetail.setMainWareAppearance("2");
					} else {
						orderDetail.setMainWareAppearance(ReverseSendServiceImpl.tempMap.get(reverseSpare
								.getArrtCode2().toString()));
					}

					if (reverseSpare.getArrtCode1() == null || reverseSpare.getArrtCode1().intValue() == 0) {
						orderDetail.setMainWarePackage("1");
					} else {
						orderDetail.setMainWarePackage(ReverseSendServiceImpl.tempMap.get(reverseSpare.getArrtCode1()
								.toString()));
					}

					if (reverseSpare.getArrtCode4() == null || reverseSpare.getArrtCode4().intValue() == 0) {
						orderDetail.setAttachments("8");
					} else {
						orderDetail.setAttachments(ReverseSendServiceImpl.tempMap.get(reverseSpare.getArrtCode4()
								.toString()));
					}

					WChoice wChoice = new WChoice();
					wChoice.setQueryWaybillC(true);
					wChoice.setQueryWaybillE(true);
					wChoice.setQueryWaybillM(true);
					try {
						Integer consignerId = this.waybillQueryApi.getDataByChoice(waybillCode, wChoice).getData()
								.getWaybill().getConsignerId();

						if (null != consignerId) {
							orderDetail.setSupplierId(consignerId.toString());
						} else {
							orderDetail.setSupplierId("0");
						}
					} catch (Exception e) {
						this.logger.error(waybillCode + "处理备件库退货,waybillQueryWSProxy出错,", e);
						orderDetail.setSupplierId("0");
					}
					orderDetail.setPartCode(reverseSpare.getSpareCode());

					this.logger.info(waybillCode + "处理备件库退货,reverseSpare.getSpareCode=" + reverseSpare.getSpareCode());
					orderDetail.setIsLuxury(this.isLuxury(waybill.getSendPay()));
					orderDetail.setSerialNo(null);
					de.add(orderDetail);
				}
				order.setOrderDetail(de);

				order.setFlashOrgId(waybill.getOrgId());
				order.setFlashStoreId(waybill.getStoreId());

				String jsonString = JsonHelper.toJson(order);

				this.logger.info("处理备件库退货,生成的json串为：" + jsonString);
				PostMethod postMethod = new PostMethod(spwmsUrl);

				String contentType = "application/json";
				String charset = "UTF-8";
				RequestEntity requestEntity = new StringRequestEntity(jsonString, contentType, charset);
				postMethod.setRequestEntity(requestEntity);
				postMethod.addRequestHeader("Accept", contentType);
				// headedr
				// spwms.360buy.com线上需要去配置文件中获取
				ReverseSendServiceImpl.setMethodHeaders(postMethod, spwmsToken, "");

				HttpClient httpClient = new HttpClient();
				httpClient.executeMethod(postMethod);
				try {
					// 业务流程监控, 备件库埋点
					Map<String, String> data = new HashMap<String, String>();
					data.put("orderId", order.getOrderId().toString());
					Profiler.bizNode("Reverse_ws_dms2spare", data);
				} catch (Exception e) {
					this.logger.error("推送UMP发生异常.", e);
				}
				if (postMethod.getStatusCode() != 200) {
					this.logger.error("postMethod.getStatusCode() != 200 [" + postMethod.getStatusCode() + "]");
					continue;
				}
				String bodyContent = postMethod.getResponseBodyAsString();
				String transferId = JsonHelper.fromJson(bodyContent, MessageResult.class).getTransferId();
				this.logger.info(waybillCode + "返回json结果:" + bodyContent);
				if (null == transferId) {
					this.logger.error(waybillCode + "spwms发货备件库失败，返回json结果:" + bodyContent);
				}else{
					//向报丢系统发送订单消息，锁定报丢，不再允许报丢，直至被驳回
					sendReportLoss(order.getOrderId().toString(), RECEIVE_TYPE_SPARE, sendM.getCreateSiteCode(), sendM.getReceiveSiteCode());
				}

				postMethod.releaseConnection();
				if (null != transferId) {
					List<ReverseSpare> reverseSpares = new ArrayList<ReverseSpare>();
					for (ReverseSpare aReverseSpare : sendReverseSpare) {
						if (null == aReverseSpare.getSpareTranCode()) {
							aReverseSpare.setSpareTranCode(transferId);
							reverseSpares.add(aReverseSpare);
						}
					}
					this.reverseSpareService.batchAddOrUpdate(reverseSpares);
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception ex) {
				this.logger.error("运单号=[" + waybillCode + "]send_d_id=[" + sendDetail.getSendDId() + "][spwms发货备件库失败]",
						ex);
			}
		}
		return true;
	}

	private List<ReverseSpare> setReverseSpares(SendDetail sendDetail, List<ReverseSendSpwmsOrder> spwmsOrders) {
		List<ReverseSpare> reverseSpares = new ArrayList<ReverseSpare>();
		for (ReverseSendSpwmsOrder reverseSendSpwmsOrder : spwmsOrders) {
			ReverseSpare aReverseSpare = new ReverseSpare();
			aReverseSpare.setSpareCode(reverseSendSpwmsOrder.getSpareCode());
			aReverseSpare.setSendCode(reverseSendSpwmsOrder.getSendCode());
			aReverseSpare.setWaybillCode(reverseSendSpwmsOrder.getWaybillCode());
			aReverseSpare.setProductId(reverseSendSpwmsOrder.getProductId());
			aReverseSpare.setProductCode(null);
			aReverseSpare.setProductPrice(reverseSendSpwmsOrder.getProductPrice());
			aReverseSpare.setProductName(reverseSendSpwmsOrder.getProductName());

			// 三方七折 根据send_d 新增字段判断
			if (sendDetail.getFeatureType() != null && 2 == sendDetail.getFeatureType().intValue()) {
				aReverseSpare.setArrtCode1(102);
				aReverseSpare.setArrtDesc1("商品外包装：有/非新");
				aReverseSpare.setArrtCode2(201);
				aReverseSpare.setArrtDesc2("主商品外观：好");
				aReverseSpare.setArrtCode3(303);
				aReverseSpare.setArrtDesc3("主商品功能：好");
				aReverseSpare.setArrtCode4(401);
				aReverseSpare.setArrtDesc4("附件情况：完整");
			} else {
				// 按订单分拣商品属性置为默认值
				aReverseSpare.setArrtCode1(101);
				aReverseSpare.setArrtDesc1("商品外包装：新");
				aReverseSpare.setArrtCode2(201);
				aReverseSpare.setArrtDesc2("主商品外观：新");
				aReverseSpare.setArrtCode3(302);
				aReverseSpare.setArrtDesc3("未检测");
				aReverseSpare.setArrtCode4(401);
				aReverseSpare.setArrtDesc4("完整");
			}
			reverseSpares.add(aReverseSpare);
		}
		return reverseSpares;
	}

	private SendDetail paramSendDetail(SendM sendM) {
		SendDetail sendDetail = new SendDetail();
		sendDetail.setSendCode(sendM.getSendCode());
		sendDetail.setCreateSiteCode(sendM.getCreateSiteCode());
		return sendDetail;
	}

	private Waybill getWaybill(String waybillCode) {
		Waybill waybill = this.waybillCommonService.getWaybillFromOrderService(waybillCode);
		if (null == waybill) {
			this.logger.error(waybillCode + "获取订单中间件ooms订单数据失败");
			waybill = this.waybillCommonService.getHisWaybillFromOrderService(waybillCode);
		}
		return waybill;
	}

	private List<Spare> getSpare(Integer baseOrgId, SendDetail tSendDetail, List<Product> products) {
		Spare spare = new Spare();
		spare.setType(this.getOrgType(baseOrgId));
		spare.setCreateUserCode(tSendDetail.getCreateUserCode());
		spare.setCreateUser(tSendDetail.getCreateUser());
		spare.setQuantity(this.getProductQuantity(products));
		spare.setStatus(0);
		spare.setTimes(0);

		// 获取备件库条码信息
		List<Spare> spares = this.spareService.print(spare);
		return spares;
	}

	private int getProductQuantity(List<Product> products) {
		int num = 0;
		for (Product product : products) {
			num = num + product.getQuantity();
		}
		return num;
	}

	private String getOrgType(Integer baseOrgId) {
		String orgType = null;

		switch (baseOrgId) {
		case 6:
			orgType = "PB";
			break;
		case 3:
			orgType = "PS";
			break;
		case 10:
			orgType = "PG";
			break;
		case 4:
			orgType = "PC";
			break;
		case 600:
			orgType = "PW";
			break;
		case 709:
			orgType = "PW";
			break;
		case 611:
			orgType = "PY";
			break;
		default:
			this.logger.error("获取机构id失败");
		}
		return orgType;
	}

	private Boolean isLuxury(String sendPay) {
		if (StringHelper.isEmpty(sendPay)) {
			return false;
		}

		String identifier = sendPay.substring(19, 20);
		return "1".equals(identifier) ? true : false;
	}

	private static void setMethodHeaders(HttpMethod httpMethod, String name, String password) {
		if (httpMethod instanceof PostMethod || httpMethod instanceof PutMethod) {
			httpMethod.setRequestHeader("Content-Type", "application/json");
		}

		httpMethod.setDoAuthentication(false);
		httpMethod.setRequestHeader("Accept", "application/json");
		httpMethod.setRequestHeader("Authorization",
				"Basic " + ReverseSendServiceImpl.base64Encode(name + ":" + password));
	}

	private static String base64Encode(String value) {
		return Base64Utility.encode(value.getBytes(Charset.defaultCharset()));
	}

	public Integer getLossType(Waybill waybill, SendDetail sendDetail) {
		Integer lossType = 4; // 默认配送损

		if (waybill != null && sendDetail != null && sendDetail.getFeatureType() != null) {
			if (2 == NumberHelper.getIntegerValue(sendDetail.getFeatureType())) {
				lossType = 13; // 三方打折
			} else if (16 == NumberHelper.getIntegerValue(waybill.getShipmentType())) {
				lossType = 9; // 三方包裹破损
			}
		}

		return lossType;
	}
	
	/**
	 * 判断一个目的地站点是否是亚一中件仓
	 * 
	 * @param reseiveSiteCode
	 * @return
	 */
	private boolean isAsionNoOneSiteCode(Integer reseiveSiteCode) {
		if (reseiveSiteCode == null) {
			return false;
		}
		return ASION_NO_ONE_SITE_CODE_LIST.contains(reseiveSiteCode);
	}

    /**
     * 备件库和仓库发货后回传MQ消息给报损系统，锁定定单不让再提报损
     * @param orderId    订单id
     * @param receiveType 收货类型，区分退大库、备件库
     * @param createSiteCode 创建站点
     * @param receiveSiteCode 收货站点，在这里指的是仓库、备件库
     */
    private void sendReportLoss(String orderId, Integer receiveType, Integer createSiteCode, Integer receiveSiteCode) {
    	
    	//判断收货类型，非大库、备件库直接返回
    	if(receiveType!=RECEIVE_TYPE_WMS && receiveType!=RECEIVE_TYPE_SPARE) return;
    	
    	ReverseReceiveLoss reverseReceiveLoss = new ReverseReceiveLoss();
    	try {
	    	String dmsId=null;
	    	String dmsName=null;
	    	String storeId=null;
	    	String storeName=null;
	    	
	    	//仓储收货回传
    		BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
    		dmsId = dto.getSiteCode().toString();
    		dmsName = dto.getSiteName();
    		BaseStaffSiteOrgDto dto1 = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
    		storeId = dto1.getSiteCode().toString();
    		storeName = dto1.getSiteName();
	    	
	    	reverseReceiveLoss.setOrderId(orderId);
	    	reverseReceiveLoss.setReceiveType(receiveType); //发货时没有这两个字段内容
	    	reverseReceiveLoss.setUpdateDate(null); //因为没有这个时间
	    	
	    	reverseReceiveLoss.setDmsId(dmsId);
	    	reverseReceiveLoss.setDmsName(dmsName);
	    	reverseReceiveLoss.setStoreId(storeId);
	    	reverseReceiveLoss.setStoreName(storeName);
	    	
		    reverseReceiveLoss.setIsLock(ReverseReceiveLoss.LOCK);
		    String jsonStr = JsonHelper.toJson(reverseReceiveLoss);
	    	logger.error("青龙逆向发货后回传报损系统锁定MQ orderid为" + orderId);
	    	logger.error("青龙逆向发货后回传报损系统锁定MQ json为"+jsonStr);
    	
    	
			this.messageClient.sendMessage("dms_send_loss", jsonStr, orderId);
			logger.info("青龙逆向发货后回传报损系统锁定MQ消息成功，订单号为" + orderId);
		} catch (Exception e) {
			logger.error("青龙逆向发货后回传报损系统锁定MQ消息失败，订单号为" + orderId, e);
		}
		
	}


    /**
     * <p>是不是特殊处理的单子, 不需要发大库</p>
     *
     * @param send
     * @return <code>true</code> 如果是迷你仓、eclp订单
     */
    private  Boolean isSpecial(ReverseSendWms send,String wayBillCode) {
    	
        if (StringHelper.isNotEmpty(send.getWaybillSign())) {
        	//迷你仓新需求，waybillsign第一位=8的 不推送库房， 因为不属于逆向 guoyongzhi
	        if ('8' ==send.getWaybillSign().charAt(0)) {
	            logger.info("运单号： "+wayBillCode+ " 的 waybillsign 【"+send.getWaybillSign()+"】 第一位是8 ,不掉用库房webservice");
	            //增加系统日志
                SystemLog sLogDetail = new SystemLog();
                sLogDetail.setKeyword1(wayBillCode);
                sLogDetail.setKeyword2(send.getSendCode());
                sLogDetail.setKeyword3("迷你仓");
                sLogDetail.setKeyword4(Long.valueOf(1));//表示处理成功
                sLogDetail.setType(Long.valueOf(12004));
                sLogDetail.setContent("不发送逆向报文!");
                SystemLogUtil.log(sLogDetail);
                
	            return Boolean.TRUE;
	        }
        }
       
        if(StringHelper.isNotEmpty(send.getSourceCode())){
        	//ECLP订单 不推送wms ， 发mq
        	if(send.getSourceCode().equals("ECLP")){
                //发MQ-->开发平台
                logger.info("运单号： "+wayBillCode+ " 的 waybillsign 【"+send.getSourceCode()+"】 =ECLP ,不掉用库房webservice");
                
                //给eclp发送mq, eclp然后自己组装逆向报文
                ReverseSendMQToECLP sendmodel=new ReverseSendMQToECLP();
                sendmodel.setJdOrderCode(send.getOrderId());
                sendmodel.setSourceCode(send.getSourceCode());
                sendmodel.setWaybillCode(wayBillCode);
                sendmodel.setRejType(3);
                sendmodel.setRejRemark("分拣中心逆向分拣ECLP");
                String jsonStr = JsonHelper.toJson(sendmodel);
                logger.info("推送ECLP的 MQ消息体 "+jsonStr);
                
                //增加系统日志
                SystemLog sLogDetail = new SystemLog();
                sLogDetail.setKeyword1(wayBillCode);
                sLogDetail.setKeyword2(send.getSendCode());
                sLogDetail.setKeyword3("ECLP");
                sLogDetail.setType(Long.valueOf(12004));
                sLogDetail.setContent(jsonStr);
        		
                try {
                    messageProducer.send("bd_to_josl_rej", jsonStr, wayBillCode);
                    sLogDetail.setKeyword4(Long.valueOf(1));//表示发送成功
				} catch (Exception e) {
                    logger.error("推送ECLP MQ 发生异常.", e);
                    sLogDetail.setKeyword4(Long.valueOf(-1));//表示发送失败
                }
                SystemLogUtil.log(sLogDetail);

                return Boolean.TRUE;
            }
        }
        
        return Boolean.FALSE;
    }

}