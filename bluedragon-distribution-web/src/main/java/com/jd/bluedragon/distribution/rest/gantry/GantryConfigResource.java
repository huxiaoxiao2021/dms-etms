package com.jd.bluedragon.distribution.rest.gantry;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.GantryDeviceConfigRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by wangtingwei on 2016/8/23.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class GantryConfigResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GantryDeviceConfigService gantryDeviceConfigService;

    @Autowired
    GantryDeviceService gantryDeviceService;

    @POST
    @Path("/gantryConfig/findAllGantryDeviceCurrentConfig")
    public InvokeResult<List<GantryDeviceConfig>> findAllGantryDeviceCurrentConfig(GantryDeviceConfigRequest request) {
        InvokeResult<List<GantryDeviceConfig>> response = new InvokeResult<List<GantryDeviceConfig>>();
        try {
            List<GantryDeviceConfig> list = gantryDeviceConfigService.findAllGantryDeviceCurrentConfig(request.getCreateSiteCode(),request.getVersion());
            response.setData(list);
        } catch (Throwable ex) {
            String message = "获取龙门架" + request.toString() + ex.toString();
            log.error(message,ex);
            response.error(ex);
        }
        return response;
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
        temp.setMachineId(Integer.valueOf(config.getMachineId()));
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
    @Path("/gantryConfig/updateLockStatus")
    public InvokeResult<GantryDeviceConfig> updateLockStatus(GantryDeviceConfigRequest request) {
        InvokeResult<GantryDeviceConfig> response = new InvokeResult<GantryDeviceConfig>();
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
                response.setData(temp);
            }
        } catch (Throwable ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            log.error(message,ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    @POST
    @Path("/gantryConfig/updateBusinessType")
    public  InvokeResult<GantryDeviceConfig>  updateBusinessType(GantryDeviceConfigRequest request) {
        if(log.isDebugEnabled()){
            log.debug(request.toString());
        }
        InvokeResult<GantryDeviceConfig>  response = new  InvokeResult<GantryDeviceConfig> ();
        try {
            GantryDeviceConfig gantryDeviceConfig = new GantryDeviceConfig();
            gantryDeviceConfig.setId(Long.parseLong(request.getId().toString()));
            gantryDeviceConfig.setBusinessType(request.getBusinessType());
            int count = gantryDeviceConfigService.updateBusinessType(gantryDeviceConfig);
            if (count == 1) {
                com.jd.bluedragon.distribution.api.response.GantryDeviceConfig config = new com.jd.bluedragon.distribution.api.response.GantryDeviceConfig();
                GantryDeviceConfig temp = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getId());
                response.setData(temp);
            }
        } catch (Throwable ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            log.error(message, ex);
            response.error(ex);
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
        gantryDeviceConfig.setMachineId(String.valueOf(request.getMachineId()));
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
    @Path("/gantryConfig/addGantryDeviceConfig")
    public InvokeResult<GantryDeviceConfig> addGantryDeviceConfig(GantryDeviceConfigRequest request) {
        if(log.isDebugEnabled()) {
            log.debug(request.toString());
        }

        InvokeResult<GantryDeviceConfig> response = new InvokeResult<GantryDeviceConfig>();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        com.jd.bluedragon.distribution.api.response.GantryDeviceConfig config = null;
        try {
            if(StringUtils.isNotEmpty(request.getSendCode())) {
                GantryDeviceConfig gantryDeviceConfig = gantryDeviceConfigService.checkSendCode(request.getSendCode());
                if (null != gantryDeviceConfig) {
                    response.setCode(JdResponse.CODE_PARAM_ERROR);
                    response.setMessage("批次号重复，请扫描新批次号");
                    return response;
                }
            }
            GantryDeviceConfig oldRecord = toGantryDeviceConfig(request);
            int count = gantryDeviceConfigService.add(oldRecord);
            if (count == 1) {
                GantryDeviceConfig gantryDeviceConfig = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
                oldRecord.setEndTime(gantryDeviceConfig.getStartTime());
                oldRecord.setUpdateUserErp(gantryDeviceConfig.getOperateUserErp());
                oldRecord.setUpdateUserName(gantryDeviceConfig.getOperateUserName());
                response.setData(gantryDeviceConfig);
            }
        } catch (Throwable ex) {
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            log.error(message,ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }
        return response;
    }

    @POST
    @Path("/gantryConfig/checkSendCode")
    public InvokeResult checkSendCode(GantryDeviceConfigRequest request) {
        if(log.isDebugEnabled()) {
            log.debug(request.toString());
        }
        InvokeResult response = new InvokeResult();
        try {
            GantryDeviceConfig gantryDeviceConfig = gantryDeviceConfigService.checkSendCode(request.getSendCode());
            if(gantryDeviceConfig!=null){
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
            }else {
                response.setCode(10000);
                response.setMessage("不存在");
            }
        }catch (Throwable ex){
            String message = "更新龙门架状态失败" + request.toString() + ex.toString();
            log.error(message,ex);
            response.setCode(JdResponse.CODE_INTERNAL_ERROR);
            response.setMessage(message);
        }

        return response;
    }

    @POST
    @Path("/gantryConfig/findMaxStartTimeGantryDeviceConfigByMachineId")
    public InvokeResult<GantryDeviceConfig> findMaxStartTimeGantryDeviceConfigByMachineId(GantryDeviceConfigRequest request){
        if(log.isDebugEnabled()) {
            log.debug("获取龙门架的最新的状态 --> findMaxStartTimeGantryDeviceConfigByMachineId --> 设备名：{}" , request.getMachineId());
        }
        InvokeResult<GantryDeviceConfig> result = new InvokeResult<GantryDeviceConfig>();
        result.setCode(200);
        result.setMessage("服务调用成功");
        try{
            GantryDeviceConfig  gantryDeviceConfig = null;
            if(request.getMachineId() != null){
                gantryDeviceConfig = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(request.getMachineId());
            }
            if(gantryDeviceConfig == null){
                result.setCode(500);
                result.setMessage("没有查到相关的龙门架设备信息");
                result.setData(gantryDeviceConfig);
                log.warn("没有查询ID为{}的龙门架设备信息",request.getMachineId());
            }else{
                result.setData(gantryDeviceConfig);
            }
        }catch(Exception e){
            log.error("服务调用异常。machineID为:{}" , request.getMachineId(),e);
            result.setCode(400);
            result.setMessage("服务调用异常");
            result.setData(null);
        }
        return result;
    }

}
