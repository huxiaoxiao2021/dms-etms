package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.request.CapacityInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.request.CheckTransportCodeRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarTaskInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;

/**
 * 封车发布物流网关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/8
 */
public interface NewSealVehicleGatewayService {
    JdCResponse cancelSeal(CancelSealRequest gatewayRequest);

    JdCResponse<SealCarTaskInfoDto> getTaskInfo(SealCarTaskInfoRequest request);

    JdCResponse<Integer> getAndCheckTransportCode(CapacityInfoRequest request);

    JdCResponse<TransportInfoDto> getTransportInfoByCode(CapacityInfoRequest request);

    JdCResponse checkTransportCode(CheckTransportCodeRequest request);

    JdCResponse checkTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType);

    JdCResponse newCheckTranCodeAndBatchCode(SealCarPreRequest sealCarPreRequest);

    JdCResponse sealCar(SealCarRequest sealCarRequest);

    JdCResponse verifyVehicleJobByVehicleNumber(String transportCode, String vehicleNumber, Integer sealCarType);

    JdCResponse newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest);

    JdCResponse doSealCarWithVehicleJob(SealCarRequest sealCarRequest);

}
