package com.jd.bluedragon.distribution.receive.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.ReceiveRequest;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.departure.dao.DepartureLogDao;
import com.jd.bluedragon.distribution.departure.domain.DepartureLog;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.receive.dao.ReceiveDao;
import com.jd.bluedragon.distribution.receive.dao.TurnoverBoxDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.reverse.domain.PickWare;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.seal.service.SealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.TurnoverBoxInfo;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("receiveService")
public class ReceiveServiceImpl implements ReceiveService {

	private Logger log = LoggerFactory.getLogger(ReceiveServiceImpl.class);

    public static final String CARCODE_MARK = "0";  // 按车次号查询
    public static final String BOXCODE_MARK = "1";  // 按箱号查询

	@Autowired
	private ReceiveDao receiveDao;

	@Autowired
	private SealVehicleService sealVehicleService;

	@Autowired
	private OperationLogService operationLogService;

	@Autowired
	private CenConfirmService cenConfirmService;

	@Autowired
    @Qualifier("turnoverBoxMQ")
    private DefaultJMQProducer turnoverBoxMQ;

    @Autowired
    @Qualifier("pickwarePushMQ")
    private DefaultJMQProducer pickwarePushMQ;

	@Autowired
	private TurnoverBoxDao turnoverBoxDao;

	@Autowired
	private SealBoxService sealBoxService;

	@Autowired
	private BaseService baseService;

	@Autowired
	private TaskService taskService;

	@Autowired
    private DeliveryService deliveryService;

	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    private DepartureService departureService;

	@Autowired
	private DepartureLogDao departureLogDao;

