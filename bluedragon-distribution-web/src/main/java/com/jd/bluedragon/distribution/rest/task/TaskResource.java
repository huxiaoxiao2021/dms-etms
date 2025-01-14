package com.jd.bluedragon.distribution.rest.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.request.AutoSortingPackageDto;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.service.BoxRelationService;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.common.authorization.RestAuthorization;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.alibaba.fastjson.JSONObject;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static com.jd.bluedragon.distribution.auto.domain.UploadData.NOT_PACKAGECODE_BOXCDOE;
import static com.jd.bluedragon.distribution.auto.domain.UploadData.NOT_PACKAGECODE_BOXCDOE_MESSAGE;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

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
    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private DmsConfigManager dmsConfigManager;








    @POST
    @Path("/tasks/add")
    @JProfiler(jKey = "DMS.WEB.TaskResource.add", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer add(Task task) {
        task.setTableName("task_waybill");
        return taskService.add(task, false);
    }

    @GET
    @Path("/tasks/{taskId}")
    @JProfiler(jKey = "DMS.WEB.TaskResource.get", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TaskResponse get(@PathParam("taskId") Long taskId) {
        Assert.notNull(taskId, "taskId must not be null");
        return this.toTaskResponse(new Task());
    }

    /**
     * 分拣机验货任务
     * @param request
     * @return
     */
    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.autoAddTask", mState = {
            JProEnum.TP, JProEnum.FunctionError})
    @POST
    @Path("/autoAddInspectionTask")
    public TaskResponse autoAddInspectionTask(TaskRequest request){
        TaskResponse response = null;
        if (StringUtils.isBlank(request.getBody())) {
            response = new TaskResponse(JdResponse.CODE_PARAM_ERROR, "参数错误：body内容为空");
            return response;
        }
        List<InspectionAS> inspections = JsonHelper.jsonToList(request.getBody(), InspectionAS.class);
        if(CollectionUtils.isEmpty(inspections)){
            response = new TaskResponse(JdResponse.CODE_PARAM_ERROR, "body格式错误，内容反序列化后为空");
            return response;
        }
        //过滤妥投的运单
        Iterator<InspectionAS> it = inspections.iterator();
        while (it.hasNext()){
            InspectionAS inspection = it.next();
            String waybillCode = WaybillUtil.getWaybillCode(inspection.getPackageBarOrWaybillCode());
            if(StringUtils.isBlank(waybillCode)){
                log.warn("验货数据{}非包裹或运单号", inspection.getPackageBarOrWaybillCode());
                it.remove();
            }
            if(waybillTraceManager.isWaybillFinished(waybillCode)){
                log.warn("运单{}已妥投", waybillCode);
                it.remove();
            }

            //inspection.setBizSource(InspectionBizSourceEnum.AUTOMATIC_SORTING_MACHINE_INSPECTION.getCode());
            
            OperatorData operatorData = BeanConverter.convertToOperatorDataForAuto(inspection);
            inspection.setOperatorTypeCode(operatorData.getOperatorTypeCode());
            inspection.setOperatorId(operatorData.getOperatorId());
            inspection.setOperatorData(operatorData); 
            
        }
        if(inspections.size() ==0){
            return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                    DateHelper.formatDateTime(new Date()));
        }
        request.setBody(JsonHelper.toJson(inspections));
        return add(request);
    }  
    @JProfiler(jKey = "Bluedragon_dms_center.dms.method.task.addPack", mState = {
            JProEnum.TP, JProEnum.FunctionError})
    @SuppressWarnings("unchecked")
    @POST
    @Path("/tasks")
    public TaskResponse add(TaskRequest request) {
        try{
            return taskService.add(request);
        }catch (Exception e){
            log.error("TaskResource add error!  req:{}",JsonHelper.toJson(request),e);
            return  new TaskResponse(JdResponse.CODE_SERVICE_ERROR,
                    JdResponse.MESSAGE_SERVICE_ERROR);
        }

    }



    private void taskAssemblingAndSave(TaskRequest request, String jsonStr) {
        log.info("taskAssemblingAndSave:[{}]{}" ,request.getType(), jsonStr);
        Task task = this.taskService.toTask(request, jsonStr);
        if (task.getBoxCode() != null && task.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
            log.warn("箱号超长，无法插入任务，参数：{}" , JsonHelper.toJson(task));
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.checkPendingTaskStatusHealth", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.findFailTasksNumsByType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.checkPendingTaskStatusHealthIgnoreType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.findFailTasksNumsIgnoreType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.getSYSDate", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TaskResponse getSYSDate() {
        return new TaskResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK,
                DateHelper.formatDateTime(new Date()));
    }


    /**
     * 亚一或者大福线自动分拣机任务交接接口
     */
    @POST
    @Path("/astasks")
    @JProfiler(jKey = "DMS.WEB.TaskResource.addASTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TaskResponse addASTask(TaskRequest request) {
        Assert.notNull(request, "autosorting task request must not be null");
        //加入监控，开始
        CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.astask.add", false, true);
        if(log.isInfoEnabled()){
            this.log.info("总部接口接收到自动分拣机传来的交接数据,数据 ：{}" , JsonHelper.toJson(request));
        }
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
            log.error("总部接口接收到自动分拣机传来的交接数据插入分拣交接数据失败：{}" , JsonHelper.toJson(request), ex);
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.addInspectSortingTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TaskResponse addInspectSortingTask(AutoSortingPackageDto packageDtos) {
        CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.addInspectSortingTask", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            taskService.addInspectSortingTaskDirectly(packageDtos);
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("智能分拣线插入交接、分拣任务失败，原因:{}",JsonHelper.toJson(packageDtos), e);
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
    @JProfiler(jKey = "DMS.WEB.TaskResource.saveTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
            //非包裹号和箱号不处理
            if (!WaybillUtil.isPackageCode(domain.getBarCode()) && !BusinessUtil.isBoxcode(domain.getBarCode())) {
                result.customMessage(NOT_PACKAGECODE_BOXCDOE, NOT_PACKAGECODE_BOXCDOE_MESSAGE);
                log.warn("龙门架上传接口，包裹号{}非法[非包裹号和箱号]", domain.getBarCode());
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
                    log.error("验货时间校验常量转换失败！daysStr:{}" , daysStr, e);
                }

            }
            //比调整后的时间还早，说明上传时间有问题
            if (DateHelper.compareAdjustDate(scannerTime, days) < 0) {
                scannerTime = new Date();
                GantryException gantryException = this.convert2GantryException(domain,7);
                gantryExceptionService.addGantryException(gantryException);
                log.warn("验货时间早于调整后的时间！时间调整数为：{},domain={}" , days.toString() , JsonHelper.toJson(domain));
            }else {
                scannerTime = correctScannerTimeHeaderDate(scannerTime, date, registerNo);
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
            log.error("龙门架自动发货任务上传异常:{}", JsonHelper.toJson(domain), throwable);
            result.error(throwable);
        }
        return result;
    }

    /**
     * 根据header 里的date校验龙门架扫描时间
     * @param scannerTime
     * @return scannerTime 调整后的操作时间
     */
    private Date correctScannerTimeHeaderDate(Date scannerTime, String headerDate, String registerNo){
        try{
            //配置校验的场地 为空校验全部场地
            String correctionRegisterNo = PropertiesHelper.newInstance().getValue("GANTRY_SCANNER_TIME_CORRECTION_REGISTER_NO");
            //均不校验
            if(org.apache.commons.lang3.StringUtils.isNotBlank(correctionRegisterNo)){
                String[] registerNos = correctionRegisterNo.split(",");
                //不包含直接返回不处理
                if(!ArrayUtils.contains(registerNos, registerNo)){
                    return scannerTime;
                }
            }

            Date gantryDate = null;
            gantryDate = DateHelper.parseDateTime(headerDate);
            if(gantryDate == null){
                log.warn("headerDate 转 date 失败headerDate:{}" , headerDate);
                return scannerTime;
            }
            long deviationMillSecond = System.currentTimeMillis() - gantryDate.getTime();
            int deviationSecond = (int)deviationMillSecond/1000;
            Date scannerTimeAfterCorrect = DateUtils.addSeconds(scannerTime, deviationSecond);
            log.info("registerNo:{} 扫描时间：{},校准为:{}", registerNo,
                    DateHelper.formatDateTimeMs(scannerTime),
                    DateHelper.formatDateTimeMs(scannerTimeAfterCorrect));
            if(scannerTimeAfterCorrect.getTime() > System.currentTimeMillis()){
                return new Date();
            }
            return scannerTimeAfterCorrect;
        }catch (Exception e){
            log.error("校准龙门架时间时异常：scannerTime：{} headerDate：{}, registerNo:{}",
                    DateHelper.formatDateTimeMs(scannerTime), headerDate, registerNo,e);
        }
        return scannerTime;

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
            log.error("分拣机自动发货任务上传：{}",JsonHelper.toJson(domain), throwable);
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
    private GantryException convert2GantryException(UploadData domain, int type) {
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
        gantryException.setType(type);
        return gantryException;
    }

}
