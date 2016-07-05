package com.jd.bluedragon.core.message.consumer.reverse;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.proxy.Profiler;

@Service("reverseReceiveConsumer")
public class ReverseReceiveConsumer extends MessageBaseConsumer {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseRejectService reverseRejectService;

	@Autowired
	private ReverseReceiveService reverseReceiveService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
    private SendMDao sendMDao;
	
	@Autowired
    private SendDatailDao sendDatailDao;
	
	@Autowired
    private ReverseSpareDao sparedao;
	
	@Autowired
    private BaseMajorManager baseMajorManager;

	@Autowired
	WaybillSyncApi waybillSyncApi;

	@Override
	public void consume(Message message) {

		String messageContent = message.getText();
		this.logger.info("逆向收货消息messageContent：" + messageContent);

		ReverseReceiveRequest jrequest = null;
		ReceiveRequest xrequest = null;
		ReverseReceive reverseReceive = new ReverseReceive();

		if (XmlHelper.isXml(messageContent, ReceiveRequest.class, null)) {
			xrequest = (ReceiveRequest) XmlHelper.toObject(messageContent, ReceiveRequest.class);
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
			}
			reverseReceive.setRejectMessage(xrequest.getRejectMessage());

		} else if (JsonHelper.isJson(messageContent, ReverseReceiveRequest.class)) {
			jrequest = JsonHelper.fromJson(messageContent, ReverseReceiveRequest.class);
			this.logger.info("逆向收货消息ReverseReceiveRequest：" + jrequest.toString());

			Date date = null;
			try {
				BeanHelper.copyProperties(reverseReceive, jrequest);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = sdf.parse(jrequest.getReceiveTime());
				reverseReceive.setReceiveTime(date);
			} catch (Exception e) {
				this.logger.error("逆向收货消息转换失败：" + e);
			}
		}
		try {
			//业务流程监控, 各系统埋点
			Map<String, String> data = new HashMap<String, String>();
			data.put("orderId", reverseReceive.getPackageCode());
			data.put("outboundNo", reverseReceive.getPackageCode());
			data.put("packageCode", reverseReceive.getPackageCode());
			
			if (reverseReceive.getReceiveType() == 1){//仓储
				Profiler.bizNode("bd_dms_reverse_receive_wms", data);
			}else if (reverseReceive.getReceiveType() == 2){//售后系统
				Profiler.bizNode("Reverse_mq_dms2stock", data);
			}else if (reverseReceive.getReceiveType() == 3){//备件库系统
				Profiler.bizNode("Reverse_mq_ams2dms", data);
			}
		} catch (Exception e) {
			this.logger.error("推送UMP发生异常.", e);
		}
		
		//添加订单处理，判断是否是T单 2016-1-8
		SendDetail tsendDatail = new SendDetail();
		tsendDatail.setSendCode(reverseReceive.getSendCode());
		if (reverseReceive.getReceiveType() == 3) {//如果是备件库的,则找到其真正的send_code
			List<ReverseSpare> tReverseSpareList = sparedao.queryBySpareTranCode(xrequest.getSendCode());
			if (tReverseSpareList != null && tReverseSpareList.size()>0) {
				String sendCode = tReverseSpareList.get(0).getSendCode();
				tsendDatail.setSendCode(sendCode);
			}
		}
		
		tsendDatail.setWaybillCode(Constants.T_WAYBILL + reverseReceive.getOrderId());
		List<SendDetail> sendDatailist = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
		if (sendDatailist != null && !sendDatailist.isEmpty()){
			reverseReceive.setOrderId(Constants.T_WAYBILL + reverseReceive.getOrderId());
		}
		
		this.reverseReceiveService.aftersaleReceive(reverseReceive);

		// 对于备件库系统,接受拒收消息后自动处理驳回接口
		if (reverseReceive.getReceiveType() == 3 && reverseReceive.getCanReceive() == 0) {
			ReverseReject reverseReject = new ReverseReject();
			reverseReject.setBusinessType(reverseReceive.getReceiveType());
			reverseReject.setPackageCode(reverseReceive.getPackageCode());
			reverseReject.setOrderId(reverseReceive.getPackageCode());

			if(null != xrequest.getOrgId()){
				reverseReject.setOrgId(Integer.parseInt(xrequest.getOrgId()));
			}
			if(null != xrequest.getStoreId()){
				reverseReject.setStoreId(Integer.parseInt(xrequest.getStoreId()));
			}

			reverseReject.setOperateTime(reverseReceive.getReceiveTime());
			reverseReject.setOperator(reverseReceive.getOperatorName());
			reverseReject.setOperatorCode(reverseReceive.getOperatorId());

			this.reverseRejectService.reject(reverseReject);
		}

