package com.jd.bluedragon.distribution.partnerWaybill.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.WayBillCodeRequest;
import com.jd.bluedragon.distribution.partnerWaybill.dao.PartnerWaybillDao;
import com.jd.bluedragon.distribution.partnerWaybill.domain.PartnerWaybill;
import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.ThridPackageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("partnerWaybillService")
public class PartnerWaybillServiceImpl implements PartnerWaybillService {

	private final Logger log = LoggerFactory.getLogger(PartnerWaybillServiceImpl.class);

	@Autowired
	private PartnerWaybillDao partnerWaybillDao;

	@Autowired
	WaybillPackageApi waybillPackageApi;

	@Autowired
	private TaskService taskService;

	/**
	 * 添加运单关联包裹信息
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean doCreateWayBillCode(PartnerWaybill partnerWaybill) {
		Date date = new Date();
		partnerWaybill.setUpdateTime(date);
		int reslut = this.partnerWaybillDao.checkHas(partnerWaybill);
		if (reslut == 0) {
			// 不存在
			this.partnerWaybillDao.add(PartnerWaybillDao.namespace,
					partnerWaybill);
			// 添加到task表
			this.taskService.add(this.toTask(JsonHelper.toJson(partnerWaybill),
					partnerWaybill));
		}
		return true;
	}

	private Task toTask(String jsonVal, PartnerWaybill wayBillCode) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_INSPECTION);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setType(Task.TASK_TYPE_PARTNER_WAY_BILL_NOTIFY);
		task.setKeyword1(wayBillCode.getWaybillCode());
		task.setKeyword2(wayBillCode.getPartnerWaybillCode());
		task.setBody(jsonVal);
		String ownSign = BusinessHelper.getOwnSign();
		task.setOwnSign(ownSign);
		return task;
	}

	/**
	 * 处理运单号关联包裹信息
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public boolean doWayBillCodesProcessed(List<Task> taskList) {
		this.log.info("开始处理运单号关联包裹数据");
		// 接口参数
		List<ThridPackageDto> params = new ArrayList<ThridPackageDto>();
		Map<Long, Task> taskMap = new HashMap<Long, Task>();
		for (Task task : taskList) {
			this.taskService.doLock(task);
			try {
				PartnerWaybill partnerWaybill = JsonHelper.fromJson(
						task.getBody(), PartnerWaybill.class);
				ThridPackageDto to = new ThridPackageDto();
				to.setBizId(task.getId());
				to.setPackageBarcode(partnerWaybill.getPackageBarcode());
				to.setVendorBarcode(partnerWaybill.getPartnerWaybillCode());
				to.setWaybillCode(partnerWaybill.getWaybillCode());
				params.add(to);
				taskMap.put(task.getId(), task);
			} catch (Exception e) {
				this.taskService.doError(task);
				this.log.error("处理收货任务失败[taskId={}]异常信息为：{}",task.getId(), e.getMessage(), e);
				continue;
			}
		}
		if (!params.isEmpty()) {
			try {
				BaseEntity<Map<Long, Boolean>> baseEntity = this.waybillPackageApi
						.updateThirdOrder(params);
				if (baseEntity.getResultCode() == Constants.INTERFACE_CALL_SUCCESS) {
					// 接口调用成功
					Map<Long, Boolean> data = baseEntity.getData();
					Iterator<Map.Entry<Long, Task>> iter = taskMap.entrySet()
							.iterator();
					while (iter.hasNext()) {
						Map.Entry<Long, Task> entry = iter.next();
						Task task = entry.getValue();
						Boolean bl = data.get(task.getId());
						if (bl) {
							this.taskService.doDone(task);
						} else {
							this.log.warn("接口处理失败:data.get(taskId)=false:{}", baseEntity.getMessage());
							this.taskService.doRevert(task);
						}
					}
					this.log.info("处理运单号关联包裹数据完成");
				} else {
					this.log.warn("调用接口失败:resultCode!=1:{}", baseEntity.getMessage());
					this.unLockTask(taskMap);
				}
			} catch (Exception e) {
				this.log.error("调用接口异常:{}" , e.getMessage(), e);
				this.unLockTask(taskMap);
			}
		}
		return true;
	}

	private void unLockTask(Map<Long, Task> taskMap) {
		Iterator<Map.Entry<Long, Task>> iter = taskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Long, Task> entry = iter.next();
			Task task = entry.getValue();
			this.taskService.doRevert(task);
		}
	}

	/**
	 * 解析json数据 json 串to WayBillCode 对象
	 */
	public PartnerWaybill doParse(Task task) {
		String jsonReceive = task.getBody();
		this.log.info("运单号关联包裹json数据：{}" , jsonReceive);
		List<WayBillCodeRequest> wayBillCodeRequests = Arrays.asList(JsonHelper
				.jsonToArray(jsonReceive, WayBillCodeRequest[].class));
		WayBillCodeRequest way = wayBillCodeRequests.get(0);
		this.log.info("运单号关联包裹json数据转化后：{}" , way);
		PartnerWaybill wayBillCode = new PartnerWaybill();
		// 第三方运单号
		wayBillCode.setPartnerWaybillCode(way.getWaybillCode() == null ? ""
				: way.getWaybillCode());
		// 判断包裹号还是京东运单号
		if (WaybillUtil.isPackageCode(way.getPackageBarcode())) {
			// 包裹号
			String pkCode = way.getPackageBarcode();
			wayBillCode.setPackageBarcode(pkCode);
			// 京东运单号
			wayBillCode.setWaybillCode(WaybillUtil.getWaybillCode(pkCode));
		} else {
			// 包裹号
			wayBillCode.setPackageBarcode("");
			// 京东运单号
			wayBillCode.setWaybillCode(way.getPackageBarcode() == null ? ""
					: way.getPackageBarcode());
		}
		wayBillCode.setCreateSiteCode(way.getSiteCode());
		wayBillCode
				.setCreateTime(DateHelper.getSeverTime(way.getOperateTime()));
		wayBillCode
				.setUpdateTime(DateHelper.getSeverTime(way.getOperateTime()));
		wayBillCode.setCreateUser(way.getUserName());
		wayBillCode.setCreateUserCode(way.getUserCode());
		wayBillCode.setPartnerSiteCode(way.getPartnerSiteCode());
		return wayBillCode;
	}

}
