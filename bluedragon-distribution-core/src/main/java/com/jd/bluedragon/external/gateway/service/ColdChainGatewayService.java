package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainInAndOutBoundRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainQueryUnloadTaskRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadCompleteRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadDto;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadQueryResultDto;
import com.jd.bluedragon.distribution.coldchain.dto.VehicleTypeDict;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainGatewayService
 * @date 2020/3/12
 */
public interface ColdChainGatewayService {

    /**
     * 新增卸货任务
     *
     * @param unloadDto
     * @return
     */
    JdCResponse<Boolean> addUpload(ColdChainUnloadDto unloadDto);

    /**
     * 查询卸货任务
     *
     * @param request
     * @return
     */
    JdCResponse<List<ColdChainUnloadQueryResultDto>> queryUnload(ColdChainQueryUnloadTaskRequest request);

    /**
     *
     * @param request
     * @return
     */
    JdCResponse<Boolean> unloadComplete(ColdChainUnloadCompleteRequest request);

    /**
     *
     * @param args
     * @return
     */
    JdCResponse<List<VehicleTypeDict>> getVehicleModelList(String args);

    /**
     *
     *
     * @param request
     * @return
     */
    JdCResponse inAndOutBound(ColdChainInAndOutBoundRequest request);

}
