package com.jd.bluedragon.distribution.rest.QualityControl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private QualityControlService qualityControlService;

    @Autowired
    private SortingService sortingService;

    @POST
    @Path("/qualitycontrol/exceptioninfo")
    public QualityControlResponse exceptionInfo(QualityControlRequest request) {
        logger.warn("PDA调用异常配送接口开始，参数信息 " + JsonHelper.toJson(request));
        QualityControlResponse response = new QualityControlResponse();
        if(StringUtils.isEmpty(request.getQcValue()) || !WaybillCodeRuleValidateUtil.isEffectiveOperateCode(request.getQcValue())){
            logger.error(MessageFormat.format("PDA调用异常配送接口插入质控任务表失败-参数错误[{0}]",JsonHelper.toJson(request)));
            response.setCode(response.CODE_SERVICE_ERROR);
            response.setMessage("请扫描运单号或者包裹号！");
            return response;
        }
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
            if(!WaybillCodeRuleValidateUtil.isEffectiveOperateCode(waybillCode)){
                logger.error(MessageFormat.format("PDA调用异常配送接口插入质控任务表失败-参数错误[{0}]",JsonHelper.toJson(request)));
                response.setCode(response.CODE_SERVICE_ERROR);
                response.setMessage("请扫描运单号或者包裹号！");
                return response;
            }
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

    /**
     * 协商再投状态校验
     * @param request
     * @return
     */
    @POST
    @Path("/qualitycontrol/redeliverycheck")
    public InvokeResult<Boolean> redeliveryCheck(RedeliveryCheckRequest request){
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setData(true);

        if(StringUtils.isEmpty(request.getCode()) || null==request.getCodeType() || request.getCodeType()<1){
            logger.error(MessageFormat.format("PDA调用协商再投状态验证接口失败-参数错误[{0}]",JsonHelper.toJson(request)));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("请扫描者包裹号、运单号或箱号！");
            return result;
        }

        try{
            List<String> waybillCodeList=new ArrayList<String>();

            //如果是包裹或运单
            if (request.getCodeType()==1 || request.getCodeType()==2){
                String waybillCode= WaybillUtil.getWaybillCode(request.getCode());
                waybillCodeList.add(waybillCode);
            }

            //如果是箱号
            if (request.getCodeType()==3){
                waybillCodeList = sortingService.getWaybillCodeListByBoxCode(request.getCode());
            }

            if(waybillCodeList != null && waybillCodeList.size() > 0){
                for (String waybillCode :waybillCodeList){
                    Integer busID=getBusiId(waybillCode);
                    if (null != busID){
                        int res=qualityControlService.getRedeliveryState(waybillCode,busID);
                        if (res==0){
                            result.setData(false);
                            logger.info("运单:"+waybillCode+" 协商再投未完成处理");
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
             logger.error("PDA调用协商再投状态验证接口失败，原因 " + ex);
             result.setCode(InvokeResult.SERVER_ERROR_CODE);
             result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
           }

        return result;
    }

    /**
     * 根据运单号获取商家ID
     * @param waybillCode
     * @return
     */
    private Integer getBusiId(String waybillCode){
        Integer res=null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
        if (null != baseEntity
            && Constants.RESULT_SUCCESS == baseEntity.getResultCode()
            && null != baseEntity.getData()
            && null != baseEntity.getData().getWaybill()
            )
        {
            res = baseEntity.getData().getWaybill().getBusiId();
        }

        return res;
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
