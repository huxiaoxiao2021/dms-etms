package com.jd.bluedragon.distribution.rest.task;

import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.sun.tools.jxc.apt.Const;
import com.thoughtworks.xstream.io.xml.JDomReader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class TaskResource {

	private final Logger logger = Logger.getLogger(TaskResource.class);

	@Autowired
	private TaskService taskService;

	@GET
	@Path("/tasks/{taskId}")
	@Profiled
	public TaskResponse get(@PathParam("taskId") Long taskId) {
		Assert.notNull(taskId, "taskId must not be null");
		return this.toTaskResponse(new Task());
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.addPack", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	@SuppressWarnings("unchecked")
	@POST
	@Path("/tasks")
	public TaskResponse add(TaskRequest request) {
		//加入监控，开始
		CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.task.add", false, true);
		
		Assert.notNull(request, "request must not be null");

		TaskResponse response = null;
		if (StringUtils.isBlank(request.getBody())) {
			response = new TaskResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}
		String json = request.getBody();
		Object[] array = JsonHelper.jsonToArray(json, Object[].class);

		if (array.length > 200) {
			return new TaskResponse(
					InspectionECResponse.CODE_PARAM_UPPER_LIMIT,
					InspectionECResponse.MESSAGE_PARAM_UPPER_LIMIT);
		}

		//加入监控结束
		Profiler.registerInfoEnd(info); 
		
		for (Object element : array) {
			if (Task.TASK_TYPE_REVERSE_SPWARE.equals(request.getType())) {
				Map<String, Object> reverseSpareMap = (Map<String, Object>) element;
				List<Map<String, Object>> reverseSpareDtos = (List<Map<String, Object>>) reverseSpareMap
						.get("data");
				for (Map<String, Object> dto : reverseSpareDtos) {
					List<Map<String, Object>> tempReverseSpareDtos = Arrays
							.asList(dto);
					reverseSpareMap.put("data", tempReverseSpareDtos);
					request.setKeyword2(String.valueOf(dto.get("spareCode")));
					String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
							+ JsonHelper.toJson(reverseSpareMap)
							+ Constants.PUNCTUATION_CLOSE_BRACKET;
					logger.warn("[" + request.getType() + "]" + eachJson);
					this.taskService.add(
							this.taskService.toTask(request, eachJson), true);
				}
			} else {
				String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
						+ JsonHelper.toJson(element)
						+ Constants.PUNCTUATION_CLOSE_BRACKET;
				logger.warn("[" + request.getType() + "]" + eachJson);
				this.taskService.add(
						this.taskService.toTask(request, eachJson), true);
			}
		}
		
		return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
				DateHelper.formatDateTime(new Date()));
	}

	private TaskResponse toTaskResponse(Task task) {
		TaskResponse response = new TaskResponse();
		response.setId(task.getId());
		response.setCreateTime(DateHelper.formatDateTime(task.getCreateTime()));
		return response;
	}

	@GET
	@Path("/checktasks/checkPendingTaskStatus")
	@Profiled
	public TaskResponse checkPendingTaskStatusHealth(
			@QueryParam("type") Integer type,
			@QueryParam("fetchNum") Integer fetchNum,
			@QueryParam("ownSign") String ownSign) {

		Integer queryNum = this.taskService.findTasksNumsByType(type, ownSign);
		if (queryNum.compareTo(fetchNum) == 1) {
			return new TaskResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		} else {
			return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		}
	}
	
	@GET
	@Path("/task/findFailTasksNumsByType")
	@Profiled
	public TaskResponse findFailTasksNumsByType(
			@QueryParam("type") Integer type,
			@QueryParam("fetchNum") Integer fetchNum,
			@QueryParam("ownSign") String ownSign) {

		Integer queryNum = this.taskService.findFailTasksNumsByType(type, ownSign);
		if (queryNum.compareTo(fetchNum) == 1) {
			return new TaskResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		} else {
			return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		}
	}

	@GET
	@Path("/systemDate")
	public TaskResponse getSYSDate() {
		return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
				DateHelper.formatDateTime(new Date()));
	}


	/** 亚一或者大福线自动分拣机任务交接接口 */
	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.astask.addPack", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	@POST
	@Path("/astasks")
	public TaskResponse addASTask(TaskRequest request){
		//加入监控，开始
		CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.astask.add", false, true);
		Assert.notNull(request, "autosorting task request must not be null");
		this.logger.info("总部接口接收到自动分拣机传来的交接数据,数据 ：" + JsonHelper.toJson(request));
		TaskResponse response = null;
		if (StringUtils.isBlank(request.getBody())) {
			response = new TaskResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
			return response;
		}
		//加入监控结束
		Profiler.registerInfoEnd(info);
		try{
			taskService.addInspectSortingTask(request);
		}catch (Exception ex){
			logger.error("总部接口接收到自动分拣机传来的交接数据插入分拣交接数据失败。原因 " + ex);
			response = new TaskResponse(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
			return response;
		}
		return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
				DateHelper.formatDateTime(new Date()));
	}

    /**
     * 上海邮通等自动交接，分拣任务。没有波次概念（上海亚一和大福线的区别）
     * @param packageDtos
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.addInspectSortingTask", mState = {
            JProEnum.TP, JProEnum.FunctionError })
    @POST
    @Path("/InspectSortingTask")
    public TaskResponse addInspectSortingTask(AutoSortingPackageDto packageDtos){
        try{
            taskService.addInspectSortingTaskDirectly(packageDtos);
        }  catch (Exception e){
            logger.error("智能分拣线插入交接、分拣任务失败，原因",e);
            return new TaskResponse(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                DateHelper.formatDateTime(new Date()));
    }

}
