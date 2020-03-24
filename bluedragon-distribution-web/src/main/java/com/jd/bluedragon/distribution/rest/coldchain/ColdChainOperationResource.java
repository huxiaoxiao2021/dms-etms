package com.jd.bluedragon.distribution.rest.coldchain;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainInAndOutBoundRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainQueryUnloadTaskRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadCompleteRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadDto;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadQueryResultDto;
import com.jd.bluedragon.distribution.coldchain.dto.VehicleTypeDict;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainOperationService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.dms.logger.annotation.BusinessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ColdChainOperationResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ColdChainOperationService coldChainOperationService;

    /**
     * 新增卸货任务
     *
     * @param unloadDto
     * @return
     */
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1016, operateType = 101601)
    @POST
    @Path("/coldChain/operation/addUpload")
    public ColdChainOperationResponse<Boolean> addUpload(ColdChainUnloadDto unloadDto) {
        ColdChainOperationResponse<Boolean> response = new ColdChainOperationResponse<>();
        try {
            if (this.checkParams(unloadDto)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
                response.setData(coldChainOperationService.addUploadTask(unloadDto));
            } else {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("[冷链操作-冷链卸货]上传卸货数据时发生异常,request:" + JsonHelper.toJson(unloadDto), e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 参数检查
     *
     * @param unloadDto
     * @return
     */
    private boolean checkParams(ColdChainUnloadDto unloadDto) {
        if (unloadDto == null) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getOrgId())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getOrgName())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getSiteId())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getSiteName())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getUnloadTime())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getOperateERP())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getVehicleNo())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getVehicleModelNo())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getVehicleModelName())) {
            return false;
        }

        if (StringUtils.isBlank(unloadDto.getTemp())) {
            return false;
        }
        return true;
    }

    /**
     * 查询卸货任务
     *
     * @param request
     * @return
     */
    @POST
    @Path("/coldChain/operation/queryUnload")
    public ColdChainOperationResponse<List<ColdChainUnloadQueryResultDto>> queryUnload(ColdChainQueryUnloadTaskRequest request) {
        ColdChainOperationResponse<List<ColdChainUnloadQueryResultDto>> response = new ColdChainOperationResponse<>();
        try {
            if (this.checkParams(request)) {
                List<ColdChainUnloadQueryResultDto> result = coldChainOperationService.queryUnloadTask(request);
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
                response.setData(result);
            } else {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("[冷链操作-卸货任务查询]查询卸货任务时发生异常,request:" + JsonHelper.toJson(request), e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 参数检查
     *
     * @param request
     * @return
     */
    private boolean checkParams(ColdChainQueryUnloadTaskRequest request) {
        if (request == null) {
            return false;
        }

        if (StringUtils.isBlank(request.getSiteId())) {
            return false;
        }
        return true;
    }


    /**
     * 卸货任务完成
     *
     * @param request
     * @return
     */
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1016, operateType = 101602)
    @POST
    @Path("/coldChain/operation/unloadComplete")
    public ColdChainOperationResponse<Boolean> unloadComplete(ColdChainUnloadCompleteRequest request) {
        ColdChainOperationResponse<Boolean> response = new ColdChainOperationResponse<>();
        try {
            if (this.checkParams(request)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage(JdResponse.MESSAGE_OK);
                response.setData(coldChainOperationService.unloadTaskComplete(request.getTaskNo(), request.getOperateERP()));
            } else {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("[冷链操作-卸货完成]完成卸货时发生异常,request:" + JsonHelper.toJson(request), e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 参数检查
     *
     * @param request
     * @return
     */
    private boolean checkParams(ColdChainUnloadCompleteRequest request) {
        if (request == null) {
            return false;
        }

        if (StringUtils.isBlank(request.getOperateERP())) {
            return false;
        }

        if (StringUtils.isBlank(request.getTaskNo())) {
            return false;
        }
        return true;
    }

    @GET
    @Path("/coldChain/getVehicleTypeByType")
    public ColdChainOperationResponse<List<VehicleTypeDict>> getVehicleModelList() {
        ColdChainOperationResponse<List<VehicleTypeDict>> response = new ColdChainOperationResponse<>();
        try {
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
            response.setData(coldChainOperationService.getVehicleModelList());
        } catch (Exception e) {
            log.error("[冷链操作-获取冷链车型]查询冷链车型时发生异常", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 冷链货物出入库操作
     *
     * @param request
     * @return
     */
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1016, operateType = 101603)
    @POST
    @Path("/coldChain/operation/inAndOutBound")
    public ColdChainOperationResponse inAndOutBound(ColdChainInAndOutBoundRequest request) {
        ColdChainOperationResponse response = new ColdChainOperationResponse<>();
        try {
            if (this.checkParams(request)) {
                response = coldChainOperationService.inAndOutBound(request);
            } else {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("[冷链操作-出入库]操作出入库时发生异常,request:" + JsonHelper.toJson(request), e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 参数检查
     *
     * @param request
     * @return
     */
    private boolean checkParams(ColdChainInAndOutBoundRequest request) {
        if (request == null) {
            return false;
        }

        if (StringUtils.isBlank(request.getBarCode())) {
            return false;
        }

        if (request.getOrgId() == null) {
            return false;
        }

        if (StringUtils.isBlank(request.getOrgName())) {
            return false;
        }

        if (request.getSiteId() == null) {
            return false;
        }

        if (StringUtils.isBlank(request.getSiteName())) {
            return false;
        }

        if (StringUtils.isBlank(request.getOperateTime())) {
            return false;
        }

        if (StringUtils.isBlank(request.getOperateERP())) {
            return false;
        }

        if (request.getOperateType() == null) {
            return false;
        }

        return true;
    }

}
