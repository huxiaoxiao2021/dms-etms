package com.jd.bluedragon.distribution.receive.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationEnum;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.departure.dao.DepartureLogDao;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.receive.dao.TurnoverBoxDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.Receive;
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
import com.jd.bluedragon.distribution.task.domain.DmsTaskExecutor;
import com.jd.bluedragon.distribution.task.domain.TaskContext;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

public abstract class BaseReceiveTaskExecutor<T extends Receive> extends DmsTaskExecutor<T> {

	private static Logger log = LoggerFactory.getLogger(BaseReceiveTaskExecutor.class);

	@Autowired
	ReceiveService receiveService;

	@Autowired
	protected SealVehicleService sealVehicleService;

	@Autowired
	protected OperationLogService operationLogService;

	@Autowired
	protected CenConfirmService cenConfirmService;

	@Autowired
	@Qualifier("turnoverBoxMQ")
	protected DefaultJMQProducer turnoverBoxMQ;

	@Autowired
	@Qualifier("pickwarePushMQ")
	protected DefaultJMQProducer pickwarePushMQ;

	@Autowired
	protected TurnoverBoxDao turnoverBoxDao;

	@Autowired
	protected SealBoxService sealBoxService;

	@Autowired
	protected BaseService baseService;

	@Autowired
	protected TaskService taskService;

	@Autowired
	protected DeliveryService deliveryService;

	@Autowired
	protected WaybillPickupTaskApi waybillPickupTaskApi;

	@Autowired
	protected DepartureService departureService;

	@Autowired
	protected DepartureLogDao departureLogDao;

	@Autowired
	private InspectionNotifyService inspectionNotifyService;

    @Autowired
    @Qualifier("cycleMaterialSendMQ")
    private DefaultJMQProducer cycleMaterialSendMQ;

    @Autowired
    private CycleBoxService cycleBoxService;
	
	/**
	 * 收货
	 * 
	 * @param
	 */
	@JProfiler(jKey = "DMSWEB.ReceiveTaskExecutor.execute", mState = { JProEnum.TP })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean execute(TaskContext<T> taskContext, String ownSign) {
		List<CenConfirm> cenConfirmList = null;
		// step1-保存收货记录
		saveReceive(taskContext);
		// 必须有封车号，才更新封车表
		updateSealVehicle(taskContext);
		// 解封箱
		unsealBox(taskContext);
		// 判断是否大件商品
		boolean isBoxingType = Constants.BOXING_TYPE.equals(taskContext.getBody()
				.getBoxingType());
		// 插收货确认表并发送全程跟踪
		if (isBoxingType) {
			// 大件商品-单条处理
			saveCenConfirmAndSendTrack(taskContext,true);
		} else {
			// 非大件商品-批量处理
			cenConfirmList = batchSaveCenConfirmAndSendTrack(taskContext);
		}
		// 推送mq消息--周转箱
		pushTurnoverBoxInfo(taskContext);

		pushReceiveInfo(cenConfirmList);

		// 循环集包袋发送消息
		pushCycleMaterialMQ(taskContext);

		return true;
	}

	protected void pushCycleMaterialMQ(TaskContext<T> context) {

	    try {
	        if (null == context || null == context.getBody()) return;

            Receive receive = context.getBody();
            if (StringUtils.isNotBlank(receive.getBoxCode()) && BusinessUtil.isBoxcode(receive.getBoxCode())) {

                String materialCode = cycleBoxService.getBoxMaterialRelation(receive.getBoxCode());
                if (StringUtils.isNotBlank(materialCode)) {
                    BoxMaterialRelationMQ loopPackageMq = new BoxMaterialRelationMQ();
                    loopPackageMq.setBoxCode(receive.getBoxCode());
                    loopPackageMq.setBusinessType(BoxMaterialRelationEnum.TRANSFER.getType());
                    loopPackageMq.setMaterialCode(materialCode);
                    loopPackageMq.setOperatorCode(receive.getCreateUserCode() == null ? 0: receive.getCreateUserCode());
                    loopPackageMq.setOperatorName(receive.getCreateUser());
                    loopPackageMq.setSiteCode(receive.getCreateSiteCode() + "");
                    loopPackageMq.setOperatorTime(receive.getUpdateTime());

                    cycleMaterialSendMQ.sendOnFailPersistent(loopPackageMq.getMaterialCode(), JsonHelper.toJson(loopPackageMq));
                }
            }
        }
	    catch (Exception ex) {
	        log.error("push cycle material mq error.", ex);
        }
    }

	/**
	 * step1-保存收货记录
	 * 
	 * @param taskContext
	 */
	protected void saveReceive(TaskContext<T> taskContext) {
		receiveService.addReceive(taskContext.getBody());
	}

