package com.jd.bluedragon.distribution.rest.batchForward;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by hujiping on 2018/7/31.
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BatchForwardResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BatchForwardService batchForwardService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private DeliveryService deliveryService;


    @GET
    @Path("/batchForward/checkSendCode/{sendCode}/{flage}")
    public InvokeResult<CreateAndReceiveSiteInfo> checkSendCode(
            @PathParam("sendCode") String sendCode,@PathParam("flage") Integer flage){

        InvokeResult<CreateAndReceiveSiteInfo> result = new InvokeResult<CreateAndReceiveSiteInfo>();
        //验证sendCode
        InvokeResult<Boolean> invokeResult = deliveryService.checkSendCodeStatus(sendCode);
        if(invokeResult != null && InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
            result.customMessage(invokeResult.getCode(),invokeResult.getMessage());
            return result;
        }
        //查询批次下是否有包裹和箱
        Boolean isHaveBox = batchForwardService.isHaveBox(sendCode);
        if(flage == 1 && isHaveBox){
            result.error("批次下有箱或包裹，请换批次");
            return result;
        }

        CreateAndReceiveSiteInfo createAndReceiveSite = siteService.getCreateAndReceiveSiteBySendCode(sendCode);
        result.setMessage("success");
        result.setData(createAndReceiveSite);
        return result;
    }

    /**
     * 批次号整批转发
     * @param request
     * @return
     */
    @POST
    @Path("/batchForward/batchForwardSend")
    public InvokeResult batchForwardSend(BatchForwardRequest request){
        if(log.isInfoEnabled()){
            log.info(JsonHelper.toJsonUseGson(request));
        }
        InvokeResult result = new InvokeResult();
        try{

            result = batchForwardService.batchSend(request);
        }catch (Exception e){
            result.error(e);
            this.log.error("整批转发",e);
        }
        if(log.isInfoEnabled()){
            log.info(JsonHelper.toJsonUseGson(result));
        }
        return result;
    }
}
