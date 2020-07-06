package com.jd.bluedragon.distribution.rest.loadAndUnload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.unloadCar.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.dms.logger.annotation.BusinessLog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.List;

/**
 * 装卸车REST接口
 *
 * @author: hujiping
 * @date: 2020/6/23 10:19
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class LoadAndUnloadVehicleResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarService unloadCarService;

    /**
     * 获取卸车任务
     * @param sealCarCode 封车编码
     * @return
     */
    @GET
    @Path("/unload/getUnloadCar/{sealCarCode}")
    public InvokeResult<UnloadCarScanResult> getUnloadCar(@PathParam("sealCarCode") String sealCarCode) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        return unloadCarService.getUnloadCarBySealCarCode(sealCarCode);
    }

    /**
     * 卸车扫描
     * @param unloadCarScanRequest
     * @return
     */
    @POST
    @Path("/unload/barCodeScan")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB,bizType = 1016,operateType = 101601)
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest unloadCarScanRequest) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        String remindMessage = unloadParamsCheck(unloadCarScanRequest);
        if(remindMessage != null){
            result.parameterError(remindMessage);
            return result;
        }
        return unloadCarService.barCodeScan(unloadCarScanRequest);
    }

    /**
     * 卸车扫描参数校验
     * @param request
     * @return
     */
    private String unloadParamsCheck(UnloadCarScanRequest request) {
        if(request == null){
            return InvokeResult.PARAM_ERROR;
        }
        if(StringUtils.isEmpty(request.getSealCarCode())){
            return "封车编码为空!";
        }
        if(!WaybillUtil.isPackageCode(request.getBarCode())){
            return "包裹号不符合规则!";
        }
        if(request.getOperateUserCode() == null
                || StringUtils.isEmpty(request.getOperateUserErp())
                || StringUtils.isEmpty(request.getOperateUserName())){
            return "操作人不存在!";
        }
        if(request.getOperateSiteCode() == null
                || StringUtils.isEmpty(request.getOperateSiteName())){
            return "操作站点不存在!";
        }
        return null;
    }

    /**
     * 获取卸车扫描任务明细
     * @param sealCarCode 封车编码
     * @return
     */
    @GET
    @Path("/unload/getUnloadCarDetail/{sealCarCode}")
    public InvokeResult<List<UnloadCarDetailScanResult>> getUnloadCarDetail(@PathParam("sealCarCode") String sealCarCode) {
        InvokeResult<List<UnloadCarDetailScanResult>> result = new InvokeResult<List<UnloadCarDetailScanResult>>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        return unloadCarService.searchUnloadDetail(sealCarCode);
    }

    /**
     * 获取分配给责任人的任务
     * @param unloadCarTaskReq 卸车任务请求信息
     * @return
     */
    @POST
    @Path("/unload/getUnloadCarTask")
    public InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq) {
        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<List<UnloadCarTaskDto>>();
        if (unloadCarTaskReq == null || unloadCarTaskReq.getUser() == null ||
                unloadCarTaskReq.getUser().getUserErp() == null || unloadCarTaskReq.getCurrentOperate().getSiteCode() <=0) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }

        return unloadCarService.getUnloadCarTask(unloadCarTaskReq);
    }

    /**
     * 修改任务状态
     * @param unloadCarTaskReq 修改卸车任务状态请求信息
     * @return
     */
    @POST
    @Path("/unload/updateUnloadCarTaskStatus")
    public InvokeResult<List<UnloadCarTaskDto>> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq) {
        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        if (unloadCarTaskReq == null || unloadCarTaskReq.getTaskCode() == null || unloadCarTaskReq.getTaskStatus() == null
            || unloadCarTaskReq.getUser() == null || unloadCarTaskReq.getUser().getUserErp() == null
            || unloadCarTaskReq.getCurrentOperate().getSiteCode() <=0
            || unloadCarTaskReq.getOperateTime() == null) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }

        return unloadCarService.updateUnloadCarTaskStatus(unloadCarTaskReq);
    }

    /**
     * 获取任务协助人列表
     * @param taskCode 任务编码
     * @return
     */
    @GET
    @Path("/unload/getUnloadCarTaskHelpers/{taskCode}")
    public InvokeResult<List<HelperDto>> getUnloadCarTaskHelpers(@PathParam("taskCode") String taskCode) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        if(StringUtils.isEmpty(taskCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }

        return unloadCarService.getUnloadCarTaskHelpers(taskCode);
    }

    /**
     * 添加、删除任务协助人
     * @param taskHelpersReq
     * @return
     */
    @POST
    @Path("/unload/updateUnloadCarTaskHelpers")
    public InvokeResult<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        if (taskHelpersReq == null || taskHelpersReq.getTaskCode() == null || taskHelpersReq.getOperateType() == null
                || taskHelpersReq.getUser() == null || taskHelpersReq.getUser().getUserErp() == null
                || taskHelpersReq.getCurrentOperate() == null || taskHelpersReq.getCurrentOperate().getSiteCode() <= 0
                || taskHelpersReq.getOperateTime() == null) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        return unloadCarService.updateUnloadCarTaskHelpers(taskHelpersReq);
    }

    /**
     * 获取已开始未完成的任务
     * @param taskHelpersReq
     * @return
     */
    @POST
    @Path("/unload/getUnloadCarTaskScan")
    public InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq) {
        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        if (taskHelpersReq == null || taskHelpersReq.getUser() == null || taskHelpersReq.getUser().getUserErp() == null
                || taskHelpersReq.getCurrentOperate() == null || taskHelpersReq.getCurrentOperate().getSiteCode() <= 0) {
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        return unloadCarService.getUnloadCarTaskScan(taskHelpersReq);
    }

}
