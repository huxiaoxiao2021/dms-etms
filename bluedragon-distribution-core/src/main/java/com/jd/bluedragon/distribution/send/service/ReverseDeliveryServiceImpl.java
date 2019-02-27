package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.core.base.ThirdPartyLogisticManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.WaybillInfoResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillEntity;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.whems.Ems4JingDongPortType;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillInfo;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.third.service.dto.BaseResult;
import com.jd.etms.third.service.dto.OrderShipsReturnDto;
import com.jd.etms.third.service.dto.ShipCarrierReturnDto;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.domain.Assort;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service("reversedeliveryService")
public class ReverseDeliveryServiceImpl implements ReverseDeliveryService {

	private final Logger logger = Logger
			.getLogger(ReverseDeliveryServiceImpl.class);

	@Autowired
	private SendMDao sendMDao;

	@Autowired
	private SendDatailDao sendDatailDao;

	@Autowired
	private Ems4JingDongPortType whemsClientService;

	@Autowired
	private ThirdPartyLogisticManager thirdPartyLogisticManager;

	@Autowired
	WaybillQueryManager waybillQueryManager;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private WaybillService waybillService;

	@Autowired
	private BaseService baseService;

	@Autowired
	private DefaultJMQProducer whSmsSendMq;

	@Autowired
	private DefaultJMQProducer emsSendMq;

	// 自营
	public static final Integer businessTypeONE = 10;
	// 退货
	public static final Integer businessTypeTWO = 20;
	// 第三方
	public static final Integer businessTypeTHR = 30;

	//EMS快递站点信息读取
	public static final String EMS_SITE = "EMS_SITE";

	private final Integer BATCH_NUM = 49;

	//EMS快生开关
	public static final String EMS_ONOFF = "EMS_ONOFF";

	@Autowired
    private QuickProduceService quickProduceService;

