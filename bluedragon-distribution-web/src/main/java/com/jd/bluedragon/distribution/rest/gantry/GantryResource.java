package com.jd.bluedragon.distribution.rest.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.response.GantryDeviceResponse;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.utils.BeanHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/10
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class GantryResource {
    private final Log logger = LogFactory.getLog(this.getClass());


    @Autowired
    GantryDeviceService gantryDeviceService;

    @POST
    @Path("/gantryDevice/findAllGantryDevice")
    public GantryDeviceResponse findAllGantryDeviceCurrentConfig(GantryDeviceConfigRequest request) {
        GantryDeviceResponse response=new GantryDeviceResponse();
        logger.info(request.toString());
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            List<GantryDevice> list = gantryDeviceService.getGantryByDmsCode(request.getCreateSiteCode());
            response.setData(this.ok(list));
        } catch (Exception ex) {
            String message = "获取龙门架" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    private List<com.jd.bluedragon.distribution.api.response.GantryDevice> ok(List<GantryDevice> list) {
        List<com.jd.bluedragon.distribution.api.response.GantryDevice> listGantryDevice=new ArrayList<com.jd.bluedragon.distribution.api.response.GantryDevice>();
        for (GantryDevice gantryDevice : list) {
            com.jd.bluedragon.distribution.api.response.GantryDevice temp=new com.jd.bluedragon.distribution.api.response.GantryDevice();
            BeanHelper.copyProperties(temp,gantryDevice);
            listGantryDevice.add(temp);
        }
        return listGantryDevice;
    }
}
