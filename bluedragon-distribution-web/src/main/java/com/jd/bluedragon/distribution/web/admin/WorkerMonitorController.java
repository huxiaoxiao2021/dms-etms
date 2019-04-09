package com.jd.bluedragon.distribution.web.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.admin.service.WorkerMonitorService;
import com.jd.bluedragon.distribution.api.request.WorkerRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.etms.erp.service.dto.CommonDto;

@Controller
@RequestMapping("/admin/worker-monitor")
public class WorkerMonitorController {

	private static final Logger logger = Logger.getLogger(WorkerMonitorController.class);

	@Autowired
	private WorkerMonitorService workerMonitorService;

	@Authorization(Constants.DMS_WEB_DEVELOP_DBTASK_R)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(Model model) {
		try {
			ErpUser erpUser = ErpUserClient.getCurrUser();
			model.addAttribute("erpUser", erpUser);
		} catch (Exception e) {
			logger.error("index error!", e);
		}
		return "admin/worker-monitor/worker-monitor-index";
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_DBTASK_R)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<Pager<List<Task>>> doQueryWorker(WorkerRequest request, Pager<List<Task>> pager, Model model) {
		CommonDto<Pager<List<Task>>> cdto = new CommonDto<Pager<List<Task>>>();
		try {
			logger.info("WorkerMonitorController doQueryWorker begin...");
			if (null == request || StringUtils.isBlank(request.getTableName())) {
				cdto.setCode(CommonDto.CODE_WARN);
				cdto.setMessage("参数[tableName]不能为空！");
				return cdto;
			}
			Map<String, Object> params = this.getParamsFromRequest(request);
			// 设置分页对象
			if (pager == null) {
				pager = new Pager<List<Task>>(Pager.DEFAULT_PAGE_NO);
			} else {
				pager = new Pager<List<Task>>(pager.getPageNo(), pager.getPageSize());
			}
			params.putAll(ObjectMapHelper.makeObject2Map(pager));

			List<Task> taskList = workerMonitorService.findPageTask(params);
			Integer totalSize = workerMonitorService.findCountTask(params);
			pager.setTotalSize(totalSize);
			pager.setData(taskList);
			pager.setTableName(request.getTableName());
			logger.info("查询符合条件的规则数量：" + totalSize);

			cdto.setData(pager);
			cdto.setCode(CommonDto.CODE_NORMAL);
		} catch (Exception e) {
			logger.error("doQueryWorker-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_DBTASK_R)
	@RequestMapping(value = "/queryTaskTypeByTableName", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<List<Integer>> queryTaskTypeByTableName(Model model, @RequestParam(value = "tableName", required = false) String tableName) {
		CommonDto<List<Integer>> cdto = new CommonDto<List<Integer>>();
		try {
			logger.info("WorkerMonitorController queryTaskTypeByTableName begin...");
			if (tableName == null || StringUtils.isBlank(tableName)) {
				cdto.setCode(CommonDto.CODE_WARN);
				cdto.setMessage("参数[tableName]不能为空！");
				return cdto;
			}

			List<Integer> taskTypeList = workerMonitorService.findTaskTypeByTableName(tableName);
			if (null == taskTypeList || taskTypeList.size() < 1) {
				cdto.setData(new ArrayList<Integer>());
			} else {
				cdto.setData(taskTypeList);
			}
			cdto.setCode(CommonDto.CODE_NORMAL);
		} catch (Exception e) {
			logger.error("queryTaskTypeByTableName-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_DBTASK_R)
	@RequestMapping(value = "/doTaskReset", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<Integer> doTaskReset(Model model, @RequestParam(value = "id", required = false) Integer id, @RequestParam(value = "tableName", required = false) String tableName) {
		CommonDto<Integer> cdto = new CommonDto<Integer>();
		try {
			logger.info("WorkerMonitorController taskReset begin...");
			if (id == null || id <= 0) {
				cdto.setCode(CommonDto.CODE_WARN);
				cdto.setMessage("参数[id]不合法！");
				return cdto;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", id);
			params.put("tableName", tableName);
			Integer result = workerMonitorService.updateTaskById(params);
			cdto.setData(result);
			cdto.setCode(CommonDto.CODE_NORMAL);
		} catch (Exception e) {
			logger.error("taskReset-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

    @Authorization(Constants.DMS_WEB_DEVELOP_DBTASK_R)
	@RequestMapping(value = "/doTaskBatchReset", method = RequestMethod.POST)
	@ResponseBody
	public CommonDto<Integer> doTaskBatchReset(WorkerRequest request, Model model) {
		CommonDto<Integer> cdto = new CommonDto<Integer>();
		try {
			logger.info("WorkerMonitorController doTaskBatchReset begin...");
			if (null == request || StringUtils.isBlank(request.getTableName())) {
				cdto.setCode(CommonDto.CODE_WARN);
				cdto.setMessage("参数[tableName]不能为空！");
				return cdto;
			}
			Map<String, Object> params = this.getParamsFromRequest(request);
			Integer result = workerMonitorService.updateBatchTask(params);
			cdto.setData(result);
			cdto.setCode(CommonDto.CODE_NORMAL);
		} catch (Exception e) {
			logger.error("doQueryWorker-error!", e);
			cdto.setCode(CommonDto.CODE_EXCEPTION);
			cdto.setData(null);
			cdto.setMessage(e.getMessage());
		}
		return cdto;
	}

	private Map<String, Object> getParamsFromRequest(WorkerRequest request) throws ParseException {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(request.getTableName())){
			params.put("tableName", request.getTableName());
		}
		Integer tastType = request.getTaskType();
		if(null != tastType && tastType > 0){
			params.put("taskType", tastType);
		}
		Integer taskStatus = request.getTaskStatus();
		if(null != taskStatus && taskStatus >= 0){
			params.put("taskStatus", taskStatus);
		}
		Integer executeCount = request.getExecuteCount();
		if(null != executeCount && executeCount >= 0){
			params.put("executeCount", executeCount);
		}
		if(StringUtils.isNotBlank(request.getKeyword1())){
			params.put("keyword1", request.getKeyword1());
		}
		if(StringUtils.isNotBlank(request.getKeyword2())){
			params.put("keyword2", request.getKeyword2());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTime = request.getStartTime();
		String endTime = request.getEndTime();
		if (StringUtils.isNotBlank(startTime)) {
			params.put("startTime", sdf.parse(startTime));
		}
		if (StringUtils.isNotBlank(endTime)) {
			params.put("endTime", sdf.parse(endTime));
		}
		return params;
	}

}
