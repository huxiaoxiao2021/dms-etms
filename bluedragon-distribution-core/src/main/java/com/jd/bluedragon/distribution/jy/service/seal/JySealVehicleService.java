package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

public interface JySealVehicleService {

    /**
     * 根据运输任务bizId查询车的封签号列表
     * @param sealCodeReq
     * @return
     */
    InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq);

    /**
     * 查询封车数据详情
     * @param sealVehicleInfoReq
     * @return
     */
    InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);

    /**
     * 根据运力编码查询运输信息
     * @param transportReq
     * @return
     *
     *
     */
    InvokeResult<TransportResp>  getTransportResourceByTransCode(TransportReq transportReq);

    /**
     * 校验运力编码和任务简码是否匹配
     *
     *
     */
    InvokeResult checkTransportCode(CheckTransportCodeReq checkTransportCodeReq);
    /**
     * 根据任务简码查询车牌号信息
     *
     *
     */
    InvokeResult<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 提交封车
     *
     * NewSealVehicleGatewayServiceImpl#sealCar
     */
    InvokeResult sealVehicle(SealVehicleReq sealVehicleReq);

}