	/**
	 * 收货
	 *
	 * @param
	 */
	@JProfiler(jKey= "DMSWEB.receiveService.doReceiveing", mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Deprecated
	public void doReceiveing(Receive receive) {
		this.addReceive(receive);
		// 必须有封车号，才更新封车表
		String code = receive.getShieldsCarCode();
		if (code != null && !code.equals("")) {
			createSealVehicle(receive, code);
		}
		//解封箱
		if(1==receive.getBoxingType().intValue()&&receive.getSealBoxCode()!=null&&!receive.getSealBoxCode().equals("")){
			createSealBox(receive);
		}
		// 插收货确认表
		if (Constants.BOXING_TYPE.equals(receive.getBoxingType())) {
			addOperationLog(receive,"ReceiveServiceImpl#doReceiveing");// 记录日志
			CenConfirm cenConfirm=cenConfirmService.createCenConfirmByReceive(receive);
			cenConfirmService.saveOrUpdateCenConfirm(cenConfirm);
			returnTrack(cenConfirm);

			/*//增加发车回传全称跟踪
			try {
				if(receive.getDepartureCarId()!=null &&
						receive.getDepartureCarId()>0)
					returnSendBdTrace(receive ,null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("收货的时候回传车辆到达全称跟踪异常");
			}*/
			//取件单推送mq
			if(WaybillUtil.isSurfaceCode(receive.getBoxCode())){
				BaseEntity<PickupTask> pickup =null;
				try {
				pickup = this.waybillPickupTaskApi.getDataBySfCode(receive.getBoxCode());
				} catch (Exception e) {
                    log.error("分拣中心收货[备件库-取件单]:调用取件单号信息ws接口异常[{}]",receive.getBoxCode(), e);
                }
              if(pickup != null && pickup.getData()!=null) {
					 pushPickware(receive,receive.getBoxCode(),pickup.getData().getPickupCode());
                 }
            }
		}else{
			List<SendDetail> sendDetails=deliveryService.getCancelSendByBox(receive.getBoxCode());
			if (sendDetails == null || sendDetails.isEmpty()){
				log.warn("根据[boxCode={}]获取包裹信息[deliveryService.getSendByBox(boxCode)]返回null或空,[收货]不能回传全程跟踪",receive.getBoxCode());
			}else{
				CenConfirm cenConfirm = paseCenConfirm(receive);
				for(SendDetail  sendDetail:sendDetails){
					receive.setPackageBarcode(sendDetail.getPackageBarcode());
					addOperationLog(receive,"ReceiveServiceImpl#doReceiveing");// 记录日志
					cenConfirm.setPackageBarcode(sendDetail.getPackageBarcode());
					returnTrack(cenConfirm);
					
					/*//增加发车回传全称跟踪
					try {
						if(receive.getDepartureCarId()!=null &&
								receive.getDepartureCarId()>0)
							returnSendBdTrace(receive ,sendDetail);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.error("收货的时候回传车辆到达全称跟踪异常");
					}*/

					//取件单推送mq
					if(WaybillUtil.isSurfaceCode(sendDetail.getPackageBarcode())){
					pushPickware(receive,sendDetail.getPackageBarcode(),sendDetail.getPickupCode());
					}
				}
			}
		}
		// 推送mq消息
		pushTurnoverBoxInfo(receive);
	}

	private CenConfirm paseCenConfirm(Receive receive) {
		CenConfirm cenConfirm=new CenConfirm();
		cenConfirm.setBoxCode(receive.getBoxCode());
		cenConfirm.setType(receive.getReceiveType());
		cenConfirm.setCreateSiteCode(receive.getCreateSiteCode());
		cenConfirm.setOperateType(Constants.OPERATE_TYPE_SH);
		cenConfirm.setOperateTime(receive.getCreateTime());
		cenConfirm.setOperateUser(receive.getCreateUser());
		cenConfirm.setOperateUserCode(receive.getCreateUserCode());
		return cenConfirm;
	}

	private void returnTrack(CenConfirm cenConfirm) {
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(cenConfirm
				.getCreateSiteCode());
		if (bDto == null) {
			log.warn("[PackageBarcode={}][boxCode={}]根据[siteCode={}]获取基础资料站点信息[getSiteBySiteID]返回null,[收货]不能回传全程跟踪",
					cenConfirm.getPackageBarcode(), cenConfirm.getBoxCode(),cenConfirm.getCreateSiteCode());
		}else{
		    WaybillStatus waybillStatus=cenConfirmService.createBasicWaybillStatus(cenConfirm, bDto, null);
		    if(Constants.BUSSINESS_TYPE_POSITIVE == cenConfirm.getType().intValue()||Constants.BUSSINESS_TYPE_SITE==cenConfirm.getType()){
		    	waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_SH);//正向
		    }else{
		    	waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_REVERSE_SH);//逆向
		    }
		    if (cenConfirmService.checkFormat(waybillStatus, cenConfirm.getType())) {
				// 添加到task表
				taskService.add(cenConfirmService.toTask(waybillStatus, Constants.OPERATE_TYPE_SH));
			} else {
				log.warn("[PackageCode={}][boxCode={}][参数信息不全],[收货]不能回传全程跟踪",waybillStatus.getPackageCode(),waybillStatus.getBoxCode());
			}

		}
	}
	
	
	/*private void returnSendBdTrace(Receive receive, SendDetail  sendDetail) {
		
		WaybillStatus tWaybillStatus = new WaybillStatus();
		tWaybillStatus.setCreateSiteCode(receive.getCreateSiteCode());
		tWaybillStatus.setCreateSiteName(receive.getCreateSiteName());
		if(sendDetail==null){
			tWaybillStatus.setPackageCode(receive.getPackageBarcode());
			tWaybillStatus.setWaybillCode(receive.getWaybillCode());
		}else{
			tWaybillStatus.setPackageCode(sendDetail.getPackageBarcode());
			tWaybillStatus.setWaybillCode(sendDetail.getWaybillCode());
		}
		tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_FCS);
		tWaybillStatus.setOperateTime(receive.getCreateTime());
		tWaybillStatus.setOperator(receive.getCreateUser());
		tWaybillStatus.setOperatorId(receive.getCreateUserCode());
		
		departureService.sendWaybillAddWS(departureService.toWaybillStatus(tWaybillStatus));
	}*/

	private void pushTurnoverBoxInfo(Receive receive) {
		TurnoverBoxInfo turnoverBoxInfo = new TurnoverBoxInfo();
		turnoverBoxInfo.setTurnoverBoxCode(receive.getTurnoverBoxCode());
		turnoverBoxInfo.setOperatorSortingId(receive.getCreateSiteCode());
		turnoverBoxInfo.setMessageType("SORTING_REVERSE_RECEIVE_QUEUE");
		turnoverBoxInfo.setOperatorId(receive.getCreateUserCode());
		turnoverBoxInfo.setOperatorName(receive.getCreateUser());
		turnoverBoxInfo.setOperateTime(DateHelper.formatDateTime(receive
				.getCreateTime()));
		turnoverBoxInfo.setFlowFlag(receive.getReceiveType().toString());
		try {
			//messageClient.sendMessage("turnover_box",JsonHelper.toJson(turnoverBoxInfo), receive.getBoxCode());
            turnoverBoxMQ.send(receive.getBoxCode(),JsonHelper.toJson(turnoverBoxInfo));
		} catch (Exception e) {
			log.error("分拣中心收货推送MQ[周转箱]信息失败：{}" ,JsonHelper.toJson(receive), e);
		}
	}

	private void pushPickware(Receive receive,String packageCode,String pickwareCode) {
		log.info("面单号：[{}]取件单号：[{}]",packageCode,pickwareCode);
		PickWare pickWare = new PickWare();
		pickWare.setBoxCode(receive.getBoxCode());
		pickWare.setPackageCode(packageCode);
		if(StringHelper.isEmpty(pickwareCode)){
		pickWare.setPickwareCode("");
		}else{
		pickWare.setPickwareCode(pickwareCode);
		}
		pickWare.setOperator(receive.getCreateUser()+"|"+receive.getCreateUserCode());
		pickWare.setOperateTime(DateHelper.formatDateTime(receive
				.getCreateTime()));
		try {
		    String json=JsonHelper.toJson(pickWare);
		    log.info("分拣中心收货推送MQ[备件库-取件单]json:[{}]",json);
			//messageClient.sendMessage("pickware_push",json, receive.getBoxCode());
            pickwarePushMQ.send(receive.getBoxCode(),json);
		} catch (Exception e) {
			log.error("分拣中心收货推送MQ[备件库-取件单]信息失败[{}]" ,receive.getBoxCode(), e);
		}
	}

	/**
	 * 插入pda操作日志表
	 *
	 * @param receive
	 */
	private void addOperationLog(Receive receive,String methodName) {
		OperationLog operationLog = new OperationLog();
		operationLog.setBoxCode(receive.getBoxCode());
		operationLog.setCreateSiteCode(receive.getCreateSiteCode());
		operationLog.setCreateSiteName(receive.getCreateSiteName());
		operationLog.setCreateTime(receive.getCreateTime());
		operationLog.setCreateUser(receive.getCreateUser());
		operationLog.setCreateUserCode(receive.getCreateUserCode());
		operationLog.setLogType(OperationLog.LOG_TYPE_RECEIVE);
		operationLog.setOperateTime(receive.getCreateTime());
		operationLog.setPackageCode(receive.getPackageBarcode());
		operationLog.setUpdateTime(receive.getUpdateTime());
		operationLog.setWaybillCode(receive.getWaybillCode());
		operationLog.setMethodName(methodName);
		operationLogService.add(operationLog);
	}

	private void createSealVehicle(Receive receive, String code) {
		SealVehicle sealVehicle = new SealVehicle();
		sealVehicle.setCode(code);
		sealVehicle.setVehicleCode(receive.getCarCode());
		sealVehicle.setUpdateUser(receive.getCreateUser());
		sealVehicle.setUpdateUserCode(receive.getCreateUserCode());
		sealVehicle.setCreateSiteCode(receive.getCreateSiteCode());
		sealVehicle.setReceiveSiteCode(receive.getCreateSiteCode());
		this.sealVehicleService.updateSealVehicle(sealVehicle);
		// sealVehicleService.saveOrUpdate(sealVehicle);
	}

	private void createSealBox(Receive receive){
		SealBoxRequest sealBoxRequest = new SealBoxRequest();
		sealBoxRequest.setSealCode(receive.getSealBoxCode());
		sealBoxRequest.setBoxCode(receive.getBoxCode());
		sealBoxRequest.setSiteCode(receive.getCreateSiteCode());
		sealBoxRequest.setSiteName(receive.getCreateSiteName());
		sealBoxRequest.setUserCode(receive.getCreateUserCode());
		sealBoxRequest.setUserName(receive.getCreateUser());
		sealBoxRequest.setOperateTime(DateHelper.formatDateTimeMs(receive
		        .getUpdateTime()));
		SealBox sealBox = SealBox.toSealBox2(sealBoxRequest);
		sealBox.setBoxCode(sealBoxRequest.getBoxCode());
		sealBox.setCreateTime(DateHelper.getSeverTime(sealBoxRequest.getOperateTime()));
		sealBox.setUpdateTime(DateHelper.getSeverTime(sealBoxRequest.getOperateTime()));
		sealBoxService.saveOrUpdate(sealBox);
	}

	public Receive taskToRecieve(Task task) {
		String jsonReceive = task.getBody();
		log.info("收货json数据：{}" , jsonReceive);
		List<ReceiveRequest> receiveRequests = Arrays.asList(JsonHelper
				.jsonToArray(jsonReceive, ReceiveRequest[].class));
		log.info("收货json数据转化后：{}" , receiveRequests);
		Receive receive = new Receive();
		ReceiveRequest receiveRequest = receiveRequests.get(0);
		String tmpNumber = receiveRequest.getPackOrBox();
		// 根据规则得出包裹号、箱号、运单号
		if (BusinessHelper.isBoxcode(tmpNumber)) {
			if(tmpNumber.length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT){
				log.warn("收货任务JSON数据非法，箱号超长，收货消息体：{}" , jsonReceive);
				return null;
			}
			// 字母开头为箱号
			receive.setBoxCode(tmpNumber);
			// 装箱类型（1 箱包装 2 单件包裹）
			receive.setBoxingType(Short.parseShort("1"));
		} else {
			// 包裹
			if (WaybillUtil.isSurfaceCode(tmpNumber)) {
				// 取件单(暂不设运单号)
				receive.setBoxCode(tmpNumber);
				receive.setPackageBarcode(tmpNumber);
				receive.setBoxingType(Short.parseShort("2"));
			} else if(WaybillUtil.isPackageCode(tmpNumber) || WaybillUtil.isWaybillCode(tmpNumber)){
				// 包裹号(=箱号)
				receive.setBoxCode(tmpNumber);
				receive.setPackageBarcode(tmpNumber);
				// 装箱类型（1 箱包装 2 单件包裹）
				receive.setBoxingType(Short.parseShort("2"));
				receive.setWaybillCode(WaybillUtil.getWaybillCode(tmpNumber));
			}else {
				log.warn("收货任务JSON数据非法，无法识别的扫描单号，收货消息体：{}" , jsonReceive);
				return null;
			}
		}
		receive.setCarCode(receiveRequest.getCarCode());
		receive.setShieldsCarCode(receiveRequest.getShieldsCarCode());
		if (StringUtils.isNotBlank(receiveRequest.getShieldsCarTime())) {
			receive.setShieldsCarTime(DateHelper.getSeverTime(receiveRequest
					.getShieldsCarTime()));
		}
		receive.setSealBoxCode(receiveRequest.getSealBoxCode());
		receive.setReceiveType(receiveRequest.getBusinessType().shortValue());
		receive.setCreateSiteCode(receiveRequest.getSiteCode());
		receive.setCreateTime(DateHelper.getSeverTime(receiveRequest
				.getOperateTime()));
		receive.setCreateUser(receiveRequest.getUserName());
		receive.setCreateUserCode(receiveRequest.getUserCode());
		receive.setUpdateTime(DateHelper.getSeverTime(receiveRequest
				.getOperateTime()));
		receive.setCreateSiteName(receiveRequest.getSiteName());
		receive.setTurnoverBoxCode(receiveRequest.getTurnoverBoxCode());
		receive.setQueueNo(receiveRequest.getQueueNo());

		return receive;
	}

	/**
	 * 收/发/取消[空周转箱]
	 *
	 * @param
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void turnoverBoxAdd(TurnoverBox turnoverBox) {
		turnoverBoxDao.add(TurnoverBoxDao.namespace, turnoverBox);
		//推送mq
		TurnoverBoxInfo turnoverBoxInfo = new TurnoverBoxInfo();
		turnoverBoxInfo.setTurnoverBoxCode(turnoverBox.getTurnoverBoxCode());
		turnoverBoxInfo.setOperatorSortingId(turnoverBox.getCreateSiteCode());
		turnoverBoxInfo.setOperatorSortingName(turnoverBox.getCreateSiteName());
		turnoverBoxInfo.setMessageType("SORTING_REVERSE_RECEIVE_NULL_QUEUE");
		turnoverBoxInfo.setOperatorId(turnoverBox.getCreateUserCode());
		turnoverBoxInfo.setOperatorName(turnoverBox.getCreateUser());
		turnoverBoxInfo.setOperateTime(DateHelper.formatDateTime(turnoverBox
				.getOperateTime()));
		turnoverBoxInfo.setDestSiteId(turnoverBox.getReceiveSiteCode());
		turnoverBoxInfo.setDestSiteName(turnoverBox.getReceiveSiteName());
		// 1.收 2.发 3.取消
		//10 正向（发）20逆向（收）30 取消
		if(1==turnoverBox.getOperateType().intValue()){
			turnoverBoxInfo.setFlowFlag("20");
		}else if(2==turnoverBox.getOperateType().intValue()){
			turnoverBoxInfo.setFlowFlag("10");
		}else if(3==turnoverBox.getOperateType().intValue()){
			turnoverBoxInfo.setFlowFlag("30");
		}
		try {
			if(log.isDebugEnabled()){
				log.debug("分拣中心推送监控MQ[空周转箱]信息：{}",JsonHelper.toJson(turnoverBoxInfo));
			}
			//messageClient.sendMessage("turnover_box",JsonHelper.toJson(turnoverBoxInfo),turnoverBoxInfo.getTurnoverBoxCode());
            turnoverBoxMQ.send(turnoverBoxInfo.getTurnoverBoxCode(),JsonHelper.toJson(turnoverBoxInfo));
		} catch (Exception e) {
			log.error("分拣中心推送监控MQ[空周转箱]信息失败：{}" ,JsonHelper.toJson(turnoverBoxInfo), e);
		}
	}


	/**
	 * 按条件查询[收箱打印]清单集合
	 * @param paramMap
	 * @return
	 */
	@Override
	public List<Receive> findReceiveJoinList(Map<String, Object> paramMap){
		return this.receiveDao.findReceiveJoinList(paramMap);
	}

	@Override
	public int findReceiveJoinTotalCount(Map<String, Object> paramMap) {
		return this.receiveDao.findReceiveJoinTotalCount(paramMap);
	}

    @Override
    public DeparturePrintResponse dellSendMq2ArteryBillingSys(String key, String code,String message) {
        DeparturePrintResponse departureInfo = null;
        DeparturePrintResponse departurePrintResponse = new DeparturePrintResponse();
        /*if(CARCODE_MARK.equals(key)){
            // 车次号有效性判断
            Long departureCarID = null;
            try{
                departureCarID = Long.parseLong(code);
            }catch(NumberFormatException e){
                log.error("车次号获取干线计费信息失败，车次号不符合要求。车次号 " + code);
                departurePrintResponse.setCode(DeparturePrintResponse.CODE_PARAM_ERROR);
                departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_PARAM_ERROR);
                return departurePrintResponse;
            }
            departureInfo = departureService.queryArteryBillingInfo(departureCarID);
        }else{
            departureInfo = departureService.queryArteryBillingInfoByBoxCode(code);
        }

        log.info("车次号或者运单号[ " + code + " ]获取干线计费信息为 " + JsonHelper.toJson(departureInfo));

        if(null == departureInfo){
            log.warn("获取干线计费信息失败，获取干线计费信息为空");
            departurePrintResponse.setCode(DeparturePrintResponse.CODE_OK_NULL);
            departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_OK_NULL);
            return departurePrintResponse;
		}

		departureInfo.setReceiveTime(DateHelper.formatDateTime(new Date()));
		// 监控报表加记录
		// addReceiveDepartureLog(departureInfo,message);

		// 不是干线的数据，不需要传给计费系统
		if(departureInfo.getRouteType() != DepartureCar.TYPE_TRUNK_LINE){
			log.warn("获取运输信息不是干线的，不发送计费系统");
			departurePrintResponse.setCode(DeparturePrintResponse.CODE_UNEXCEPT_RESULT);
			departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_UNEXCEPT_RESULT);
			try{
				addMq2ArteryBillingSys(key, code, null, message);
			} catch(Throwable e){
				log.error("获取干线计费信息推送MQ失败，添加任务失败 " + e);
				departurePrintResponse.setCode(DeparturePrintResponse.CODE_SERVICE_ERROR);
				departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_SERVICE_ERROR);
				return departurePrintResponse;
			}
			return departurePrintResponse;
		}

        try{
            addMq2ArteryBillingSys(key,code,departureInfo ,message);
        }catch(Throwable e){
            log.error("获取干线计费信息推送MQ失败，添加任务失败 " + e);
            departurePrintResponse.setCode(DeparturePrintResponse.CODE_SERVICE_ERROR);
            departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_SERVICE_ERROR);
            return departurePrintResponse;
        }*/

        departurePrintResponse.setCode(DeparturePrintResponse.CODE_OK);
        departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_OK);
        return departurePrintResponse;
    }

    /**
     *  增加发送给计费系统MQ的任务
     * */
    public void addMq2ArteryBillingSys(String key, String code, DeparturePrintResponse departureInfo ,String message){
        Task tTask = new Task();
        tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        if(departureInfo!=null)
        	tTask.setBody(JsonHelper.toJson(departureInfo));
        else
			tTask.setBody("false");
        tTask.setKeyword1(key);
        tTask.setKeyword2(message);
        tTask.setBoxCode(code);
        tTask.setExecuteCount(0);
        tTask.setType(Task.TASK_TYPE_PUSH_MQ);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_PUSH_MQ));
        tTask.setSequenceName(Task.getSequenceName(tTask.getTableName()));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        StringBuilder fingerprint = new StringBuilder();
        fingerprint.append(tTask.getType()).append(code);
        tTask.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        tTask.setCreateTime(new Date());

        taskService.add(tTask);
    }

	// 监控报表加记录
	public void addReceiveDepartureLog(DeparturePrintResponse departureInfo,String message){
		DepartureLog departureLog = new DepartureLog();
		departureLog.setCapacityCode(departureInfo.getCapacityCode());
		departureLog.setDepartureTime(DateHelper.parseDateTime(departureInfo.getCreateTime()));
		departureLog.setDepartureCarID(departureInfo.getDepartureCarID());
		departureLog.setDistributeCode(Integer.parseInt(message.split("&")[0]));
		departureLog.setDistributeName(message.split("&")[1]);
		departureLog.setOperatorCode(Integer.parseInt(message.split("&")[2]));
		departureLog.setOperatorName(message.split("&")[3]);
		departureLog.setReceiveTime(DateHelper.parseDateTime(departureInfo.getReceiveTime()));
		departureLog.setFingerPrint(Md5Helper.encode(departureLog.getDistributeCode()
                                                    + "_" + departureLog.getDepartureCarID()));
		if(departureLogDao.findByFingerPrint(departureLog.getFingerPrint()) > 0){
            log.warn("监控报表加发车记录重复，分拣中心[{}],发车批次号[{}]",
					departureLog.getDistributeCode(),departureLog.getDepartureCarID());
        }else{
            departureLogDao.insert(departureLog);
        }
	}

	public static void main(String[] args) {
		PickWare pickWare = new PickWare();
		pickWare.setBoxCode("GC1234567890");
		pickWare.setPackageCode("W1234560");
		pickWare.setPickwareCode("Q1234567890");
		pickWare.setOperator("lidong");
		pickWare.setOperateTime("2013-08-19 15:33:20");
		System.out.println(JsonHelper.toJson(pickWare));
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public boolean addReceive(Receive receive) {
		return receiveDao.add(ReceiveDao.namespace, receive)==1;
	}

}
