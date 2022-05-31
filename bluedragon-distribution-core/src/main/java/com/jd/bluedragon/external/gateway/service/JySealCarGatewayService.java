package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;

/**
 * jy封车相关网关api
 */
public interface JySealCarGatewayService {

    /**
     * 根据运输任务bizId查询车的封签号列表
     * @param sealCodeReq
     * @return
     */
    JdCResponse<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq);

    /**
     * 查询封车数据详情
     * @param sealVehicleInfoReq
     * @return
     */
    JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);

    /**
     * 根据运力编码查询运输信息
     * @param transportReq
     * @return
     *
     */
    JdCResponse<TransportInfoDto>  getTransportResourceByTransCode(TransportReq transportReq);

    /**
     * 校验运力编码和任务简码是否匹配
     *
     *
     */
    JdCResponse checkTransportCode(CheckTransportCodeReq checkTransportCodeReq);

    /**
     * 根据任务简码查询运输任务相关信息(可用于 用户录入任务简码后 校验任务简码对应车牌号信息跟当前车牌号是否一致)
     *
     */
    JdCResponse<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 提交封车
     *
     * NewSealVehicleGatewayServiceImpl#sealCar
     */
    JdCResponse sealVehicle(SealVehicleReq sealVehicleReq);

}
