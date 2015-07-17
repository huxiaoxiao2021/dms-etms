package com.jd.bluedragon.distribution.rest.batch;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.domain.BatchSendRequest;
import com.jd.bluedragon.distribution.batch.domain.BatchSendResponse;
import com.jd.bluedragon.distribution.batch.service.BatchSendService;

import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BatchSendResource {


    @Autowired
    private BatchSendService batchSendService;

    private final Log logger = LogFactory.getLog(this.getClass());


    @POST
    @Path("/batchsend/findBatchSend")
    public BatchSendResponse findBatchSend(BatchSendRequest request) {
        if(request.getCreateSiteCode()==null){
        	return new BatchSendResponse(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
        }
        
        return batchSendService.findBatchSend(request);
    }

    @POST
    @Path("/batchsend/addbatchsend")
    public JdResponse add(BatchSend batchSend){

        JdResponse jdResponse=new JdResponse();
        try {
            if (batchSend == null) {
                jdResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                jdResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                logger.error("插入批次号批次对象为空！");
                return jdResponse;
            }
            if (batchSend.getCreateSiteCode() == null || batchSend.getReceiveSiteCode() == null
                    || batchSend.getSendCode() == null || batchSend.getCreateUser() == null || batchSend.getCreateUserCode() == null) {
                jdResponse.setCode(JdResponse.CODE_PARAM_ERROR);
                jdResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
                logger.error("插入批次号失败："+JsonHelper.toJson(batchSend));
                return jdResponse;
            }

            batchSendService.add(batchSend);
            logger.info("插入批次号成功：" + batchSend.getSendCode());
            jdResponse.setCode(JdResponse.CODE_OK);
            jdResponse.setMessage(JdResponse.MESSAGE_OK);
        }
        catch (Exception e){
            logger.error("插入批次号失败：",e);
            jdResponse.setCode(JdResponse.CODE_INTERNAL_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return  jdResponse;
    }
   
}