	protected CenConfirm paseCenConfirm(TaskContext<T> taskContext) {
		T receive = taskContext.getBody();
		CenConfirm cenConfirm = new CenConfirm();
		cenConfirm.setBoxCode(receive.getBoxCode());
		cenConfirm.setType(receive.getReceiveType());
		cenConfirm.setCreateSiteCode(receive.getCreateSiteCode());
		cenConfirm.setOperateType(Constants.OPERATE_TYPE_SH);
		cenConfirm.setOperateTime(receive.getCreateTime());
		cenConfirm.setOperateUser(receive.getCreateUser());
		cenConfirm.setOperateUserCode(receive.getCreateUserCode());
		return cenConfirm;
	}

	/**
	 * 发送全程跟踪
	 * 
	 * @param cenConfirm
	 */
	protected void sendTrack(TaskContext<T> taskContext,CenConfirm cenConfirm) {
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(cenConfirm
				.getCreateSiteCode());
		if (bDto == null) {
			log.warn("[PackageBarcode={}][boxCode={}]根据[siteCode={}]获取基础资料站点信息[getSiteBySiteID]返回null,[收货]不能回传全程跟踪",
					cenConfirm.getPackageBarcode(), cenConfirm.getBoxCode(),cenConfirm.getCreateSiteCode());
		} else {
			WaybillStatus waybillStatus = cenConfirmService
					.createBasicWaybillStatus(cenConfirm, bDto, null);
			if (Constants.BUSSINESS_TYPE_POSITIVE == cenConfirm.getType()
					.intValue()
					|| Constants.BUSSINESS_TYPE_SITE == cenConfirm.getType()) {
				waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_SH);// 正向
			} else {
				waybillStatus
						.setOperateType(WaybillStatus.WAYBILL_TRACK_REVERSE_SH);// 逆向
			}
			if (cenConfirmService.checkFormat(waybillStatus,
					cenConfirm.getType())) {
				// 添加到task表
				taskService.add(cenConfirmService.toTask(waybillStatus,
						Constants.OPERATE_TYPE_SH));
			} else {
				log.warn("[PackageCode={}][boxCode={}][参数信息不全],[收货]不能回传全程跟踪",
						waybillStatus.getPackageCode(), waybillStatus.getBoxCode());
			}

		}
	}

	protected void pushTurnoverBoxInfo(TaskContext<T> taskContext) {
		T receive = taskContext.getBody();
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
			// messageClient.sendMessage("turnover_box",JsonHelper.toJson(turnoverBoxInfo),
			// receive.getBoxCode());
			turnoverBoxMQ.send(receive.getBoxCode(),
					JsonHelper.toJson(turnoverBoxInfo));
		} catch (Exception e) {
			log.error("分拣中心收货推送MQ[周转箱]信息失败：{}" , receive.getBoxCode(), e);
		}
	}

	/**
	 * 发送验货MQ
	 * @param cenConfirmList
	 */
	protected void pushReceiveInfo(List<CenConfirm> cenConfirmList){
		if(cenConfirmList == null || cenConfirmList.size() < 1){
			return ;
		}
		Set sendInspectionKey = new HashSet();
		for (CenConfirm cenConfirm:cenConfirmList) {
			if(StringUtils.isBlank(cenConfirm.getWaybillCode()) || cenConfirm.getCreateSiteCode() == null || cenConfirm.getCreateSiteCode() <=0
					||sendInspectionKey.contains(cenConfirm.getWaybillCode())){
				log.warn("没有有效的运单号或者操作站点或已发送，不发送验货消息，cenConfirm={}",JSON.toJSONString(cenConfirm));
				continue;
			}
			sendInspectionKey.add(cenConfirm.getWaybillCode());
			InspectionMQBody body = new InspectionMQBody();
			body.setWaybillCode(cenConfirm.getWaybillCode());
			body.setInspectionSiteCode(cenConfirm.getCreateSiteCode());
			body.setCreateUserCode(cenConfirm.getInspectionUserCode());
			body.setCreateUserName(cenConfirm.getInspectionUser());
			body.setOperateTime(null != cenConfirm.getInspectionTime() ?cenConfirm.getInspectionTime() : new Date());
			body.setSource("DMS-RECEIVE");

			inspectionNotifyService.send(body);
		}
	}

	protected void pushPickware(T receive, String packageCode,
			String pickwareCode) {
		log.debug("面单号：[{}]取件单号：[{}]",packageCode, pickwareCode);
		PickWare pickWare = new PickWare();
		pickWare.setBoxCode(receive.getBoxCode());
		pickWare.setPackageCode(packageCode);
		if (StringHelper.isEmpty(pickwareCode)) {
			pickWare.setPickwareCode("");
		} else {
			pickWare.setPickwareCode(pickwareCode);
		}
		pickWare.setOperator(receive.getCreateUser() + "|"
				+ receive.getCreateUserCode());
		pickWare.setOperateTime(DateHelper.formatDateTime(receive
				.getCreateTime()));
		try {
			String json = JsonHelper.toJson(pickWare);
			log.info("分拣中心收货推送MQ[备件库-取件单]json:[{}]",json);
			// messageClient.sendMessage("pickware_push",json,
			// receive.getBoxCode());
			pickwarePushMQ.send(receive.getBoxCode(), json);
		} catch (Exception e) {
			log.error("分拣中心收货推送MQ[备件库-取件单]信息失败[{}]",receive.getBoxCode(), e);
		}
	}

	/**
	 * 插入pda操作日志表
	 * 
	 * @param receive
	 */
	protected void addOperationLog(T receive,String methodName) {
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

	/**
	 * 必须有封车号，才更新封车表
	 * 
	 * @param taskContext
	 */
	protected void updateSealVehicle(TaskContext<T> taskContext) {
		T receive = taskContext.getBody();
		String code = receive.getShieldsCarCode();
		if (code != null && !code.equals("")) {
			SealVehicle sealVehicle = new SealVehicle();
			sealVehicle.setCode(code);
			sealVehicle.setVehicleCode(receive.getCarCode());
			sealVehicle.setUpdateUser(receive.getCreateUser());
			sealVehicle.setUpdateUserCode(receive.getCreateUserCode());
			sealVehicle.setCreateSiteCode(receive.getCreateSiteCode());
			sealVehicle.setReceiveSiteCode(receive.getCreateSiteCode());
			this.sealVehicleService.updateSealVehicle(sealVehicle);
		}
	}

	/**
	 * 执行解封箱操作
	 * 
	 * @param taskContext
	 */
	protected void unsealBox(TaskContext<T> taskContext) {
		T receive = taskContext.getBody();
		if (1 == receive.getBoxingType().intValue()
				&& receive.getSealBoxCode() != null
				&& !receive.getSealBoxCode().equals("")) {
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
			sealBox.setCreateTime(DateHelper.getSeverTime(sealBoxRequest
					.getOperateTime()));
			sealBox.setUpdateTime(DateHelper.getSeverTime(sealBoxRequest
					.getOperateTime()));
			sealBoxService.saveOrUpdate(sealBox);
		}
	}

	/**
	 * 保存收货确认信息并发送全程跟踪
	 * 
	 * @param taskContext
	 */
	public List<CenConfirm> saveCenConfirmAndSendTrack(TaskContext<T> taskContext,boolean saveOrUpdateCenConfirmFlg) {
		T receive = taskContext.getBody();
		addOperationLog(receive,"BaseReceiveTaskExecutor#saveCenConfirmAndSendTrack");// 记录日志
		List<CenConfirm> cenConfirmList = new ArrayList<>();
		CenConfirm cenConfirm = cenConfirmService
				.createCenConfirmByReceive(receive);
		cenConfirmList.add(cenConfirm);
		if(saveOrUpdateCenConfirmFlg){
			cenConfirmService.saveOrUpdateCenConfirm(cenConfirm);
		}
		sendTrack(taskContext,cenConfirm);

		// 取件单推送mq
		if (WaybillUtil.isSurfaceCode(receive.getBoxCode())) {
			BaseEntity<PickupTask> pickup = null;
			try {
				pickup = this.waybillPickupTaskApi.getDataBySfCode(receive
						.getBoxCode());
			} catch (Exception e) {
				log.error("分拣中心收货[备件库-取件单]:调用取件单号信息ws接口异常[{}]",receive.getBoxCode(), e);
			}
			if (pickup != null && pickup.getData() != null) {
				pushPickware(receive, receive.getBoxCode(), pickup.getData()
						.getPickupCode());
			}
		}
		return cenConfirmList;
	}

	/**
	 * 批量保存收货确认信息并发送全程跟踪
	 * 
	 * @param taskContext
	 */
	public List<CenConfirm> batchSaveCenConfirmAndSendTrack(TaskContext<T> taskContext) {
		T receive = taskContext.getBody();
		List<CenConfirm> cenConfirmList = new ArrayList<>();
		//非箱号按包裹号处理单条
		if(!BusinessHelper.isBoxcode(receive.getBoxCode())){
			saveCenConfirmAndSendTrack(taskContext, false);
		}
		List<SendDetail> sendDetails = deliveryService.getCancelSendByBox(receive
				.getBoxCode());
		if (sendDetails == null || sendDetails.isEmpty()) {
			log.warn("根据[boxCode={}]获取包裹信息[deliveryService.getSendByBox(boxCode)]返回null或空,[收货]不能回传全程跟踪",receive.getBoxCode());
		} else {
			for (SendDetail sendDetail : sendDetails) {
				CenConfirm cenConfirm = paseCenConfirm(taskContext);
				receive.setPackageBarcode(sendDetail.getPackageBarcode());
				addOperationLog(receive,"BaseReceiveTaskExecutor#batchSaveCenConfirmAndSendTrack");// 记录日志
				cenConfirm.setPackageBarcode(sendDetail.getPackageBarcode());
				cenConfirm.setWaybillCode(sendDetail.getWaybillCode());
				sendTrack(taskContext,cenConfirm);

				cenConfirmList.add(cenConfirm);

				// 取件单推送mq
				if (WaybillUtil.isSurfaceCode(sendDetail.getPackageBarcode())) {
					pushPickware(receive, sendDetail.getPackageBarcode(),
							sendDetail.getPickupCode());
				}
			}
		}

		return cenConfirmList;
	}
}
