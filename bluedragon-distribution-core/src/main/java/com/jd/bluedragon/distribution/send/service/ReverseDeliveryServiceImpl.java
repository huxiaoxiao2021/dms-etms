package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.Constants;
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
import com.jd.bluedragon.external.crossbow.whems.domain.WuHanEMSWaybillEntityDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private final Logger log = LoggerFactory.getLogger(ReverseDeliveryServiceImpl.class);

	@Autowired
	private SendMDao sendMDao;

	@Autowired
	private SendDatailDao sendDatailDao;

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
				this.log.warn("batchProcessOrderInfo2DSF武汉邮政推送批次号：{}",task.getBoxCode());
				batchProcessOrderInfo2DSF(tSendM);
			}else{
				List<SysConfig> configs=baseService.queryConfigByKeyWithCache(EMS_SITE);
				for(SysConfig sys : configs){
					if(StringHelper.matchSiteRule(sys.getConfigContent(), task.getReceiveSiteCode().toString())){
						this.log.warn("全国邮政推送批次号：{}",task.getBoxCode());
						batchProcesstoEmsServer(tSendM);
					}
				}
			}
		}
		return true;
	}

	public void updateIsCancelToWaybillByBox(SendM tSendM,
			List<SendDetail> tlist) {
		// 取消发货的时候添加运单回传worker
		if (tlist != null && !tlist.isEmpty()) {
			for (SendDetail dSendDatail : tlist) {
				dSendDatail.setSendCode(tSendM.getSendCode());
				dSendDatail.setOperateTime(tSendM.getOperateTime());
				dSendDatail.setCreateUser(tSendM.getUpdaterUser());
				dSendDatail.setCreateUserCode(tSendM.getUpdateUserCode());
				dSendDatail.setUpdateTime(new Date());
				dSendDatail.setIsCancel(2);
				dSendDatail.setOperatorData(tSendM.getOperatorData());
				dSendDatail.setOperatorId(tSendM.getOperatorId());
				dSendDatail.setOperatorTypeCode(tSendM.getOperatorTypeCode());
			}
			deliveryService.updateWaybillStatus(tlist);
		}
	}

	public void updateIsCancelByBox(SendM tSendM) {
		deliveryService.cancelSendDatailByBox(tSendM);
		deliveryService.cancelSendM(tSendM);
	}

	public void updateIsCancelToWaybillByPackageCode(SendM tSendM,
			SendDetail tSendDatail) {
		List<SendDetail> tlist = new ArrayList<SendDetail>();
		tSendDatail.setIsCancel(2);
		if (StringUtils.isNotEmpty(tSendM.getSendCode())) {
			tSendDatail.setSendCode(tSendM.getSendCode());
		}
		tSendDatail.setOperateTime(tSendM.getOperateTime());
		tSendDatail.setCreateUser(tSendM.getUpdaterUser());
		tSendDatail.setCreateUserCode(tSendM.getUpdateUserCode());
		tSendDatail.setUpdateTime(new Date());
		tSendDatail.setOperatorData(tSendM.getOperatorData());
		tSendDatail.setOperatorId(tSendM.getOperatorId());
		tSendDatail.setOperatorTypeCode(tSendM.getOperatorTypeCode());
		tlist.add(tSendDatail);
		deliveryService.updateWaybillStatus(tlist);
	}

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

	public void batchProcessOrderInfo2DSF(List<SendM> tSendMList) {

		List<SendDetail> sendList = new ArrayList<SendDetail>();
		deliveryService.getAllList(tSendMList, sendList);
		this.log.info("batchProcessOrderInfo2DSF武汉邮政推送接口sendList长度:{}", sendList.size());
		if (sendList != null && !sendList.isEmpty()) {
			Set<String> waybillset = new HashSet<String>();
			for (SendDetail dSendDatail : sendList) {
				if (waybillset.contains(dSendDatail.getWaybillCode())) {
					continue;
				}
				waybillset.add(dSendDatail.getWaybillCode());
				pushWhemsWaybill(dSendDatail.getWaybillCode());
			}
		}
	}

	public void batchProcesstoEmsServer(List<SendM> tSendMList) {

		List<SendDetail> sendList = new ArrayList<SendDetail>();
		deliveryService.getAllList(tSendMList, sendList);
		this.log.info("batchProcesstoEmsServer邮政推送接口sendList长度:{}", sendList.size());
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
		this.log.info("batchProcessOrderInfo3PL推送数据");
		List<SendDetail> slist = new ArrayList<SendDetail>();
		deliveryService.getAllList(tSendMList, slist);
		this.log.info("batchProcessOrderInfo3PL推送接口sendList长度:{}", slist.size());
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
			this.log.info("dataTo3PlServer处理任务数据:{}", waybillList.size());
			List<String>[] splitListResultAl = splitList(waybillList);
			for (List<String> wlist : splitListResultAl) {
				List<OrderShipsReturnDto> returnDtoList = new ArrayList<OrderShipsReturnDto>();
				String encryptData = Md5Helper.encode3PL(wlist.get(0)+"123456");
				for(String waybillCode : wlist){
					OrderShipsReturnDto returnDto = new OrderShipsReturnDto();
					returnDto.setClearOld(0);

					this.log.info("调用运单接口, 订单号为： {}" , waybillCode);
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
					this.log.info("调用接口返回状态失败, 信息： {}" , baseResult.getMessage());
				}
			}
		}
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

	@Override
	@JProfiler(jKey = "dms.core.ReverseDeliveryService.pushWhemsWaybill", jAppName = Constants.UMP_APP_NAME_DMSWORKER)
	public void pushWhemsWaybill(String waybillCode) {
		if (StringHelper.isEmpty(waybillCode) || !WaybillUtil.isWaybillCode(waybillCode)) {
			log.warn("推送武汉邮政数据，运单号错误。waybillCode:{}", waybillCode);
			return;
		}

		/* 快生开关 注意取反 */
		boolean quickFlag = true;
		List<SysConfig> configs=baseService.queryConfigByKeyWithCache(EMS_ONOFF);
		for(SysConfig sys : configs){
			if(StringHelper.matchSiteRule(sys.getConfigContent(), "EMS_OFF")){
				quickFlag =false;
			}
		}
		BigWaybillDto waybillDto;
		if (quickFlag) {
			waybillDto = waybillService.getWaybill(waybillCode);
		} else {
			waybillDto = getWaybillQuickProduce(waybillCode);
		}

		if (waybillDto == null || waybillDto.getWaybill() == null) {
			log.warn("获取运单数据失败，是否快生模式：{}, 运单号：{}", quickFlag, waybillCode);
			return;
		}
		Waybill waybill = waybillDto.getWaybill();

		WuHanEMSWaybillEntityDto waybillEntityDto = new WuHanEMSWaybillEntityDto();
		waybillEntityDto.setOrderId(waybillCode);
		waybillEntityDto.setUserName(waybill.getReceiverName());
		waybillEntityDto.setAddress(waybill.getReceiverAddress());
		waybillEntityDto.setTelPhoneNumber(waybill.getReceiverTel());
		waybillEntityDto.setCellphoneNumber(waybill.getReceiverMobile());
		waybillEntityDto.setPostalCode(StringHelper.isEmpty(waybill.getReceiverZipCode())?
				"" : waybill.getReceiverZipCode());

		String province = waybill.getProvinceName();
		waybillEntityDto.setProvince(province);
		if (StringUtils.isBlank(province)) {
			if (waybill.getProvinceId() != null){
				Assort assort = baseService.getOneAssortById(waybill.getProvinceId());
				if (assort != null){
					waybillEntityDto.setProvince(assort.getAssDis());
				}
			}
		}

		String city = waybill.getCityName();
		waybillEntityDto.setCity(city);
		if (StringUtils.isBlank(city)) {
			if (waybill.getCityId() != null){
				Assort assort = baseService.getOneAssortById(waybill.getCityId());
				if (assort != null){
					waybillEntityDto.setCity(assort.getAssDis());
				}
			}
		}

		String county = waybill.getCountryName();
		waybillEntityDto.setArea(county);
		if (StringUtils.isBlank(county)) {
			if (waybill.getCountryId() != null){
				Assort assort = baseService.getOneAssortById(waybill.getCountryId());
				if (assort != null){
					waybillEntityDto.setArea(assort.getAssDis());
				}
			}
		}
		waybillEntityDto.setAddress("<![CDATA[" + waybill.getReceiverAddress() + "]]");
		waybillEntityDto.setDeliveryTime("");
		waybillEntityDto.setEmailAddress("");
		waybillEntityDto.setWeight("0");
		waybillEntityDto.setRemark("");

		String payment = String.valueOf(waybill.getPayment());
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
		waybillEntityDto.setCollection(collection);
		waybillEntityDto.setNeedFund(needFund);
		if (waybillDto.getPackageList() != null && waybillDto.getPackageList().size() > 0) {
			waybillEntityDto.setBagQuantity(String.valueOf(waybillDto.getPackageList().size()));
			for (DeliveryPackageD deliveryPackageD : waybillDto.getPackageList()) {
				waybillEntityDto.setId(deliveryPackageD.getPackageBarcode());
				waybillEntityDto.setBagId(deliveryPackageD.getPackageBarcode());
				if (waybill.getGoodWeight() != null) {
					waybillEntityDto.setWeight(String.valueOf(deliveryPackageD.getGoodWeight() * 1000));
				}
				if (waybill.getAgainWeight() != null) {
					waybillEntityDto.setWeight(String.valueOf(deliveryPackageD.getAgainWeight() * 1000));
				}
				waybillEntityDto.setwBulk(String.valueOf(deliveryPackageD.getGoodVolume()));
				/* 逐条发送 */
				whSmsSendMq.sendOnFailPersistent(waybillCode,JsonHelper.toJson(waybillEntityDto));
			}
		}
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
	public void toEmsServer(List<String> waybillList) {
		for(String waybillCode : waybillList){
			if(StringHelper.isEmpty(waybillCode)){
				continue;
			}
			String sysAccount = "";
			List<WaybillInfo> list = getWaybillInfo(waybillCode);
			if (null == list || list.isEmpty()) {
				continue;
			}
			for (WaybillInfo info : list) {
				String businessId = info.getWaybillCode();
				String body = JsonHelper.toJson(info);
				this.log.warn("ems数据报文：{}" , body);
				emsSendMq.sendOnFailPersistent(businessId,body);// 改为一条一条的发送的话，busineId为运单号
			}
		}
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
			log.warn("JOS获取订单信息,订单接口返回空信息:{}" , waybillCode);
			return null;
		}
		return list;
	}

	public WaybillInfoResponse getEmsWaybillInfo(String waybillCode) {

		log.info("JOS获取订单信息,订单号为{}" , waybillCode);
		List<WaybillInfo> list = null;
		try {
			list = getWaybillInfo(waybillCode);
			if (list==null || list.isEmpty()) {
				log.warn("JOS获取订单信息,订单没有发货记录:{}" , waybillCode);
				return new WaybillInfoResponse(JdResponse.CODE_OK_NULL,JdResponse.MESSAGE_OK_NULL);
			}
		} catch (Exception e) {
			log.error("JOS获取订单信息失败：waybillCode={}",waybillCode,e);
			return new WaybillInfoResponse(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return new WaybillInfoResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK,JsonHelper.toJson(list));
	}

    @JProfiler(jKey = "DMSWEB.ReverseDeliveryServiceImpl.getWaybillQuickProduce",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP})
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
}
