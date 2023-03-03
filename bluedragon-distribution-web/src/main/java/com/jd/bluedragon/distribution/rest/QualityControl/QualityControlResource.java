package com.jd.bluedragon.distribution.rest.QualityControl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

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
    private BaseMajorManager baseMajorManager;

    @Autowired
    private QualityControlService qualityControlService;

    @POST
    @Path("/qualitycontrol/exceptioninfo")
    @JProfiler(jKey = "DMS.WEB.QualityControlResource.exceptionInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public QualityControlResponse exceptionInfo(QualityControlRequest request) {
        QualityControlResponse response = new QualityControlResponse();
        InvokeResult<Boolean> result = qualityControlService.exceptionSubmit(request);
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        return response;
    }

    /**
     * 可传多个运单号 逗号隔开
     * @param request
     * @return
     */
    @POST
    @Path("/qualitycontrol/exceptioninfos")
    @JProfiler(jKey = "DMS.WEB.QualityControlResource.exceptionInfos", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public QualityControlResponse exceptionInfos(QualityControlRequest request) {
        QualityControlResponse response = new QualityControlResponse();
        String waybillCodes=request.getQcValue();
        String[] waybillCodeArr=waybillCodes.split(Constants.SEPARATOR_COMMA);
        Boolean hasError=false;
        //获取操作人信息
        String usercode=request.getUserERP();
        if (usercode==null){
            response.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
            response.setMessage(QualityControlResponse.MESSAGE_SERVICE_ERROR);
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
                response.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
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
            response.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
            response.setMessage(QualityControlResponse.MESSAGE_SERVICE_ERROR);
            return response;
        }
        response.setCode(QualityControlResponse.CODE_OK);
        response.setMessage(QualityControlResponse.MESSAGE_OK);
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
        return qualityControlService.redeliveryCheck(request);
    }
}
