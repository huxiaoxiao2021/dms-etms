package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.request.CapacityInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.request.CheckTransportCodeRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SearCarRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SearCarTaskInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;

/**
 * 封车发布物流网关
 * @author : xumigen
 * @date : 2019/7/8
 */
public interface NewSealVehicleGatewayService {
    JdCResponse cancelSeal(CancelSealRequest gatewayRequest);

    JdCResponse<SealCarTaskInfoDto> getTaskInfo(SearCarTaskInfoRequest request);

    JdCResponse<Integer> getAndCheckTransportCode(CapacityInfoRequest request);

    JdCResponse checkTransportCode(CheckTransportCodeRequest request);

    JdCResponse checkTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType);

    JdCResponse sear(SearCarRequest searCarRequest);

}
