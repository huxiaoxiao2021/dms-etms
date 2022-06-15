package com.jd.bluedragon.distribution.jy.service.seal;

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
     * 查询封车数据详情
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

}
