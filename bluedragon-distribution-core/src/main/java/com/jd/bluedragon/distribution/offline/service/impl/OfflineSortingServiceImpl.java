package com.jd.bluedragon.distribution.offline.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.offline.service.OfflineSortingService;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 离线分拣service
 *
 * @author libin
 *
 */
@Service
public class OfflineSortingServiceImpl implements OfflineSortingService {
	private static Log log = LogFactory.getLog(OfflineSortingServiceImpl.class);
	@Autowired
	private BaseService baseService;
	@Autowired
	private BoxService boxService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SealBoxService sealBoxService;
	@Autowired
	WaybillPackageManager waybillPackageManager;
	@Autowired
	private InspectionDao inspectionDao;
	@Autowired
	private OperationLogService operationLogService;    //操作日志service

	public static final String OFFLINE_SORTING_REMARK = "离线分拣";    //离线分拣操作备注

	/**
	 * 插入分拣任务表
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int insert(OfflineLogRequest request) {
		int n = 0;
		if (StringUtils.isEmpty(request.getPackageCode())) {// 按照运单进行分拣
			String waybillCode = request.getWaybillCode();
			BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager
					.getPackListByWaybillCode(waybillCode);
			if (baseEntity != null) {
				List<DeliveryPackageD> packageList = baseEntity.getData();
				if (packageList != null && packageList.size() > 0) {
					for (DeliveryPackageD deliveryPackageD : packageList) {
						request.setPackageCode(deliveryPackageD
								.getPackageBarcode());
						Task task = this.toTask(request);
						if (task == null) {
							request.setPackageCode(null);
							return 0;
						}
						int m = this.taskService.add(task);
						addOperationLog(request);    //添加离线分拣操作日志
						request.setPackageCode(null);
						n += m;
					}

				}else{
					log.error("离线分拣：根据运单号获取包裹，结果为空，运单号："+waybillCode);
				}
			} else {
				log.error("离线分拣数据处理失败，通过运单查询包裹信息为空;" + request);
				return 0;
			}

		} else {
			Task task = this.toTask(request);
			if (task == null) {
				log.error("离线分拣数据转task失败:"+request);
				return 0;
			}
			n = this.taskService.add(task);
			addOperationLog(request);    //添加离线分拣操作日志
			if(n==0){
				log.error("离线分拣:执行新增task返回结果为0.");
			}

		}
		return n;
	}

	/**
	 * 封箱
	 *
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int insertSealBox(OfflineLogRequest request) {
		int n = this.sealBoxService.addSealBox(toSealBox(request));
		return n;
	}

	/**
	 * 超区处理,取消分拣，取消验货（可有可无） 可以按照运单号或者包裹号进行超区退回, 往分拣任务表插入一条取消分拣的任务
	 *
	 * @return
	 */
	public int exceedArea(OfflineLogRequest request) {
		if (!checkExceedData(request)) {
			return 0;
		}
		/*
		 * Integer siteCode = request.getSiteCode(); Integer userCode =
		 * request.getUserCode(); String userName = request.getUserName();
		 * Sorting sorting = new Sorting(); sorting.setType(30);
		 * sorting.setWaybillCode(waybillCode);
		 * sorting.setPackageCode(packageCode);
		 * sorting.setUpdateUserCode(userCode); sorting.setUpdateUser(userName);
		 * sorting.setCreateSiteCode(siteCode); Boolean result =
		 * this.sortingService.canCancelSorting(sorting); if (result) { return
		 * 1; } else { throw new
		 * RuntimeException("离线执行超区退回时，未更新成功，可能分拣数据还未到业务表。"); }
		 */

		int n = this.taskService.add(this.exceedAreaTaskToSortingTask(request));
		return n;

	}


	public int cancelSorting(OfflineLogRequest request){
		int n = this.taskService.add(this.exceedAreaTaskToSortingTask(request));
		return n;
	}

