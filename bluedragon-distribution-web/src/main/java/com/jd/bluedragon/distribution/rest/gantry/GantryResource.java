package com.jd.bluedragon.distribution.rest.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.request.GantryVelocityRequest;
import com.jd.bluedragon.distribution.api.response.GantryDeviceResponse;
import com.jd.bluedragon.distribution.api.response.GantryVelocityResponse;
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
        logger.info("查找所有" + (request.getVersion() == 1?"新的":"老的") + "龙门架设备：" + request.toString());
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            List<GantryDevice> list = gantryDeviceService.getGantryByDmsCode(request.getCreateSiteCode(),request.getVersion());//添加version字段，1新  0旧 null默认旧
            response.setData(this.ok(list));
        } catch (Throwable ex) {
            String message = "获取龙门架设备异常" + request.toString() + ex.toString();
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

    @POST
    @Path("/gantryDevice/getGantryVelocity")
    public GantryVelocityResponse findAllGantryDeviceCurrentConfig(GantryVelocityRequest request) {
        GantryVelocityResponse response=new GantryVelocityResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {

            Integer velocity = gantryDeviceService.getGantryVelocity(request.getCreateSiteCode(),request.getGantrySerialNumber(),request.getStartTime(),request.getEndTime());

            //这里需要做一个判断  龙门架有个最大流速3600 和 最大流速 5000


            if(velocity == 0){
                velocity = 3600;
            }

            if(velocity > 5000){
                velocity = 5000;
            }

            response.setVelocity(velocity);
        } catch (Throwable ex) {
            String message = "获取龙门架设备异常" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }
}