	@SuppressWarnings("rawtypes")
	@JProfiler(jKey= "DMSWORKER.ReverseDeliveryService.findsendMToReverse",mState = {JProEnum.TP})
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public boolean findsendMToReverse(Task task) throws Exception {
		if (task == null || task.getBoxCode() == null
				|| task.getCreateSiteCode() == null
				|| task.getKeyword2() == null)
			return true;
		List<SendM> tSendM = this.sendMDao.selectBySiteAndSendCode(
				task.getCreateSiteCode(), task.getBoxCode());
		if (tSendM != null
				&& !tSendM.isEmpty()
				&& (task.getKeyword2().equals(String.valueOf(businessTypeONE)) || task
						.getKeyword2().equals(String.valueOf(businessTypeTHR)))) {
			HashMap send3plConfigMap = new HashMap();
			send3plConfigMap = getSend3plConfigMap(send3plConfigMap);
			if(send3plConfigMap.containsKey(String.valueOf(task.getReceiveSiteCode()))){
				batchProcessOrderInfo3PL(tSendM,task.getReceiveSiteCode(),(String)send3plConfigMap.get(String.valueOf(task.getReceiveSiteCode())));
			}else if (task.getReceiveSiteCode() == 452
					|| task.getReceiveSiteCode() == 1376
					|| task.getReceiveSiteCode() == 2228) {
				this.logger.info("batchProcessOrderInfo2DSF武汉邮政推送批次号："+task.getBoxCode());
				batchProcessOrderInfo2DSF(tSendM);
			}else{
				List<SysConfig> configs=baseService.queryConfigByKeyWithCache(EMS_SITE);
				for(SysConfig sys : configs){
					if(StringHelper.matchSiteRule(sys.getConfigContent(), task.getReceiveSiteCode().toString())){
						this.logger.error("全国邮政推送批次号："+task.getBoxCode());
						batchProcesstoEmsServer(tSendM);
					}
				}
			}
		}
		return true;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateIsCancelToWaybillByBox(SendM tSendM,
			List<SendDetail> tlist) {
		// 取消发货的时候添加运单回传worker
		if (tlist != null && !tlist.isEmpty()) {
			for (SendDetail dSendDatail : tlist) {
				dSendDatail.setSendCode(tSendM.getSendCode());
				dSendDatail.setOperateTime(new Date());
				dSendDatail.setCreateUser(tSendM.getUpdaterUser());
				dSendDatail.setCreateUserCode(tSendM.getUpdateUserCode());
				dSendDatail.setUpdateTime(new Date());
				dSendDatail.setIsCancel(2);
			}
			deliveryService.updateWaybillStatus(tlist);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateIsCancelByBox(SendM tSendM, List<SendDetail> tlist) {
		deliveryService.cancelSendDatailByBox(tlist);
		deliveryService.cancelSendM(tSendM);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateIsCancelToWaybillByPackageCode(SendM tSendM,
			SendDetail tSendDatail) {
		List<SendDetail> tlist = new ArrayList<SendDetail>();
		tSendDatail.setIsCancel(2);
		if (StringUtils.isNotEmpty(tSendM.getSendCode())) {
			tSendDatail.setSendCode(tSendM.getSendCode());
		}
		tSendDatail.setOperateTime(new Date());
		tSendDatail.setCreateUser(tSendM.getUpdaterUser());
		tSendDatail.setCreateUserCode(tSendM.getUpdateUserCode());
		tSendDatail.setUpdateTime(new Date());
		tlist.add(tSendDatail);
		deliveryService.updateWaybillStatus(tlist);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateIsCancelByPackageCode(SendM tSendM, SendDetail tSendDatail) {
		// 由于使用了双写，写从库为异步操作，更新双写主从数据库的对象使用同一个，
		// 若后续代码对该对象进行修改，会导致写从库写入出现问题，故此处copy一个新对象
		SendDetail tSendDCopy = new SendDetail();
		BeanUtils.copyProperties(tSendDatail, tSendDCopy);
		tSendDCopy.setSendCode(null);
		deliveryService.cancelSendDatailByPackage(tSendDCopy);
		if (WaybillUtil.isPackageCode(tSendDatail.getBoxCode())) {
			tSendM.setBoxCode(tSendDatail.getBoxCode());
			deliveryService.cancelSendM(tSendM);
		}
	}

	/**
     * 2012-10-12
     * 拆分数据数据 每组49
     * @param transresult
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String>[] splitList(List<String> transresult){
        List<List<String>> splitList = new ArrayList<List<String>>();
        for(int i = 0;i<transresult.size();i+=BATCH_NUM){
            int size = i+BATCH_NUM>transresult.size()?transresult.size():i+BATCH_NUM;
            List<String> tmp = (List<String>)transresult.subList(i, size);
            splitList.add(tmp);
        }
        return splitList.toArray(new List[0]);
    }

    @SuppressWarnings("unchecked")
    private List<String>[] splitList1(List<String> transresult){
        List<List<String>> splitList = new ArrayList<List<String>>();
        for(int i = 0;i<transresult.size();i+=1){
            int size = i+1>transresult.size()?transresult.size():i+1;
            List<String> tmp = (List<String>)transresult.subList(i, size);
            splitList.add(tmp);
        }
        return splitList.toArray(new List[0]);
    }

	public void batchProcessOrderInfo2DSF(List<SendM> tSendMList) {

		List<SendDetail> sendList = new ArrayList<SendDetail>();
		deliveryService.getAllList(tSendMList, sendList);
		this.logger.info("batchProcessOrderInfo2DSF武汉邮政推送接口sendList长度:"
				+ sendList.size());
		if (sendList != null && !sendList.isEmpty()) {
			Set<String> waybillset = new HashSet<String>();
			for (SendDetail dSendDatail : sendList) {
				waybillset.add(dSendDatail.getWaybillCode());
			}
			List<String> waybillList = new CollectionHelper<String>()
					.toList(waybillset);
			boolean bool = sendMqToWhsmsServer(waybillList);
			if(!bool){
				this.logger.error("武汉邮政推送自消费类型的MQ失败：" + waybillList.toString());
			}
		}
	}

	public void batchProcesstoEmsServer(List<SendM> tSendMList) {

		List<SendDetail> sendList = new ArrayList<SendDetail>();
		deliveryService.getAllList(tSendMList, sendList);
		this.logger.info("batchProcesstoEmsServer邮政推送接口sendList长度:"
				+ sendList.size());
		if (sendList != null && !sendList.isEmpty()) {
			Set<String> waybillset = new HashSet<String>();
			for (SendDetail dSendDatail : sendList) {
				waybillset.add(dSendDatail.getWaybillCode());
			}
			List<String> waybillList = new CollectionHelper<String>()
					.toList(waybillset);
			toEmsServer(waybillList);
		}
	}

	public void batchProcessOrderInfo3PL(List<SendM> tSendMList,Integer siteCode,String siteName) {
		this.logger.info("batchProcessOrderInfo3PL推送数据");
		List<SendDetail> slist = new ArrayList<SendDetail>();
		deliveryService.getAllList(tSendMList, slist);
		this.logger.info("batchProcessOrderInfo3PL推送接口sendList长度:"
				+ slist.size());
		if (slist != null && !slist.isEmpty()) {
			Set<String> waybillset = new HashSet<String>();
			for (SendDetail dSendDatail : slist) {
				waybillset.add(dSendDatail.getWaybillCode());
			}
			List<String> waybillList = new CollectionHelper<String>()
					.toList(waybillset);
			dataTo3PlServer(waybillList,siteCode,siteName);
		}
	}

	/**
	 * 调用3pl接口回传数据
	 * @param waybillList
	 */
	private void dataTo3PlServer(List<String> waybillList ,Integer siteCode,String siteName) {
		if (waybillList != null && !waybillList.isEmpty()) {
			this.logger.info("dataTo3PlServer处理任务数据"+ waybillList.size());
			List<String>[] splitListResultAl = splitList(waybillList);
			for (List<String> wlist : splitListResultAl) {
				List<OrderShipsReturnDto> returnDtoList = new ArrayList<OrderShipsReturnDto>();
				String encryptData = Md5Helper.encode3PL(wlist.get(0)+"123456");
				for(String waybillCode : wlist){
					OrderShipsReturnDto returnDto = new OrderShipsReturnDto();
					returnDto.setClearOld(0);

					this.logger.info("调用运单接口, 订单号为： " + waybillCode);
					WChoice wChoice = new WChoice();
					wChoice.setQueryWaybillC(true);
					wChoice.setQueryWaybillE(true);
					wChoice.setQueryWaybillM(true);
					BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,
					        wChoice);
					if (baseEntity != null && baseEntity.getData() != null) {
						com.jd.etms.waybill.domain.Waybill waybillWS = baseEntity.getData().getWaybill();
						returnDto.setJdOrderType(waybillWS.getWaybillType());
					}
					returnDto.setOrderId(waybillCode);
					List<ShipCarrierReturnDto> shipCarrierList = new ArrayList<ShipCarrierReturnDto>();
					ShipCarrierReturnDto rd = new ShipCarrierReturnDto();
					rd.setFlagOrderType(0);
					rd.setOrderId(waybillCode);
					List<String> wl = new ArrayList<String>();
					wl.add(waybillCode);
					rd.setShipIds(wl);
					rd.setThirdId(siteCode);
					rd.setThirdName(siteName);
					rd.setThirdType(16);
					shipCarrierList.add(rd);
					returnDto.setShipCarrierList(shipCarrierList);
					returnDtoList.add(returnDto);
				}
				BaseResult<List<OrderShipsReturnDto>>  baseResult = thirdPartyLogisticManager.insertOrderShips(returnDtoList, encryptData);
				if(baseResult!=null && baseResult.getCallState()==0){
					this.logger.info("调用接口返回状态失败, 信息： " + baseResult.getMessage());
				}
			}
		}
	}

	/**
	 * 调用邮政接口回传数据
	 * @param waybillList
	 */
	private boolean sendMqToWhsmsServer(List<String> waybillList) {
		boolean resultBool = false;
		if (waybillList != null && !waybillList.isEmpty()) {
			boolean flage = true;
			List<SysConfig> configs=baseService.queryConfigByKeyWithCache(EMS_ONOFF);
			for(SysConfig sys : configs){
				if(StringHelper.matchSiteRule(sys.getConfigContent(), "EMS_OFF")){
					flage =false;
				}
			}
			List<String>[] splitListResultAl = splitList1(waybillList);
			for (List<String> wlist : splitListResultAl) {
				WChoice queryWChoice = new WChoice();
				queryWChoice.setQueryPackList(true);
				queryWChoice.setQueryWaybillC(true);
				List<BigWaybillDto> tWaybillList = null;
				if(flage)
					tWaybillList = deliveryService.getWaillCodeListMessge(queryWChoice, wlist);
				else
					tWaybillList =getWaillCodeListMessge(queryWChoice, wlist);

				StringBuffer buffer = new StringBuffer();
				if (tWaybillList != null && !tWaybillList.isEmpty()) {
					/*this.logger
							.info("batchProcessOrderInfo2DSF武汉邮政推送接口-调用运单接口不为空");*/
					for (BigWaybillDto tWaybill : tWaybillList) {
						if (tWaybill != null && tWaybill.getWaybill() != null) {
							Waybill waybill = tWaybill.getWaybill();
							String payment = String.valueOf(waybill
									.getPayment());
							String declaredValue = waybill.getCodMoney();
							String collection = "";
							String needFund = "0.00";
							if (payment.equals("1") || payment.equals("3")) {
								collection = "1";
								if (declaredValue != null) {
									Double d = Math.round(Double
											.parseDouble(declaredValue) * 100) / 100.00;
									needFund = d.toString();
								}
							} else {
								collection = "0";
								needFund = "0.00";
							}
							Object[] sendData = new Object[17];
							sendData[0] = waybill.getWaybillCode();
							sendData[1] = waybill.getWaybillCode();
							;
							sendData[2] = waybill.getReceiverName();
							if (null == waybill.getReceiverZipCode()) {
								sendData[3] = "";
							} else {
								sendData[3] = waybill.getReceiverZipCode();
							}
							String province = waybill.getProvinceName();
							if (StringUtils.isBlank(province)) {
								if (waybill.getProvinceId() != null){
									Assort assort = baseService.getOneAssortById(waybill.getProvinceId());
									if (assort != null){
										sendData[4] = assort.getAssDis();
									}else {
										sendData[4] = "";								}
								} else {
									sendData[4] = "";
								}
							} else {
								sendData[4] = province;
							}
							String city = waybill.getCityName();
							if (StringUtils.isBlank(city)) {
								if (waybill.getCityId() != null){
									Assort assort = baseService.getOneAssortById(waybill.getCityId());
									if (assort != null){
										sendData[5] = assort.getAssDis();
									}else {
										sendData[5] = "";
									}
								} else {
									sendData[5] = "";
								}
							} else {
								sendData[5] = city;
							}
							String county = waybill.getCountryName();
							if (StringUtils.isBlank(county)) {
								if (waybill.getCountryId() != null){
									Assort assort = baseService.getOneAssortById(waybill.getCountryId());
									if (assort != null){
										sendData[6] = assort.getAssDis();
									}else {
										sendData[6] = "";
									}
								} else {
									sendData[6] = "";
								}
							} else {
								sendData[6] = county;
							}
							sendData[7] = waybill.getReceiverAddress();
							sendData[8] = waybill.getReceiverMobile();
							sendData[9] = waybill.getReceiverTel();
							sendData[10] = "";

							List<DeliveryPackageD> deliveryPackage = tWaybill
									.getPackageList();
							if (deliveryPackage != null
									&& !deliveryPackage.isEmpty()
									&& BusinessHelper
											.checkIntNumRange(deliveryPackage
													.size())) {
								for (DeliveryPackageD delivery : deliveryPackage) {
									sendData[0] = delivery.getPackageBarcode();
									sendData[11] = 0;
									if (delivery.getGoodWeight() != null)
										sendData[11] = delivery.getGoodWeight()*1000;
									if (delivery.getAgainWeight() != null)
										sendData[11] = delivery.getAgainWeight()*1000;
									sendData[12] = delivery.getGoodVolume();
									sendData[13] = collection;
									sendData[14] = needFund;
									sendData[15] = deliveryPackage.size();
									sendData[16] = delivery.getPackageBarcode();
									String tempstring = String
											.format(""
													+ "<OrderShip><Id>%1$s</Id><OrderId>%2$s</OrderId><BagId>%17$s</BagId><UserName>"
													+ "%3$s</UserName><PostalCode>%4$s</PostalCode><Province>%5$s</Province><City>%6$s</City>"
													+ "<Area>%7$s</Area><Address><![CDATA[%8$s]]></Address><CellPhoneNumber>%9$s</CellPhoneNumber>"
													+ "<TelePhoneNumber>%10$s</TelePhoneNumber><EmailAddress></EmailAddress>"
													+ "<DeliveryTime>%11$s</DeliveryTime><Weight>%12$s</Weight><Wbulk>%13$s</Wbulk>"
													+ "<Collection>%14$s</Collection><NeedFund>%15$s</NeedFund><Remark></Remark>"
													+ "<BagQuatity>%16$s</BagQuatity></OrderShip>",
													(Object[]) sendData);
									buffer.append(tempstring);
								}
							}
						}
					}
					String whEmsKey = PropertiesHelper.newInstance().getValue(
							"encpKey");
					if (StringHelper.isEmpty(whEmsKey))
						return resultBool;
					String body = "<PlaintextData><OrderShipList>"
							+ buffer.toString()
							+ "</OrderShipList></PlaintextData>";
					this.logger.error("数据报文：" + body);
					String businessId = wlist.get(0);//改为逐条发送的话，只有一条运单数据
					whSmsSendMq.sendOnFailPersistent(businessId,body);
					resultBool = true;
				}
			}

		}
		return resultBool;
	}

	public static String md5(String input) {
		MessageDigest md = null;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(input.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "1";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "1";
		}
		return new BigInteger(1, md.digest()).toString(16);
	}

	@JProfiler(jKey = "com.jd.bluedragon.distribution.send.service.ReverseDeliveryServiceImpl.getWhemsWaybill",mState = JProEnum.TP)
	public WhemsWaybillResponse getWhemsWaybill(List<String> wlist) {

		WhemsWaybillResponse response = new WhemsWaybillResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
		for (String wayCode : wlist) {
			SendDetail send = new SendDetail();
			send.setWaybillCode(wayCode);
			send.setIsCancel(0);
			List<SendDetail> sendlist = sendDatailDao
					.querySendDatailsBySelective(send);//FIXME:无create_site_code有跨节点风险
			if (sendlist.isEmpty()) {
				response.setCode(JdResponse.CODE_PARAM_ERROR);
				response.setMessage("没有该订单的发货数据请查证后在调用，订单号为：" + wayCode);
				return response;
			} else {
				boolean flage = true;
				for (SendDetail detail : sendlist) {
					if (detail.getSendCode() != null) {
						if ((detail.getReceiveSiteCode() == 452)
								|| (detail.getReceiveSiteCode() == 1376)
								|| (detail.getReceiveSiteCode() == 2228)) {
							flage = false;
							break;
						}
					}
				}
				if (flage) {
					response.setCode(JdResponse.CODE_PARAM_ERROR);
					response.setMessage("该订单不属于武汉邮政配送订单，订单号为：" + wayCode);
					return response;
				}
			}
		}
		List<WhemsWaybillEntity> enList = new ArrayList<WhemsWaybillEntity>();
		WChoice queryWChoice = new WChoice();
		queryWChoice.setQueryPackList(true);
		queryWChoice.setQueryWaybillC(true);

		boolean flage = true;
		List<SysConfig> configs=baseService.queryConfigByKeyWithCache(EMS_ONOFF);
		for(SysConfig sys : configs){
			if(StringHelper.matchSiteRule(sys.getConfigContent(), "EMS_OFF")){
				flage = false;
			}
		}

		try {
			List<BigWaybillDto> tWaybillList = null;
			if(flage)
				tWaybillList = deliveryService.getWaillCodeListMessge(queryWChoice, wlist);
			else
				tWaybillList =getWaillCodeListMessge(queryWChoice, wlist);

			if (tWaybillList != null && !tWaybillList.isEmpty()) {
				for (BigWaybillDto tWaybill : tWaybillList) {
					if (tWaybill != null && tWaybill.getWaybill() != null) {
						Waybill waybill = tWaybill.getWaybill();
						String payment = String.valueOf(waybill.getPayment());
						String declaredValue = waybill.getCodMoney();
						String collection = "";
						String needFund = "0.00";
						if (payment.equals("1") || payment.equals("3")) {
							collection = "1";
							if (declaredValue != null) {
								Double d = Math.round(Double.parseDouble(declaredValue) * 100) / 100.00;
								needFund = d.toString();
							}
						} else {
							collection = "0";
							needFund = "0.00";
						}
						List<DeliveryPackageD> deliveryPackage = tWaybill
								.getPackageList();
						if (deliveryPackage != null
								&& !deliveryPackage.isEmpty()
								&& BusinessHelper.checkIntNumRange(deliveryPackage
										.size())) {
							for (DeliveryPackageD delivery : deliveryPackage) {
								WhemsWaybillEntity entity = new WhemsWaybillEntity();
								entity.setOrderId(waybill.getWaybillCode());
								if (null == waybill.getReceiverZipCode()) {
									entity.setPostalCode("");
								} else {
									entity.setPostalCode(waybill.getReceiverZipCode());
								}
								entity.setUserName(waybill.getReceiverName());
								String province = waybill.getProvinceName();
								if (null == province) {
									entity.setProvince("");
								} else {
									entity.setProvince(province);
								}

								String city = waybill.getCityName();
								if (null == city) {
									entity.setCity("");
								} else {
									entity.setCity(city);
								}
								String county = waybill.getCountryName();
								if (null == county) {
									entity.setArea("");
								} else {
									entity.setArea(county);
								}
								entity.setAddress(waybill.getReceiverAddress());
								entity.setCellPhoneNumber(waybill.getReceiverMobile());
								entity.setTelePhoneNumber(waybill.getReceiverTel());
								entity.setDeliveryTime("");
								entity.setEmailAddress("");
								entity.setWbulk("0");
								if (delivery.getGoodVolume() != null)
									entity.setWbulk(delivery.getGoodVolume());
								entity.setWeight("0.0");
								if (delivery.getGoodWeight() != null)
									entity.setWeight(String.valueOf(delivery
											.getGoodWeight() * 1000));
								if (delivery.getAgainWeight() != null)
									entity.setWeight(String.valueOf(delivery
											.getAgainWeight() * 1000));
								entity.setCollection(collection);
								entity.setNeedFund(needFund);
								entity.setBagQuatity(String.valueOf(deliveryPackage
										.size()));
								entity.setBagId(delivery.getPackageBarcode());
								entity.setId(delivery.getPackageBarcode());
								enList.add(entity);
							}
						}
					}
				}
				response.setData(enList);
			}else{
				response.setCode(JdResponse.CODE_OK_NULL);
				response.setMessage(JdResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(JdResponse.CODE_SERVICE_ERROR);
			response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return response;
	}

	public void pushWhemsWaybill(List<String> wlist) {
		sendMqToWhsmsServer(wlist);
	}

	public Ems4JingDongPortType getWhemsClientService() {
		return whemsClientService;
	}

	public void setWhemsClientService(Ems4JingDongPortType whemsClientService) {
		this.whemsClientService = whemsClientService;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  HashMap getSend3plConfigMap(HashMap send3plConfigMap) {
		send3plConfigMap.put("487","上海长发物流");
		send3plConfigMap.put("485","沈阳传喜物流");
		send3plConfigMap.put("650","沈阳忠信达快递");
		send3plConfigMap.put("1076","成都立即送物流");
		send3plConfigMap.put("782","成都派尔快递（市内）");
		send3plConfigMap.put("1190","上海派尔快递");
		send3plConfigMap.put("1169","广东南都物流");
		send3plConfigMap.put("554","成都派尔快递");
		send3plConfigMap.put("1060","苏州易高物流");
		send3plConfigMap.put("1758","苏州门对门快递");
		return send3plConfigMap;
	}

	/**
	 * 调用全国邮政接口回传数据
	 * @param waybillList
	 */
	public String toEmsServer(List<String> waybillList) {
		StringBuffer errorWaybill = new StringBuffer();
		for(String waybillCode : waybillList){
			if(waybillCode == null){
				continue;
			}
			String sysAccount = "";
			List<WaybillInfo> list = getWaybillInfo(waybillCode);
			if(list!=null && !list.isEmpty()){
				StringBuffer buffer = new StringBuffer();
				for(WaybillInfo info : list){
					sysAccount = info.getSysAccount();
					buffer.append("<printData><businessType>")
					.append(info.getBusinessType())
					.append("</businessType><dateType></dateType><procdate></procdate><scontactor>")
					.append(info.getScontactor())
					.append("</scontactor><scustMobile>")
					.append(info.getScustMobile())
					.append("</scustMobile><scustTelplus></scustTelplus><scustPost></scustPost><scustAddr><![CDATA[")
					.append(info.getScustAddr())
					.append("]]></scustAddr><scustComp></scustComp><tcontactor>")
					.append(info.getTcontactor())
					.append("</tcontactor><tcustMobile>")
					.append(info.getTcustMobile())
					.append("</tcustMobile><tcustTelplus>")
					.append(info.getTcustTelplus())
					.append("</tcustTelplus><tcustPost>")
					.append(info.getTcustPost())
					.append("</tcustPost ><tcustAddr><![CDATA[")
					.append(info.getTcustAddr())
					.append("]]></tcustAddr><tcustComp></tcustComp><tcustProvince>")
					.append(info.getTcustProvince())
					.append("</tcustProvince><tcustCity>")
					.append(info.getTcustCity())
					.append("</tcustCity><tcustCounty>")
					.append(info.getTcustCounty())
					.append("</tcustCounty><weight>")
					.append(info.getWeight())
					.append("</weight><length></length><insure></insure><fee>")
					.append(info.getFee().intValue())
					.append("</fee><feeUppercase>")
					.append(info.getFeeUppercase())
					.append("</feeUppercase><cargoDesc></cargoDesc><cargoDesc1></cargoDesc1><cargoDesc2></cargoDesc2><cargoDesc3></cargoDesc3><cargoDesc4></cargoDesc4><cargoType></cargoType><deliveryclaim></deliveryclaim><remark></remark><bigAccountDataId>")
					.append(info.getPackageBarcode())
					.append("</bigAccountDataId><customerDn>")
					.append(info.getWaybillCode())
					.append("</customerDn><subBillCount>")
					.append(info.getPackageNum().equals(0)?"":info.getPackageNum())
					.append("</subBillCount><mainBillNo></mainBillNo><mainBillFlag></mainBillFlag><mainSubPayMode>4</mainSubPayMode><payMode>")
					.append(info.getPayMode())
					.append("</payMode><insureType></insureType><blank1></blank1><blank2></blank2><blank3></blank3><blank4></blank4><blank5></blank5></printData>");
				}
				String body = buffer.toString();
				body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<XMLInfo><sysAccount>"+sysAccount+"</sysAccount><passWord>e10adc3949ba59abbe56e057f20f883e</passWord><printKind>2</printKind><printDatas>"
						+ body + "</printDatas></XMLInfo>";
				this.logger.error("ems数据报文：" + body);
				String businessId = waybillCode;
                emsSendMq.sendOnFailPersistent(businessId,body);// 改为一条一条的发送的话，busineId为运单号
			}
		}
		return errorWaybill.toString();
	}

	public static String decrypt(String mingwen) {
		Base64 base64=new Base64();
		String result = "";
		try {
			result = new String(base64.decode(md5(mingwen).getBytes("utf-8")),Charset.forName("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<WaybillInfo> getWaybillInfo(String waybillCode) {
		SendDetail send = new SendDetail();
		send.setWaybillCode(waybillCode);
		List<SendDetail> sendlist = sendDatailDao.querySendDatailsBySelective(send);//FIXME:无create_site_code有跨节点风险
		if (sendlist.isEmpty()) {
			return null;
		}
		Integer createSiteCode = sendlist.get(0).getCreateSiteCode();
		Integer receiveSiteCode = sendlist.get(0).getCreateSiteCode();
		List<SysConfig> configlist=baseService.queryConfigByKeyWithCache(EMS_SITE);
		for(SendDetail dSendDetail :sendlist){
			for(SysConfig sys : configlist){
				if(StringHelper.matchSiteRule(sys.getConfigContent(),dSendDetail.getReceiveSiteCode().toString())){
					createSiteCode = dSendDetail.getCreateSiteCode();
					receiveSiteCode = dSendDetail.getReceiveSiteCode();
					break;
				}
			}
		}

		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(createSiteCode);

		List<SysConfig> configs = baseService.queryConfigByKeyWithCache(EMS_SITE + "_"+ receiveSiteCode);
		String sysAccount = "";
		for (SysConfig sys : configs) {
			if(sys.getConfigContent()!=null){
				sysAccount = sys.getConfigContent();
				break;
			}
		}

		List<WaybillInfo> list = new ArrayList<WaybillInfo>();

		BigWaybillDto WaybillDto = waybillService.getWaybill(waybillCode);

		//如果订单信息为空咋调用快生运单数据源获取信息
		if (WaybillDto == null || WaybillDto.getWaybill() == null){
			WaybillDto = getWaybillQuickProduce(waybillCode);
		}

		if (WaybillDto != null && WaybillDto.getWaybill() != null) {
			Waybill waybill = WaybillDto.getWaybill();
			List<DeliveryPackageD> deliveryPackage = WaybillDto.getPackageList();
			if (deliveryPackage != null && !deliveryPackage.isEmpty()&& BusinessHelper.checkIntNumRange(deliveryPackage.size())) {
				String collection = "";
				Double needFund = 0.00;
				String declaredValue = waybill.getCodMoney();

				String payment = String.valueOf(waybill.getPayment());

				for (DeliveryPackageD delivery : deliveryPackage) {
					WaybillInfo info = new WaybillInfo();
					info.setPackageBarcode(delivery.getPackageBarcode());
					info.setWaybillCode(waybill.getWaybillCode());
					if(waybill.getGoodNumber()!=null)
						info.setPackageNum(waybill.getGoodNumber()-1);
					else
						info.setPackageNum(0);

					if (bDto != null) {
						if(bDto.getDmsName()!=null)
							info.setScontactor(bDto.getDmsName());
						else
							info.setScontactor("");
						if(bDto.getPhone()!=null)
							info.setScustMobile(bDto.getPhone());
						else
							info.setScustMobile("");
						if(bDto.getAddress()!=null)
							info.setScustAddr(bDto.getAddress());
						else
							info.setScustAddr("");
					}

					info.setTcontactor(waybill.getReceiverName());
					info.setTcustMobile(waybill.getReceiverMobile());
					info.setTcustTelplus(waybill.getReceiverTel());
					info.setTcustPost(waybill.getReceiverZipCode());
					info.setTcustAddr(waybill.getReceiverAddress());

					if(waybill.getProvinceName()!=null)
						info.setTcustProvince(waybill.getProvinceName());
					else
						info.setTcustProvince("");

					if(waybill.getCityName()!=null)
						info.setTcustCity(waybill.getCityName());
					else
						info.setTcustCity("");

					if(waybill.getCountryName()!=null)
						info.setTcustCounty(waybill.getCountryName());
					else
						info.setTcustCounty("");
					if(delivery.getAgainWeight()!=null)
						info.setWeight(delivery.getAgainWeight());
					else
						info.setWeight(0.0);
					if(delivery.getGoodVolume()!=null)
						info.setVolume(delivery.getGoodVolume());
					else
						info.setVolume("0");
					// 1货到付款 3上门自提
					if (payment.equals("1") || payment.equals("3")) {
						collection = "2";
						if (declaredValue != null) {
							needFund = Math.round(Double
									.parseDouble(declaredValue) * 100) / 100.00;
						}
					}

					if (declaredValue == null)
						declaredValue="0.0";
					info.setFeeUppercase(new CnUpperCaser(declaredValue).getCnString());
					info.setFee(needFund);
					info.setPayMode("1");//1现金
					info.setBusinessType(collection);
					info.setTcustMobile(waybill.getReceiverMobile());
					info.setSysAccount(sysAccount);
					list.add(info);
				}
			}
		} else {
			logger.error("JOS获取订单信息,订单接口返回空信息" + waybillCode);
			return null;
		}
		return list;
	}

	public WaybillInfoResponse getEmsWaybillInfo(String waybillCode) {

		logger.error("JOS获取订单信息,订单号为" + waybillCode);
		List<WaybillInfo> list = null;
		try {
			list = getWaybillInfo(waybillCode);
			if (list==null || list.isEmpty()) {
				logger.error("JOS获取订单信息,订单没有发货记录" + waybillCode);
				return new WaybillInfoResponse(JdResponse.CODE_OK_NULL,JdResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			logger.error("JOS获取订单信息失败：信息为",e);
			return new WaybillInfoResponse(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return new WaybillInfoResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK,JsonHelper.toJson(list));
	}

	@Override
	public BigWaybillDto getWaybillQuickProduce(String waybillCode) {
		QuickProduceWabill tQuickProduceWabill = quickProduceService.getQuickProduceWabill(waybillCode);
		if (tQuickProduceWabill == null) {
			return null;
		}

		JoinDetail tJoinDetail = tQuickProduceWabill.getJoinDetail();
		com.jd.bluedragon.common.domain.Waybill waybillQP = tQuickProduceWabill.getWaybill();
		if (tJoinDetail == null || waybillQP==null) {
			return null;
		}

		BigWaybillDto tBigWaybillDto = toWaybill(waybillQP,tJoinDetail);
		return tBigWaybillDto;
	}

	private BigWaybillDto toWaybill(com.jd.bluedragon.common.domain.Waybill waybillQP ,JoinDetail tJoinDetail) {
		Waybill waybill = new Waybill();
		BigWaybillDto tBigWaybillDto = new BigWaybillDto();
		waybill.setCodMoney(String.valueOf(tJoinDetail.getDeclaredValue()));
		waybill.setPayment(tJoinDetail.getPayment());
		waybill.setWaybillCode(waybillQP.getWaybillCode());
		waybill.setReceiverName(tJoinDetail.getReceiverName());
		waybill.setReceiverMobile(tJoinDetail.getReceiverMobile());
		waybill.setReceiverTel(tJoinDetail.getReceiverTel());
		waybill.setReceiverZipCode(tJoinDetail.getReceiverZipCode());
		waybill.setReceiverAddress(tJoinDetail.getReceiverAddress());
		waybill.setProvinceName(tJoinDetail.getProvinceName());
		waybill.setCityName(tJoinDetail.getCityName());
		waybill.setCountryName(tJoinDetail.getCountryName());
		tBigWaybillDto.setWaybill(waybill);

		SendDetail tSendDatail = new SendDetail();
		tSendDatail.setWaybillCode(waybillQP.getWaybillCode());
		List<SendDetail> oneList = sendDatailDao.querySendDatailsBySelective(tSendDatail);//FIXME:无create_site_code有跨节点风险
		if (oneList != null && !oneList.isEmpty()) {
			List<DeliveryPackageD> list = generateAllPackageCodes(oneList.get(0).getPackageBarcode(), tJoinDetail);
			waybill.setGoodNumber(list.size());
			tBigWaybillDto.setPackageList(list);
		}
		return tBigWaybillDto;
	}

	/**
	* 生产包裹号码
	*/

    private List<DeliveryPackageD> generateAllPackageCodes(String input ,JoinDetail tJoinDetail){
		List<DeliveryPackageD> packList = new ArrayList<DeliveryPackageD>();

		if(WaybillUtil.isPackageCode(input)) {
			//生成包裹号列表
			List<String> packageCodes = WaybillUtil.generateAllPackageCodes(input);
			for (int i = 0; i < packageCodes.size(); i++) {
				DeliveryPackageD pack = new DeliveryPackageD();
				pack.setPackageBarcode(packageCodes.get(i));
				pack.setWaybillCode(SerialRuleUtil.getWaybillCode(packageCodes.get(i)));
				pack.setAgainWeight(tJoinDetail.getGoodWeight());
				packList.add(pack);
			}
		}
		return packList;
	}

    private List<BigWaybillDto> getWaillCodeListMessge(WChoice queryWChoice ,List<String> wlist){
    	BigWaybillDto WaybillDto = new BigWaybillDto();
    	List<BigWaybillDto> list = new ArrayList<BigWaybillDto>();
    	for(String wycode : wlist){
    		WaybillDto = waybillService.getWaybill(wycode);
    		//如果订单信息为空咋调用快生运单数据源获取信息
    		if (WaybillDto == null || WaybillDto.getWaybill() == null){
    			WaybillDto = getWaybillQuickProduce(wycode);
    			if(WaybillDto != null)
    				list.add(WaybillDto);
    		}else{
    			list.add(WaybillDto);
    		}
    	}
    	return list;
    }

    public static void main(String[] args) {
        List<String> waybillCodes = new ArrayList<String>();
        waybillCodes.add("42747094316");
        waybillCodes.add("42747094313");
        waybillCodes.add("42747094310");
        waybillCodes.add("42747094265");
        waybillCodes.add("42747090797");
        System.out.println(JsonHelper.toJson(waybillCodes));

    }
}
