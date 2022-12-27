package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.etms.vos.dto.SealCarDto;


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
     * 提交封车-干支封车
     *
     */
    InvokeResult sealVehicle(SealVehicleReq sealVehicleReq);


    /**
     * 传站封车
     *
     */
    InvokeResult czSealVehicle(SealVehicleReq sealVehicleReq);


    /**
     * 根据任务简码 获取任务详情
     * @return
     */
    InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);
    /**
     * 保存封车数据
     *
     */
	InvokeResult<Boolean> saveSealVehicle(SealVehicleReq sealVehicleReq);

    /**
     * 校验运力编码和发货批次的目的地是否一致
     */
    InvokeResult<SealCarSendCodeResp> validateTranCodeAndSendCode(ValidSendCodeReq request);


    /**
     * 校验运力编码的目的地是否一致
     */
    InvokeResult checkTransCodeScan(CheckTransportReq reqcuest);

    /**
     * 根据流向查询组板列表
     */
    InvokeResult<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request);

    /**
     * 取消封车 更新批次任务和封签列表
     */
    InvokeResult<Boolean> cancelSealCar(SealCarDto sealCarCodeOfTms, String operateUserCode, String operateUserName);
    
}
