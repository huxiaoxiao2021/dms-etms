package com.jd.bluedragon.distribution.rest.batchForward;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BatchForwardService batchForwardService;

    /**
     * 批次号整批转发
     * @param request
     * @return
     */
    @POST
    @Path("/batchForward/batchForwardSend")
    public InvokeResult batchForwardSend(BatchForwardRequest request){
        if(logger.isInfoEnabled()){
            logger.info(JsonHelper.toJsonUseGson(request));
        }
        InvokeResult result = null;
        try{

            result = batchForwardService.batchSend(request);
        }catch (Exception e){
            result.error(e);
            this.logger.error("整批转发",e);
        }
        if(logger.isInfoEnabled()){
            logger.info(JsonHelper.toJsonUseGson(result));
        }
        return result;
    }
}
