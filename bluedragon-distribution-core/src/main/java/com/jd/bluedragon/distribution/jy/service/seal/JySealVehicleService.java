package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;


public interface JySealVehicleService {

    /**
     * 根据运输任务bizId查询车的封签号列表
     * @param sealCodeReq
     * @return
     */
    InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq);

    /**
     * 查询流向任务的相关封车数据
     * @param sealVehicleInfoReq
     * @return
     */
    InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);


    /**
     * 提交封车
     *
     */
    InvokeResult sealVehicle(SealVehicleReq sealVehicleReq);


    /**
     * 根据任务简码 获取任务详情
     * @return
     */
    InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 校验运力编码和发货批次的目的地是否一致
     */
    InvokeResult<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq request);

}
