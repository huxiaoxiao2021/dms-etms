package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("offlineDeliveryService")
public class OfflineDeliveryServiceImpl implements OfflineService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private BoxService boxService;

	@Autowired
	private WaybillService waybillService;

	@Autowired
	private OfflineLogService offlineLogService;

	@Autowired
	private OperationLogService operationLogService;    //操作日志service

	public static final String OFFLINE_DELIVERY_REMARK = "离线发货";    //离线发货操作备注



	@Override
	public int parseToTask(OfflineLogRequest offlineLogRequest) {
		if (offlineLogRequest == null || offlineLogRequest.getBoxCode() == null
				|| offlineLogRequest.getSiteCode() == null
				|| offlineLogRequest.getBusinessType() == null) {
			this.log.warn("OfflineDeliveryServiceImpl --> 传入参数有误！");
			return Constants.RESULT_FAIL;
		}

		List<SendM> sendMList = new ArrayList<SendM>();

		List<OperationLog> operationLogs = new ArrayList<OperationLog>();    //离线发货操作日志集合

		List<OfflineLog> offlineLogs = new ArrayList<OfflineLog>();

		String[] boxCodes = offlineLogRequest.getBoxCode().split(
				Constants.SEPARATOR_COMMA);
		String[] turnoverBoxCodes = null;
		if (StringUtils.isNotBlank(offlineLogRequest.getTurnoverBoxCode())
				&& StringUtils.isNotBlank(offlineLogRequest
						.getTurnoverBoxCode().replace(
								Constants.SEPARATOR_COMMA, ""))) {
			turnoverBoxCodes = offlineLogRequest.getTurnoverBoxCode().split(
					Constants.SEPARATOR_COMMA);
		} else {
			offlineLogRequest.setTurnoverBoxCode(null);
		}

		Integer receiveSiteCode = null;

		if (checkBaseSite(offlineLogRequest.getReceiveSiteCode())) {
			receiveSiteCode = offlineLogRequest.getReceiveSiteCode();
		}

		for (int i = 0; i < boxCodes.length; i++) {
			String boxCode = boxCodes[i];

			if (receiveSiteCode == null) {
				// 设置目的站点
				Boolean tempGoOn = Boolean.TRUE;
				if (WaybillUtil.isPackageCode(boxCode)) {
					// 目的站点不存在，获取预分拣站点
					BigWaybillDto bigWaybillDto = this.waybillService
							.getWaybill(WaybillUtil.getWaybillCode(boxCode));
					if (bigWaybillDto != null
							&& bigWaybillDto.getWaybill() != null
							&& bigWaybillDto.getWaybill().getOldSiteId() != null
							&& !bigWaybillDto.getWaybill().getOldSiteId()
									.equals(0)) {
						receiveSiteCode = bigWaybillDto.getWaybill()
								.getOldSiteId();
					} else {
						this.log
								.error("OfflineDeliveryServiceImpl --> 传入参数有误--原包【"
										+ boxCode + "】预分拣站点问题！");
						tempGoOn = Boolean.FALSE;
					}

				} else {
					// 正常箱号，根据箱号获取目的站点信息
					Box box = this.boxService.findBoxByCode(boxCode);
					if (box == null) {
						this.log
								.error("OfflineDeliveryServiceImpl --> 传入参数有误--箱号【"
										+ boxCode + "】不存在！");
						tempGoOn = Boolean.FALSE;
					} else {
						// 设置目的站点
						receiveSiteCode = box.getReceiveSiteCode();
					}
				}
				if (!tempGoOn) {
					offlineLogs.add(requestToOffline(offlineLogRequest, Constants.RESULT_FAIL));
					continue;
				}

				if (StringUtils.isBlank(offlineLogRequest.getBatchCode())) {
					// 设置批次号
					offlineLogRequest.setBatchCode(offlineLogRequest
							.getSiteCode()
							+ "-"
							+ receiveSiteCode
							+ "-"
							+ DateHelper.formatDate(DateHelper.parseDate(
									offlineLogRequest.getOperateTime(),
									Constants.DATE_TIME_MS_FORMAT),
									Constants.DATE_TIME_MS_STRING));
				}
			}
			offlineLogRequest.setBoxCode(boxCode);
			offlineLogRequest.setReceiveSiteCode(receiveSiteCode);
			if (turnoverBoxCodes != null && turnoverBoxCodes.length > 0) {
				offlineLogRequest.setTurnoverBoxCode(turnoverBoxCodes[i]);
			}
			sendMList.add(toSendDatail(offlineLogRequest));
			offlineLogs.add(requestToOffline(offlineLogRequest, Constants.RESULT_SUCCESS));
			operationLogs.add(RequestConvertOperationLog(offlineLogRequest));
		}

		if (sendMList.size() > 0) {
			this.log.info("OfflineDeliveryServiceImpl --> 开始写入发货信息");
			this.deliveryService.dellDeliveryMessage(SendBizSourceEnum.OFFLINE_OLD_SEND, sendMList);
			this.addOfflineLog(offlineLogs);
			this.addOperationLogs(operationLogs);    //记录离线发货操作日志
			this.log.info("OfflineDeliveryServiceImpl --> 结束写入发货信息");
			return Constants.RESULT_SUCCESS;
		}

		return Constants.RESULT_FAIL;
	}

	private void addOfflineLog(List<OfflineLog> offlineLogs) {
		if (offlineLogs != null && offlineLogs.size() > 0) {
			for (OfflineLog offlineLog : offlineLogs) {
				this.offlineLogService.addOfflineLog(offlineLog);
			}
		}
	}

	private boolean checkBaseSite(Integer receiveSiteCode) {
		if (receiveSiteCode == null || receiveSiteCode.equals(0)) {
			return Boolean.FALSE;
		}

		BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
				.getBaseSiteBySiteId(receiveSiteCode);

		if (baseStaffSiteOrgDto == null
				|| baseStaffSiteOrgDto.getSiteCode() == null) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	private SendM toSendDatail(OfflineLogRequest offlineLogRequest) {

		SendM sendM = new SendM();
		sendM.setBoxCode(offlineLogRequest.getBoxCode());
		sendM.setCreateSiteCode(offlineLogRequest.getSiteCode());
		sendM.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
		sendM.setCreateUserCode(offlineLogRequest.getUserCode());
		sendM.setSendType(offlineLogRequest.getBusinessType());
		sendM.setCreateUser(offlineLogRequest.getUserName());
		sendM.setSendCode(offlineLogRequest.getBatchCode());
		sendM.setCreateTime(new Date());
		sendM.setOperateTime(DateHelper.parseDate(offlineLogRequest
				.getOperateTime(), Constants.DATE_TIME_MS_FORMAT));
		sendM.setYn(1);
		sendM.setTurnoverBoxCode(offlineLogRequest.getTurnoverBoxCode());

		return sendM;
	}

	private OfflineLog requestToOffline(OfflineLogRequest offlineLogRequest, Integer status) {
		if (offlineLogRequest == null) {
			return null;
		}
		OfflineLog offlineLog = new OfflineLog();
		offlineLog.setBoxCode(offlineLogRequest.getBoxCode());
		offlineLog.setBusinessType(offlineLogRequest.getBusinessType());
		offlineLog.setCreateSiteCode(offlineLogRequest.getSiteCode());
		offlineLog.setCreateSiteName(offlineLogRequest.getSiteName());
		offlineLog.setCreateUser(offlineLogRequest.getUserName());
		offlineLog.setCreateUserCode(offlineLogRequest.getUserCode());
		offlineLog.setExceptionType(offlineLogRequest.getExceptionType());
		offlineLog.setOperateTime(DateHelper.parseDate(offlineLogRequest
				.getOperateTime(), Constants.DATE_TIME_MS_FORMAT));
		offlineLog.setOperateType(offlineLogRequest.getOperateType());

		offlineLog.setWaybillCode(offlineLogRequest.getWaybillCode());
		offlineLog.setPackageCode(offlineLogRequest.getPackageCode());
		offlineLog.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
		offlineLog.setSealBoxCode(offlineLogRequest.getSealBoxCode());
		offlineLog.setSendCode(offlineLogRequest.getBatchCode());
		offlineLog.setSendUserCode(offlineLogRequest.getSendUserCode());
		offlineLog.setSendUser(offlineLogRequest.getSendUser());
		offlineLog.setShieldsCarCode(offlineLogRequest.getShieldsCarCode());
		offlineLog.setTaskType(offlineLogRequest.getTaskType());
		offlineLog.setVehicleCode(offlineLogRequest.getCarCode());
		offlineLog.setVolume(offlineLogRequest.getVolume());
		offlineLog.setWeight(offlineLogRequest.getWeight());
		offlineLog.setTurnoverBoxCode(offlineLogRequest.getTurnoverBoxCode());

		offlineLog.setStatus(status);

		return offlineLog;
	}

	/**
	 * 添加离线发货操作日志记录
	 * @param operationLogs
	 */
	private void addOperationLogs(List<OperationLog> operationLogs){
		if (operationLogs != null && operationLogs.size() > 0) {
			for(OperationLog operationLog : operationLogs){
				operationLogService.add(operationLog);
			}
		}
	}

	/**
	 * 将OfflineLogRequest转化为OperationLog（操作日志）
	 * @param offlineLogRequest
	 * @return
	 */
	private OperationLog RequestConvertOperationLog(OfflineLogRequest offlineLogRequest) {
		OperationLog operationLog = new OperationLog();
		operationLog.setBoxCode(offlineLogRequest.getBoxCode());
		operationLog.setWaybillCode(offlineLogRequest.getWaybillCode());
		operationLog.setPackageCode(offlineLogRequest.getPackageCode());
		operationLog.setSendCode(offlineLogRequest.getBatchCode());
		operationLog.setCreateSiteCode(offlineLogRequest.getSiteCode());
		operationLog.setCreateSiteName(offlineLogRequest.getSiteName());
		operationLog.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
		operationLog.setCreateUser(offlineLogRequest.getUserName());
		operationLog.setCreateUserCode(offlineLogRequest.getUserCode());
		operationLog.setCreateTime(new Date());

        //因为后续的操作会根据操作时间冲掉这里的记录，所以这里将时间的long值减一，以确保不会被冲掉
        Date OperateTime = DateHelper.parseDate(offlineLogRequest.getOperateTime(), Constants.DATE_TIME_MS_FORMAT);
        OperateTime.setTime(OperateTime.getTime()-1);
        operationLog.setOperateTime(OperateTime);

		operationLog.setLogType(OperationLog.LOG_TYPE_SEND_DELIVERY);
		operationLog.setRemark(OFFLINE_DELIVERY_REMARK);
		return operationLog;
	}

}
