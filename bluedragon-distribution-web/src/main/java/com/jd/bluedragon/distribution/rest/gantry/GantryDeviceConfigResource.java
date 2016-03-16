package com.jd.bluedragon.distribution.rest.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.response.GantryDeviceConfigResponse;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
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
 * Created by yanghongqiang on 2016/3/15.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class GantryDeviceConfigResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    GantryDeviceConfigService gantryDeviceConfigService;

    @POST
    @Path("/findAllGantryDeviceCurrentConfig")
    public GantryDeviceConfigResponse findAllGantryDeviceCurrentConfig(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response=new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            List<GantryDeviceConfig> list= gantryDeviceConfigService.findAllGantryDeviceCurrentConfig(request.getCreateSiteCode());
            response.setData(this.ok(list));
        }
        catch (Exception ex){
            String message="获取龙门架"+request.toString()+ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    private List<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig> ok(List<GantryDeviceConfig> list) {
        List<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig> listReturn=new ArrayList<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig>() ;
        for(GantryDeviceConfig config :list) {
            com.jd.bluedragon.distribution.api.response.GantryDeviceConfig temp=new com.jd.bluedragon.distribution.api.response.GantryDeviceConfig();
            BeanHelper.copyProperties(temp,config);
            listReturn.add(temp);
        }
        return  listReturn;
    }

    @POST
    @Path("/updateGantryDeviceConfigStatus")
    public GantryDeviceConfigResponse updateGantryDeviceConfigStatus(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response=new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            int count=gantryDeviceConfigService.updateGantryDeviceConfigStatus(toGantryDeviceConfig(request));
            if(count==1) {
                gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
            }
        }catch (Exception ex){
            String message="更新龙门架状态失败"+request.toString()+ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    private GantryDeviceConfig toGantryDeviceConfig(GantryDeviceConfigRequest request) {
        GantryDeviceConfig gantryDeviceConfig=new GantryDeviceConfig();
        gantryDeviceConfig.setBusinessType(request.getBusinessType());
        gantryDeviceConfig.setCreateSiteCode(request.getCreateSiteCode());
        gantryDeviceConfig.setCreateSiteName(request.getCreateSiteName());
        gantryDeviceConfig.setEndTime(request.getEndTime());
        gantryDeviceConfig.setGantrySerialNumber(request.getGantrySerialNumber());
        gantryDeviceConfig.setLockStatus(request.getLockStatus());
        gantryDeviceConfig.setLockUserErp(request.getLockUserErp());
        gantryDeviceConfig.setLockUserName(request.getLockUserName());
        gantryDeviceConfig.setMachineId(request.getMachineId());
        gantryDeviceConfig.setBusinessTypeRemark(request.getOperateTypeRemark());
        gantryDeviceConfig.setOperateUserErp(request.getOperateUserErp());
        gantryDeviceConfig.setOperateUserId(request.getOperateUserId());
        gantryDeviceConfig.setOperateUserName(request.getOperateUserName());
        gantryDeviceConfig.setSendCode(request.getSendCode());
        gantryDeviceConfig.setStartTime(request.getStartTime());
        gantryDeviceConfig.setUpdateUser(request.getUpdateUserErp());
        gantryDeviceConfig.setUpdateUserName(request.getUpdateUserName());
        return gantryDeviceConfig;
    }

    @POST
    @Path("/addGantryDeviceConfig")
    public GantryDeviceConfigResponse addGantryDeviceConfig(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response=new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            int count=gantryDeviceConfigService.add(toGantryDeviceConfig(request));
            if(count==1) {
                gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
            }
        }catch (Exception ex){
            String message="更新龙门架状态失败"+request.toString()+ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }
}