	public int cancelThirdInspection(OfflineLogRequest request){
		Box box = boxService.findBoxByCode(request.getBoxCode());
		if (box == null) {
			log.error("根据箱号获取不到箱子对象："+request);
			return 0;
		}
		Inspection inspection = new Inspection();
		inspection.setCreateSiteCode(request.getSiteCode());
		inspection.setUpdateUser(request.getUserName());
		inspection.setUpdateUserCode(request.getUserCode());
		inspection.setInspectionType(request.getBusinessType());
		inspection.setPackageBarcode(request.getPackageCode());
		inspection.setReceiveSiteCode(box.getReceiveSiteCode());
		return this.inspectionDao.updateYnByPackage(inspection);
	}

	/**
	 * 增加离线分拣操作日志
	 * @param offlineLogRequest
	 * @return
	 */
	private void addOperationLog(OfflineLogRequest offlineLogRequest) {
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

		//因为后续的分拣操作会根据操作时间冲掉这里的记录，所以这里将时间的long值减一，以确保不会被冲掉
		Date OperateTime = DateHelper.parseDate(offlineLogRequest.getOperateTime(), Constants.DATE_TIME_MS_FORMAT);
		OperateTime.setTime(OperateTime.getTime()-1);
		operationLog.setOperateTime(OperateTime);

		operationLog.setLogType(OperationLog.LOG_TYPE_SORTING);
		operationLog.setRemark(OFFLINE_SORTING_REMARK);
		operationLogService.add(operationLog);
	}

	private SealBox toSealBox(OfflineLogRequest request) {

		SealBox sealBox = new SealBox();
		sealBox.setBoxCode(request.getBoxCode());
		sealBox.setCode(request.getSealBoxCode());
		sealBox.setCreateSiteCode(request.getSiteCode());
		BaseStaffSiteOrgDto staff = baseService.getBaseStaffByStaffId(request
				.getUserCode());
		sealBox.setCreateUserCode(request.getUserCode());
		if (staff != null) {
			sealBox.setCreateUser(staff.getStaffName());
			sealBox.setUpdateUser(staff.getStaffName());
		}

		sealBox.setUpdateUserCode(request.getUserCode());

		Box box = boxService.findBoxByCode(request.getBoxCode());
		sealBox.setReceiveSiteCode(box.getReceiveSiteCode());

		sealBox.setCreateTime(DateHelper.parseDateTime(request.getOperateTime()));
		return sealBox;
	}

