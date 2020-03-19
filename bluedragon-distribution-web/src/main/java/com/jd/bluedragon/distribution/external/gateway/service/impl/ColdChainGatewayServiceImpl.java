package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
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
    public JdCResponse<Boolean> addUpload(ColdChainUnloadDto unloadDto) {
        JdCResponse<Boolean> response = new JdCResponse<>();

        if (unloadDto == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainOperationResponse<Boolean> operationResponse = coldChainOperationResource.addUpload(unloadDto);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSucceed();
            response.setData(operationResponse.getData());
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;
    }

    /**
     * 查询卸货任务
     *
     * @param request
     * @return
     */
    @Override
    public JdCResponse<List<ColdChainUnloadQueryResultDto>> queryUnload(ColdChainQueryUnloadTaskRequest request) {
        JdCResponse<List<ColdChainUnloadQueryResultDto>> response = new JdCResponse<>();

        if (request == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainOperationResponse<List<ColdChainUnloadQueryResultDto>> operationResponse = coldChainOperationResource.queryUnload(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSucceed();
            response.setData(operationResponse.getData());
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;

    }

    /**
     * 卸货任务完成
     *
     * @param request
     * @return
     */
    @Override
    public JdCResponse<Boolean> unloadComplete(ColdChainUnloadCompleteRequest request) {
        JdCResponse<Boolean> response = new JdCResponse<>();

        if (request == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainOperationResponse<Boolean> operationResponse = coldChainOperationResource.unloadComplete(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSucceed();
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
    public JdCResponse<List<VehicleTypeDict>> getVehicleModelList(String args) {
        JdCResponse<List<VehicleTypeDict>> response = new JdCResponse<>();
        ColdChainOperationResponse<List<VehicleTypeDict>> modelListResponse = coldChainOperationResource.getVehicleModelList();
        if (JdResponse.CODE_OK.equals(modelListResponse.getCode())) {
            response.toSucceed();
            response.setData(modelListResponse.getData());
        } else {
            response.toError(modelListResponse.getMessage());
        }
        return response;
    }

    /**
     * 出入库
     *
     * @param request
     * @return
     */
    @Override
    public JdCResponse inAndOutBound(ColdChainInAndOutBoundRequest request) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        if (request == null) {
            response.toError("请求参数为null");
            return response;
        }

        ColdChainOperationResponse operationResponse = coldChainOperationResource.inAndOutBound(request);
        if (JdResponse.CODE_OK.equals(operationResponse.getCode())) {
            response.toSucceed();
        } else {
            response.toError(operationResponse.getMessage());
        }
        return response;
    }
}
