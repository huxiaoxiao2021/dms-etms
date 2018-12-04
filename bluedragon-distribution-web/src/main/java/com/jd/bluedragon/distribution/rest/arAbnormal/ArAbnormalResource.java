package com.jd.bluedragon.distribution.rest.arAbnormal;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author tangchunqing
 * @Description: 空铁异常
 * @date 2018年11月30日 15时:06分
 */
public class ArAbnormalResource {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    ArAbnormalService arAbnormalService;
    @POST
    @Path("/arAbnormal/pushArAbnormal")
    public ArAbnormalResponse pushAbnormalOrder(ArAbnormalRequest request){
        ArAbnormalResponse response = new ArAbnormalResponse();
        try{
          return arAbnormalService.pushArAbnormal(request);
        }catch (Exception e) {
            logger.error("ArAbnormalResource.pushAbnormalOrder操作出现异常", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }
}
