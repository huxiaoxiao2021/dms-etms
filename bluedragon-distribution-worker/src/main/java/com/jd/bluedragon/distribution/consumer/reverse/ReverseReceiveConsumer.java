package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.Eclp2BdReceiveDetail;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.SparsModel;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private SendDatailDao sendDatailDao;
	
	@Autowired
    private ReverseSpareDao sparedao;
	
	@Autowired
    private BaseMajorManager baseMajorManager;
	
    @Autowired
    private ReversePrintService reversePrintService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Override
    @JProfiler(jKey = "reverseReceiveConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
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
			if(StringUtils.isNotBlank(xrequest.getWaybillCode())){
				//运单号字段非空  用运单号处理
				//Fixme 找时间统一orderId  packageCode waybillCode 含义 避免后续开发人员看不懂
				reverseReceive.setPackageCode(xrequest.getWaybillCode());
				reverseReceive.setOrderId(xrequest.getWaybillCode());
			}else{
				reverseReceive.setPackageCode(xrequest.getOrderId());
				reverseReceive.setOrderId(xrequest.getOrderId());
			}
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
				if(StringUtils.isNotBlank(jrequest.getReceiveTime())){
					date = sdf.parse(jrequest.getReceiveTime());
				}
				if(reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8 ){ //处理报文 操作人字段
					date = sdf.parse(jrequest.getOperateTime());
					reverseReceive.setOperatorName(jrequest.getOperaterName());
					reverseReceive.setOrderId(jrequest.getWaybillCode());
					reverseReceive.setPackageCode(jrequest.getWaybillCode());
				}
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
		
		if (reverseReceive.getReceiveType() == 5){//如果是开放平台订单
			InvokeResult<String> newWaybilCode = reversePrintService.getNewWaybillCode(reverseReceive.getOrderId(), false);
			if (StringHelper.isNotEmpty(newWaybilCode.getData())) {
				tsendDatail.setWaybillCode(newWaybilCode.getData());
				List<SendDetail> sendDatailistMcs = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
				if (sendDatailistMcs != null && !sendDatailistMcs.isEmpty()) {
					reverseReceive.setOrderId(sendDatailistMcs.get(0).getWaybillCode());
				}
			}
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
		if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5|| reverseReceive.getReceiveType() == 4 || reverseReceive.getReceiveType() == 6 || reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8) {
			String sendCode = "";
			if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5 || reverseReceive.getReceiveType() == 6) {
				this.logger.info("逆向添加全称跟踪sendCode" + xrequest.getSendCode());
				sendCode = xrequest.getSendCode();
			} else if ((reverseReceive.getReceiveType() == 4 || reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8) && jrequest != null) {
				this.logger.info("逆向添加全称跟踪sendCode" + jrequest.getSendCode());
				sendCode = jrequest.getSendCode();
				if(reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8){
					//ECLP退备件库时
					reverseReceive.setOrderId(jrequest.getWaybillCode());
				}else{
					reverseReceive.setOrderId(reverseReceive.getPackageCode());
				}

			}
			if (reverseReceive.getReceiveType() == 3) {
				List<ReverseSpare> tReverseSpareList = sparedao.queryBySpareTranCode(sendCode);
				if (tReverseSpareList != null && tReverseSpareList.size()>0) {
					sendCode = tReverseSpareList.get(0).getSendCode();
				}else{
					return;
				}
			}

			Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
			if(receiveSiteCode!=null){
				WaybillStatus tWaybillStatus = new WaybillStatus();
				tWaybillStatus.setOperator(reverseReceive.getOperatorName());
				tWaybillStatus.setOperateTime(reverseReceive.getReceiveTime());
				tWaybillStatus.setWaybillCode(reverseReceive.getOrderId());
				tWaybillStatus.setPackageCode(reverseReceive.getOrderId());
				tWaybillStatus.setCreateSiteCode(receiveSiteCode);
				tWaybillStatus.setOperatorId(-1);
				tWaybillStatus.setReceiveSiteCode(receiveSiteCode);
				BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
		        if(bDto!=null ){
		        	tWaybillStatus.setCreateSiteName(bDto.getSiteName());
		        	tWaybillStatus.setReceiveSiteName(bDto.getSiteName());
		        	tWaybillStatus.setCreateSiteType(bDto.getSiteType());
		        	tWaybillStatus.setReceiveSiteType(bDto.getSiteType());
		        }

				if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5 || reverseReceive.getReceiveType() == 6) {
					tWaybillStatus.setSendCode(xrequest.getSendCode());
				} else if ((reverseReceive.getReceiveType() == 4 || reverseReceive.getReceiveType() == 7 ||reverseReceive.getReceiveType() == 8) && jrequest != null ) {
					tWaybillStatus.setSendCode(jrequest.getSendCode());
				}

				if(reverseReceive.getReceiveType() == 7){
					//ECLP退备件库时 0 代表驳回  1代表收货 2 代表部分收货(仓配)
					if (reverseReceive.getCanReceive() == 0){
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
						taskService.add(this.toTask(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 1) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 2) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						tWaybillStatus.setReturnFlag(WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_HALF);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					}else {
						return;
					}
				}else if(reverseReceive.getReceiveType() == 8){
					//仓配发包裹维度的全程跟踪
					sendTraceAndUpdateWaybillStatue(jrequest,tWaybillStatus);
				}else if(reverseReceive.getReceiveType()==5 && reverseReceive.getCanReceive() == 2){
		        	//ECLP退仓半退的逻辑
					tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
					tWaybillStatus.setReturnFlag(WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_HALF);
					taskService.add(this.toTaskStatus(tWaybillStatus));
				}else{
					if (reverseReceive.getCanReceive() == 0){
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
						taskService.add(this.toTask(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 1) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 2) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_JJREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					}
				}
			}
		}

	}

	/**
	 * 整单收货/部分收货发全程跟踪
	 * @param jrequest
	 * @param type
	 * @param tWaybillStatus
	 */
	private void sendTraceAndUpdateWaybillStatue(ReverseReceiveRequest jrequest, WaybillStatus tWaybillStatus) {

		//存放   备件条码:包裹号
		Map<String,String> sparePackCodeMap = new HashMap<String,String>();
		//回传包裹数据   0拒收 1全收 2半收 -1表示此次没回传
		Map<String,Integer> packageReceiveMap = new HashMap<>();
		//
		String waybillCode = jrequest.getWaybillCode();

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryWaybillE(false);
		wChoice.setQueryWaybillM(false);
		wChoice.setQueryGoodList(true);
		wChoice.setQueryPackList(true);
		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoiceNoCache(waybillCode,wChoice);
		if(baseEntity != null && baseEntity.getData() != null &&
				baseEntity.getData().getGoodsList() != null && baseEntity.getData().getGoodsList().size() > 0){
			List<Goods> goodsList = baseEntity.getData().getGoodsList();
			for (Goods goods : goodsList){
				List<SparsModel> spareList = goods.getSpareList();
				for(SparsModel sparsModel : spareList){
					sparePackCodeMap.put(sparsModel.getSpareCode(),sparsModel.getPackBarcode());
				}
			}
		}

		for(String key : sparePackCodeMap.keySet()){
			packageReceiveMap.put(sparePackCodeMap.get(key),Integer.valueOf(-1));
		}

		for(Eclp2BdReceiveDetail eclp2BdReceiveDetail : jrequest.getDetailList()) {

			String spareCode = eclp2BdReceiveDetail.getBatchNo();
			String _packNo = sparePackCodeMap.get(spareCode);
			Integer quantity = eclp2BdReceiveDetail.getQuantity(); //只有 0 1  0没收 1收了
			//只有是半收的时候才需要去判断
			if (jrequest.getCanReceive().equals(Integer.valueOf(2))) {
				if (packageReceiveMap.get(_packNo).equals(Integer.valueOf(-1))) {
					packageReceiveMap.put(_packNo, quantity);
				} else {
					if (!packageReceiveMap.get(_packNo).equals(Integer.valueOf(2)) && !packageReceiveMap.get(_packNo).equals(quantity)) {
						packageReceiveMap.put(_packNo, Integer.valueOf(2));
					}
				}
			} else {
				// 收货  和 拒收 直接给
				packageReceiveMap.put(_packNo, jrequest.getCanReceive());
			}
		}


		for(String key : packageReceiveMap.keySet()){

			tWaybillStatus.setPackageCode(key);
			if(packageReceiveMap.get(key).equals(Integer.valueOf(0))){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
				taskService.add(this.toTask(tWaybillStatus));
			}else if(packageReceiveMap.get(key).equals(Integer.valueOf(1))){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
				taskService.add(this.toTaskStatus(tWaybillStatus));
			}else if(packageReceiveMap.get(key).equals(Integer.valueOf(2))){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
				tWaybillStatus.setReturnFlag(WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_HALF);
				taskService.add(this.toTaskStatus(tWaybillStatus));
			}
		}




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
