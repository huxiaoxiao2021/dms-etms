package com.jd.bluedragon.distribution.rest.arAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReason;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 空铁异常
 * @date 2018年11月30日 15时:06分
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ArAbnormalResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ArAbnormalService arAbnormalService;
    @POST
    @Path("/arAbnormal/pushArAbnormal")
    @JProfiler(jKey = "DMS.WEB.ArAbnormalResource.pushArAbnormal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest request){
        ArAbnormalResponse response = new ArAbnormalResponse();
        try{
          return arAbnormalService.pushArAbnormal(request);
        }catch (Exception e) {
            log.error("ArAbnormalResource.pushArAbnormal操作出现异常", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    @GET
    @Path("/arAbnormal/getArContrabandReason")
    @JProfiler(jKey = "DMS.WEB.ArAbnormalResource.getArContrabandReasonList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<ArContrabandReason>> getArContrabandReasonList(){
        InvokeResult<List<ArContrabandReason>> result = new InvokeResult<>();
        try{
            result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(arAbnormalService.getArContrabandReasonList());
        }catch (Exception e){
            log.error("获取运输方式变更原因失败",e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @GET
    @Path("/arAbnormal/getArContrabandReasonNew/{transpondType}")
    @JProfiler(jKey = "DMS.WEB.ArAbnormalResource.getArContrabandReasonListNew", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<ArContrabandReason>> getArContrabandReasonListNew(@PathParam("transpondType") Integer transpondType){
        InvokeResult<List<ArContrabandReason>> result = new InvokeResult<>();
        try{
            result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            result.setData(arAbnormalService.getArContrabandReasonListNew(transpondType));
        }catch (Exception e){
            log.error("获取运输方式变更原因失败",e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
