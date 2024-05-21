package com.jd.bluedragon.distribution.receive.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.ArReceiveRequest;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.ArReceiveService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskContext;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.domain.ArSendStatusEnum;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("arReceiveTaskExecutor")
public class ArReceiveTaskExecutor extends BaseReceiveTaskExecutor<ArReceive>{
	private static Logger log = LoggerFactory.getLogger(ArReceiveTaskExecutor.class);
	private static final String TRAC_MESSAGE_FORMAT ="货物已提出，%s — %s";
	private static final String KEY_DATA_AR_SEND_REGISTER ="arSendRegister";
	
	@Autowired
	ArSendRegisterService arSendRegisterService;
	
	@Autowired
	ArReceiveService arReceiveService;
	
	@Override
	public ArReceive parse(Task task, String ownSign) {
		String jsonReceive = task.getBody();
		log.info("空铁提货json数据：{}" , jsonReceive);
		List<ArReceiveRequest> arReceiveRequests = Arrays.asList(JsonHelper
				.jsonToArray(jsonReceive, ArReceiveRequest[].class));
		ArReceive arReceive = new ArReceive();
		ArReceiveRequest arReceiveRequest = arReceiveRequests.get(0);
		String tmpNumber = arReceiveRequest.getPackOrBox();
		// 根据规则得出包裹号、箱号、运单号
		if (BusinessHelper.isBoxcode(tmpNumber)) {
			if(tmpNumber.length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT){
				log.warn("收货任务JSON数据非法，箱号超长，收货消息体：{}" , jsonReceive);
				return null;
			}
			// 字母开头为箱号
			arReceive.setBoxCode(tmpNumber);
			// 装箱类型（1 箱包装 2 单件包裹）
			arReceive.setBoxingType(Short.parseShort("1"));
		} else if (WaybillUtil.isSurfaceCode(tmpNumber)) {
			// 取件单(暂不设运单号)
			arReceive.setBoxCode(tmpNumber);
			arReceive.setPackageBarcode(tmpNumber);
			arReceive.setBoxingType(Short.parseShort("2"));
		} else if (WaybillUtil.isPackageCode(tmpNumber)) {
			// 包裹号(=箱号)
			arReceive.setBoxCode(tmpNumber);
			arReceive.setPackageBarcode(tmpNumber);
			// 装箱类型（1 箱包装 2 单件包裹）
			arReceive.setBoxingType(Short.parseShort("2"));
			arReceive.setWaybillCode(WaybillUtil.getWaybillCode(tmpNumber));
		} else {
			log.warn("[空铁提货]不支持或无法识别的操作条码，packOrBox：{}", tmpNumber);
			return null;
		}
		arReceive.setCarCode(arReceiveRequest.getCarCode());
		arReceive.setShieldsCarCode(arReceiveRequest.getShieldsCarCode());
		if (StringUtils.isNotBlank(arReceiveRequest.getShieldsCarTime())) {
			arReceive.setShieldsCarTime(DateHelper.getSeverTime(arReceiveRequest
					.getShieldsCarTime()));
		}
		arReceive.setSealBoxCode(arReceiveRequest.getSealBoxCode());
		arReceive.setReceiveType(arReceiveRequest.getBusinessType().shortValue());
		arReceive.setCreateSiteCode(arReceiveRequest.getSiteCode());
		Date operateTime = DateHelper.getSeverTime(arReceiveRequest
				.getOperateTime());
		arReceive.setCreateTime(operateTime);
		arReceive.setCreateUser(arReceiveRequest.getUserName());
		arReceive.setCreateUserCode(arReceiveRequest.getUserCode());
		arReceive.setUpdateTime(operateTime);
		arReceive.setOperateTime(operateTime);
		arReceive.setCreateSiteName(arReceiveRequest.getSiteName());
		arReceive.setTurnoverBoxCode(arReceiveRequest.getTurnoverBoxCode());
		arReceive.setQueueNo(arReceiveRequest.getQueueNo());
		arReceive.setShuttleBusType(arReceiveRequest.getShuttleBusType());
		arReceive.setShuttleBusNum(arReceiveRequest.getShuttleBusNum());
		arReceive.setRemark(arReceiveRequest.getRemark());
		arReceive.setOperatorData(arReceiveRequest.getOperatorData());
		return arReceive;
	}
	/**
	 * step1-保存收货记录
	 * <p>1、加载空铁登记批次信息
	 * <p>2、查询空铁登记信息，并放入到TaskContext中
	 * <p>3、保存空铁提货记录
	 * @param taskContext
	 */
	protected void saveReceive(TaskContext<ArReceive> taskContext) {
		ArReceive arReceive = taskContext.getBody();
		ArSendCode arSendCode = arReceiveService.getLastArSendCodeByBarcode(arReceive.getBoxCode());
		if(arSendCode != null){
			arReceive.setSendCode(arSendCode.getSendCode());
			ArSendRegister arSendRegister = arSendRegisterService.findById(arSendCode.getSendRegisterId());
			if(arSendRegister != null){
				arReceive.setSendRegisterId(arSendRegister.getId());
				taskContext.setData(KEY_DATA_AR_SEND_REGISTER, arSendRegister);
				arSendRegister.setStatus(ArSendStatusEnum.ALREADY_DELIVERED.getType());
				arSendRegisterService.saveOrUpdate(arSendRegister);
			}
		}
//		super.saveReceive(taskContext);
		arReceiveService.saveOrUpdate(arReceive);
	}
	/**
	 * 发送全程跟踪
	 * 
	 * @param cenConfirm
	 */
	protected void sendTrack(TaskContext<ArReceive> taskContext,CenConfirm cenConfirm) {
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(cenConfirm
				.getCreateSiteCode());
		if (bDto == null) {
			log.warn("[PackageBarcode={}][boxCode={}]根据[siteCode={}]获取基础资料站点信息[getSiteBySiteID]返回null,[空铁提货]不能回传全程跟踪",
					cenConfirm.getPackageBarcode(), cenConfirm.getBoxCode(),cenConfirm.getCreateSiteCode());
		} else {
			WaybillStatus waybillStatus = cenConfirmService
					.createBasicWaybillStatus(cenConfirm, bDto, null);
			String startStationName = "";
			String endStationName = "";
			ArSendRegister arSendRegister = taskContext.getData(KEY_DATA_AR_SEND_REGISTER);
			if(arSendRegister != null){
				if(StringUtils.isNotBlank(arSendRegister.getStartStationName())) {
					startStationName = arSendRegister.getStartStationName();
				}
				if(StringUtils.isNotBlank(arSendRegister.getEndStationName())) {
					endStationName = arSendRegister.getEndStationName();
				}
			}
			waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_AR_RECEIVE);

			waybillStatus.setRemark(String.format(TRAC_MESSAGE_FORMAT, startStationName,endStationName));
			if (cenConfirmService.checkFormat(waybillStatus,
					cenConfirm.getType())) {
				// 添加到task表
				taskService.add(toTask(waybillStatus));
			} else {
				log.warn("[PackageCode={}][boxCode={}][参数信息不全],[空铁提货]不能回传全程跟踪",
						waybillStatus.getPackageCode(), waybillStatus.getBoxCode());
			}

		}
	}
    private Task toTask(WaybillStatus tWaybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(tWaybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_TRACK_AR_RECEIVE));
        task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(tWaybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }

	/**
	 * 保存收货确认信息并发送全程跟踪
	 *
	 * @param taskContext
	 */
	@Override
	public List<CenConfirm> saveCenConfirmAndSendTrack(TaskContext<ArReceive> taskContext,boolean saveOrUpdateCenConfirmFlg) {
		ArReceive receive = taskContext.getBody();
		addOperationLog(receive,"BaseReceiveTaskExecutor#saveCenConfirmAndSendTrack");// 记录日志
		List<CenConfirm> cenConfirmList = new ArrayList<>();
		CenConfirm cenConfirm = cenConfirmService
				.createCenConfirmByReceive(receive);
		cenConfirmList.add(cenConfirm);
		if(saveOrUpdateCenConfirmFlg){
			cenConfirmService.saveOrUpdateCenConfirmOnly(cenConfirm);
		}
		sendTrack(taskContext,cenConfirm);
		return cenConfirmList;
	}

    /**
     * 记录操作流水
     * @param taskContext 任务上下文
     */
	@Override
	public void handleOperateFlow(TaskContext<ArReceive> taskContext) {
		jyOperateFlowService.sendArReceiveOperateFlowData(taskContext.getBody(), OperateBizSubTypeEnum.AR_RECEIVE);
	}
}
