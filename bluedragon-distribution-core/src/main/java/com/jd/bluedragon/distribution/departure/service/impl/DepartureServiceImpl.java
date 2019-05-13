package com.jd.bluedragon.distribution.departure.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.DeparturePrintRequest;
import com.jd.bluedragon.distribution.api.request.DepartureRequest;
import com.jd.bluedragon.distribution.api.request.DepartureSendRequest;
import com.jd.bluedragon.distribution.api.request.DepartureTmpRequest;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.dao.DepartureCarDao;
import com.jd.bluedragon.distribution.departure.dao.DepartureSendDao;
import com.jd.bluedragon.distribution.departure.dao.DepartureTmpDao;
import com.jd.bluedragon.distribution.departure.domain.Departure;
import com.jd.bluedragon.distribution.departure.domain.DepartureCar;
import com.jd.bluedragon.distribution.departure.domain.DepartureSend;
import com.jd.bluedragon.distribution.departure.domain.SendBox;
import com.jd.bluedragon.distribution.departure.domain.SendMeasure;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.failqueue.dao.TaskFailQueueDao;
import com.jd.bluedragon.distribution.failqueue.domain.DealData_Departure_3PL;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.failqueue.service.IFailQueueService;
import com.jd.bluedragon.distribution.receive.domain.SendCode;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.seal.service.SealVehicleService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BigDecimalHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.ws.VosQueryWS;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class DepartureServiceImpl implements DepartureService {

	private final static Logger logger = Logger
			.getLogger(DepartureServiceImpl.class);

	private final static String DMS_ADDRESS = PropertiesHelper.newInstance().getValue("DMS_ADDRESS");

	private static final String URL = DMS_ADDRESS + "/services/departure/createDepartue";

	private static final Integer BUSINESS_TYPE_ONE = 10;

	@Autowired
	private SendMDao sendMDao;
	@Autowired
	private DepartureCarDao departureCarDao;
	@Autowired
	private SendDatailDao sendDatailDao;
	@Autowired
	private DepartureSendDao departureSendDao;
	@Autowired
	private SealVehicleService sealVehicleService;
	@Autowired
	private TaskService taskService;
	@Autowired
	TaskFailQueueDao taskFailQueueDao;

	@Autowired
	WaybillPackageManager waybillPackageManager;

	@Autowired
	private BaseService baseService;

	@Autowired
	private SiteService siteService;
	
	@Autowired
	private WaybillTraceApi waybillTraceApi;

	@Autowired
	private DepartureTmpDao departureTmpDao;

    @Autowired
    @Qualifier("receiveArteryInfoMQ")
    private DefaultJMQProducer receiveArteryInfoMQ;

	@Autowired
	private VosQueryWS vosQueryWS;

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

	public static final String CARCODE_MARK = "0";  // 按车次号查询

	private final RestTemplate template = new RestTemplate();


	/**
	 * 生成发货数据处理
	 * 
	 * @param departure
	 *            发车相关数据
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ServiceMessage<String> createDeparture(Departure departure)
			throws Exception {
		ServiceMessage<String> result = new ServiceMessage<String>();
		List<SendM> sendMs = departure.getSendMs();
		// 没有发货批次，无需生成发车表以及封车表
		if (sendMs == null) {
			result.setResult(ServiceResultEnum.FAILED);
			result.setErrorMsg("生成发车信息失败: 没有批次信息");
			return result;
		}

		long shieldsCarId = sequenceGenAdaptor.newId(DepartureCar.class.getSimpleName());
		if (Departure.DEPARTRUE_TYPE_TURNTABLE == departure.getType()) {
			/*
			 * 转车(type=2) 1.生成发车表数据 2.插[DepartureSend]中间表
			 */
			if (createDepartureCar(departure, sendMs, shieldsCarId,
					departure.getType())) {
				createDepartureSend(sendMs, shieldsCarId);
			} else {
				result.setResult(ServiceResultEnum.FAILED);
				result.setErrorMsg("create DepartureCar failed: Duplicate fingerprint");
				return result;
			}
		} else if (Departure.DEPARTRUE_TYPE_TURNTABLE != departure.getType()
				&& Integer.valueOf(Constants.BUSSINESS_TYPE_REVERSE).equals(
						departure.getBusinessType())) {
			commonLogic(departure, sendMs, shieldsCarId, departure.getType());
			// 推逆向系统  取消逆向发车的时候推送仓储任务，修改到发货环节推送 20150724
			//toReverse(sendMs);

		} else if (Departure.DEPARTRUE_TYPE_TURNTABLE != departure.getType()
				&& Integer.valueOf(Constants.BUSSINESS_TYPE_POSITIVE).equals(
						departure.getBusinessType())) {
			if (departure.getType() == Departure.DEPARTRUE_TYPE_ZHIXIAN) {// 支线正向发车
				commonLogic(departure, sendMs, shieldsCarId,
						Departure.DEPARTRUE_TYPE_BRANCH);
			} else {
				commonLogic(departure, sendMs, shieldsCarId,
						departure.getType());
			}
			
			if(departure.getType()==Departure.DEPARTRUE_TYPE_ZHIXIAN){//支线正向发车
				createDepartureSend(sendMs, shieldsCarId);
			}
			
			// 推财务
			pushFinancialData(departure, sendMs);
			
			//三方运单,推全程跟踪,修改为干支线发车推送全称跟踪2014/009/09
			this.thirdDepartureToTMS(sendMs,shieldsCarId);
			
			//推全程跟踪及epl 只有支线发车才推送数据 同时承运人类型必须是承运商
			boolean pushDeparture = departure.getType() == Departure.DEPARTRUE_TYPE_ZHIXIAN
					&& departure.getSendUserType().equals(
							Constants.SENDUSERTYEP_CARRIER) ? true : false;
			/**
			 * 与3PL王永，李文江确认过，他们的域名epartner1.etms.360buy.com已经过期，
			 * 接口/rest/ThirdJobNoRoutEntry/insertOrderShips已经下线，
			 * 他们不再需要该数据，所以不再写入数据
			 */
//			if(pushDeparture){
//				//三方运单,推3PL
//				this.thirdDepartureTo3PL(sendMs);
//			}
		}
		result.setResult(ServiceResultEnum.SUCCESS);
		return result;
	}

	private void createDepartureSend(List<SendM> sendMs, long shieldsCarId) {
		DepartureSend departureSend = new DepartureSend();
		departureSend.setShieldsCarId(shieldsCarId);
		departureSend
				.setCreateSiteCode(getCreateSiteCodeFromSendMs(sendMs));
		departureSend
				.setCreateTime(getTaskCreatedTimeFromSendMs(sendMs));
		departureSend
				.setCreateUserCode(getUpdaterUserCodeFromSendMs(sendMs));
		departureSend.setCreateUser(getUpdaterUserFromSendMs(sendMs));
		departureSend
				.setUpdateTime(getTaskCreatedTimeFromSendMs(sendMs));
		departureSend.setYn(1);
		for (SendM sendM : sendMs) {
			//暂时存放运力编码
			departureSend.setCapacityCode(sendM.getTurnoverBoxCode());
			departureSend.setSendCode(sendM.getSendCode());
			departureSend.setThirdWaybillCode(sendM.getThirdWaybillCode());
			departureSendDao.insert(departureSend);
		}
	}

	private void commonLogic(Departure departure, List<SendM> sendMs,
			long shieldsCarId, int type) {
		// 更新批次内箱子信息
		Collections.sort(sendMs);
		updateBoxsBySendCode(sendMs, shieldsCarId);
		// 生成发车表数据
		createDepartureCar(departure, sendMs, shieldsCarId, type);
		// 生成封签表数据(封签表数据只有在封签号不为空的时候才能生成)
		createSealVehicle(departure, sendMs);
		
	}

	/****************************************************************
	 * xumei 2017-07-03 推财务数据 发车产生的businessType = 10发货数据推财务
	 ****************************************************************/
	private void pushFinancialData(Departure departure, List<SendM> sendMLists) {
		logger.info("[departureServiceImpl.pushFinancialData]发车产生的businessType = 10发货数据推财务");
		departure.setSendUser(getSendUserFromSendMs(sendMLists));
		departure.setSendUserCode(getSendUserCodeFromSendMs(sendMLists));

		List<SendM> sendMs = departure.getSendMs();
		//组装task写入task_delivery_to_finance_batch表
		for (SendM sendm : sendMs) {
			String sendCode = sendm.getSendCode();
			Task task = new Task();
			task.setKeyword1(BUSINESS_TYPE_ONE+"");
			task.setBody(sendCode);
			//将task的状态设置为执行成功
			task.setStatus(2);
			task.setTableName(Task.TABLE_NAME_DELIVERY_TO_FINANCE_BATCH);
			task.setType(Task.TASK_TYPE_DELIVERY_TO_FINANCE_BATCH);
			task.setOwnSign(BusinessHelper.getOwnSign());

			taskService.doAddWithStatus(task);
		}
	}

	private void createSealVehicle(Departure departure, List<SendM> sendMs) {
		if ((departure.getShieldsCarCode() != null)
				&& (!departure.getShieldsCarCode().trim().equals(""))) {
			SealVehicle sealVehicle = new SealVehicle();
			sealVehicle.setCode(departure.getShieldsCarCode()); // 封签号
			sealVehicle.setCreateSiteCode(getCreateSiteCodeFromSendMs(sendMs)); // 创建站点
			sealVehicle.setCreateTime(getTaskCreatedTimeFromSendMs(sendMs)); // 创建时间
			sealVehicle.setCreateUser(getUpdaterUserFromSendMs(sendMs)); // 创建人
			sealVehicle.setCreateUserCode(getUpdaterUserCodeFromSendMs(sendMs)); // 创建人编码
			sealVehicle.setDriver(getSendUserFromSendMs(sendMs)); // 司机
			sealVehicle.setDriverCode(String
					.valueOf(getSendUserCodeFromSendMs(sendMs))); // 司机编码
			sealVehicle.setVehicleCode(departure.getCarCode()); // 车辆编码
			sealVehicle.setYn(1);
			// sealVehicleService.add(sealVehicle);
			this.sealVehicleService.addSealVehicle(sealVehicle);
		}
	}

	private Boolean createDepartureCar(Departure departure, List<SendM> sendMs, long shieldsCarId,int type) {
		{
			DepartureCar car = new DepartureCar();
			car.setShieldsCarId(shieldsCarId); // 封车ID
			car.setCarCode(departure.getCarCode()); // 车号(车号可以为空)

			car.setShieldsCarCode(departure.getShieldsCarCode());// 封签号(封签号可以为空)
			car.setSendUser(getSendUserFromSendMs(sendMs)); // 司机
			car.setSendUserCode(getSendUserCodeFromSendMs(sendMs)); // 司机编码
			car.setSendUserType(departure.getSendUserType()); // 承运商类型
			car.setCreateSiteCode(getCreateSiteCodeFromSendMs(sendMs)); // 创建站点
			car.setCreateUserCode(getUpdaterUserCodeFromSendMs(sendMs)); // 操作人ID
			car.setCreateUser(getUpdaterUserFromSendMs(sendMs)); // 操作人
			car.setCreateTime(getTaskCreatedTimeFromSendMs(sendMs)); // 操作时间
			car.setUpdateTime(getTaskCreatedTimeFromSendMs(sendMs)); // 最后修改时间
			car.setYn(1);
			car.setReceiveSiteCodes(departure.getReceiveSiteCodes());
			car.setRunNumber(departure.getRunNumber());
			//car.setCapacityCode(departure.getCapacityCode());  //废弃，用来保存字段区分干支线
            car.setCapacityCode(String.valueOf(departure.getRouteType()));
			// 支线发车封车表必须插入体积和重量
			car.setWeight(departure.getWeight() == null ? Double.valueOf(0.0D) : departure//FIXBUG: 0 ,会造成不必要的解封加封操作
					.getWeight());
			car.setVolume(departure.getVolume() == null ? Double.valueOf(0.0D) : departure//FIXBUG: 0 ,会造成不必要的解封加封操作
					.getVolume());
			if (Departure.DEPARTRUE_TYPE_TURNTABLE == type) {
				// 设置指纹信息
				StringBuffer fingerprint = new StringBuffer();
				fingerprint.append(car.getCarCode()).append("_")
						.append(car.getCreateSiteCode()).append("_")
						.append(car.getCreateTime()).append("_")
						.append(sendMs.size()).append("_").append(type);
				car.setFingerprint(Md5Helper.encode(fingerprint.toString()));
				// 设置转车车号
				car.setOldCarCode(departure.getOldCarCode());
				car.setDepartType(2);
			} else if(Departure.DEPARTRUE_TYPE_BRANCH == type){//支线发车正向类型
				car.setDepartType(Departure.DEPARTRUE_TYPE_BRANCH);
			} else {
				car.setDepartType(1);
			}
			if (!hasDepartureCarByFingerprint(car)) {
				departureCarDao.insert(car);
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
	}

	private Boolean hasDepartureCarByFingerprint(DepartureCar departureCar) {
		if (StringHelper.isEmpty(departureCar.getFingerprint())) {
			return Boolean.FALSE;
		}
		List<DepartureCar> departureCars = departureCarDao
				.findDepartureCarByFingerprint(departureCar);
		if (!departureCars.isEmpty() && departureCars.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private void updateBoxsBySendCode(List<SendM> sendMs, long shieldsCarId) {
		for (SendM receivedSendM : sendMs) {
			SendM sendM = new SendM();
			sendM.setCarCode(receivedSendM.getCarCode()); // 车号
			sendM.setUpdateUserCode(getUpdaterUserCodeFromSendMs(sendMs));// 操作人编码
			sendM.setUpdaterUser(getUpdaterUserFromSendMs(sendMs)); // 操作人姓名
			sendM.setUpdateTime(getTaskCreatedTimeFromSendMs(sendMs)); // 操作时间
			sendM.setShieldsCarId(shieldsCarId); // 封车表ID
			sendM.setSendUser(getSendUserFromSendMs(sendMs)); // 司机
			sendM.setSendUserCode(getSendUserCodeFromSendMs(sendMs)); // 司机编码
			sendM.setSendCode(receivedSendM.getSendCode()); // 发货单号
			sendM.setCreateSiteCode(getCreateSiteCodeFromSendMs(sendMs));// 创建站点编号
			sendMDao.updateBySendCodeSelective(sendM);
		}
	}

	private Integer getCreateSiteCodeFromSendMs(List<SendM> sendMs) {
		Integer createSiteCode = null;
		for (SendM receivedSendM : sendMs) {
			createSiteCode = receivedSendM.getCreateSiteCode();
			if (createSiteCode != null) {
				return createSiteCode;
			}
		}
		return createSiteCode;
	}

	private Integer getSendUserCodeFromSendMs(List<SendM> sendMs) {
		Integer sendUserCode = null;
		for (SendM receivedSendM : sendMs) {
			sendUserCode = receivedSendM.getSendUserCode();
			if (sendUserCode != null) {
				return sendUserCode;
			}
		}
		return sendUserCode;
	}

	private String getSendUserFromSendMs(List<SendM> sendMs) {
		String sendUser = null;
		for (SendM receivedSendM : sendMs) {
			sendUser = receivedSendM.getSendUser();
			if (sendUser != null) {
				return sendUser;
			}
		}
		return sendUser;
	}

	private Integer getUpdaterUserCodeFromSendMs(List<SendM> sendMs) {
		Integer updaterUserCode = null;
		for (SendM receivedSendM : sendMs) {
			updaterUserCode = receivedSendM.getUpdateUserCode();
			if (updaterUserCode != null) {
				return updaterUserCode;
			}
		}
		return updaterUserCode;
	}

	private String getUpdaterUserFromSendMs(List<SendM> sendMs) {
		String updaterUser = null;
		for (SendM receivedSendM : sendMs) {
			updaterUser = receivedSendM.getUpdaterUser();
			if (updaterUser != null) {
				return updaterUser;
			}
		}
		return updaterUser;
	}

	private Date getTaskCreatedTimeFromSendMs(List<SendM> sendMs) {
		Date taskCreatedTime = null;
		for (SendM receivedSendM : sendMs) {
			taskCreatedTime = receivedSendM.getUpdateTime();
			if (taskCreatedTime != null) {
				return taskCreatedTime;
			}
		}
		return taskCreatedTime;
	}

	/**
	 * 推送数据给逆向系统
	 * 
	 * @param sendMs
	 * @param receiveSiteCode
	 */
	/*private void toReverse(List<SendM> sendMs) {
		for (SendM sendM : sendMs) {
			Task tTask = new Task();
			tTask.setBoxCode(sendM.getSendCode());
			tTask.setBody(sendM.getSendCode());
			tTask.setCreateSiteCode(sendM.getCreateSiteCode());
			tTask.setKeyword2(String.valueOf(sendM.getSendType()));
			// tTask.setReceiveSiteCode(sendM.getReceiveSiteCode());
			tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
			tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
			tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
			String ownSign = BusinessHelper.getOwnSign();
			tTask.setOwnSign(ownSign);
			tTask.setKeyword1("4");// 4 逆向任务
			tTask.setFingerprint(sendM.getSendCode() + "_"
					+ tTask.getKeyword1());
			String sendCode = sendM.getSendCode();
			Integer receiveSiteCode = null;
			if (sendCode != null) {
				try {
					receiveSiteCode = Integer.parseInt(sendCode.split("-")[1]);
				} catch (Exception e) {
					// 吃掉，丫米丫米
				}
				// 亚一仓直接写数据库
				if (receiveSiteCode != null
						&& ReverseSendServiceImpl.ASION_NO_ONE_SITE_CODE_LIST
								.contains(receiveSiteCode)) {
					taskService.add(tTask);
				} else {
					// 非亚一仓的先redis后数据库
					taskService.add(tTask, true);
				}
			}else{
				taskService.add(tTask, true);
			}
		}
	}*/

	/**
	 * 推送3方运单到tmd系统,只推送第三方运单的数据
	 * 
	 * @param sendMs 发车详细信息
	 */
	private void thirdDepartureToTMS(List<SendM> sendMs,long shieldsCarId) {
		try{
			for (SendM sendM : sendMs) {
				//判断如果三方运单为空的话，直接跳过,（9-10 去掉）
				//if(StringUtils.isBlank(sendM.getThirdWaybillCode())) continue;
				
				Task tTask = new Task();
				tTask.setBoxCode(sendM.getSendCode());
				//修改为发车号
				tTask.setBody(String.valueOf(shieldsCarId));
				tTask.setCreateSiteCode(sendM.getCreateSiteCode());
				tTask.setKeyword2(String.valueOf(sendM.getSendType()));
				tTask.setType(Task.TASK_TYPE_DEPARTURE);
				tTask.setTableName(Task.getTableName(Task.TASK_TYPE_DEPARTURE));
				tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
				String ownSign = BusinessHelper.getOwnSign();
				tTask.setOwnSign(ownSign);
				tTask.setKeyword1("5");// 5 三方运单到TMS
				tTask.setFingerprint(sendM.getSendCode() + "_"
						+ tTask.getKeyword1());
				tTask.setCreateTime(getTaskCreatedTimeFromSendMs(sendMs));
				taskService.add(tTask);
			}
		}catch(Exception e){
			logger.error("建立推送支线发车三方承运商发车到全程跟踪的任务(task_send)出错!", e);
		}
	}
	
	/**
	 * 推送3方运单到3PL系统,只推送第三方运单的数据
	 * 
	 * @param sendMs 发车详细信息
	 */
	private void thirdDepartureTo3PL(List<SendM> sendMs) {
		try{
			Set<DealData_Departure_3PL> dealDatas = new HashSet<DealData_Departure_3PL>();
			//组装成DealData_Departure_3PL, 去重
			for (SendM sendM : sendMs) {
				//判断如果三方运单为空的话，直接跳过
				if(StringUtils.isBlank(sendM.getThirdWaybillCode())) continue;
				DealData_Departure_3PL dd3 = new DealData_Departure_3PL();
				dd3.setSendCode(sendM.getSendCode());
				dd3.setThirdWaybillCode(sendM.getThirdWaybillCode());
				dd3.setCarrierId(sendM.getSendUserCode());
				dd3.setCarrierName(sendM.getSendUser());
				dd3.setSendMId(sendM.getSendMId());//sendM唯一id
				dealDatas.add(dd3);
			}
			
			//存入TaskFailQueue表
			for(DealData_Departure_3PL dd3:dealDatas){
				TaskFailQueue taskFailQueueData = new TaskFailQueue();
				String body = JsonHelper.toJson(dd3);

				SendM sendM = sendMDao.selectBySendCode(dd3.getSendCode());//获得sendM

				taskFailQueueData.setBusiId(sendM==null?null:sendM.getSendMId());//sendM唯一id
				taskFailQueueData.setBusiType(IFailQueueService.DEPARTURE_TYPE_3PL);//支线三方承运商发车
				taskFailQueueData.setBody(body);

				//用于防重插入
				if(taskFailQueueDao.update(TaskFailQueueDao.namespace, taskFailQueueData)==0){
					taskFailQueueDao.add(TaskFailQueueDao.namespace, taskFailQueueData);
				}

			}
		}catch(Exception e){
			logger.error("建立推送支线发车三方承运商发车到3PL的任务(task_failqueue)出错!", e);
		}
	}
	
	/**
	 * 检查批次是否存在以及是否已经发车
	 * 
	 * @param
	 *
	 */
	public ServiceMessage<String> checkSendStatus(Integer siteCode,
			String sendCode) {

		ServiceMessage<String> result = new ServiceMessage<String>();
		/**************************************************************************************
		 * 2012-07-20 根据新需求可以发其他分拣中心的数据，所以CreateSiteCode不作为必须条件 *
		 **************************************************************************************/
		SendM sendM = sendMDao.selectOneBySiteAndSendCode(
				/* 分拣中心 siteCode */null, sendCode);
		/**************************************************************************************/
		// 查询不到该批次
		if (sendM == null) {
			result.setResult(ServiceResultEnum.NOT_FOUND);
		} else {
			// 如果已经发车，该记录的司机编码应该不为空
			if (sendM.getSendUserCode() != null) {
				result.setResult(ServiceResultEnum.WRONG_STATUS);
			} else {
				result.setResult(ServiceResultEnum.SUCCESS);
			}
		}
		return result;
	}

	@Override
	@JProfiler(jKey = "com.jd.bluedragon.distribution.departure.service.impl.DepartureServiceImpl.checkSendStatusFromVOS", mState = {JProEnum.TP})
	public ServiceMessage<Boolean> checkSendStatusFromVOS(String sendCode) {

		ServiceMessage<Boolean> result = new ServiceMessage<Boolean>();
		CommonDto<Boolean> isSealed = vosQueryWS.isBatchCodeHasSealed(sendCode);

		if(isSealed == null){
			result.setErrorMsg("服务异常，运输系统查询批次号状态为空！");
			result.setResult(ServiceResultEnum.NOT_FOUND);
			logger.warn("服务异常，运输系统查询批次号状态为空, 批次号:" + sendCode);
			return result;
		}

		if( Constants.RESULT_SUCCESS == isSealed.getCode() ){//服务正常
			if(Boolean.TRUE.equals(isSealed.getData())){      //已被封车
				result.setResult(ServiceResultEnum.WRONG_STATUS);
			}else if(Boolean.FALSE.equals(isSealed.getData())) {//未被封车
				result.setResult(ServiceResultEnum.SUCCESS);
			}
		}else if(Constants.RESULT_WARN == isSealed.getCode()){//服务报警告，给前台提示
			result.setResult(ServiceResultEnum.FAILED);
			result.setErrorMsg(isSealed.getMessage());
		}else{// 服务出错或内部异常，打出日志
			result.setResult(ServiceResultEnum.FAILED);
			result.setErrorMsg("服务异常，运输系统查询批次号状态失败！");
			logger.warn("服务异常，运输系统查询批次号状态失败, 批次号:" + sendCode);
			logger.warn("服务异常，运输系统查询批次号状态失败，失败原因:"+isSealed.getMessage());
		}
		return result;
	}

	/**
	 * 根据发货单号获得体积和重量
	 * 
	 * @param siteCode
	 *            发货站点编码
	 * @param sendCode
	 *            交接单号
	 */
	public SendMeasure getSendMeasure(Integer siteCode, String sendCode) {
		SendMeasure response = new SendMeasure();
		SendDetail queryDetail = new SendDetail();
		queryDetail.setSendCode(sendCode);
		/**************************************************************************************
		 * 2012-07-20 根据新需求可以发其他分拣中心的数据，所以CreateSiteCode不作为必须条件 *
		 **************************************************************************************/
		// queryDetail.setCreateSiteCode(siteCode);
		/**************************************************************************************/
		List<SendDetail> sendDetails = sendDatailDao
				.queryBySiteCodeAndSendCode(queryDetail);
		if (sendDetails != null && sendDetails.size() != 0) {
			double totalWeight = 0;
			Integer receiveSiteCode = -1;
			for (SendDetail sendDatail : sendDetails) {
				
				if(receiveSiteCode<0) receiveSiteCode = sendDatail.getReceiveSiteCode();//获得 操作单位编码
				
				double weight = sendDatail.getWeight() == null ? 0 : sendDatail
						.getWeight().doubleValue();
				totalWeight = BigDecimalHelper.add(totalWeight, weight);
			}
			// 将kg转换成ton，小数点后留3位
			Double totalWeightTon = BigDecimalHelper.div(totalWeight, 1000d);
			response.setWeight(totalWeightTon);
			response.setReceiveSiteCode(receiveSiteCode);
		}
		return response;
	}

	/**
	 * 检查发货单中的车号以及箱号/包裹号是否匹配
	 * 
	 * @param carCode
	 *            车号
	 * @param boxCode
	 *            箱号/包裹号
	 * @return
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public boolean checkCarBoxCodeMatch(String carCode, String boxCode) {
		String errMsg = "检查发货单车号[" + carCode + "]以及箱号[" + boxCode + "]是否匹配失败: ";
		SendDetail sendDatail;
		String sendCode;
		try {
			sendDatail = sendDatailDao.queryOneSendDatailByBoxCode(boxCode);
			if (sendDatail != null) {
				sendCode = sendDatail.getSendCode();
				SendM sendM = sendMDao.selectBySendCode(sendCode);
				if (sendM == null) {
					logger.warn(errMsg + "查询不到发货单[" + sendCode + "]");
					return false;
				} else {
					String dbCarCode = sendM.getCarCode();
					return dbCarCode == null ? false : dbCarCode
							.equals(carCode);
				}
			} else {
				logger.warn(errMsg + "查询不到和箱号相关的发货单明细表");
			}
		} catch (Exception e) {
			logger.error(errMsg + e.getMessage());
			return false;
		}
		return false;
	}

	/**
	 * 根据箱号获得箱内订单列表信息
	 * 
	 * @param boxCode
	 *            箱号
	 * @return
	 */
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<SendBox> getSendBoxInfo(String boxCode, Integer siteCode) {
		String errMsg = "获得箱[" + boxCode + "]内包裹列表信息失败: ";

		List<SendBox> result = new ArrayList<SendBox>();
		String sendCode;
		String sendUser = null;
		Date sendTime = new Date();

		try {
			SendDetail queryDetail = new SendDetail();
			queryDetail.setBoxCode(boxCode);
			List<SendDetail> sendDatails = new ArrayList<SendDetail>();
			if (BusinessHelper.isBoxcode(boxCode)) {
				sendDatails = sendDatailDao
						.querySendDatailsByBoxCode(queryDetail);
			} else if (WaybillUtil.isPackageCode((boxCode))) {
				if (siteCode == null) {
					logger.warn("所传站点为空： " + boxCode);
				} else {
					queryDetail.setReceiveSiteCode(siteCode);
					sendDatails = sendDatailDao
							.querySendDatailsByPackageCode(queryDetail);
				}
			} else {
				logger.warn("传递的箱号或者大件包裹号不对：" + boxCode);
				return result;
			}
			if (sendDatails != null) {
				for (SendDetail sendDatail : sendDatails) {
					SendBox sendBoxInfo = new SendBox();
					sendCode = sendDatail.getSendCode();
					if (sendUser == null || sendTime == null) {
						SendM sendM = sendMDao.selectOneBySiteAndSendCode(
								sendDatail.getCreateSiteCode(), sendCode);
						if (sendM != null) {
							sendUser = sendM.getSendUser();
							sendTime = sendM.getOperateTime();
						} else {
							logger.warn(errMsg + "无法获得发货单[" + sendCode + "]数据");
						}
					}
					sendBoxInfo.setBoxCode(boxCode);// 箱号
					sendBoxInfo.setSendCode(sendDatail.getSendCode());// 交接单号
					sendBoxInfo.setSendTime(sendTime);// 发送时间
					sendBoxInfo.setSendUser(sendUser);// 司机
					sendBoxInfo.setWaybillCode(sendDatail.getWaybillCode());// 运单号
					sendBoxInfo.setPackageBarcode(sendDatail
							.getPackageBarcode()); // 包裹号
					result.add(sendBoxInfo);
				}
			} else {
				logger.warn(errMsg + "无法获得发货明细数据");
			}
		} catch (Exception e) {
			logger.error(errMsg + e.getMessage());
		}
		return result;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<SendBox> getSendInfo(String sendCode) {
		String errMsg = "获得批次[" + sendCode + "]内包裹列表信息失败: ";
		List<SendBox> result = new ArrayList<SendBox>();
		try {
			List<SendM> sendMs = sendMDao.selectOneBySendCode(sendCode);
			if (sendMs == null || sendMs.size() == 0) {
				logger.warn(errMsg + "查询不到该批次号");
				return result;
			}

			SendM oneSendM = sendMs.get(0);
			String sendUser = oneSendM.getSendUser();
			Date sendTime = oneSendM.getOperateTime();
			Integer createSiteCode = oneSendM.getCreateSiteCode();
			SendDetail queryDetail = new SendDetail();
			queryDetail.setSendCode(sendCode);
			queryDetail.setCreateSiteCode(createSiteCode);
			List<SendDetail> sendDatails = sendDatailDao
					.queryBySiteCodeAndSendCode(queryDetail);
			if (sendDatails != null) {
				for (SendDetail sendDatail : sendDatails) {
					SendBox sendBoxInfo = new SendBox();
					sendBoxInfo.setBoxCode(sendDatail.getBoxCode());// 箱号
					sendBoxInfo.setSendCode(sendCode);// 交接单号
					sendBoxInfo.setSendTime(sendTime);// 发送时间
					sendBoxInfo.setSendUser(sendUser);// 司机
					sendBoxInfo.setWaybillCode(sendDatail.getWaybillCode());// 运单号
					sendBoxInfo.setPackageBarcode(sendDatail
							.getPackageBarcode()); // 包裹号
					result.add(sendBoxInfo);
				}
			} else {
				logger.warn(errMsg + "无法获得发货明细数据");
			}
		} catch (Exception e) {
			logger.error(errMsg + e.getMessage());
		}
		return result;
	}

	@Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageDto<SendBox> queryPageSendInfoByBatchCode(PageDto<SendBox> pageDto,String batchCode) {
        PageDto<SendBox> resultPage = new PageDto<>(pageDto.getCurrentPage(),pageDto.getPageSize());
        List<SendM> sendMs = sendMDao.selectOneBySendCode(batchCode);
        if (sendMs == null || sendMs.size() == 0) {
            logger.warn(MessageFormat.format("查询批次信息为空[{0}]",batchCode));
            return resultPage;
        }
        SendM oneSendM = sendMs.get(0);
        String sendUser = oneSendM.getSendUser();
        Date sendTime = oneSendM.getOperateTime();
        Integer createSiteCode = oneSendM.getCreateSiteCode();
        SendDetailDto queryDetail = new SendDetailDto();
        queryDetail.setSendCode(batchCode);
        queryDetail.setCreateSiteCode(createSiteCode);
        //查询总数
        Integer count = sendDatailDao.queryCountBySiteCodeAndSendCode(queryDetail);
        resultPage.setTotalRow(count);

        //设置分页参数
        queryDetail.setOffset(pageDto.getOffset());
        queryDetail.setLimit(pageDto.getPageSize());
        List<SendDetail> sendDatails = sendDatailDao.queryPageBySiteCodeAndSendCode(queryDetail);
        List<SendBox> result = new ArrayList<SendBox>();
        resultPage.setResult(result);
        if (sendDatails != null) {
            for (SendDetail sendDatail : sendDatails) {
                SendBox sendBoxInfo = new SendBox();
                sendBoxInfo.setBoxCode(sendDatail.getBoxCode());// 箱号
                sendBoxInfo.setSendCode(batchCode);// 交接单号
                sendBoxInfo.setSendTime(sendTime);// 发送时间
                sendBoxInfo.setSendUser(sendUser);// 司机
                sendBoxInfo.setWaybillCode(sendDatail.getWaybillCode());// 运单号
                sendBoxInfo.setPackageBarcode(sendDatail.getPackageBarcode()); // 包裹号
                result.add(sendBoxInfo);
            }
        } else {
            logger.warn(MessageFormat.format("无法获得发货明细数据sendCode[{0}]createSiteCode[{1}]",batchCode,createSiteCode));
        }
        return resultPage;
    }


    /**
	 * 批次根据运单号获得交接单号
	 * 
	 * @param
	 *
	 * @return
	 */
	public List<SendCode> getSendCodesByWaybills(List<SendCode> sendCodes) {
		if (sendCodes == null) {
			return null;
		}
		List<String> waybillCodes = new ArrayList<String>();
		for (SendCode sendCode : sendCodes) {
			String waybillCode = sendCode.getBoxCode();
			if (waybillCode != null) {
				waybillCodes.add(waybillCode);
			}
		}
		if (waybillCodes.size() == 0) {
			return sendCodes;
		}
		String waybillCodeIn = StringHelper.join(waybillCodes, ",", "(", ")",
				"'");
		// 根据运单号获得最后的批次号
		List<SendDetail> sendDatails = sendDatailDao
				.querySendCodesByWaybills(waybillCodeIn);

		List<SendCode> results = new ArrayList<SendCode>();
		Map<String, String> resultMap = new HashMap<String, String>();
		if (sendDatails != null) {
			for (SendDetail sendDatail : sendDatails) {
				resultMap.put(sendDatail.getWaybillCode(),
						sendDatail.getSendCode());
			}
		}
		for (SendCode sendCode : sendCodes) {
			String waybillCode = sendCode.getBoxCode();
			if (resultMap.containsKey(waybillCode)) {
				SendCode result = new SendCode();
				result.setBoxCode(waybillCode);
				result.setSendCode(resultMap.get(waybillCode));
				results.add(result);
			} else {
				SendCode result = new SendCode();
				result.setBoxCode(waybillCode);
				result.setSendCode(null);
				results.add(result);
			}
		}
		return results;
	}

	/**
	 * 批次更新包裹重量
	 */
	public void batchUpdateSendDMeasure(List<SendDetail> sendDatails) {
		if (sendDatails == null || sendDatails.size() == 0) {
			return;
		}

		List<String> requests = new ArrayList<String>();
		BaseEntity<List<DeliveryPackageD>> waybillWSRs;
		List<DeliveryPackageD> datas = null;

		try {
			for (SendDetail sendDatail : sendDatails) {
				requests.add(sendDatail.getPackageBarcode());
			}
			try {
				waybillWSRs = waybillPackageManager
						.queryPackageListForParcodes(requests);
				datas = waybillWSRs.getData();
				logger.info("调用运单queryPackageListForParcodes结束");
			} catch (Exception e) {
				logger.error("调用运单queryPackageListForParcodes接口时候失败");
			}
			HashMap<String, Double> packageWeightMap = new HashMap<String, Double>();
			if (datas != null && datas.size() != 0) {
				for (DeliveryPackageD deliveryPackageD : datas) {
					packageWeightMap.put(deliveryPackageD.getPackageBarcode(),
							deliveryPackageD.getGoodWeight());
				}
			}
			for (SendDetail sendDatail : sendDatails) {
				String packageBarcode = sendDatail.getPackageBarcode();
				if (packageWeightMap.containsKey(packageBarcode)) {
					sendDatail.setWeight(packageWeightMap.get(packageBarcode));
				} else {
					sendDatail.setWeight(0d);
				}
				sendDatailDao.updateWeight(sendDatail);
			}
		} catch (Exception e) {
			logger.error("调用运单batchUpdateSendDMeasure接口时候失败: " + e);
		}
	}

	/** 
	 * 调用运单接口发送全称跟踪
	 * 运输车辆出发	1800
	       运输车辆到达 	1900
	 * */
	@SuppressWarnings("rawtypes")
	public void sendWaybillAddWS(BdTraceDto bdTraceDto) {
		logger.info("发车回传全称跟踪信息，调用运单接口-----------bdTraceDto="+bdTraceDto.getWaybillCode()+"信息"+bdTraceDto.getOperatorDesp());
		BaseEntity baseEntity = waybillTraceApi.sendBdTrace(bdTraceDto);
		if(baseEntity!=null){
			if(baseEntity.getResultCode()!=1){
				logger.warn("发车数据回传全程跟踪异常："+baseEntity.getMessage());
			}
		}else{
			logger.warn("发车数据回传全程跟踪接口异常");
		}
	}
	
	public BdTraceDto toWaybillStatus(WaybillStatus tWaybillStatus) {
		BdTraceDto bdTraceDto = new BdTraceDto();
		bdTraceDto.setOperateType(tWaybillStatus.getOperateType());
		bdTraceDto.setOperatorSiteId(tWaybillStatus.getCreateSiteCode());
		bdTraceDto.setOperatorSiteName(tWaybillStatus.getCreateSiteName());
		bdTraceDto.setOperatorTime(tWaybillStatus.getOperateTime());
		bdTraceDto.setOperatorUserName(tWaybillStatus.getOperator());
		bdTraceDto.setPackageBarCode(tWaybillStatus.getPackageCode());
		bdTraceDto.setWaybillCode(tWaybillStatus.getWaybillCode());
		bdTraceDto.setOperatorUserId(tWaybillStatus.getOperatorId());
		
		if (WaybillStatus.WAYBILL_TRACK_XFC.equals(tWaybillStatus.getOperateType())) {
			StringBuffer message = new StringBuffer();
			if(tWaybillStatus.getReceiveSiteName()!=null && !tWaybillStatus.getReceiveSiteName().equals("")){
				message.append(tWaybillStatus.getCreateSiteName() + "-"
					+ tWaybillStatus.getReceiveSiteName() + "车辆已经发车，");
				
				if (tWaybillStatus.getOrgName() != null
						&& !tWaybillStatus.getOrgName().equals("")) {
					message.append("承运商" + tWaybillStatus.getOrgName()+"，");
				}
			}

			message.append("发车条码" + tWaybillStatus.getRemark());
			bdTraceDto.setOperatorDesp(message.toString());
		} else
			bdTraceDto.setOperatorDesp("车辆已到达");
		return bdTraceDto;
	}
	
	/** (non-Javadoc)
	 * @see com.jd.bluedragon.distribution.departure.service.DepartureService#sendThirdDepartureInfoToTMS(com.jd.bluedragon.distribution.task.domain.Task)
	 */
	@Override
	public boolean sendThirdDepartureInfoToTMS(Task task) {

		logger.info("发车回传全称跟踪信息-----------task.getId()=" + task.getId()+"---"+task.getBody());
		if (task == null || task.getBoxCode() == null || task.getBody() == null)
			return true;
		try {
			this.taskService.doLock(task);
			// 运力编码为空的批次不回传10/11
			List<DepartureSend> sendList = getDepartureSendByCarId(Long
					.parseLong(task.getBody()));
			Integer receiveSiteCode = null;
			String operator = null;
			for (DepartureSend send : sendList) {
				operator = send.getCreateUser();
				if (task.getBoxCode().equals(send.getSendCode())
						&&StringHelper.isNotEmpty(send.getCapacityCode())) {
					RouteTypeResponse response = siteService.getCapacityCodeInfo(send.getCapacityCode());
					if (response != null && JdResponse.CODE_OK.equals(response.getCode())) {
						receiveSiteCode = response.getSiteCode();
					}
				}
			}
			List<SendM> tSendM = this.sendMDao
					.selectBySiteAndSendCodeBYtime(
							task.getCreateSiteCode(), task.getBoxCode());
			SendDetail tSendDatail = new SendDetail();
			for (SendM newSendM : tSendM) {
				tSendDatail.setBoxCode(newSendM.getBoxCode());
				tSendDatail.setCreateSiteCode(newSendM.getCreateSiteCode());
				tSendDatail.setReceiveSiteCode(newSendM.getReceiveSiteCode());
				tSendDatail.setIsCancel(0);
				List<SendDetail> sendDatailList = this.sendDatailDao
						.querySendDatailsBySelective(tSendDatail);
				BaseStaffSiteOrgDto cDto = null;
				BaseStaffSiteOrgDto rDto = null;
				for (SendDetail detail : sendDatailList) {
					try {
						if (cDto == null) {
							cDto = this.baseService
									.getSiteBySiteID(newSendM.getCreateSiteCode());
						}
						if (rDto == null && receiveSiteCode!=null) {
							rDto = this.baseService
									.getSiteBySiteID(receiveSiteCode);
						}
						WaybillStatus tWaybillStatus = new WaybillStatus();
						tWaybillStatus.setPackageCode(detail
								.getPackageBarcode());
						tWaybillStatus.setCreateSiteCode(detail
								.getCreateSiteCode());
						if (cDto != null) {// 只有不为空时才能用
							tWaybillStatus.setCreateSiteName(cDto
									.getSiteName());
							tWaybillStatus.setCreateSiteType(cDto
									.getSiteType());
							tWaybillStatus.setOrgId(newSendM
									.getSendUserCode());
							tWaybillStatus.setOrgName(newSendM
									.getSendUser());
						}
						tWaybillStatus.setOperatorId(detail
								.getCreateUserCode());
						tWaybillStatus.setOperateTime(task.getCreateTime());
						if(operator!=null)
							tWaybillStatus.setOperator(operator);
						else
							tWaybillStatus.setOperator(detail.getCreateUser());

						tWaybillStatus.setWaybillCode(detail
								.getWaybillCode());
						tWaybillStatus.setReceiveSiteCode(detail
								.getReceiveSiteCode());
						if (rDto != null) {// 只有不为空时才能用
							tWaybillStatus.setReceiveSiteName(rDto
									.getSiteName());
							tWaybillStatus.setReceiveSiteType(rDto
									.getSiteType());
						}
						tWaybillStatus
								.setOperateType(WaybillStatus.WAYBILL_TRACK_XFC);
						tWaybillStatus.setRemark(task.getBody());
						sendWaybillAddWS(toWaybillStatus(tWaybillStatus));
					} catch (Exception e) {
						logger.error(
								"建立推送支线发车三方承运商发车到全程跟踪的明细任务(task_waybill)出错!",
								e);
						taskService.doError(task);
					}
				}
			}

		} catch (Exception e) {
			logger.error("建立推送支线发车三方承运商发车到全程跟踪的明细任务(task_waybill)出错!", e);
			taskService.doError(task);
		}
		taskService.doDone(task);

		return true;

	}
	
	
	public List<DepartureSend> getDepartureSendByCarId(Long departureCarId){
		return this.departureSendDao.getDepartureSendByCarId(departureCarId);
	}
	
	public List<DepartureCar> findDepartureList(DeparturePrintRequest departurPrintRequest){
		return this.departureCarDao.findDepartureList(departurPrintRequest);
	}
	
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<SendDetail> getWaybillsByDeparture(String code, Integer type) {

		List<SendDetail> result = new ArrayList<SendDetail>();
		if (type == 1) {// 发车号查询
			Long shieldsCarId = Long.parseLong(code);
			List<SendM> sendMs = sendMDao.querySendCodesByDepartue(shieldsCarId);
			if (sendMs != null) {
				for (SendM sendM : sendMs) {
					SendDetail detail = new SendDetail();
					if (sendM.getSendCode() == null) {
						continue;
					}
					detail.setSendCode(sendM.getSendCode());
					List<SendDetail> sendDetails = sendDatailDao
							.queryBySendCodeAndSendType(detail);
					result.addAll(sendDetails);
				}
			}
		} else if (type == 2) {// 按三方运单号查询
			List<DepartureSend> departureSends = departureSendDao
					.getByThirdWaybillCode(code);
			if (departureSends != null) {
				for (DepartureSend departureSend : departureSends) {
					SendDetail detail = new SendDetail();
					if (departureSend.getSendCode() == null) {
						continue;
					}
					detail.setSendCode(departureSend.getSendCode());
					List<SendDetail> sendDetails = sendDatailDao
							.queryBySendCodeAndSendType(detail);
					result.addAll(sendDetails);
				}
			}
		}

		return result;
	}

	@Override
	public boolean updatePrintTime(long departureCarId) {
		return departureCarDao.updatePrintTime(departureCarId);
	}

	@Override
	public List<DeparturePrintResponse> queryDeliveryInfoByOrderCode(String ordercode) {
		return departureCarDao.queryDeliveryInfoByOrderCode(ordercode);
	}

	@Override
	public List<DeparturePrintResponse> queryDepartureInfoBySendCode(List<String> sendCodes) {
		return departureCarDao.queryDepartureInfoBySendCode(sendCodes);
	}

	@Override
    public DeparturePrintResponse queryArteryBillingInfo(long carCode) {
        return departureCarDao.queryArteryBillingInfo(carCode);
    }

    @Deprecated
    @Override
    public DeparturePrintResponse queryArteryBillingInfoByBoxCode(String boxCode) {
        return departureCarDao.queryArteryBillingInfoByBoxCode(boxCode);
	}


    @Override
    public boolean pushMQ2ArteryBillingSysByTask(Task ArteryInfoTask) throws Exception {
        if(!ArteryInfoTask.getBody().equals("false")){
        	DeparturePrintResponse dpr = new DeparturePrintResponse();
            try{
                dpr = JsonHelper.fromJson(ArteryInfoTask.getBody(), DeparturePrintResponse.class);
            }catch(Exception e){
                logger.error("执行干线计费信息任务转换失败，消息不合法 " + ArteryInfoTask.getBody());
                taskService.doError(ArteryInfoTask);
                return false;
            }

            if(null == dpr || null == dpr.getDepartureCarID()){
                logger.warn("执行干线计费信息任务转换失败，消息不合法 " + ArteryInfoTask.getBody());
                taskService.doError(ArteryInfoTask);
                return false;
            }

            logger.info("推送干线计费信息 MQ 开始 " + ArteryInfoTask.getBody());
            try{
                receiveArteryInfoMQ.send("" + dpr.getDepartureCarID(),ArteryInfoTask.getBody());
            }catch(Exception e){
                logger.error("执行干线计费信息任务发送MQ失败，失败原因 " + e);
                taskService.doError(ArteryInfoTask);
            }
        }
       
      //增加发车回传全称跟踪
		try {
			if(ArteryInfoTask.getKeyword1()!=null &&
					ArteryInfoTask.getBoxCode()!=null && 
					ArteryInfoTask.getKeyword1().equals(CARCODE_MARK))
			taskSendBdTrace(ArteryInfoTask);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("收货的时候回传车辆到达全称跟踪异常");
		}
        
        return taskService.doDone(ArteryInfoTask);
    }

	@Override
	public boolean dealDepartureTmpToSend(Task sendTask) {
		logger.info("开始处理PDA 批量发车200的任务，任务body " + JsonHelper.toJson(sendTask.getBody()));

		DepartureRequest request = null;
		try{
			DepartureRequest[] requests = JsonHelper.fromJson(sendTask.getBody(),DepartureRequest[].class);
			request = requests[0];	// 正常数据只有一条发车
		}catch(Exception ex){
			logger.warn("处理PDA 批量发车200的任务反序列化失败，原因 " + ex.getMessage());
			return false;
		}

		if(null == request || StringHelper.isEmpty(request.getBatchKey())){
			logger.warn("处理PDA 批量发车200的任务波次号为空，不执行");
			return false;
		}

		List<DepartureTmpRequest> tmpDeparture = departureTmpDao.queryDepartureTmpByBatchCode(request.getBatchKey());
		if(null == tmpDeparture || tmpDeparture.size() <= 0){
			logger.warn("处理PDA 批量发车200的任务没有获取到波次号下面的批次，不执行");
			return true;
		}

		List<DepartureSendRequest> sendDepartures = new ArrayList<DepartureSendRequest>();
		for(DepartureTmpRequest tr : tmpDeparture){
			sendDepartures.add(toDepartureSendRequest(request,tr));
		}
		request.setSends(sendDepartures);

		JdResponse response = null;
		try{
			response = this.template.postForObject(URL,request,JdResponse.class);
		}catch(Throwable ex){
			logger.error("处理PDA 批量发车200的任务调用WEB发车接口失败，原因 " + ex.getMessage());
			return false;
		}
		if(null == response || response.getCode() != 200){
			if(null == response){
				logger.warn("处理PDA 批量发车200的任务调用WEB发车接口处理失败，返回为空");
			}else{
				logger.warn("处理PDA 批量发车200的任务调用WEB发车接口处理失败，编码 " + response.getCode() + "; 信息 " + response.getMessage());
			}
			return false;
		}
		return true;
	}

	private DepartureSendRequest toDepartureSendRequest(DepartureRequest dr, DepartureTmpRequest tr){

		DepartureSendRequest req = new DepartureSendRequest();
		req.setSendCode(tr.getSendCode());
		req.setThirdWaybillCode(dr.getThirdWaybillCode()); // 发货运单号
		req.setCarCode(dr.getCarCode()); // 车号
		req.setSendCarSealsID(dr.getSendCarSealsID());
		req.setSendUser(dr.getSendUser());
		req.setSendUserCode(dr.getSendUserCode());
		req.setType(dr.getType());
		req.setBusinessType(dr.getBusinessType());
		req.setKey(dr.getKey());
		req.setOperateTime(dr.getOperateTime());
		req.setSiteCode(dr.getSiteCode());
		req.setSiteName(dr.getSiteName());
		req.setUserCode(dr.getUserCode());
		req.setUserName(dr.getUserName());

		return req;
	}

	private void taskSendBdTrace(Task task) {
		logger.info("收货的时候回传车辆到达全称跟踪，车次号为" + task.getBoxCode());
		String[] result = task.getKeyword2().split("&");
		if(result!=null && result.length==4){
			List<DepartureSend> sendList = getDepartureSendByCarId(Long
					.parseLong(task.getBoxCode()));
			SendM lSendM = new SendM();
			for (DepartureSend send : sendList) {
				lSendM.setSendCode(send.getSendCode());
				List<SendM> tSendM = this.sendMDao.selectBySendSiteCode(lSendM);
				SendDetail tSendDatail = new SendDetail();
				for (SendM newSendM : tSendM) {
					tSendDatail.setBoxCode(newSendM.getBoxCode());
					tSendDatail.setCreateSiteCode(newSendM.getCreateSiteCode());
					tSendDatail.setReceiveSiteCode(newSendM.getReceiveSiteCode());
					tSendDatail.setIsCancel(0);
					List<SendDetail> sendDatailList = this.sendDatailDao
							.querySendDatailsBySelective(tSendDatail);
					for(SendDetail sendDetail : sendDatailList){
						sendDetail.setOperateTime(task.getCreateTime());
						returnSendBdTrace(sendDetail , result);
					}
				}
			}
		}
	}

	private void returnSendBdTrace(SendDetail sendDetail,String[] result) {

		WaybillStatus tWaybillStatus = new WaybillStatus();
		tWaybillStatus.setCreateSiteCode(Integer.parseInt(result[0]));
		tWaybillStatus.setCreateSiteName(result[1]);
		tWaybillStatus.setPackageCode(sendDetail.getPackageBarcode());
		tWaybillStatus.setWaybillCode(sendDetail.getWaybillCode());
		tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_FCS);
		tWaybillStatus.setOperateTime(sendDetail.getOperateTime());
		tWaybillStatus.setOperator(result[3]);
		tWaybillStatus.setOperatorId(Integer.parseInt(result[2]));

		sendWaybillAddWS(toWaybillStatus(tWaybillStatus));
	}

	public static void main(String args[]) {
		String sendCode = "Y3011-47064-201407011545400";
		// String sendCode="3047-48349-201407011639170";
		Integer receiveSiteCode = Integer.parseInt(sendCode.split("-")[1]);
		System.out.println(receiveSiteCode);
	}
}
