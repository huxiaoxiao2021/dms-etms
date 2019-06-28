package com.jd.bluedragon.distribution.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.request.SearCarRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SearCarTaskInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.request.CapacityInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.request.CheckTransportCodeRequest;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;

/**
 * SealCarGatewayService
 * 封车服务
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public interface SealCarGatewayService {

    JdCResponse<SealCarTaskInfoDto> getTaskInfo(SearCarTaskInfoRequest request);

    JdCResponse<Integer> getAndCheckTransportCode(CapacityInfoRequest request);

    JdCResponse checkTransportCode(CheckTransportCodeRequest request);

    JdCResponse checkTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType);

    JdCResponse sear(SearCarRequest searCarRequest);



}
