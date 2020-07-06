package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarDetailScanResult;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanRequest;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanResult;

import java.util.List;

/**
 * 装卸车服务
 *  发布到物流网关 由安卓调用
 *
 * @author: hujiping
 * @date: 2020/6/24 17:14
 */
public interface LoadAndUnloadCarGatewayService {

    JdCResponse<UnloadCarScanResult> getUnloadCar(String sealCarCode);

    JdCResponse<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest unloadCarScanRequest);

    JdCResponse<List<UnloadCarDetailScanResult>> getUnloadCarDetail(String sealCarCode);

}
