package com.jd.bluedragon.distribution.receive.service.impl;

import com.jd.bluedragon.distribution.api.request.ArReceiveRequest;
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
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("arReceiveTaskExecutor")
public class ArReceiveTaskExecutor extends BaseReceiveTaskExecutor<ArReceive>{
	private static Logger log = Logger.getLogger(ArReceiveTaskExecutor.class);
	private static final String TRAC_MESSAGE_FORMAT ="货物已提出，%s — %s";
	private static final String KEY_DATA_AR_SEND_REGISTER ="arSendRegister";
	
	@Autowired
	ArSendRegisterService arSendRegisterService;
	
	@Autowired
	ArReceiveService arReceiveService;
	
	@Override
	public ArReceive parse(Task task, String ownSign) {
		String jsonReceive = task.getBody();
		log.info("空铁提货json数据：" + jsonReceive);
		List<ArReceiveRequest> arReceiveRequests = Arrays.asList(JsonHelper
				.jsonToArray(jsonReceive, ArReceiveRequest[].class));
		log.info("空铁提货json数据转化后：" + arReceiveRequests);
		ArReceive arReceive = new ArReceive();
		ArReceiveRequest arReceiveRequest = arReceiveRequests.get(0);
		String tmpNumber = arReceiveRequest.getPackOrBox();
		// 根据规则得出包裹号、箱号、运单号
		if (BusinessHelper.isBoxcode(tmpNumber)) {
			// 字母开头为箱号
			arReceive.setBoxCode(tmpNumber);
			// 装箱类型（1 箱包装 2 单件包裹）
			arReceive.setBoxingType(Short.parseShort("1"));
		} else {
			// 包裹
			if (WaybillUtil.isSurfaceCode(tmpNumber)) {
				// 取件单(暂不设运单号)
				arReceive.setBoxCode(tmpNumber);
				arReceive.setPackageBarcode(tmpNumber);
				arReceive.setBoxingType(Short.parseShort("2"));
			} else {
				// 包裹号(=箱号)
				arReceive.setBoxCode(tmpNumber);
				arReceive.setPackageBarcode(tmpNumber);
				// 装箱类型（1 箱包装 2 单件包裹）
				arReceive.setBoxingType(Short.parseShort("2"));
				arReceive.setWaybillCode(WaybillUtil.getWaybillCode(tmpNumber));
			}

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
		arReceive.setDepartureCarId(StringHelper.longParseString(arReceiveRequest.getDepartureCarId()));
		arReceive.setShuttleBusType(arReceiveRequest.getShuttleBusType());
		arReceive.setShuttleBusNum(arReceiveRequest.getShuttleBusNum());
		arReceive.setRemark(arReceiveRequest.getRemark());
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
			log.error("[PackageBarcode=" + cenConfirm.getPackageBarcode()
					+ "][boxCode=" + cenConfirm.getBoxCode() + "]根据[siteCode="
					+ cenConfirm.getCreateSiteCode()
					+ "]获取基础资料站点信息[getSiteBySiteID]返回null,[空铁提货]不能回传全程跟踪");
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
				log.error("[PackageCode=" + waybillStatus.getPackageCode()
						+ "][boxCode=" + waybillStatus.getBoxCode()
						+ "][参数信息不全],[空铁提货]不能回传全程跟踪");
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
}
