package com.jd.bluedragon.distribution.rest.QualityControl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.domain.RedeliveryMode;
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
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(QualityControlResource.class);

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
        QualityControlResponse response = new QualityControlResponse();
        if(StringUtils.isEmpty(request.getQcValue()) || !WaybillCodeRuleValidateUtil.isEffectiveOperateCode(request.getQcValue())){
            log.warn("PDA调用异常配送接口插入质控任务表失败-参数错误[{}]",JsonHelper.toJson(request));
            response.setCode(response.CODE_SERVICE_ERROR);
            response.setMessage("请扫描运单号或者包裹号！");
            return response;
        }
        try{
            this.qualityControlService.convertThenAddTask(request);
        }catch(Exception ex){
            log.error("PDA调用异常配送接口插入质控任务表失败，原因 " , ex);
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
                log.warn("PDA调用异常配送接口插入质控任务表失败-参数错误[{}]",JsonHelper.toJson(request));
                response.setCode(response.CODE_SERVICE_ERROR);
                response.setMessage("请扫描运单号或者包裹号！");
                return response;
            }
            request.setQcValue(waybillCode);
            request.setTrackContent("订单扫描异常【"+waybillCode+"】。");
            try{
                this.qualityControlService.convertThenAddTask(request);
            }catch(Exception ex){
                log.error("PDA调用异常配送接口插入质控任务表失败，原因 ", ex);
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
     * 协商再投状态校验 已下架
     * @param request
     * @return
     */
    @POST
    @Path("/qualitycontrol/redeliverycheck")
    @JProfiler(jKey = "DMSWEB.QualityControlResource.redeliveryCheck", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> redeliveryCheck(RedeliveryCheckRequest request){
        InvokeResult<Boolean> result=new InvokeResult<Boolean>();

        InvokeResult<RedeliveryMode> res=redeliveryCheckNew(request);
        result.setCode(res.getCode());
        result.setMessage(res.getMessage());
        result.setData(res.getData().getIsCompleted());

        return result;
    }

    /**
     * 协商再投状态校验
     * @param request
     * @return
     */
    @POST
    @Path("/qualitycontrol/redeliverychecknew")
    @JProfiler(jKey = "DMSWEB.QualityControlResource.redeliveryCheckNew", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<RedeliveryMode> redeliveryCheckNew(RedeliveryCheckRequest request){
        InvokeResult<RedeliveryMode> result=new InvokeResult<RedeliveryMode>();

        RedeliveryMode data=new RedeliveryMode();
        data.setIsCompleted(true);

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setData(data);

        if(StringUtils.isEmpty(request.getCode()) || null==request.getCodeType() || request.getCodeType()<1){
            log.warn("PDA调用协商再投状态验证接口失败-参数错误。入参:{}",JsonHelper.toJson(request));
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
                    Integer busID = waybillQueryManager.getBusiId(waybillCode);
                    if (null != busID){
                        int res=qualityControlService.getRedeliveryState(waybillCode,busID);
                        if (res==0){
                            data.setIsCompleted(false);
                            data.setWaybillCode(waybillCode);
                            result.setData(data);
                            break;
                        }
                    }
                    else {
                        log.warn("PDA调用协商再投状态验证接口失败-无商家信息。运单号:{},入参:{}",waybillCode,JsonHelper.toJson(request));
                    }
                }
            }
            else {
                log.warn("PDA调用协商再投状态验证接口失败-无运单信息。入参:{}",JsonHelper.toJson(request));
                result.setCode(InvokeResult.RESULT_NULL_WAYBILLCODE_CODE);
                result.setMessage(InvokeResult.RESULT_NULL_WAYBILLCODE_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("PDA调用协商再投状态验证接口失败。异常信息:{}",ex.getMessage(),ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
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
