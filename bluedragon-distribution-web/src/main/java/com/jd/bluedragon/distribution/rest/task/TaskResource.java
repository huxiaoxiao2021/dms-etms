package com.jd.bluedragon.distribution.rest.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.authorization.RestAuthorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TaskResource {

    private final Logger logger = Logger.getLogger(TaskResource.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private RestAuthorization restAuthorization;

    /**
     * 龙门架扫描发出mq消息
     */
    @Autowired
    private DefaultJMQProducer gantryScanPackageMQ;

    @Autowired
    private GantryExceptionService gantryExceptionService;


    @POST
    @Path("/tasks/add")
    public Integer add(Task task) {
        task.setTableName("task_waybill");
        return taskService.add(task, false);
    }

    @GET
    @Path("/tasks/{taskId}")
    public TaskResponse get(@PathParam("taskId") Long taskId) {
        Assert.notNull(taskId, "taskId must not be null");
        return this.toTaskResponse(new Task());
    }

    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.addPack", mState = {
            JProEnum.TP, JProEnum.FunctionError})
    @SuppressWarnings("unchecked")
    @POST
    @Path("/tasks")
    public TaskResponse add(TaskRequest request) {
        //加入监控，开始
        CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.task.add", false, true);

        Assert.notNull(request, "request must not be null");
        if (logger.isInfoEnabled()) {
            logger.info("TaskRequest [" + JsonHelper.toJson(request) + "]");
        }

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
                    this.taskAssemblingAndSave(request, eachJson);
                }
            } else {
                String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                        + JsonHelper.toJson(element)
                        + Constants.PUNCTUATION_CLOSE_BRACKET;
                this.taskAssemblingAndSave(request, eachJson);
            }
        }

        return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                DateHelper.formatDateTime(new Date()));
    }

    private void taskAssemblingAndSave(TaskRequest request, String jsonStr) {
        logger.warn("[" + request.getType() + "]" + jsonStr);
        Task task = this.taskService.toTask(request, jsonStr);
        if (task.getBoxCode() != null && task.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
            logger.warn("箱号超长，无法插入任务，参数：" + JsonHelper.toJson(task));
        } else {
            this.taskService.add(task, true);
        }
    }

    private TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setCreateTime(DateHelper.formatDateTime(task.getCreateTime()));
        return response;
    }

    @GET
    @Path("/checktasks/checkPendingTaskStatus")
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
    public TaskResponse findFailTasksNumsByType(
            @QueryParam("type") Integer type,
            @QueryParam("fetchNum") Integer fetchNum,
            @QueryParam("ownSign") String ownSign,
            @QueryParam("keyword1") Integer keyword1) {

        Integer queryNum = this.taskService.findFailTasksNumsByType(type, ownSign, keyword1);
        if (queryNum.compareTo(fetchNum) == 1) {
            return new TaskResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        } else {
            return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }
    }

    //----------------改版的任务监控api,重点是忽略sql中的type过滤条件--------start--------------

    @GET
    @Path("/checktasks/checkPendingTaskStatusIgnoreType")
    public TaskResponse checkPendingTaskStatusHealthIgnoreType(
            @QueryParam("type") Integer type,
            @QueryParam("fetchNum") Integer fetchNum,
            @QueryParam("ownSign") String ownSign) {

        Integer queryNum = this.taskService.findTasksNumsIgnoreType(type, ownSign);
        if (queryNum.compareTo(fetchNum) == 1) {
            return new TaskResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        } else {
            return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }
    }

    @GET
    @Path("/task/findFailTasksNumsIgnoreType")
    public TaskResponse findFailTasksNumsIgnoreType(
            @QueryParam("type") Integer type,
            @QueryParam("fetchNum") Integer fetchNum,
            @QueryParam("ownSign") String ownSign) {

        Integer queryNum = this.taskService.findFailTasksNumsIgnoreType(type, ownSign);
        if (queryNum.compareTo(fetchNum) == 1) {
            return new TaskResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
        } else {
            return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }
    }

    //----------------改版的任务监控api,重点是忽略sql中的type过滤条件--------end----------------


    @GET
    @Path("/systemDate")
    public TaskResponse getSYSDate() {
        return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                DateHelper.formatDateTime(new Date()));
    }


    /**
     * 亚一或者大福线自动分拣机任务交接接口
     */
    @POST
    @Path("/astasks")
    public TaskResponse addASTask(TaskRequest request) {
        Assert.notNull(request, "autosorting task request must not be null");
        //加入监控，开始
        CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.astask.add", false, true);
        this.logger.info("总部接口接收到自动分拣机传来的交接数据,数据 ：" + JsonHelper.toJson(request));
        TaskResponse response = null;
        if (StringUtils.isBlank(request.getBody())) {
            response = new TaskResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        try {
            taskService.addInspectSortingTask(request);
        } catch (Exception ex) {
            Profiler.functionError(info);
            logger.error("总部接口接收到自动分拣机传来的交接数据插入分拣交接数据失败。原因 " + ex);
            response = new TaskResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
            return response;
        }finally {
            //加入监控结束
            Profiler.registerInfoEnd(info);
        }
        return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                DateHelper.formatDateTime(new Date()));
    }

    /**
     * 上海邮通等自动交接，分拣任务。没有波次概念（上海亚一和大福线的区别）
     *
     * @param packageDtos
     * @return
     */
    @POST
    @Path("/InspectSortingTask")
    public TaskResponse addInspectSortingTask(AutoSortingPackageDto packageDtos) {
        CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.addInspectSortingTask", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            taskService.addInspectSortingTaskDirectly(packageDtos);
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.warn("智能分拣线插入交接、分拣任务失败，原因"+JsonHelper.toJson(packageDtos), e);
            return new TaskResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                DateHelper.formatDateTime(new Date()));
    }

    /**
     * 龙门加任务插入
     *
     * @param request 请求对象
     * @param domain  数据对象
     * @return
     */
    @POST
    @Path("/task/saveTask")
    public InvokeResult saveTask(@Context HttpServletRequest request, UploadData domain) {
        Integer source = domain.getSource();
        // 判断请求来源 2-分拣机 其他来源目前均为走龙门架逻辑
        if (source != null && source.intValue() == 2) { //分拣机
            return saveSortMachineTask(domain);
        } else { // 龙门架
            return saveScannerFrameTask(request, domain);
        }
    }

    /**
     * 处理龙门架上传任务
     *
     * @param request
     * @param domain
     * @return
     */
    private InvokeResult saveScannerFrameTask(HttpServletRequest request, UploadData domain) {
        InvokeResult result = new InvokeResult();
        String registerNo = request.getHeader(RestAuthorization.REGISTER_NO);
        String authorization = request.getHeader(RestAuthorization.AUTHORIZATION);
        String date = request.getHeader(RestAuthorization.DATE);
        if (!restAuthorization.authorizeDateTime(date)) {
            result.customMessage(RestAuthorization.DATE_DIFF_NOT_MATCH, RestAuthorization.DATE_DIFF_NOT_MATCH_MESSAGE);
            return result;
        }
        if (!restAuthorization.authorize(registerNo, authorization, date)) {
            result.customMessage(RestAuthorization.NO_AUTHORIZE, RestAuthorization.NO_AUTHORIZE_MESSAGE);
            return result;
        }
        try {
            domain.setRegisterNo(registerNo);
            if (StringHelper.isEmpty(domain.getBarCode())) {
                result.customMessage(UploadData.BARCODE_NULL_OR_EMPTY_CODE, UploadData.BARCODE_NULL_OR_EMPTY_MESSAGE);
                return result;
            }
            if (domain.getBarCode().trim().length() > UploadData.MAX_BARCODE_LENGTH) {
                result.customMessage(UploadData.MAX_BARCODE_LENGTH_CODE, UploadData.MAX_BARCODE_LENGTH_MESSAGE);
                return result;
            }
            //added by hanjiaxing3 2018.05.04
            Date scannerTime = new Date(DateHelper.adjustTimestampToJava(domain.getScannerTime().getTime()));
            String daysStr = PropertiesHelper.newInstance().getValue("GANTRY_CHECK_DAYS");
            Integer days = Constants.GANTRY_CHECK_DAYS;
            if (StringHelper.isNotEmpty(daysStr)) {
                try {
                    days = Integer.parseInt(daysStr);
                }
                catch (Exception e) {
                    logger.error("验货时间校验常量转换失败！daysStr:" + daysStr, e);
                }

            }
            //比调整后的时间还早，说明上传时间有问题
            if (DateHelper.compareAdjustDate(scannerTime, days) < 0) {
                scannerTime = new Date();
                GantryException gantryException = this.convert2GantryException(domain);
                gantryExceptionService.addGantryException(gantryException);
                logger.warn("验货时间早于调整后的时间！时间调整数为：" + days.toString() + JsonHelper.toJsonUseGson(domain));
            }

            domain.setScannerTime(scannerTime);
            //added end
            if (this.addTask(domain) <= 0) {
                result.customMessage(0, "保存数据失败");
            }

            /**
             * 扫描成功之后发出mq消息 用来计算龙门架流速
             */
            domain.setScannerTime(new Date(DateHelper.adjustTimestampToJava(domain.getScannerTime().getTime())));
            gantryScanPackageMQ.sendOnFailPersistent(domain.getBarCode(), JsonHelper.toJsonUseGson(domain));

        } catch (Throwable throwable) {
            logger.error("龙门架自动发货任务上传", throwable);
            result.error(throwable);
        }
        return result;
    }
    

    /**
     * 保存自动分拣机发货任务
     *
     * @param domain
     * @return
     */
    private InvokeResult saveSortMachineTask(UploadData domain) {
        InvokeResult result = new InvokeResult();
        try {
            if (this.addTask(domain) <= 0) {
                result.customMessage(0, "保存数据失败");
            }
        } catch (Throwable throwable) {
            logger.error("分拣机自动发货任务上传", throwable);
            result.error(throwable);
        }
        return result;
    }

    /**
     * 添加调度任务
     *
     * @param domain
     * @return
     */
    private Integer addTask(UploadData domain) {
        Task task = new Task();
        task.setBody(JsonHelper.toJson(domain));
        task.setCreateSiteCode(domain.getDistributeId() == null ? 0 : domain.getDistributeId());
        task.setType(Task.TASK_TYPE_SCANNER_FRAME);
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setBusinessType(10);
        task.setKeyword1(domain.getBarCode());
        task.setFingerprint(Md5Helper.encode(task.getBody()));
        task.setOperateTime(new Date(DateHelper.adjustTimestampToJava(domain.getScannerTime().getTime())));
        task.setBoxCode(domain.getBarCode());
        task.setKeyword2(domain.getRegisterNo());
        task.setTableName(Task.getTableName(task.getType()));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        return taskService.add(task, true);
    }

    /**
     * 上传信息转换为异常信息
     *
     * @param domain
     * @return
     */
    private GantryException convert2GantryException(UploadData domain) {
        Date date = new Date();

        GantryException gantryException = new GantryException();

        gantryException.setMachineId(domain.getRegisterNo());
        String barCode = domain.getBarCode();
        gantryException.setBarCode(barCode);
        if (!BusinessUtil.isBoxcode(barCode)) {
            gantryException.setPackageCode(barCode);
            gantryException.setWaybillCode(SerialRuleUtil.getWaybillCode(barCode));
        }

        gantryException.setCreateSiteCode(domain.getDistributeId() == null ? 0 : domain.getDistributeId().longValue());
        gantryException.setOperateTime(domain.getScannerTime());
        //操作时间异常：7
        gantryException.setType(7);
        return gantryException;
    }

}
