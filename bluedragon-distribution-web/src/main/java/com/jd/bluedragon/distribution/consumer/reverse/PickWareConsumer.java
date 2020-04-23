package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.reverse.domain.PickWare;
import com.jd.bluedragon.distribution.reverse.service.PickWareService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.dto.BaseDmsStoreDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pickWareConsumer")
public class PickWareConsumer extends MessageBaseConsumer {  
	 
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	PickWareService pickWareService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private BasicSafInterfaceManager basicSafInterfaceManager;
	
	public void consume(Message message) throws Exception {
		if (message == null || message.getText() == null) {
			return;
		}
		String messageContent = message.getText();
        this.log.info("[备件库售后取件单-交接/拆包]messageContent：{}", messageContent);
        PickWare pickWare = JsonHelper.fromJson(messageContent, PickWare.class);
        pickWare.setPickwareTime(DateHelper.parseDateTime(pickWare.getOperateTime()));
       
        pickWareService.add(pickWare);
        
        //增加全程跟踪
        WaybillStatus tWaybillStatus = new WaybillStatus();
		tWaybillStatus.setOperator(pickWare.getOperator());
		tWaybillStatus.setOperateTime(pickWare.getPickwareTime());
		tWaybillStatus.setWaybillCode(pickWare.getPackageCode());
		tWaybillStatus.setPackageCode(pickWare.getPackageCode());
		
		//备件库售后取件单-交接/拆包 不经过分拣中心操作, 所以起始站点未知
		tWaybillStatus.setOperatorId(-1);
		tWaybillStatus.setCreateSiteCode(0);
		tWaybillStatus.setCreateSiteName("【备件库售后交接拆包】");
		tWaybillStatus.setCreateSiteType(-1);
		 
		//获得备件库操作单位
		Integer cky2 = pickWare.getCky2();
		Integer orgId = pickWare.getOrgId();
		Integer storeId = pickWare.getStoreId();
		tWaybillStatus.setOrgId(orgId);
		
		BaseResult<BaseDmsStoreDto> dtoResult = basicSafInterfaceManager.getDmsInfoByStoreInfo(cky2, orgId, storeId);
		 if(dtoResult.getData()!=null){
			 tWaybillStatus.setReceiveSiteCode(dtoResult.getData().getDmsId());
			 tWaybillStatus.setReceiveSiteName(dtoResult.getData().getDmsName());
			 tWaybillStatus.setReceiveSiteType(-1);
		 }else{
			 tWaybillStatus.setReceiveSiteCode(0);
			 tWaybillStatus.setReceiveSiteName("0");
			 tWaybillStatus.setReceiveSiteType(-1);
		 }

		if (pickWare.getCanReceive() == 0){
			tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_AMS_BH);
			taskService.add(this.toTask(tWaybillStatus));
		}
		else{
			tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
			taskService.add(this.toTaskStatus(tWaybillStatus));
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
		task.setKeyword2(String.valueOf(tWaybillStatus.getOperateType()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));
		task.setType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
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
