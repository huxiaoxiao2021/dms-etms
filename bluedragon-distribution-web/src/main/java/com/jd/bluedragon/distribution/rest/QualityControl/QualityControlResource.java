package com.jd.bluedragon.distribution.rest.QualityControl;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;

/**
 * Created by dudong on 2014/12/1.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class QualityControlResource {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @POST
    @Path("/qualitycontrol/exceptioninfo")
    public QualityControlResponse exceptionInfo(QualityControlRequest request) {
        logger.warn("PDA调用异常配送接口开始，参数信息 " + JsonHelper.toJson(request));
        QualityControlResponse response = new QualityControlResponse();
        try{
            convertThenAddTask(request);
        }catch(Exception ex){
            logger.error("PDA调用异常配送接口插入质控任务表失败，原因 " + ex);
            response.setCode(response.CODE_SERVICE_ERROR);
            response.setMessage(response.MESSAGE_SERVICE_ERROR);
            return response;
        }

        response.setCode(response.CODE_OK);
        response.setMessage(response.MESSAGE_OK);
        return response;
    }

    /**
     * 可传多个运单号 逗号隔开
     * @param request
     * @return
     */
    @POST
    @Path("/qualitycontrol/exceptioninfos")
    public QualityControlResponse exceptionInfos(QualityControlRequest request) {
        logger.warn("WEB调用异常配送接口开始，参数信息 " + JsonHelper.toJson(request));
        QualityControlResponse response = new QualityControlResponse();
        String waybillCodes=request.getQcValue();
        String[] waybillCodeArr=waybillCodes.split(Constants.SEPARATOR_COMMA);
        Boolean hasError=false;
        //获取操作人信息
        String usercode=request.getUserERP();
        if (usercode==null){
            response.setCode(response.CODE_SERVICE_ERROR);
            response.setMessage(response.MESSAGE_SERVICE_ERROR);
            return response;
        }
        BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(usercode);
        request.setDistCenterID(userDto.getSiteCode());
        request.setDistCenterName(userDto.getSiteName());
        request.setUserID(userDto.getStaffNo());
        request.setUserName(userDto.getStaffName());
        request.setUserERP(userDto.getAccountNumber());
        request.setOperateTime(new Date());
        for (String waybillCode:waybillCodeArr){
            request.setQcValue(waybillCode);
            request.setTrackContent("订单扫描异常【"+waybillCode+"】。");
            try{
                convertThenAddTask(request);
            }catch(Exception ex){
                logger.error("PDA调用异常配送接口插入质控任务表失败，原因 " + ex);
                hasError=true;
            }
        }
        if (hasError){
            response.setCode(response.CODE_SERVICE_ERROR);
            response.setMessage(response.MESSAGE_SERVICE_ERROR);
            return response;
        }
        response.setCode(response.CODE_OK);
        response.setMessage(response.MESSAGE_OK);
        return response;
    }
    public void convertThenAddTask(QualityControlRequest request) throws Exception{

        Task qcTask = new Task();
        qcTask.setKeyword1(request.getQcType() + "");
        qcTask.setKeyword2(request.getQcValue());
        qcTask.setOwnSign(BusinessHelper.getOwnSign());
        qcTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        qcTask.setType(Task.TASK_TYPE_REVERSE_QUALITYCONTROL);
        qcTask.setTableName(Task.getTableName(qcTask.getType()));
        qcTask.setSequenceName(Task.getSequenceName(qcTask.getTableName()));
        qcTask.setBody(JsonHelper.toJson(request));
        qcTask.setCreateTime(new Date());
        qcTask.setCreateSiteCode(Integer.parseInt(String.valueOf(request.getDistCenterID())));
        qcTask.setExecuteCount(0);
        StringBuilder fringerprint = new StringBuilder();
        fringerprint.append(request.getDistCenterID() + "_" + qcTask.getType() + "_" + qcTask.getKeyword1() + "_" + qcTask.getKeyword2());
        qcTask.setFingerprint(Md5Helper.encode(fringerprint.toString()));

        taskService.add(qcTask);
    }

    public static void main(String[] args) {
        QualityControlRequest request = new QualityControlRequest();
        request.setIsSortingReturn(true);
        QualityControlRequest request1 = JsonHelper.fromJson("{" +
                "\"isSortingReturn\" : true" +
                "}",QualityControlRequest.class);
        System.out.println(request1.getIsSortingReturn());
    }
}