		//添加全称跟踪
		if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5|| reverseReceive.getReceiveType() == 4) {
			String sendCode = "";
			if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5) {
				this.logger.info("逆向添加全称跟踪sendCode" + xrequest.getSendCode());
				sendCode = xrequest.getSendCode();
			} else if (reverseReceive.getReceiveType() == 4) {
				this.logger.info("逆向添加全称跟踪sendCode" + jrequest.getSendCode());
				sendCode = jrequest.getSendCode();
				reverseReceive.setOrderId(reverseReceive.getPackageCode());
			}
			if (reverseReceive.getReceiveType() == 3) {
				List<ReverseSpare> tReverseSpareList = sparedao.queryBySpareTranCode(sendCode);
				if (tReverseSpareList != null && tReverseSpareList.size()>0) {
					sendCode = tReverseSpareList.get(0).getSendCode();
				}else{
					return;
				}
			}
			SendM tSendM = sendMDao.selectBySendCode(sendCode);
			if(tSendM!=null){
				WaybillStatus tWaybillStatus = new WaybillStatus();
				tWaybillStatus.setOperator(reverseReceive.getOperatorName());
				tWaybillStatus.setOperateTime(reverseReceive.getReceiveTime());
				tWaybillStatus.setWaybillCode(reverseReceive.getOrderId());
				tWaybillStatus.setPackageCode(reverseReceive.getOrderId());
				tWaybillStatus.setCreateSiteCode(tSendM.getReceiveSiteCode());
				tWaybillStatus.setOperatorId(-1);
				tWaybillStatus.setReceiveSiteCode(tSendM.getReceiveSiteCode());
				BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(tSendM.getReceiveSiteCode());
		        if(bDto!=null ){
		        	tWaybillStatus.setCreateSiteName(bDto.getSiteName());
		        	tWaybillStatus.setReceiveSiteName(bDto.getSiteName());
		        	tWaybillStatus.setCreateSiteType(bDto.getSiteType());
		        	tWaybillStatus.setReceiveSiteType(bDto.getSiteType());
		        }

				if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5) {
					tWaybillStatus.setSendCode(xrequest.getSendCode());
				} else if (reverseReceive.getReceiveType() == 4) {
					tWaybillStatus.setSendCode(jrequest.getSendCode());
				}
				if (reverseReceive.getCanReceive() == 0){
					tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
					taskService.add(this.toTask(tWaybillStatus));
				} else if (reverseReceive.getCanReceive() == 1) {
					// 维修外单的逆向收货,需要调妥投的接口(接口不仅妥投,且附带全程跟踪的功能)
					if (BusinessHelper.isMCSCode(reverseReceive.getPackageCode())) {
						this.confirmReceive(reverseReceive);
					} else { //正常的逆向收货,只发全程跟踪
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					}
				} else if (reverseReceive.getCanReceive() == 2) {
					tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_JJREVERSE);
					taskService.add(this.toTaskStatus(tWaybillStatus));
				}
			}
		}

	}

	/**
	 * 调运单接口,妥投运单
	 * @param reverseReceive
	 */
	private void confirmReceive(ReverseReceive reverseReceive) {
		List<WaybillSyncParameter> waybillSyncParameterList = new ArrayList<WaybillSyncParameter>();
		WaybillSyncParameter waybillSyncParameter = new WaybillSyncParameter();
		waybillSyncParameter.setOperatorCode(reverseReceive.getWaybillCode());
		waybillSyncParameter.setOperatorId(reverseReceive.getOperatorId() == null ? -1 : Integer.parseInt(reverseReceive.getOperatorId()));
		waybillSyncParameter.setOperatorName(reverseReceive.getOperatorName());
		waybillSyncParameter.setOperateTime(reverseReceive.getReceiveTime());
		waybillSyncParameter.setOrgId(reverseReceive.getOrgId());
		waybillSyncParameterList.add(waybillSyncParameter);
		BaseEntity<List<String>> result = waybillSyncApi.batchUpdateWaybillByOperatorCode(waybillSyncParameterList, WaybillStatus.WAYBILL_STATUS_SHREVERSE);
		logger.info(MessageFormat.format("维修外单妥投成功:{0}", JsonHelper.toJson(waybillSyncParameterList)));
	}

	private Task toTask(WaybillStatus tWaybillStatus) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(tWaybillStatus.getOperateType()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		StringBuffer fingerprint = new StringBuffer();
		fingerprint
				.append(tWaybillStatus.getCreateSiteCode())
				.append("__")
				.append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
						: tWaybillStatus.getReceiveSiteCode())).append("_")
				.append(tWaybillStatus.getOperateType()).append("_")
				.append(tWaybillStatus.getWaybillCode()).append("_")
				.append(tWaybillStatus.getOperateTime()).append("_")
				.append(tWaybillStatus.getSendCode());
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		return task;
	}
	
	private Task toTaskStatus(WaybillStatus tWaybillStatus) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_WAYBILL);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(tWaybillStatus.getWaybillCode()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));

		Integer operateType = tWaybillStatus.getOperateType();
		if (operateType != null) {
			if (operateType.intValue() ==  WaybillStatus.WAYBILL_STATUS_SHREVERSE) {
				task.setType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
			} else if (operateType.intValue() ==  WaybillStatus.WAYBILL_STATUS_JJREVERSE) {
				task.setType(WaybillStatus.WAYBILL_STATUS_JJREVERSE);
			}
		}

		task.setOwnSign(BusinessHelper.getOwnSign());
		StringBuffer fingerprint = new StringBuffer();
		fingerprint
				.append(tWaybillStatus.getCreateSiteCode())
				.append("__")
				.append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
						: tWaybillStatus.getReceiveSiteCode())).append("_")
				.append(tWaybillStatus.getOperateType()).append("_")
				.append(tWaybillStatus.getWaybillCode()).append("_")
				.append(tWaybillStatus.getOperateTime()).append("_")
				.append(tWaybillStatus.getSendCode());
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		return task;
	}
}
