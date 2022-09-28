package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
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
     * 查询流向任务封车数据详情
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
     */
    JdCResponse checkTransportCode(CheckTransportCodeReq checkTransportCodeReq);

    /**
     * 根据任务简码查询运输任务相关信息--原pda调用接口逻辑
     *
     */
    JdCResponse<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 根据任务简码查询任务详情
     * @param getVehicleNumberReq
     * @return
     */
    JdCResponse<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 提交封车
     *
     */
    JdCResponse sealVehicle(SealVehicleReq sealVehicleReq);

    /**
     * 校验运力编码和发货批次的目的地是否一致
     */
    JdCResponse<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq validSendCodeReq);

}
