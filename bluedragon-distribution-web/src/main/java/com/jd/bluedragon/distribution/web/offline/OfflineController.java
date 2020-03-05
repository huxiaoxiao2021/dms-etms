package com.jd.bluedragon.distribution.web.offline;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.offline.service.OfflineSortingService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.JsonResult;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/offline")
public class OfflineController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private OfflineLogService offlineLogService;

	@Resource(name = "offlineInspectionService")
	private OfflineService offlineInspectionService;

	@Resource(name = "offlineReceiveService")
	private OfflineService offlineReceiveService;

	@Resource(name = "offlineDeliveryService")
	private OfflineService offlineDeliveryService;

	@Resource(name = "offlineAcarAbillDeliveryService")
	private OfflineService offlineAcarAbillDeliveryService;

	@Autowired
	private OfflineSortingService offlineSortingService;

	@Resource(name = "offlinePopPickupService")
	private OfflineService offlinePopPickupService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

	@Authorization(Constants.DMS_WEB_SORTING_OFFLINELOG_R)
	@RequestMapping(value = "/goListPage", method = RequestMethod.GET)
	public String goListpage(Model model) {

		initSelectObject(null, model);
		ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
		BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
		businessLogProfiler.setSourceSys(1);
		businessLogProfiler.setBizType(Constants.BIZTYPE_URL_CLICK);
		businessLogProfiler.setOperateType(Constants.OFFLINELOG_CLICK);
		JSONObject request=new JSONObject();
		request.put("operatorName",erpUser.getUserName());
		request.put("operatorCode",erpUser.getUserCode());
		businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));
		BusinessLogWriter.writeLog(businessLogProfiler);

        model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		return "offline/offline";
	}

	@Authorization(Constants.DMS_WEB_SORTING_OFFLINELOG_R)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String queryOperateLog(OfflineLog offlineLog,
			Pager<OperationLog> pager, Model model) {
		Map<String, Object> params = ObjectMapHelper.makeObject2Map(offlineLog);
		initSelectObject(params, model);
		// 设置分页对象
		if (pager == null) {
			pager = new Pager<OperationLog>(Pager.DEFAULT_PAGE_NO);
		} else {
			pager = new Pager<OperationLog>(pager.getPageNo(),
					pager.getPageSize());
		}

		params.putAll(ObjectMapHelper.makeObject2Map(pager));

		// 获取总数量
		int totalsize = offlineLogService.totalSizeByParams(params);
		pager.setTotalSize(totalsize);

		log.info("查询符合条件的规则数量：{}", totalsize);

		model.addAttribute("offlinelogs",
				offlineLogService.queryByParams(params));
		model.addAttribute("offlineLogqueryDto", offlineLog);
		model.addAttribute("pager", pager);
        model.addAttribute("oldLogPageTips",uccPropertyConfiguration.getOldLogPageTips());
		return "offline/offline";
	}

	@Authorization(Constants.DMS_WEB_SORTING_OFFLINELOG_R)
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String addOrDetail(Model model, Long offlineLogId) {
		OfflineLog offlineLog = new OfflineLog();
		offlineLog.setOfflineLogId(offlineLogId);
		offlineLog = offlineLogService.findByObj(offlineLog);
		model.addAttribute("offlineLog", offlineLog);
		return "offline/offlineDetail";
	}

	@Authorization(Constants.DMS_WEB_SORTING_OFFLINELOG_R)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public JsonResult save(OfflineLog offlineLog) {
		this.log.info("saveOfflineLog --> 保存 开始");
		try {
			int result=Constants.RESULT_FAIL;
			offlineLog.setStatus(Constants.RESULT_SUCCESS);
			offlineLogService.update(offlineLog);
			offlineLog = offlineLogService.findByObj(offlineLog);
			OfflineLogRequest offlineLogRequest = paseToRequest(offlineLog);
			 
			
			if (Task.TASK_TYPE_RECEIVE.equals(offlineLogRequest.getTaskType())) {
				// 分拣中心收货
				result=this.offlineReceiveService.parseToTask(offlineLogRequest);
			} else if (Task.TASK_TYPE_INSPECTION.equals(offlineLogRequest
					.getTaskType())) {
				// 分拣中心验货
				result=this.offlineInspectionService.parseToTask(offlineLogRequest);
			} else if (Task.TASK_TYPE_SORTING.equals(offlineLogRequest
					.getTaskType())) {
				// 分拣
				result=this.offlineSortingService.insert(offlineLogRequest);
            } else if (Task.TASK_TYPE_SEND_DELIVERY.equals(offlineLogRequest
                    .getTaskType())) {
                // 发货
                result=this.offlineDeliveryService.parseToTask(offlineLogRequest);
            } else if (Task.TASK_TYPE_ACARABILL_SEND_DELIVERY.equals(offlineLogRequest.getTaskType())) {
                // 一车一单发货
                result=this.offlineAcarAbillDeliveryService.parseToTask(offlineLogRequest);
            } else if (Task.TASK_TYPE_SEAL_BOX.equals(offlineLogRequest
            .getTaskType())) {// 分拣封箱
				result=this.offlineSortingService.insertSealBox(offlineLogRequest);
			} else if (Task.TASK_TYPE_OFFLINE_EXCEEDAREA
					.equals(offlineLogRequest.getTaskType())) {// 三方超区退货
				result=this.offlineSortingService.exceedArea(offlineLogRequest);
			} else if (Task.TASK_TYPE_BOUNDARY.equals(offlineLogRequest
					.getTaskType())) {
				// pop上门接货
				result=this.offlinePopPickupService.parseToTask(offlineLogRequest);
			}
			if(result==Constants.RESULT_SUCCESS){
				return new JsonResult(true, "提交成功");
			}else{
				offlineLog.setStatus(Constants.RESULT_FAIL);
				offlineLogService.update(offlineLog);
				return new JsonResult(false, "处理失败");
			}
			
	     } catch (Exception e) {
			this.log.error("saveOfflineLog异常：", e);
			offlineLog.setStatus(Constants.RESULT_FAIL);
			offlineLogService.update(offlineLog);
			return new JsonResult(false, "服务器执行异常");
		}
	}

	private OfflineLogRequest paseToRequest(OfflineLog offlineLog) {
		OfflineLogRequest offlineLogRequest = new OfflineLogRequest();
		offlineLogRequest.setBoxCode(offlineLog.getBoxCode());
		offlineLogRequest.setBusinessType(offlineLog.getBusinessType());
		offlineLogRequest.setSiteCode(offlineLog.getCreateSiteCode());
		offlineLogRequest.setSiteName(offlineLog.getCreateSiteName());
		offlineLogRequest.setUserName(offlineLog.getCreateUser());
		offlineLogRequest.setUserCode(offlineLog.getCreateUserCode());

		offlineLogRequest.setExceptionType(offlineLog.getExceptionType());
		offlineLogRequest.setOperateTime(DateHelper.formatDateTimeMs(offlineLog.getOperateTime()));
		offlineLogRequest.setOperateType(offlineLog.getOperateType());
		offlineLogRequest.setWaybillCode(offlineLog.getWaybillCode());
		offlineLogRequest.setPackageCode(offlineLog.getPackageCode());
		offlineLogRequest.setReceiveSiteCode(offlineLog.getReceiveSiteCode());
		offlineLogRequest.setSealBoxCode(offlineLog.getSealBoxCode());
		offlineLogRequest.setBatchCode(offlineLog.getSendCode());
		offlineLogRequest.setSendUserCode(offlineLog.getSendUserCode());
		offlineLogRequest.setSendUser(offlineLog.getSendUser());
		offlineLogRequest.setShieldsCarCode(offlineLog.getShieldsCarCode());
		offlineLogRequest.setTaskType(offlineLog.getTaskType());
		offlineLogRequest.setCarCode(offlineLog.getVehicleCode());
		offlineLogRequest.setVolume(offlineLog.getVolume());
		offlineLogRequest.setWeight(offlineLog.getWeight());
		offlineLogRequest.setTurnoverBoxCode(offlineLog.getTurnoverBoxCode());
		offlineLogRequest.setTransporttype(offlineLog.getTransporttype());
		return offlineLogRequest;
	}

	/**
	 * 初始化查询条件
	 * 
	 * @param paramMap
	 * @param model
	 */
	private void initSelectObject(Map<String, Object> paramMap, Model model) {
		try {
            model.addAttribute("orgList", baseService.getAllOrg());
			List<BaseStaffSiteOrgDto> siteList = new ArrayList<BaseStaffSiteOrgDto>();
			if (paramMap != null && paramMap.get("orgCode") != null) {
                siteList = this.baseMajorManager.getBaseSiteByOrgIdSiteType((Integer) paramMap.get("orgCode"), Constants.DMS_SITE_TYPE);
            } else{
                ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseService.getBaseStaffByStaffId(erpUser.getStaffNo());
                if(baseStaffSiteOrgDto.getOrgId() != null){
                    siteList = this.baseMajorManager.getBaseSiteByOrgIdSiteType(baseStaffSiteOrgDto.getOrgId(), Constants.DMS_SITE_TYPE);
                }
            }
			model.addAttribute("siteList", siteList);
		} catch (Exception e) {
			this.log.error("初始化查询条件异常", e);
		}
	}
}
