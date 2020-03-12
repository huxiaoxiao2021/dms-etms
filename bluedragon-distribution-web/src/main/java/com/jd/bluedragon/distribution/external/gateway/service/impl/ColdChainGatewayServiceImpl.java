package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainInAndOutBoundRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainQueryUnloadTaskRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadCompleteRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadDto;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadQueryResultDto;
import com.jd.bluedragon.distribution.coldchain.dto.VehicleTypeDict;
import com.jd.bluedragon.distribution.rest.coldchain.ColdChainOperationResource;
import com.jd.bluedragon.external.gateway.service.ColdChainGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainGatewayServiceImpl
 * @date 2020/3/12
 */
public class ColdChainGatewayServiceImpl implements ColdChainGatewayService {

    @Autowired
    private ColdChainOperationResource coldChainOperationResource;

    /**
     * 新增卸货任务
     *
     * @param unloadDto
     * @return
     */
    @Override
    public JdVerifyResponse<Boolean> addUpload(ColdChainUnloadDto unloadDto) {
        JdVerifyResponse<Boolean> response = new JdVerifyResponse<>();

        if (unloadDto == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainUnloadDto request = new ColdChainUnloadDto();
        request.setOrgId(unloadDto.getOrgId());
        request.setOrgName(unloadDto.getOrgName());
        request.setSiteId(unloadDto.getSiteId());
        request.setSiteName(unloadDto.getSiteName());
        request.setUnloadTime(unloadDto.getUnloadTime());
        request.setOperateERP(unloadDto.getOperateERP());
        request.setVehicleNo(unloadDto.getVehicleNo());
        request.setVehicleModelNo(unloadDto.getVehicleModelNo());
        request.setVehicleModelName(unloadDto.getVehicleModelName());
        request.setTemp(unloadDto.getTemp());
        ColdChainOperationResponse<Boolean> operationResponse = coldChainOperationResource.addUpload(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSuccess();
            response.setData(operationResponse.getData());
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;
    }

    /**
     * 查询卸货任务
     *
     * @param taskRequest
     * @return
     */
    @Override
    public JdVerifyResponse<List<ColdChainUnloadQueryResultDto>> queryUnload(ColdChainQueryUnloadTaskRequest taskRequest) {
        JdVerifyResponse<List<ColdChainUnloadQueryResultDto>> response = new JdVerifyResponse<>();

        if (taskRequest == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainQueryUnloadTaskRequest request = new ColdChainQueryUnloadTaskRequest();
        request.setSiteId(taskRequest.getSiteId());
        request.setQueryDays(taskRequest.getQueryDays());
        request.setState(taskRequest.getState());
        request.setVehicleNo(taskRequest.getVehicleNo());
        ColdChainOperationResponse<List<ColdChainUnloadQueryResultDto>> operationResponse = coldChainOperationResource.queryUnload(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSuccess();
            response.setData(operationResponse.getData());
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;

    }

    /**
     * 卸货任务完成
     *
     * @param completeRequest
     * @return
     */
    @Override
    public JdVerifyResponse<Boolean> unloadComplete(ColdChainUnloadCompleteRequest completeRequest) {
        JdVerifyResponse<Boolean> response = new JdVerifyResponse<>();

        if (completeRequest == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainUnloadCompleteRequest request = new ColdChainUnloadCompleteRequest();
        request.setOperateERP(completeRequest.getOperateERP());
        request.setTaskNo(completeRequest.getTaskNo());
        ColdChainOperationResponse<Boolean> operationResponse = coldChainOperationResource.unloadComplete(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSuccess();
            response.setData(operationResponse.getData());
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;
    }

    /**
     * 查询冷链车辆类型
     *
     * @param args
     * @return
     */
    @Override
    public JdVerifyResponse<List<VehicleTypeDict>> getVehicleModelList(String args) {
        JdVerifyResponse<List<VehicleTypeDict>> response = new JdVerifyResponse<>();
        ColdChainOperationResponse<List<VehicleTypeDict>> modelListResponse = coldChainOperationResource.getVehicleModelList();
        if (JdResponse.CODE_OK.equals(modelListResponse.getCode())) {
            response.toSuccess();
            response.setData(modelListResponse.getData());
        } else {
            response.toError(modelListResponse.getMessage());
        }
        return response;
    }

    /**
     * 出入库
     *
     * @param inAndOutBoundRequest
     * @return
     */
    @Override
    public JdVerifyResponse inAndOutBound(ColdChainInAndOutBoundRequest inAndOutBoundRequest) {
        JdVerifyResponse<Boolean> response = new JdVerifyResponse<>();
        if (inAndOutBoundRequest == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainInAndOutBoundRequest request = new ColdChainInAndOutBoundRequest();
        request.setBarCode(inAndOutBoundRequest.getBarCode());
        request.setOrgId(inAndOutBoundRequest.getOrgId());
        request.setOrgName(inAndOutBoundRequest.getOrgName());
        request.setSiteId(inAndOutBoundRequest.getSiteId());
        request.setSiteName(inAndOutBoundRequest.getSiteName());
        request.setOperateERP(inAndOutBoundRequest.getOperateERP());
        request.setOperateTime(inAndOutBoundRequest.getOperateTime());
        request.setOperateType(inAndOutBoundRequest.getOperateType());
        ColdChainOperationResponse operationResponse = coldChainOperationResource.inAndOutBound(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSuccess();
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;
    }
}
