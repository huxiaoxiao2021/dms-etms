package com.jd.bluedragon.distribution.rest.arAbnormal;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
}