	/**
	 * 将离线work消息体，转化为正式分拣表对象
	 *
	 * @param request
	 * @return
	 */
	private Task toTask(OfflineLogRequest request) {
		Task task = new Task();
		task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING));
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setType(Task.TASK_TYPE_SORTING);

		task.setCreateSiteCode(request.getSiteCode());
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(request.getBoxCode())) {// 大件分拣，没有箱号;
			map.put("boxCode", request.getPackageCode());
			if (request.getReceiveSiteCode() != null) {
				BaseStaffSiteOrgDto recieveSite = baseService
						.getSiteBySiteID(request.getReceiveSiteCode());
				if (recieveSite != null) {
					map.put("receiveSiteCode", recieveSite.getSiteCode());
					map.put("receiveSiteName", recieveSite.getSiteName());
				} else {
					log.error("离线分拣:"+request.getReceiveSiteCode() + "站点不存在."+request);
//					log.error(arg0);
					return null;
				}
				task.setReceiveSiteCode(request.getReceiveSiteCode());
			}
		} else {
			map.put("boxCode", request.getBoxCode());
			Box box = boxService.findBoxByCode(request.getBoxCode());
			if (box != null) {
				map.put("receiveSiteCode", box.getReceiveSiteCode());
				map.put("receiveSiteName", box.getReceiveSiteName());
				task.setReceiveSiteCode(box.getReceiveSiteCode());
			} else {
				log.error("离线分拣:"+request.getBoxCode() + "箱号不存在."+request);
				return null;
			}
		}
		map.put("packageCode", request.getPackageCode());
		map.put("isCancel", 0);
		map.put("businessType", request.getBusinessType());
		map.put("userCode", request.getUserCode());
		map.put("siteCode", request.getSiteCode());
		map.put("operateTime", request.getOperateTime());

		System.out.println("request.getSiteCode()------------------##"+request.getSiteCode()+"##");
		BaseStaffSiteOrgDto site = baseService.getSiteBySiteID(request
				.getSiteCode());
		map.put("siteName", site.getSiteName());
		BaseStaffSiteOrgDto staff = baseService.getBaseStaffByStaffId(request
				.getUserCode());
		if (staff != null) {
			map.put("userName", staff.getStaffName());
		} else {
			log.error("离线分拣:"+request.getUserCode() + "用户ID不存在."+request);
			return null;
		}

		String jsonVal = Constants.PUNCTUATION_OPEN_BRACKET
				+ JsonHelper.toJson(map, false)
				+ Constants.PUNCTUATION_CLOSE_BRACKET;
		task.setBody(jsonVal);
		this.initOthers(jsonVal, task);
		if (StringUtils.isNotBlank(request.getBoxCode())) {
			task.setBoxCode(request.getBoxCode());
		}
		String ownSign = BusinessHelper.getOwnSign();
		task.setOwnSign(ownSign);
		return task;
	}

	private Task exceedAreaTaskToSortingTask(OfflineLogRequest request) {
		Task task = new Task();
		task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING));
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setType(Task.TASK_TYPE_SORTING);

		task.setCreateSiteCode(request.getSiteCode());
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("packageCode", request.getPackageCode());
		map.put("waybillCode", request.getWaybillCode());
		map.put("isCancel", 1);
		map.put("businessType", 30);
		map.put("userCode", request.getUserCode());
		map.put("siteCode", request.getSiteCode());
		map.put("operateTime", request.getOperateTime());
		map.put("boxCode", request.getBoxCode());

		String jsonVal = Constants.PUNCTUATION_OPEN_BRACKET
				+ JsonHelper.toJson(map, false)
				+ Constants.PUNCTUATION_CLOSE_BRACKET;
		task.setBody(jsonVal);
		this.initOthers(jsonVal, task);
		String ownSign = BusinessHelper.getOwnSign();
		task.setOwnSign(ownSign);
		return task;

	}

	private void initOthers(String jsonVal, Task task) {
		String arrayToObject = jsonVal.substring(1, jsonVal.length() - 1);
		Map<String, Object> map = JsonHelper.json2MapNormal(arrayToObject);
		Object packageBarcode = map.get("packageBarcode");
		packageBarcode = null == packageBarcode ? map.get("packageCode")
				: packageBarcode;
		packageBarcode = null == packageBarcode ? map
				.get("packageBarOrWaybillCode") : packageBarcode;
		packageBarcode = null == packageBarcode ? map.get("packOrBox")
				: packageBarcode;

		if (null != map.get("boxCode") && map.get("boxCode") instanceof String
				&& StringUtils.isNotBlank(map.get("boxCode").toString())) {
			task.setBoxCode(map.get("boxCode").toString());
		}

		if (null != packageBarcode
				&& StringUtils.isNotBlank(packageBarcode.toString())) {
			task.setKeyword2(packageBarcode.toString());
		}

		Object businessType = map.get("businessType");
		if (StringUtils.isNotBlank(businessType.toString())) {
			task.setBusinessType(Integer.valueOf(businessType.toString()));
		}

		Object operateTime = map.get("operateTime");
		task.setOperateTime(StringUtils.isNotBlank(operateTime.toString()) ? DateHelper
				.getSeverTime(operateTime.toString()) : new Date());

		if (Task.TASK_TYPE_INSPECTION.equals(task.getType())
				&& Constants.BUSSINESS_TYPE_REVERSE == task.getBusinessType()
				&& null != map.get("operateType")
				&& map.get("operateType") instanceof Integer) {
			try {
				task.setOperateType(Integer.valueOf(map.get("operateType")
						.toString()));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 超区退回数据检查
	 *
	 * @param request
	 * @return
	 */
	private boolean checkExceedData(OfflineLogRequest request) {
		String packageCode = request.getPackageCode();
		String waybillCode = request.getWaybillCode();
		if (StringUtils.isEmpty(packageCode)
				&& StringUtils.isEmpty(waybillCode)) {
			log.error("离线超区退回传入参数不正确：" + request);
			return false;
		}
		return true;
	}

}
