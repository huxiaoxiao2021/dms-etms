package com.jd.bluedragon.distribution.rest.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.api.response.GantryDeviceConfigResponse;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
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

    @Autowired
    GantryDeviceService gantryDeviceService;

    @POST
    @Path("/gantryDeviceConfig/findAllGantryDeviceCurrentConfig")
    public GantryDeviceConfigResponse findAllGantryDeviceCurrentConfig(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response = new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            List<GantryDeviceConfig> list = gantryDeviceConfigService.findAllGantryDeviceCurrentConfig(request.getCreateSiteCode());
            response.setData(this.ok(list));
        } catch (Exception ex) {
            String message = "获取龙门架" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    private List<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig> ok(List<GantryDeviceConfig> list) {
        List<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig> listReturn = new ArrayList<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig>();
        for (GantryDeviceConfig config : list) {
            com.jd.bluedragon.distribution.api.response.GantryDeviceConfig temp = getGantryDeviceConfig(config);
            listReturn.add(temp);
        }
        return listReturn;
    }

    private com.jd.bluedragon.distribution.api.response.GantryDeviceConfig getGantryDeviceConfig(GantryDeviceConfig config) {
        com.jd.bluedragon.distribution.api.response.GantryDeviceConfig temp = new com.jd.bluedragon.distribution.api.response.GantryDeviceConfig();
//            BeanHelper.copyProperties(temp, config);
        temp.setId(config.getId());
        temp.setUpdateUserErp(config.getUpdateUserErp());
        temp.setUpdateUserName(config.getUpdateUserName());
        temp.setEndTime(config.getEndTime());
        temp.setBusinessType(config.getBusinessType());
        temp.setCreateSiteCode(config.getCreateSiteCode());
        temp.setCreateSiteName(config.getCreateSiteName());
        temp.setDbTime(config.getDbTime());
        temp.setGantrySerialNumber(config.getGantrySerialNumber());
        temp.setUpdateUserName(config.getUpdateUserName());
        temp.setLockStatus(config.getLockStatus());
        temp.setLockUserErp(config.getLockUserErp());
        temp.setLockUserName(config.getLockUserName());
        temp.setMachineId(config.getMachineId());
        temp.setOperateTypeRemark(config.getBusinessTypeRemark());
        temp.setOperateUserErp(config.getOperateUserErp());
        temp.setOperateUserId(config.getOperateUserId());
        temp.setOperateUserName(config.getOperateUserName());
        temp.setSendCode(config.getSendCode());
        temp.setStartTime(config.getStartTime());
        temp.setUpdateUserErp(config.getUpdateUserErp());
        temp.setUpdateUserName(config.getUpdateUserName());
        temp.setYn(config.getYn());
        return temp;
    }

    @POST
    @Path("/gantryDeviceConfig/updateGantryDeviceConfigStatus")
    public GantryDeviceConfigResponse updateGantryDeviceConfigStatus(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response = new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            int count = gantryDeviceConfigService.updateGantryDeviceConfigStatus(toGantryDeviceConfig(request));
            if (count == 1) {
                gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
            }
        } catch (Exception ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    @POST
    @Path("/gantryDeviceConfig/updateLockStatus")
    public GantryDeviceConfigResponse updateLockStatus(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response = new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
            gantryDeviceConfig.setId(Long.parseLong(request.getId().toString()));
            gantryDeviceConfig.setLockStatus(request.getLockStatus());
            gantryDeviceConfig.setLockUserName(request.getLockUserName());
            gantryDeviceConfig.setLockUserErp(request.getLockUserErp());
            int count = gantryDeviceConfigService.updateLockStatus(gantryDeviceConfig);
            if (count == 1) {
                com.jd.bluedragon.distribution.api.response.GantryDeviceConfig config = new com.jd.bluedragon.distribution.api.response.GantryDeviceConfig();
                GantryDeviceConfig temp = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getId());
                BeanHelper.copyProperties(config, temp);
                response.setData(new ArrayList<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig>());
                response.getData().add(config);
            }
        } catch (Exception ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    @POST
    @Path("/gantryDeviceConfig/updateBusinessType")
    public GantryDeviceConfigResponse updateBusinessType(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response = new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        try {
            GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
            gantryDeviceConfig.setId(Long.parseLong(request.getId().toString()));
            gantryDeviceConfig.setBusinessType(request.getBusinessType());
            int count = gantryDeviceConfigService.updateBusinessType(gantryDeviceConfig);
            if (count == 1) {
                com.jd.bluedragon.distribution.api.response.GantryDeviceConfig config = new com.jd.bluedragon.distribution.api.response.GantryDeviceConfig();
                GantryDeviceConfig temp = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getId());
                BeanHelper.copyProperties(config, temp);
                response.setData(new ArrayList<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig>());
                response.getData().add(config);
            }
        } catch (Exception ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    private GantryDeviceConfig toGantryDeviceConfig(GantryDeviceConfigRequest request) {
        GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
        gantryDeviceConfig.setId(Long.parseLong(request.getId().toString()));
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
        gantryDeviceConfig.setUpdateUserErp(request.getUpdateUserErp());
        gantryDeviceConfig.setUpdateUserName(request.getUpdateUserName());
        return gantryDeviceConfig;
    }

    @POST
    @Path("/gantryDeviceConfig/addGantryDeviceConfig")
    public GantryDeviceConfigResponse addGantryDeviceConfig(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        GantryDeviceConfigResponse response = new GantryDeviceConfigResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        com.jd.bluedragon.distribution.api.response.GantryDeviceConfig config = null;
        try {
            GantryDeviceConfig oldRecord = toGantryDeviceConfig(request);
            int count = gantryDeviceConfigService.add(oldRecord);
            if (count == 1) {
                GantryDeviceConfig gantryDeviceConfig = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
                oldRecord.setEndTime(gantryDeviceConfig.getStartTime());
                oldRecord.setUpdateUserErp(gantryDeviceConfig.getOperateUserErp());
                oldRecord.setUpdateUserName(gantryDeviceConfig.getOperateUserName());
                gantryDeviceConfigService.updateGantryDeviceConfigStatus(oldRecord);
                response.setData(new ArrayList<com.jd.bluedragon.distribution.api.response.GantryDeviceConfig>());
                config=getGantryDeviceConfig(gantryDeviceConfig);
                response.getData().add(config);
            }
        } catch (Exception ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            logger.error(message);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    @POST
    @Path("/gantryDeviceConfig/checkSendCode")
    public JdResponse checkSendCode(GantryDeviceConfigRequest request) {
        logger.debug(request.toString());
        JdResponse response = new JdResponse();
        GantryDeviceConfig gantryDeviceConfig = gantryDeviceConfigService.checkSendCode(request.getSendCode());
        if(gantryDeviceConfig!=null){
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
        }else {
            response.setCode(10000);
            response.setMessage("不存在");
        }

        return response;
    }
}
